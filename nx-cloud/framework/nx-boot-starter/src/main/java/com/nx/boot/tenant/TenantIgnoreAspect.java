package com.nx.boot.tenant;


import com.nx.common.banner.BannerUtils;
import com.nx.common.context.spi.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 忽略多租户的 Aspect，基于 {@link TenantIgnore} 注解实现，用于一些全局的逻辑。
 * 例如说，一个定时任务，读取所有数据，进行处理。
 * 又例如说，读取所有数据，进行缓存。
 *
 * @author 芋道源码
 */
@Aspect
@Slf4j
public class TenantIgnoreAspect {

    public TenantIgnoreAspect(){
        if (TenantContextHolder.enabled()){
            BannerUtils.push(this.getClass(),new String[]{"nx-boot-starter：Tenant enabled"});
        }else{
            BannerUtils.push(this.getClass(),new String[]{"nx-boot-starter：Tenant disabled"});
        }
    }

    @Around("@annotation(tenantIgnore)")
    public Object around(ProceedingJoinPoint joinPoint, TenantIgnore tenantIgnore) throws Throwable {
        boolean oldIgnore = TenantIgnoreUtils.getIgnoreTenantId();

        try {
            TenantIgnoreUtils.setIgnoreTenantId(true);
            // 执行逻辑
            return joinPoint.proceed();
        } finally {
            TenantIgnoreUtils.setIgnoreTenantId(oldIgnore);
        }
    }



}
