package com.nx.cloud.loadbalancer.nacos;

import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.alibaba.nacos.client.naming.utils.Chooser;
import com.alibaba.nacos.client.naming.utils.Pair;
import com.nx.cloud.loadbalancer.InstancesType;
import com.nx.cloud.loadbalancer.LoadBalancerTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @ClassName NacosWeightRandomLoadBalancer
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/14 15:57
 * @Version 1.0
 **/
public class NacosWeightRandomLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private static final Log log = LogFactory.getLog(NacosWeightRandomLoadBalancer.class);

    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    private final String serviceId;

    /**
     * @param serviceInstanceListSupplierProvider a provider of
     *     {@link ServiceInstanceListSupplier} that will be used to get available
     *     instances
     * @param serviceId id of the service for which to choose an instance
     */
    public NacosWeightRandomLoadBalancer(
            ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
            String serviceId) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get().next().map(this::getInstanceResponse);
    }

    private Response<ServiceInstance> getInstanceResponse(
            List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            log.warn("No servers available for service: " + this.serviceId);
            return new EmptyResponse();
        }

        Map<InstancesType,List<ServiceInstance>> effectServiceInstance =  LoadBalancerTools.getEffectServiceInstance(instances);

        ServiceInstance instance = getHostByRandomWeight(effectServiceInstance.get(InstancesType.SpringCloud));

        return new DefaultResponse(instance);
    }




    protected ServiceInstance getHostByRandomWeight(
            List<ServiceInstance> serviceInstances) {
        log.debug("entry randomWithWeight");
        if (serviceInstances == null || serviceInstances.size() == 0) {
            log.debug("serviceInstances == null || serviceInstances.size() == 0");
            return null;
        }

        Chooser<String, ServiceInstance> instanceChooser = new Chooser<>(
                "com.kapukapu");

        List<Pair<ServiceInstance>> hostsWithWeight = serviceInstances.stream()
                .map(serviceInstance -> new Pair<>(serviceInstance,
                        getWeight(serviceInstance)))
                .collect(Collectors.toList());

        instanceChooser.refresh(hostsWithWeight);
        log.debug("refresh instanceChooser");
        return instanceChooser.randomWithWeight();
    }

    /**
     * Get {@link ServiceInstance} weight metadata.
     *
     * @param serviceInstance instance
     * @return The weight of the instance.
     *
     * @see NacosServiceDiscovery#hostToServiceInstance
     */
    protected double getWeight(ServiceInstance serviceInstance) {
        return Double.parseDouble(serviceInstance.getMetadata().get("nacos.weight"));
    }



}