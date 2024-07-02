package com.nx.redis.serializer;

import com.nx.redis.serializer.kryo.KryoPoolSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.io.IOException;

/**
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2016年12月28日
 */
public class SerializeUtils {

	static KryoPoolSerializer kryoPoolSerializer = new KryoPoolSerializer();
	static Jackson2JsonRedisSerializer<Object> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);


	/**
	 * 序列化
	 *
	 * @param object 需要序列化的对象
	 * @return
	 */
	public static byte[] serialize(Object object,String serializerType) {
		SerializerTypeEnum serializerTypeEnum = SerializerTypeEnum.valueOfByCode(serializerType);


		 if(SerializerTypeEnum.Kryo==serializerTypeEnum){
			 try {
				 return kryoPoolSerializer.serialize(object);
			 } catch (IOException e) {
				 throw new RuntimeException(e);
			 }
		 }
		return jsonRedisSerializer.serialize(object);
	}



    /**
     * 反序列化
     *
     * @param bytes 需要被反序列化的数据
     * @return
     */
    public static Object deserialize(byte[] bytes,String serializerType) {
		SerializerTypeEnum serializerTypeEnum = SerializerTypeEnum.valueOfByCode(serializerType);

		if(SerializerTypeEnum.Kryo==serializerTypeEnum){
			try {
				return kryoPoolSerializer.deserialize(bytes);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return jsonRedisSerializer.deserialize(bytes);
    }
}
