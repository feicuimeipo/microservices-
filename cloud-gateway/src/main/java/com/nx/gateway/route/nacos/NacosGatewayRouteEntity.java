package com.nx.gateway.route.nacos;


import com.nx.gateway.route.persistence.model.GatewayRoute;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class NacosGatewayRouteEntity extends GatewayRoute implements java.io.Serializable{

}
