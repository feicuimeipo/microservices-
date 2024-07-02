package com.nx.gateway.route.model;

import lombok.Data;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;


/**
 * RouteDefinition
 * Gateway的路由定义模型
 * @Author nxl
 */
@Data
@Validated
public class GatewayRouteDefinition  implements java.io.Serializable{
    /**
     * 路由的Id
     */
    @Valid
    @NotEmpty
    protected String id= UUID.randomUUID().toString();

    /**
     * 路由断言集合配置
     */
    @Valid
    @NotEmpty
    protected List<PredicateDefinition> predicates = new ArrayList<>();

    /**
     * 路由过滤器集合配置
     */
    @Valid
    protected List<FilterDefinition> filters = new ArrayList<>();



    /**
     * 路由规则转发的目标uri
     */
    @NotNull
    protected String uri;

    /**
     * 路由执行的顺序
     */
    protected int order = 1;


    protected Map<String, Object> metadata = new LinkedHashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GatewayRouteDefinition routeDefinition = (GatewayRouteDefinition) o;
        return  Objects.equals(id, routeDefinition.getId()) &&
                Objects.equals(predicates, routeDefinition.getPredicates()) &&
                Objects.equals(filters, routeDefinition.getFilters()) &&
                Objects.equals(order, routeDefinition.getOrder()) &&
                Objects.equals(uri, routeDefinition.getUri()) &&
                Objects.equals(metadata, routeDefinition.getMetadata())
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, predicates,filters, uri);
    }

    @Override
    public String toString() {
        return "GatewayRouteDefinition{" +
                "id='" + id + '\'' +
                ", predicates=" + predicates +
                ", filters=" + filters +
                ", uri=" + uri +
                ", order=" + order +
                ", metadata=" + metadata +
                '}';
    }

}
