
package com.nx.cache.expression;

import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
public class ExpireableObject<T> {


	private T target;

	private long expireAt;

	public ExpireableObject() {}

	/**
	 * 单位豪标
	 * @param target
	 * @param timeout
	 */
	public ExpireableObject(T target, long timeout, TimeUnit timeUnit) {
		this.target = target;
		if (timeout==-1){
			this.expireAt = -1;
		}else{
			this.expireAt = System.currentTimeMillis() + timeUnit.toMillis(timeout);
		}
	}

	public T getTarget() {
		return target;
	}

	public void setTarget(T target) {
		this.target = target;
	}

	public long getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(long expireAt) {
		this.expireAt = expireAt;
	}
}
