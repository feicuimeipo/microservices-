package com.nx.amqp.adapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nx.amqp.adapter.utils.MQBeanUtils;
import com.nx.amqp.adapter.utils.MQContext;
import com.nx.common.context.CurrentRuntimeContext;
import com.nx.httpclient.NxHttpUtil;
import com.nx.utils.BeanUtils;
import com.nx.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@JsonInclude(Include.NON_NULL)
public class MQMessage {

	private String msgId;
	private String topic;
	private String tag;
	private String bizKey;
	private Object body;
	private String checkUrl;
	private String txId;
	private Map<String, String> headers;
	@JsonIgnore
	private Long processTime; //处理时间
	private Long deliverTime; //定时消息
	//=======================

	private Integer partition;
	@JsonIgnore
	private long offset;
	private int consumeTimes;
	@JsonIgnore
	private Object originMessage;

	MQProviderConfig config;

	public MQMessage() {}

	public static MQMessage build(String json) {
		MQMessage message = JsonUtil.toObject(json, MQMessage.class);
		if (!BeanUtils.isSimpleDataType(message.body.getClass())) {
			message.setBody(JsonUtil.toJson(message.body));
		}
		return message;
	}

	public MQMessage(String topic, Object body) {
		this(topic, null, body);
	}

	public MQMessage(String topic, String bizKey, Object body) {
		this(topic, null, bizKey, body);
	}

	public MQMessage(String topic, String tag, String bizKey, Object body) {
		this();

		config = MQProviderConfigFactory.getConfig();
		this.topic = MQContext.rebuildWithNamespace(topic,config);
		this.tag = tag;
		this.bizKey = bizKey;
		if (body instanceof byte[]) {
			this.body = new String((byte[]) body, StandardCharsets.UTF_8);
		} else {
			this.body = body;
		}
	}


	public String getCheckUrl() {
		return checkUrl;
	}

	public void setCheckUrl(String checkUrl) {
		this.checkUrl = checkUrl;
	}

	/**
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * @param topic
	 *            the topic to set
	 */
	public void setTopic(String topic) {
		this.topic =  MQContext.rebuildWithNamespace(topic,config);
	}


	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @return the bizKey
	 */
	public String getBizKey() {
		return bizKey;
	}

	/**
	 * @param bizKey
	 *   the bizKey to set
	 */
	public void setBizKey(String bizKey) {
		this.bizKey = bizKey;
	}

	public Long getProcessTime() {
		return processTime;
	}

	public void setProcessTime(Long processTime) {
		this.processTime = processTime;
	}

	/**
	 * @return the body
	 */
	public Object getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(Object body) {
		this.body = body;
	}


	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Long getDeliverTime() {
		return deliverTime;
	}

	public void setDeliverTime(Long deliverTime) {
		this.deliverTime = deliverTime;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public long getOffset() {
		return offset;
	}

	public int getConsumeTimes() {
		return consumeTimes;
	}

	public Integer getPartition() {
		return partition;
	}

	public void setPartition(Integer partition) {
		this.partition = partition;
	}

	public <T> T getOriginMessage(Class<?> clazz) {
		return (T) this.originMessage;
	}

	public void setOriginMessage(Object originMessage) {
		this.originMessage = originMessage;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public void addHeader(String name,String value) {
		if(headers == null)headers = new LinkedHashMap<>();
		headers.put(name, value);
	}

	public void onProducerFinished(String msgId,int partition,long offset) {
		this.msgId = msgId;
		this.partition = partition;
		this.offset = offset;
	}

	public byte[] bodyAsBytes() {
		if (BeanUtils.isSimpleDataType(body.getClass())) {
			return body.toString().getBytes(StandardCharsets.UTF_8);
		} else {
			return JsonUtil.toJson(body).getBytes(StandardCharsets.UTF_8);
		}
	}

	public String toMessageValue(boolean onlyBody) {
		if(onlyBody) {
			if (BeanUtils.isSimpleDataType(body.getClass())) {
				return body.toString();
			}else {
				return JsonUtil.toJson(body);
			}
		}
		mergeContextHeaders();
		return JsonUtil.toJson(this);
	}

	public void mergeContextHeaders() {
		if(headers == null) {
			headers = CurrentRuntimeContext.getContextHeaders();
		}else {
			headers.putAll(CurrentRuntimeContext.getContextHeaders());
		}
	}

	public <T> T toObject(Class<T> clazz) {
		if(body instanceof String == false) {
			if(body.getClass() == clazz) {
				return (T) body;
			}else {
				return MQBeanUtils.copy(body, clazz);
			}
		}
		return JsonUtil.toObject(body.toString(), clazz);
	}

	public <T> List<T> toList(Class<T> clazz) {
		if(body instanceof List) {
			return MQBeanUtils.copy((List)body, clazz);
		}
		return JsonUtil.toList(body.toString(), clazz);
	}

	public boolean originStatusCompleted() {
		if (StringUtils.isAnyBlank(txId, checkUrl))
			return true;

		String url = String.format("%s?txId=%s", checkUrl,txId);

		String status = NxHttpUtil.get(url).getBody();
		if (Boolean.parseBoolean(status)) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
			status = NxHttpUtil.get(url).getBody();
		}

		return Boolean.parseBoolean(status);

	}

	public String logString() {
		return "[msgId=" + msgId + ", topic=" + topic + ", tag=" + tag + ", bizKey=" + bizKey + "]";
	}


}