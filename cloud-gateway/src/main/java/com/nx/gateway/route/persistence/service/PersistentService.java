package com.nx.gateway.route.persistence.service;


import com.nx.gateway.route.persistence.model.GatewayRoute;

import java.util.List;

public interface PersistentService {



    List<GatewayRoute> listRoutes(String configType);

    /**
     * 如果表不存在，则创建表
     */
    public void createTableIfNotExist();


    public Integer insertOrUpdate(List<GatewayRoute> routes);


    public Integer deleteLogic(List<String>  ids);


}
