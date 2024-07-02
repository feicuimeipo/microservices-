package com.nx.amqp.adapter.utils.async;


public interface ICaller<V> {

    V call() throws Exception;
}