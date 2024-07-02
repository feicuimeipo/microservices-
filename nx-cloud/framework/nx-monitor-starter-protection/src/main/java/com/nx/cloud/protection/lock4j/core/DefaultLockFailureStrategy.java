package com.nx.cloud.protection.lock4j.core;

import com.baomidou.lock.LockFailureStrategy;
import com.nx.common.exception.ServiceException;
import com.nx.common.model.constant.ResultCode;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 自定义获取锁失败策略，抛出 {@link ServiceException} 异常
 */
@Slf4j
public class DefaultLockFailureStrategy implements LockFailureStrategy {

    @Override
    public void onLockFailure(String key, Method method, Object[] arguments) {
        log.debug("[onLockFailure][线程:{} 获取锁失败，key:{}, method:{}, 获取超时时长:{} ms]", Thread.currentThread().getName(), key, method, (arguments==null?"arguments is null":arguments));
        throw new ServiceException(ResultCode.LOCKED);
    }
}
