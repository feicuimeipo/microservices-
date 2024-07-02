package com.nx.common.context.spi;


import com.nx.common.context.CurrentRuntimeContext;
import com.nx.common.exception.ServerException;
import org.springframework.core.Ordered;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public interface TenantContextHolder extends Comparable<TenantContextHolder>{
    List<TenantContextHolder> instances=new CopyOnWriteArrayList<>();
    Boolean ENABLE_DEFAULT = true;

    default public boolean tenantEnabled(){
        return ENABLE_DEFAULT;
    }

    /**
     * 需要忽略多租户的请求
     *
     * 默认情况下，每个请求需要带上 tenant-id 的请求头。但是，部分请求是无需带上的，例如说短信回调、支付回调等 Open API！
     */
    default Set<String> ignoreUrls(){
        return Collections.emptySet();
    }

    default List<String> getIgnoreTables(Long tenantId){
        return Collections.emptyList();
    }

    static List<String> ignoreTables(){ return TenantContextHolder.getInstance().getIgnoreTables(CurrentRuntimeContext.getTenantId());}

    static boolean enabled(){
        if (TenantContextHolder.getInstance()==null){
            return false;
        }
        return TenantContextHolder.getInstance().tenantEnabled();
    }

    /**
     * 租户校验
     * @param tenantId
     * @return
     */
    default void validTenant(Long tenantId){
        //todo something
    }


    static TenantContextHolder getInstance(){
        if (instances.size()==0) {
            synchronized (TenantContextHolder.class) {
                if (instances.size() == 0) {
                    List<TenantContextHolder> list = new ArrayList<>();
                    ServiceLoader.load(TenantContextHolder.class).forEach(list::add);
                    list.stream().sorted(Comparator.comparing(TenantContextHolder::getOrder)).collect(Collectors.toList());
                    instances.addAll(list);
                }
            }
        }

        if (instances!=null && instances.size()>0){
            throw new ServerException(500,"存在多个TenantContextHolder");
        }

        return instances==null || instances.size()==0? null:instances.get(0);
    }

    default int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    default int compareTo(TenantContextHolder o) {
        return Integer.compare(this.getOrder(), o.getOrder());
    }


}
