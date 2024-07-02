package com.nx.cloud.protection.resilience4j.exception;


import com.nx.common.exception.BaseException;

import java.util.function.Predicate;


public class RecordFailurePredicate implements Predicate<Throwable> {

    @Override
    public boolean test(Throwable throwable) {
        if (throwable.getCause() instanceof BaseException) return false;
        else return true;
    }

}
