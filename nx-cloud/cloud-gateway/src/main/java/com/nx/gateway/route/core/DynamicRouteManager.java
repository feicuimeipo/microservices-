package com.nx.gateway.route.core;


import com.nx.gateway.route.model.GatewayRouteDefinition;
import com.nx.gateway.route.persistence.model.GatewayRoute;
import com.nx.gateway.route.persistence.service.PersistentService;
import com.nx.gateway.route.utils.GatewayFilterUtils;
import com.nx.gateway.route.utils.GatewayPredicateUtils;
import com.nx.gateway.route.nacos.NacosGatewayRouteEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * @Author NianXiaoLing
 * @Description TODO
 * @Date 2020/10/17 17:46
 * @Version 1.0
 */
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE+100)
public class DynamicRouteManager {
    protected volatile static  Set<String> InMemoryRouteIds = new CopyOnWriteArraySet<>();

    @Autowired
    private RouteCoreService routeCoreService;

    @Autowired
    private PersistentService persistentService;

    @Autowired
    private DynamicRouteConfig dynamicRouteConfig;

    /**
     * init
     * 把路由加载到内存
     */
    public synchronized void loadRoutesIntoMemoryAndGateway(){
        persistentService.createTableIfNotExist();

        List<GatewayRoute> list =  persistentService.listRoutes(dynamicRouteConfig.getConfigType());
        list.stream().forEach(item->{
            try {
                GatewayRouteDefinition gatewayRouteDefinition = getGatewayRouteDefinition(item);
                routeCoreService.loadRoutes(gatewayRouteDefinition, false);
                InMemoryRouteIds.add(item.getId());
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
        });
    }



    private static GatewayRouteDefinition getGatewayRouteDefinition(GatewayRoute route){
        GatewayRouteDefinition gatewayRouteDefinition = new GatewayRouteDefinition();
        gatewayRouteDefinition.setId(route.getId());
        gatewayRouteDefinition.setUri(route.getUri());
        gatewayRouteDefinition.setOrder(route.getOrder());

        Yaml yaml = new Yaml();

        List<PredicateDefinition> predicateDefinitions = new ArrayList<>();
        if (StringUtils.hasText(route.getPredicates())){
            List<Object> originPredicts = yaml.loadAs(route.getPredicates(),List.class);
            for (Object text : originPredicts) {
                PredicateDefinition predicateDefinition = GatewayPredicateUtils.parsePredicateDefinition(text);
                if (predicateDefinition!=null && org.apache.commons.lang3.StringUtils.isNotEmpty(predicateDefinition.getName())) {
                    PredicateDefinition definition = new PredicateDefinition();
                    definition.setName(predicateDefinition.getName());
                    definition.setArgs(predicateDefinition.getArgs());
                    predicateDefinitions.add(predicateDefinition);
                }
            }
            if (predicateDefinitions.size()>0) {
                gatewayRouteDefinition.setPredicates(predicateDefinitions);
            }
        }

        if (StringUtils.hasText(route.getFilters())){
            List<Object> originFilters = (List<Object>) yaml.loadAs(route.getFilters(),List.class);
            List<FilterDefinition> filterDefinitions = new ArrayList<>();
            for (Object text : originFilters) {
                FilterDefinition filterDefinition = GatewayFilterUtils.parseFilterDefinition(text);
                if (filterDefinition != null && org.apache.commons.lang3.StringUtils.isNotEmpty(filterDefinition.getName())) {
                    FilterDefinition definition = new FilterDefinition();
                    definition.setName(filterDefinition.getName());
                    definition.setArgs(filterDefinition.getArgs());
                    filterDefinitions.add(filterDefinition);
                }
            }
            if (filterDefinitions.size() > 0) {
                gatewayRouteDefinition.setFilters(filterDefinitions);
            }
        }
        if (StringUtils.hasText(route.getMetadata())){
            LinkedHashMap<String,Object> metadata =  yaml.loadAs(route.getFilters(),LinkedHashMap.class);
            gatewayRouteDefinition.setMetadata(metadata);
        }

        return gatewayRouteDefinition;
    }


    private boolean isValid(GatewayRoute route){
        return route!=null
                && StringUtils.hasText(route.getId())
                && StringUtils.hasText(route.getUri())
                && StringUtils.hasText(route.getPredicates());
    }

    /**
     * //TODO: 可以做一个二阶段提交的事务
     * 发布一个内嵌数据
     * @param gatewayRoutes
     */
    public void push(NacosGatewayRouteEntity... gatewayRoutes){
        if (gatewayRoutes==null) return;

        List<String> allIds = new ArrayList<>();

        List<GatewayRoute> persistentGatewayRoutes = new ArrayList<>();
        for (NacosGatewayRouteEntity dto : gatewayRoutes) {
            if (dto==null || dto.getId()==null) {
                log.error("dto==null or dto.getRouteDefinition()==null" + dto.toString());
                continue;
            }

            GatewayRoute gatewayRoute = new GatewayRoute();
            BeanUtils.copyProperties(dto,gatewayRoute);
            if (!isValid(gatewayRoute)) continue;

            persistentGatewayRoutes.add(gatewayRoute);
            GatewayRouteDefinition gatewayRouteDefinition = getGatewayRouteDefinition(gatewayRoute);
            routeCoreService.loadRoutes(gatewayRouteDefinition,InMemoryRouteIds.contains(gatewayRouteDefinition.getId()));
            InMemoryRouteIds.add(gatewayRouteDefinition.getId());
            allIds.add(gatewayRouteDefinition.getId());
        }

        //TODO: 1.从逻辑删中恢复数据，
        persistentService.insertOrUpdate(persistentGatewayRoutes);


       List<String> removeIds = InMemoryRouteIds.stream().filter(item->!allIds.contains(item)).collect(Collectors.toList());
       persistentService.deleteLogic(removeIds);
       InMemoryRouteIds.removeAll(removeIds);
    }


    @Deprecated
    public void push(List<GatewayRoute> gatewayRoutes){
        if (gatewayRoutes==null) return;

        Yaml yaml = new Yaml();
        List<String> routes = new ArrayList<>();
        Set<String> ids = Collections.emptySet();
        for (GatewayRoute route : gatewayRoutes) {
            if (route == null) {
                log.error("dto==null or dto.getRouteDefinition()==null" + route.toString());
                continue;
            }
            String dumpAsMaproute = yaml.dumpAsMap(route);
            routes.add(dumpAsMaproute);
            ids.add(route.getId());
        }

        //TODO: 可
        //以做一个事务
        routeCoreService.writeRoute(routes);
    }


}
