package com.nx.common.context.spi;

import com.nx.common.context.constant.ServiceProtocol;
import com.nx.common.exception.ServerException;
import org.springframework.core.Ordered;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public interface ServiceVerification extends Comparable<ServiceVerification>{


    List<ServiceVerification> instances=new CopyOnWriteArrayList<>();

    /**
     * api接口验证
     * @param appId
     * @param accessToken
     * @param protocol
     * @return
     */
    boolean serviceApiAccessAuthCheck(String appId, String accessToken, ServiceProtocol protocol);


    /**
     * 服务接口
     * @param appId
     * @param accessToken
     * @param protocol
     * @return
     */
    static boolean apiAccessCheck(String appId, String accessToken, ServiceProtocol protocol){
        if (getInstance()==null){
            return true;
        }
        return getInstance().serviceApiAccessAuthCheck(appId,accessToken,protocol);
    }


    static ServiceVerification getInstance(){
        if (instances.size()==0) {
            synchronized (ServiceVerification.class) {
                if (instances.size() == 0) {
                    List<ServiceVerification> list = new ArrayList<>();
                    ServiceLoader.load(ServiceVerification.class).forEach(list::add);
                    list.stream().sorted(Comparator.comparing(ServiceVerification::getOrder)).collect(Collectors.toList());
                    instances.addAll(list);
                }
            }
        }
        if (instances!=null && instances.size()>0){
            throw new ServerException(500,"存在多个ServiceVerification");
        }
        return instances==null || instances.size()==0? null:instances.get(0);
    }


    default int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    default int compareTo(ServiceVerification o) {
        return Integer.compare(this.getOrder(), o.getOrder());
    }
}
