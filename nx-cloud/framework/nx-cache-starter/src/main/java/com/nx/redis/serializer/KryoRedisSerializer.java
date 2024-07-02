package com.nx.redis.serializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;


public class KryoRedisSerializer implements RedisSerializer<Object>{

	@Override
	public byte[] serialize(Object t) throws SerializationException {
		return SerializeUtils.serialize(t, SerializerTypeEnum.Kryo.getCode());
	}

	@Override
	public Object deserialize(byte[] bytes) throws SerializationException {
		return SerializeUtils.deserialize(bytes,SerializerTypeEnum.Kryo.getCode());
	}

}
