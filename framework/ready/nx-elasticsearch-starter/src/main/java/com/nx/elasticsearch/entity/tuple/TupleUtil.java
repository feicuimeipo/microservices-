package com.nx.elasticsearch.entity.tuple;


/**
 * 作者：王坤造
 * 时间：2017/10/25
 * 名称：
 * 备注：
 */
public class TupleUtil {

    public static <A, B> TwoTuple<A, B> tuple(A a, B b) {
        return new TwoTuple<>(a, b);
    }

    public static <A, B, C> ThreeTuple<A, B, C> tuple(A a, B b, C c) {
        return new ThreeTuple<>(a, b, c);
    }

    public static <A, B, C, D> FourTuple<A, B, C, D> tuple(A a, B b, C c, D d) {
        return new FourTuple<>(a, b, c, d);
    }
}
