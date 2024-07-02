package com.nx.amqp.adapter.provider;

import com.nx.amqp.adapter.MQConsumer;
import com.nx.amqp.adapter.MQMessage;
import com.nx.amqp.adapter.MQProviderConfig;
import com.nx.amqp.adapter.MQProviderConfigFactory;
import com.nx.amqp.adapter.utils.*;
import com.nx.amqp.adapter.utils.async.DelayRetryExecutor;
import com.nx.amqp.adapter.utils.async.ICaller;
import com.nx.amqp.adapter.utils.async.StandardThreadExecutor;
import com.nx.amqp.adapter.utils.async.StandardThreadFactory;
import com.nx.amqp.adapter.enums.ActionType;
import com.nx.amqp.adapter.MessageHandler;
import com.nx.common.context.CurrentRuntimeContext;
import com.nx.common.context.ThreadLocalContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class AbstractConsumer implements MQConsumer {

	protected static Logger logger = LoggerFactory.getLogger(AbstractConsumer.class);
	protected Map<String, MessageHandler> messageHandlers;
	protected int batchSize;
	private AtomicBoolean closed = new AtomicBoolean(false);
	// 接收线程
	protected StandardThreadExecutor fetchExecutor;
	// 默认处理线程池
	protected StandardThreadExecutor asyncProcessExecutor;

	protected DelayRetryExecutor retryExecutor;

	MQProviderConfig config;
	//
	protected Semaphore semaphore;

	public AbstractConsumer(Map<String, MessageHandler> messageHandlers) {
		config = MQProviderConfigFactory.getConfig();
		this.messageHandlers = messageHandlers;
		this.batchSize = config.getConsumer().getFetchBatchSize();
	}

	protected void startWorker() {

		int fetchCoreThreads = 1; //异步处理拉取线程默认1
		int fetchMaxThreads = fetchCoreThreads;
		//异步处理
		if(config.getConsumer().isAsync()) {
			int maxThread = config.getConsumer().getProcessThreads();
			semaphore = new Semaphore(maxThread);
			this.asyncProcessExecutor = new StandardThreadExecutor(1, maxThread,60, TimeUnit.SECONDS,maxThread,new StandardThreadFactory("messageAsyncProcessor"));
			//
			fetchMaxThreads = maxThread;
			logger.info("MENDMIX-TRACE-LOGGGING-->> init asyncProcessExecutor finish -> maxThread:{}",maxThread);
		}
		//
		this.fetchExecutor = new StandardThreadExecutor(fetchCoreThreads, fetchMaxThreads,0, TimeUnit.SECONDS, fetchMaxThreads * 10,new StandardThreadFactory("messageFetcher"));
		fetchExecutor.execute(new Worker());

		//异步重试
		retryExecutor = new DelayRetryExecutor(1,5000, 1000, 3);

		logger.info("MENDMIX-TRACE-LOGGGING-->> init fetchExecutor finish -> fetchMaxThreads:{}",fetchMaxThreads);

	}

	public abstract List<MQMessage> fetchMessages();

	public abstract String handleMessageConsumed(MQMessage message);


	/**
	 * 日志记录
	 * @param message
	 * @param ex
	 */
	private void processMessageConsumeLog(MQMessage message,Exception ex){
		if(ex == null) {
			handleMessageConsumed(message);
		}
		MQContext.processMessageLog(config,message,ActionType.sub, ex);
	}

	/**
	 * 处理消息
	 * @param message
	 * @throws InterruptedException
	 */
	private void asyncConsumeMessage(MQMessage message) throws InterruptedException {

		if(config.getConsumer().getMaxRetryTimes() > 0 && message.getConsumeTimes() > config.getConsumer().getMaxRetryTimes()) {
			return;
		}

		//信号量获取通行证
		semaphore.acquire();
		asyncProcessExecutor.execute(new Runnable() {
			@Override
			public void run() {
				consumeMessage(message);
			}

		});
	}
	
	private void consumeMessage(MQMessage message) {
		MessageHandler messageHandler = messageHandlers.get(message.getTopic());
		try {
			//上下文
			if(message.getHeaders() != null) {
				message.getHeaders().keySet().forEach(key->{
					CurrentRuntimeContext.addContextHeader(key,message.getHeaders().get(key));
				});

			}
			//消息状态检查
			if(!message.originStatusCompleted()) {
				if(message.getConsumeTimes() <= 1) {
					retryExecutor.submit("message:"+message.getMsgId(), new ICaller<Void>() {
						@Override
						public Void call() throws Exception{
							if(message.originStatusCompleted()) {
								messageHandler.process(message);
							}
							return null;
						}
					});
				}else {
					logger.info("MENDMIX-TRACE-LOGGGING-->> MQmessage_TRANSACTION_STATUS_INVALID ->topic:{},txId:{}",message.getTopic(),message.getTxId());
					processMessageConsumeLog(message,new IllegalArgumentException("txId["+message.getTxId()+"] not found"));
				}
				logger.info("MQmessage_CONSUME_ABORT_ADD_RETRY -> message:{}",message.logString());
				return;
			}
			messageHandler.process(message);
			//处理成功，删除
			processMessageConsumeLog(message,null);
			if(logger.isDebugEnabled()) {
				logger.debug("MENDMIX-TRACE-LOGGGING-->> MQmessage_CONSUME_SUCCESS -> message:{}",message.logString());
			}
		}catch (Exception e) {
			logger.error(String.format("MENDMIX-TRACE-LOGGGING-->> MQmessage_CONSUME_ERROR -> [%s]",message.logString()),e);
			if(messageHandler.retrieable()) {
				retryExecutor.submit("message:"+message.getMsgId(), new ICaller<Void>() {
					@Override
					public Void call() throws Exception{
						messageHandler.process(message);
						return null;
					}
				});
			}else {
				processMessageConsumeLog(message,e);
			}
		} finally {
			ThreadLocalContext.unset();
			//释放信号量
			if(semaphore != null)semaphore.release();
		}
	}
	
	@Override
	public void shutdown() {
		closed.set(true);
		if(fetchExecutor != null) {
			fetchExecutor.shutdown();
		}
		if(asyncProcessExecutor != null) {
			asyncProcessExecutor.shutdown();
		}
	}

	private class Worker implements Runnable{

		@Override
		public void run() {
			while(!closed.get()){
				try {
					if(asyncProcessExecutor != null && asyncProcessExecutor.getSubmittedTasksCount() >= config.getConsumer().getProcessThreads()) {
						Thread.sleep(1);
						continue;
					}
					List<MQMessage> messages = fetchMessages();
					if(messages == null || messages.isEmpty()){
						Thread.sleep(100);
						continue;
					}
					for (MQMessage message : messages) {
						if(asyncProcessExecutor == null) {
							consumeMessage(message);
						}else {
							asyncConsumeMessage(message);
						}
					}
				} catch (Exception e) {

				}
			}
		}

	}
}
