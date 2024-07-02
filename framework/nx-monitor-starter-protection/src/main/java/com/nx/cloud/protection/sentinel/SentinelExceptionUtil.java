package com.nx.cloud.protection.sentinel;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * @ClassName ExceptionUtil
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/13 15:10
 * @Version 1.0
 **/
public class SentinelExceptionUtil {
    public static void handleException(BlockException ex) {
        System.out.println("Oops: " + ex.getClass().getCanonicalName());
    }
}
