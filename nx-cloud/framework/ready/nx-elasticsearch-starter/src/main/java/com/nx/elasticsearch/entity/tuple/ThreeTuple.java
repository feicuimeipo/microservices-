package com.nx.elasticsearch.entity.tuple;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ThreeTuple<A, B, C> extends TwoTuple<A, B>  implements Serializable {
	public C getThird() {
		return third;
	}

	public void setThird(C third) {
		this.third = third;
	}

	private  C third;

	public ThreeTuple(A a, B b, C c) {
		super(a, b);
		this.third = c;
	}
	@Override
	public int hashCode() {
		return  HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
}