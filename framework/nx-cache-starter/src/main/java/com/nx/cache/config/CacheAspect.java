/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.cache.config;

import com.nx.cache.CacheProviderFactory;
import com.nx.cache.CacheProvider;
import com.nx.cache.annotation.*;
import com.nx.cache.expression.CacheOperationExpressionEvaluator;
import com.nx.cache.expression.CacheOperationInvoker;
import com.nx.redis.RedisConfigFactory;
import lombok.Getter;
import org.apache.commons.lang3.SerializationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.nx.cache.CacheProviderFactory.DEFAULT_CACHE_GROUP_NAME;
import static com.nx.cache.config.NxCacheConfig.getCacheName;

/**
 *
 * 缓存拦截，用于注册方法信息
 *
 * @author 佚名
 * @email xlnian@163.com
 * @date 2020年6月16日
 */
@Aspect
public class CacheAspect {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String CACHE_KEY_ERROR_MESSAGE = "缓存Key %s 不能为NULL";
    private static final String CACHE_NAME_ERROR_MESSAGE = "缓存名称不能为NULL";

    /**
     * SpEL表达式计算器
     */
    private final CacheOperationExpressionEvaluator evaluator = new CacheOperationExpressionEvaluator();

    @Autowired
    private CacheProviderFactory cacheProviderFactory;


    @Pointcut("@annotation(com.nx.cache.annotation.Cacheable)")
    public void cacheablePointcut() {
    }

    @Pointcut("@annotation(com.nx.cache.annotation.CachePut)")
    public void cachePutPointcut() {
    }

    @Pointcut("@annotation(com.nx.cache.annotation.CacheField)")
    public void cacheGetSetPointcut() {
    }

    @Pointcut("@annotation(com.nx.cache.annotation.CacheFields)")
    public void cacheGetSetPointcuts() {
    }

    @Pointcut("@annotation(com.nx.cache.annotation.CacheEvict)")
    public void cacheEvictPointcut() {
    }

    //@Around("cacheablePointcut()")
    @Around("cacheablePointcut()")
    public Object cacheablePointcut(ProceedingJoinPoint joinPoint)  {
        //定义一个函数
        CacheOperationInvoker invoker = getCacheOperationInvoker(joinPoint);

        // 获取method
        Method method = this.getSpecificmethod(joinPoint);

        // 获取注解
        Cacheable cacheable = AnnotationUtils.findAnnotation(method, Cacheable.class);
        // 执行查询缓存方法
        // 解析SpEL表达式获取cacheName和key
        String[] cacheNames = getCacheName(cacheable.cacheName());

        Assert.notEmpty(cacheNames, CACHE_NAME_ERROR_MESSAGE);
        String key =  getCacheKey(new CacheKey(cacheable.prefix(),cacheable.key(),cacheable.pureKey()), method, joinPoint.getArgs(), joinPoint.getTarget());

        Assert.notNull(key, String.format(CACHE_KEY_ERROR_MESSAGE,key));
        try {
            // 从注解中获取缓存配置
            CacheProvider cache = cacheProviderFactory.getCache(cacheNames[0]);

            // 通Cache获取值
            Object result = cache.get(key, () -> invoker.invoke());
            if (cacheable.getAndSet() && result!=null){
                // 从解决中获取缓存配置
                // 处理过期时间
                long firstCacheExpireTime = getFirstCacheExpireTime(cacheable.firstCache(), method, joinPoint.getArgs(), joinPoint.getTarget());
                // 处理过期时间
                long secondaryCacheExpireTime = getSecondaryCacheExpireTime(cacheable.secondaryCache(), method, joinPoint.getArgs(), joinPoint.getTarget());
                cache.set(key,result,firstCacheExpireTime,secondaryCacheExpireTime,cacheable.type(),cacheable.firstCache().allowNull(),cacheable.secondaryCache().allowNull());
            }
            return result;
        } catch (Exception e) {
            if (e instanceof SerializationException){
                try{
                    // 删除缓存
                    delete(cacheNames, new CacheKey(cacheable.prefix(), cacheable.key(), cacheable.pureKey()), method, joinPoint.getArgs(), joinPoint.getTarget());
                }catch (Exception  e1) {
                    logger.error(e.getMessage(), e1);
                }
            }
            if (cacheable.ignoreException()) {
                logger.warn(e.getMessage(), e);
                return invoker.invoke();
            }
            throw e;
        }
    }



    @Around("cacheGetSetPointcuts()")
    public Object cacheGetSetPointcuts(ProceedingJoinPoint joinPoint) throws Throwable {
        CacheOperationInvoker invoker = getCacheOperationInvoker(joinPoint);
        // 获取method
        Method method = this.getSpecificmethod(joinPoint);

        CacheFields cacheFields = AnnotationUtils.findAnnotation(method, CacheFields.class);
        CacheField[] cacheFieldList = cacheFields.value();

        if (cacheFields.value().length==0){
            return invoker.invoke();
        }

        try {
            return cacheField(invoker, method, joinPoint.getArgs(),joinPoint.getTarget(),cacheFieldList);
        }catch (NoSuchFieldException | IllegalAccessException e){
            throw  e;
        }catch (Exception e){
            throw e;
        }

    }

    @Around("cacheGetSetPointcut()")
    public Object cacheParamPointcut(ProceedingJoinPoint joinPoint) throws Throwable {
        CacheOperationInvoker invoker = getCacheOperationInvoker(joinPoint);

        // 获取method
        Method method = this.getSpecificmethod(joinPoint);
        // 获取注解
        CacheField cacheParam = AnnotationUtils.findAnnotation(method, CacheField.class);


        try {
            return cacheField(invoker, method, joinPoint.getArgs(),joinPoint.getTarget(),cacheParam);
        }catch (NoSuchFieldException | IllegalAccessException e){
            throw  e;
        }catch (Exception e){
            // 忽略操作缓存过程中遇到的异常
            if (cacheParam.ignoreException()) {
                logger.warn(e.getMessage(), e);
                return invoker.invoke();
            }
            throw e;
        }

    }

    private Object cacheField(CacheOperationInvoker invoker,Method method,Object[] args,Object target, CacheField... cacheFields) throws NoSuchFieldException, IllegalAccessException {
        // 执行查询缓存方法
        // 解析SpEL表达式获取cacheName和key
        Field[] fields = new Field[cacheFields.length];
        Object[] fieldBeforeValues = new Object[cacheFields.length];
        int i=0;
        for (CacheField cacheParam : cacheFields) {
            //String[] cacheNames = cacheParam.cacheName();
            String[] cacheNames = getCacheName(cacheParam.cacheName());// new String[]{cacheParam.cacheName()};
            Assert.notEmpty(cacheNames, CACHE_NAME_ERROR_MESSAGE);

            String fieldName = cacheParam.fieldName();
            Assert.notNull(fieldName, "字段名不能为空！");
            Field declaredField = target.getClass().getDeclaredField(fieldName);
            Object fieldBeforeValue = declaredField.get(target);
            declaredField.setAccessible(true);

            if (fieldBeforeValue == null) {
                String key = getCacheKey(new CacheKey(cacheParam.prefix(), cacheParam.key(),cacheParam.pureKey()), method, args, target);
                CacheProvider cache = cacheProviderFactory.getCache(cacheNames[0]);
                // 从注解中获取缓存配置
                // 通过cacheName和缓存配置获取Cache
                Object value = cache.get(key, declaredField.getGenericType().getClass());
                if (value != null) {
                    declaredField.set(target, value);
                }
            }
            fieldBeforeValues[i] = fieldBeforeValue;
            fields[i] =  declaredField;
            i++;
        }


        Object result = invoker.invoke();

        i=0;
        for (CacheField cacheParam : cacheFields) {
            Field field  = fields[i];
            Object fieldBeforeValue = fieldBeforeValues[i];

            //String[] cacheNames = cacheParam.cacheName();
            //String[] cacheNames = new String[]{cacheParam.cacheName()};
            String[] cacheNames = getCacheName(cacheParam.cacheName());// new String[]{cacheParam.cacheName()};

            Object fieldAfterValue = field.get(target);
            if (fieldAfterValue != null && !fieldBeforeValue.equals(fieldAfterValue)) {
                // 处理过期时间
                long firstCacheExpireTime = getFirstCacheExpireTime(cacheParam.firstCache(), method, args, target);

                // 处理过期时间
                long secondaryCacheExpireTime = getSecondaryCacheExpireTime(cacheParam.secondaryCache(), method, args, target);

                String key = getCacheKey(new CacheKey(cacheParam.prefix(), cacheParam.key(),cacheParam.pureKey()), method, args, target);

                for (String cacheName : cacheNames) {
                    CacheProvider cacheSave = cacheProviderFactory.getCache(cacheName);
                    cacheSave.set(key, fieldAfterValue, firstCacheExpireTime, secondaryCacheExpireTime);
                }
            }
            i++;
        }

        return result;
    }

    @AfterReturning(pointcut = "cachePutPointcut()", returning = "result")
    public void cachePutPointcut(ProceedingJoinPoint joinPoint,Object result)  {

        // 获取method
        Method method = this.getSpecificmethod(joinPoint);
        // 获取注解
        CachePut cachePut = AnnotationUtils.findAnnotation(method, CachePut.class);

        // 执行查询缓存方法
        //String[] cacheNames = new String[]{cachePut.cacheName()};
        String[] cacheNames = getCacheName(cachePut.cacheName());// new String[]{cacheParam.cacheName()};
        //String[] cacheNames = cachePut.cacheName();
        Assert.notEmpty(cacheNames, CACHE_NAME_ERROR_MESSAGE);

        // 解析SpEL表达式获取 key
        String  key = getCacheKey(new CacheKey(cachePut.prefix(),cachePut.key(),cachePut.pureKey()), method, joinPoint.getArgs(), joinPoint.getTarget());

        try {

            // 从解决中获取缓存配置
            // 处理过期时间
            long firstCacheExpireTime = getFirstCacheExpireTime(cachePut.firstCache(), method, joinPoint.getArgs(), joinPoint.getTarget());

            // 处理过期时间
            long secondaryCacheExpireTime = getSecondaryCacheExpireTime(cachePut.secondaryCache(), method, joinPoint.getArgs(), joinPoint.getTarget());

            // 指定调用方法获取缓存值
            //Object result = invoker.invoke();

            // 通过cacheName和缓存配置获取Cache
            for (String cacheName : cacheNames) {
                CacheProvider cache = cacheProviderFactory.getCache(cacheName);
                cache.set(key,result,firstCacheExpireTime,secondaryCacheExpireTime,cachePut.type(),cachePut.firstCache().allowNull(),cachePut.secondaryCache().allowNull());
            }

        } catch (Exception e) {
            // 忽略操作缓存过程中遇到的异常
            if (cachePut.ignoreException()) {
                logger.warn(e.getMessage(), e);
            }
            throw e;
        }
    }


    @Around("cacheEvictPointcut()")
    public Object cacheEvictPointcut(ProceedingJoinPoint joinPoint) throws Throwable {
        CacheOperationInvoker invoker = getCacheOperationInvoker(joinPoint);

        // 获取method
        Method method = this.getSpecificmethod(joinPoint);

        // 获取注解
        CacheEvict cacheEvict = AnnotationUtils.findAnnotation(method, CacheEvict.class);

        try {
            // 执行查询缓存方法
            // 解析SpEL表达式获取cacheName和key
            //String[] cacheNames = cacheEvict.cacheName();
            //String[] cacheNames = new String[]{cacheEvict.cacheName()};
            String[] cacheNames = getCacheName(cacheEvict.cacheName());// new String[]{cacheParam.cacheName()};
            Assert.notEmpty(cacheNames, CACHE_NAME_ERROR_MESSAGE);


            // 删除指定key
            delete(cacheNames, new CacheKey(cacheEvict.prefix(), cacheEvict.key(), cacheEvict.pureKey()), method, joinPoint.getArgs(), joinPoint.getTarget());


            // 执行方法
            return invoker.invoke();

        } catch (Exception e) {
            // 忽略操作缓存过程中遇到的异常
            if (cacheEvict.ignoreException()) {
                logger.warn(e.getMessage(), e);
                return invoker.invoke();
            }
            throw e;
        }
    }





    /**
     * 删除执行缓存名称上的指定key
     *
     * @param cacheNames 缓存名称
     * @param method     {@link Method}
     * @param args       参数列表
     * @param target     目标类
     */
    private void delete(String[] cacheNames,CacheKey cacheKey, Method method, Object[] args, Object target) {

        String key = getCacheKey(cacheKey, method, args, target);


        for (String cacheName : cacheNames) {
            CacheProvider caches = cacheProviderFactory.getCache(cacheName);
            caches.evict(key);
        }

    }


    private CacheOperationInvoker getCacheOperationInvoker(ProceedingJoinPoint joinPoint) {
        return () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable ex) {
                throw new CacheOperationInvoker.ThrowableWrapperException(ex);
            }
        };
    }

    /**
     * 解析SpEL表达式，获取注解上的key属性值
     *
     * @return Object
     */
    private String getCacheKey(CacheKey key,Method method, Object[] args, Object target) {

        String prefix = key.getPrefix();
        if (StringUtils.hasText(prefix)){
            prefix = prefix + ":";
        }

        if (!StringUtils.hasText(key.getKey())){
            StringBuffer keyBuffer =new StringBuffer(target.getClass().getName() + "." + method.getName());

            if (args!=null && args.length>0){
                keyBuffer.append(Arrays.deepHashCode(args));
            }else{
                keyBuffer.append(Arrays.deepHashCode(method.getParameters()));
            }
            return prefix + keyBuffer.toString();
        }else if(key.pureKey){
            return prefix + key.getKey();
        }

        //不会存在空值
        String keySpEl = key.getKey();
        // 获取注解上的key属性值
        Class<?> targetClass = getTargetClass(target);

        EvaluationContext evaluationContext =   evaluator.createEvaluationContext(method, args, target, targetClass, CacheOperationExpressionEvaluator.NO_RESULT);

        AnnotatedElementKey methodCacheKey = new AnnotatedElementKey(method, targetClass);

        // 兼容传null值得情况
        Object keyValue = evaluator.key(keySpEl, methodCacheKey, evaluationContext);

        Assert.notNull(keyValue, String.format(CACHE_KEY_ERROR_MESSAGE, keySpEl));

        return prefix + keyValue;
    }
    
    /**
     * 获取firstCache的过期时间
     * 返回毫秒的单位
     * <p>优先获取expireTimeExp所计算出来的过期时间，没有或者计算出错时获取expireTime的值。</p>
     * @param firstCache
     * @param method
     * @param args
     * @param target
     * @return
     */
    private long getFirstCacheExpireTime(FirstCache firstCache, Method method, Object[] args, Object target) {
    	if(firstCache==null) {
    		return 0;
    	}
    	long expireTime = firstCache.expireTime();
    	Object expireTimeByExp = generateExpireTime(firstCache.expireTimeExp(), method, args, target);
    	if(expireTimeByExp!=null) {
    		if(expireTimeByExp instanceof String) {
    			try{
    				int parseInt = Integer.parseInt(((String)expireTimeByExp));
    				if(parseInt > -1) {
    					expireTime = parseInt;
    				}
    			}
    			catch(Exception ex) {
    				logger.warn("解析方法：{}上的expireTimeExp：{}时出错了", method.getName(), firstCache.expireTimeExp());
    			}
    		}
    		else if(expireTimeByExp instanceof Integer) {
    			expireTime = (Integer)expireTimeByExp;
    		}
    	}
        expireTime = firstCache.timeUnit().toMillis(expireTime);
        //TimeUnit.SECONDS.convert(expireTime,firstCache.timeUnit());
    	return expireTime;
    }
    
    /**
     * 获取secondaryCache的过期时间
     * <p>优先获取expireTimeExp所计算出来的过期时间，没有或者计算出错时获取expireTime的值。</p>
     * @param secondaryCache
     * @param method
     * @param args
     * @param target
     * @return
     */
    private long getSecondaryCacheExpireTime(SecondaryCache secondaryCache, Method method, Object[] args, Object target) {
    	if(secondaryCache==null) {
    		return 0;
    	}
        long expireTime = secondaryCache.expireTime();
    	Object expireTimeByExp = generateExpireTime(secondaryCache.expireTimeExp(), method, args, target);
    	if(expireTimeByExp!=null) {
    		if(expireTimeByExp instanceof String) {
    			try{
    				int parseInt = Integer.parseInt(((String)expireTimeByExp));
    				if(parseInt > -1) {
    					expireTime = parseInt;
    				}
    			}
    			catch(Exception ex) {
    				logger.warn("解析方法：{}上的expireTimeExp：{}时出错了", method.getName(), secondaryCache.expireTimeExp());
    			}
    		}
    		else if(expireTimeByExp instanceof Integer) {
    			expireTime = (Integer)expireTimeByExp;
    		}
    	}
        expireTime = secondaryCache.timeUnit().toMillis(expireTime);
    	return expireTime;
    }
    
    /**
     * 解析SpEL表达式，获取注解上的expireTimeExp属性值
     * @param expireTimeExp
     * @param method
     * @param args
     * @param target
     * @return
     */
    private Object generateExpireTime(String expireTimeExp, Method method, Object[] args, Object target) {
    	// 获取注解上的key属性值
        Class<?> targetClass = getTargetClass(target);
        if (StringUtils.hasText(expireTimeExp)) {
            EvaluationContext evaluationContext = evaluator.createEvaluationContext(method, args, target, targetClass, CacheOperationExpressionEvaluator.NO_RESULT);

            AnnotatedElementKey methodCacheKey = new AnnotatedElementKey(method, targetClass);
            // 兼容传null值得情况
            Object keyValue = evaluator.key(expireTimeExp, methodCacheKey, evaluationContext);
            return Objects.isNull(keyValue) ? "null" : keyValue;
        }
        return null;
    }

    /**
     * 获取类信息
     *
     * @param target Object
     * @return targetClass
     */
    private Class<?> getTargetClass(Object target) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        if (targetClass == null) {
            targetClass = target.getClass();
        }
        return targetClass;
    }


    /**
     * 获取Method
     *
     * @param pjp ProceedingJoinPoint
     * @return {@link Method}
     */
    private Method getSpecificmethod(ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        // The method may be on an interface, but we need attributes from the
        // target class. If the target class is null, the method will be
        // unchanged.
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(pjp.getTarget());
        if (targetClass == null && pjp.getTarget() != null) {
            targetClass = pjp.getTarget().getClass();
        }
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        // If we are dealing with method with generic parameters, find the
        // original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        return specificMethod;
    }



    @Getter
    public  static class CacheKey {
        private String prefix= "";
        private String key = "";
        private boolean pureKey=false;

        public CacheKey(String prefix, String key, boolean pureKey){
            this.prefix = prefix;
            this.key = key;
            this.pureKey = pureKey;
        }

    }

}