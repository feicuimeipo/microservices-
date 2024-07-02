package com.nx.amqp.adapter.provider.eventbus;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.nx.amqp.adapter.MQMessage;
import com.nx.amqp.adapter.MQProducer;
import com.nx.amqp.adapter.MessageHandler;
import com.nx.common.banner.BannerUtils;
import com.nx.common.context.CurrentRuntimeContext;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
/**
 *
 * <br>
 * Class Name : EventbusProducerAdapter
 *
 * @author jiangwei
 * @version 1.0.0
 * @date 2020年3月24日
 */
public class EventbusProducerAdapter implements MQProducer {

	private final static Logger logger = LoggerFactory.getLogger(EventbusProducerAdapter.class);

	private final static Translator TRANSLATOR = new Translator();
	private static EventHandler<MQMessageEvent> eventHandler = new MQMessageEventHandler();
	private static Map<String, MessageHandler> messageHandlers = new HashMap<>();

	private Disruptor<MQMessageEvent> disruptor;

	@Override
	public void start() throws Exception {
		EventFactory<MQMessageEvent> eventFactory = new MQMessageEventFactory();
		int ringBufferSize = 1024 * 1024;
		BasicThreadFactory factory = new BasicThreadFactory.Builder()
				.namingPattern("workerthread-%d")
				.daemon(true)
				.priority(Thread.MAX_PRIORITY)
				.build();
		disruptor = new Disruptor<MQMessageEvent>(eventFactory,
				ringBufferSize, factory, ProducerType.SINGLE,
				new YieldingWaitStrategy());

		disruptor.handleEventsWith(eventHandler);

		disruptor.start();


	}

	@Override
	public String sendMessage(MQMessage message, boolean async) {
		message.mergeContextHeaders();
		RingBuffer<MQMessageEvent> ringBuffer = disruptor.getRingBuffer();
		ringBuffer.publishEvent(TRANSLATOR, message);
		return null;
	}

	@Override
	public void shutdown() {
		disruptor.shutdown();
	}

	public static void setMessageHandlers(Map<String, MessageHandler> messageHandlers) {
		EventbusProducerAdapter.messageHandlers = messageHandlers;
	}

	static class MQMessageEvent {
		private MQMessage value;
		public void set(MQMessage value) {
			this.value = value;
		}
		public MQMessage get() {
			return value;
		}
	}

	static class MQMessageEventFactory implements EventFactory<MQMessageEvent> {
		public MQMessageEvent newInstance() {
			return new MQMessageEvent();
		}
	}

	static class MQMessageEventHandler implements EventHandler<MQMessageEvent> {
		public void onEvent(MQMessageEvent event, long sequence, boolean endOfBatch) {
			MQMessage message = event.get();
			//上下文
			if(message.getHeaders() != null) {
				message.getHeaders().keySet().forEach(key->{
					CurrentRuntimeContext.addContextHeader(key,message.getHeaders().get(key));
				});

			}
			MessageHandler handler = messageHandlers.get(message.getTopic());
			if(handler != null){
				try {
					messageHandlers.get(message.getTopic()).process(message);
					if(logger.isDebugEnabled())logger.debug("MENDMIX-TRACE-LOGGGING-->> MQ_MESSAGE_CONSUME_SUCCESS ->topic:{},message:{}",message.getTopic(),sequence);
				} catch (Exception e) {
					logger.error(String.format("MENDMIX-TRACE-LOGGGING-->> MQ_MESSAGE_CONSUME_ERROR ->topic:%s,msgId:%s", message.getTopic(),sequence),e);
				}
			}
		}
	}

	static class Translator implements EventTranslatorOneArg<MQMessageEvent, MQMessage> {
		@Override
		public void translateTo(MQMessageEvent event, long sequence, MQMessage data) {
			event.set(data);
		}
	}

}