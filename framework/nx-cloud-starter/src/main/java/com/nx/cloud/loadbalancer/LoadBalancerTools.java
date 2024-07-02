package com.nx.cloud.loadbalancer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ClassName RandomSample
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/14 16:13
 * @Version 1.0
 **/
@Slf4j
public class LoadBalancerTools {

    public static Response<ServiceInstance> getRandom(List<ServiceInstance> instances){
        // 随机算法
        Random random = new Random();
        int size = instances.size();
        ServiceInstance instance = instances.get(random.nextInt(size));
        return new DefaultResponse(instance);
    }

    public static Map<InstancesType,List<ServiceInstance>> getEffectServiceInstance(List<ServiceInstance> instances){
        List<ServiceInstance> springCLoudInstances = new CopyOnWriteArrayList<>();
        List<ServiceInstance> dubboInstances = new CopyOnWriteArrayList<>();

        // 遍历可以实例元数据，若匹配则返回此实例
        for (ServiceInstance instance : instances) {
             springCLoudInstances.add(instance);
        }


        Map<InstancesType,List<ServiceInstance>> map = new HashMap<>();
        map.put(InstancesType.SpringCloud,springCLoudInstances);
        return map;
    }
}
