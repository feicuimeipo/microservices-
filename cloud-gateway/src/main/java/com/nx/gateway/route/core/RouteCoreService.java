package com.nx.gateway.route.core;

import com.nx.boot.launch.StopWatch;
import com.nx.gateway.route.model.GatewayFilterDefinition;
import com.nx.gateway.route.model.GatewayPredicateDefinition;
import com.nx.gateway.route.model.GatewayRouteDefinition;
import com.nx.gateway.route.utils.GatewayFilterUtils;
import com.nx.gateway.route.utils.GatewayPredicateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 实时发布实时刷新
 */
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE+90)
class RouteCoreService implements ApplicationEventPublisherAware {


    final  private RouteDefinitionWriter routeDefinitionWriter;

    private ApplicationEventPublisher publisher;

    public RouteCoreService(RouteDefinitionWriter routeDefinitionWriter) {
        this.routeDefinitionWriter = routeDefinitionWriter;
    }

    /**
     * 写路由
     */
    @Deprecated
    protected void writeRoute(List<String> routeContentList){
        if (routeContentList==null) return;

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        int resultcount=0;
        int total  =routeContentList.size();

        for (Object obj : routeContentList) {
            try {
                LinkedHashMap<String, Object>  map = (LinkedHashMap<String, Object>) obj;
                String routeId = map.get("id").toString();

                RouteDefinition routeDefinition = new RouteDefinition();
                routeDefinition.setId(routeId);

                URI uri = URI.create(map.get("uri").toString());
                routeDefinition.setUri(uri);

                if (map.get("order") == null || map.get("order").toString().equals("")) {
                    routeDefinition.setOrder(0);
                } else {
                    routeDefinition.setOrder(Integer.parseInt(map.get("order").toString()));
                }
                if (map.containsKey("filters") && map.get("filters")!=null) {
                    //String value = map.get("filters");
                    List<FilterDefinition> filterDefinitionList = new ArrayList<>();
                    for (Object text : (ArrayList<Object>) map.get("filters")) {
                        GatewayFilterDefinition gatewayFilterDefinition = GatewayFilterUtils.parseGatewayFilterDefinition(text);
                        if (gatewayFilterDefinition!=null && org.apache.commons.lang3.StringUtils.isNotEmpty(gatewayFilterDefinition.getName())) {
                            FilterDefinition definition = new FilterDefinition();
                            definition.setName(gatewayFilterDefinition.getName());
                            definition.setArgs(gatewayFilterDefinition.getArgs());
                            filterDefinitionList.add(definition);
                        }
                    }
                    routeDefinition.setFilters(filterDefinitionList);
                }
                if (map.containsKey("predicates") && map.get("predicates") != null) {

                    List<PredicateDefinition> predicateDefinitions = new ArrayList<>();
                    int i = 1;
                    for (Object text : (ArrayList<Object>) map.get("predicates")) {
                        GatewayPredicateDefinition gatewayPredicateDefinition = GatewayPredicateUtils.parseGatewayPredicateDefinition(text);
                        if (gatewayPredicateDefinition!=null && org.apache.commons.lang3.StringUtils.isNotEmpty(gatewayPredicateDefinition.getName())) {
                            PredicateDefinition definition = new PredicateDefinition();
                            definition.setName(gatewayPredicateDefinition.getName());
                            definition.setArgs(gatewayPredicateDefinition.getArgs());
                            predicateDefinitions.add(definition);
                        }
                    }
                    routeDefinition.setPredicates(predicateDefinitions);
                }
                if (routeDefinition.getPredicates().size() > 0) {
                    if (DynamicRouteManager.InMemoryRouteIds.contains(routeId)){
                        update(routeDefinition);
                    }else{
                        add(routeDefinition);
                    }
                    resultcount++;
                    log.info("update route : {}", map.toString());
                }else{
                    throw new Exception("Predicates is not allow null! route info:"+map.toString());
                }
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }
        stopWatch.stop();
        log.info("一共有{}条网关信息需要更新，实际更新了{}条记录，共耗时{}",total,resultcount,stopWatch.formatTime());
    }


    private boolean isValid(RouteDefinition definition){
        return definition!=null && StringUtils.hasText(definition.getId()) && definition.getUri()!=null
                && definition.getPredicates()!=null && definition.getPredicates().size()>0;
    }

    public String loadRoutes(GatewayRouteDefinition gatewayRouteDefinition, boolean isUpdate) {
        log.info("gateway add route {}",gatewayRouteDefinition);
        RouteDefinition definition = new RouteDefinition();
        definition.setId(gatewayRouteDefinition.getId());
        definition.setUri(URI.create(gatewayRouteDefinition.getUri()));
        definition.setOrder(gatewayRouteDefinition.getOrder());
        definition.setPredicates(gatewayRouteDefinition.getPredicates());
        definition.setFilters(gatewayRouteDefinition.getFilters());
        definition.setMetadata(gatewayRouteDefinition.getMetadata());

        if (isValid(definition)){
            if (isUpdate){
                update(definition);
            }else{
                add(definition);
            }
        }
        return "success";
    }

    /**
     * 增加路由
     * @param definition
     * @return
     */
    private String add(RouteDefinition definition) {
        log.info("gateway add route {}",definition);
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
        return "success";
    }



    /**
     * 更新路由
     * @param definition
     * @return
     */
    private String update(RouteDefinition definition) {
        try {
            log.info("gateway update route {}",definition);
            this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
        } catch (Exception e) {
            return "update fail,not find route  routeId: "+definition.getId();
        }
        try {
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            return "success";
        } catch (Exception e) {
            return "update route fail";
        }
    }



    /**
     * 删除路由
     * @param id
     * @return
     */
    protected String delete(String id) {
        try {
            this.routeDefinitionWriter.delete(Mono.just(id));
            return "delete success";
        } catch (Exception e) {
            e.printStackTrace();
            return "delete fail";
        }

    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }



}
