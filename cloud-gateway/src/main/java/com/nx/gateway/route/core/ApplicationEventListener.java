package com.nx.gateway.route.core;

import com.google.auto.service.AutoService;
import com.nx.boot.launch.spi.NxApplicationListener;
import com.nx.gateway.conf.context.ReactiveAppUtils;
import com.nx.gateway.route.nacos.NacosRouteService;
import com.nx.gateway.route.persistence.service.PersistentService;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ApplicationEvent
 * @Description TODO
 * @Author NianXiaoLing
 * @Date 2022/6/14 23:21
 * @Version 1.0
 **/
@AutoService(NxApplicationListener.class)
public class ApplicationEventListener implements NxApplicationListener {
    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {

    }

    Map<String, Object> serialize(Route route) {
        HashMap<String, Object> r = new HashMap<>();
        r.put("route_id", route.getId());
        r.put("uri", route.getUri().toString());
        r.put("order", route.getOrder());
        r.put("predicate", route.getPredicate().toString());
        if (!CollectionUtils.isEmpty(route.getMetadata())) {
            r.put("metadata", route.getMetadata());
        }

        ArrayList<String> filters = new ArrayList<>();

        for (int i = 0; i < route.getFilters().size(); i++) {
            GatewayFilter gatewayFilter = route.getFilters().get(i);
            filters.add(gatewayFilter.toString());
        }

        r.put("filters", filters);
        return r;
    }
    /**
     * 事件启动之后
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //RouteLocator routeLocator = ReactiveAppUtils.getBean(RouteLocator.class);
        //routeLocator.getRoutes();

        DynamicRouteManager dynamicRouteManager = ReactiveAppUtils.getBean(DynamicRouteManager.class);

        dynamicRouteManager.loadRoutesIntoMemoryAndGateway();

        DynamicRouteConfig dynamicRouteConfig = ReactiveAppUtils.getBean(DynamicRouteConfig.class);

        PersistentService persistentService  = ReactiveAppUtils.getBean(PersistentService.class);

        NacosRouteService nacosRouteService = new NacosRouteService(dynamicRouteConfig,dynamicRouteManager, persistentService);

        nacosRouteService.start();
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event){
        H2DBServerListener.stop();
    }

}
