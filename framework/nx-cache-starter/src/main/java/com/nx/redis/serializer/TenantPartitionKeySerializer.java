package com.nx.redis.serializer;


import com.nx.redis.utils.RedisKeyUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TenantPartitionKeySerializer implements RedisSerializer<String>{

	
	private final Charset charset;


	public TenantPartitionKeySerializer() {
		this(StandardCharsets.UTF_8);
	}

	public TenantPartitionKeySerializer(Charset charset) {
		Assert.notNull(charset, "Charset must not be null!");
		this.charset = charset;
	}

	@Override
	public String deserialize(@Nullable byte[] bytes) {
		if(bytes == null)return null;
		String key = new String(bytes, charset);
		return key;
	}


	@Override
	public byte[] serialize(@Nullable String key) {
		if (key==null) return null;
		key = RedisKeyUtils.buildNameSpaceKey(key);
		return key.getBytes(charset);
	}

	@Override
	public Class<?> getTargetType() {
		return String.class;
	}

}
