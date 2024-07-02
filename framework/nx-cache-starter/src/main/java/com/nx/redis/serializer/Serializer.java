package com.nx.redis.serializer;

import java.io.IOException;

/**
 * 对象序列化接口
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2016年12月28日
 */
public interface Serializer {
	
	public String name();

	public byte[] serialize(Object obj) throws IOException ;
	
	public Object deserialize(byte[] bytes) throws IOException ;
	
}
