package com.nx.gateway.route.persistence.service.impl;


import com.nx.gateway.route.persistence.mapper.GatewayRouteDao;
import com.nx.gateway.route.persistence.model.GatewayRoute;
import com.nx.gateway.route.persistence.service.PersistentService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@MapperScan(value = "com.pharmcube.gateway.route.persistence.mapper",sqlSessionFactoryRef = "sqlSessionFactory")
public class PersistentServiceImpl implements PersistentService {
    private final GatewayRouteDao gatewayRouteDao;

    @Autowired
    public PersistentServiceImpl(GatewayRouteDao gatewayRouteDao) {
        this.gatewayRouteDao = gatewayRouteDao;
    }

    @Override
    public List<GatewayRoute> listRoutes(String configType){
       return gatewayRouteDao.listRoutes(configType);
    }


    /**
     * 如果表不存在，则创建表
     */
    @Override
    @Transactional
    public void createTableIfNotExist(){

        try {
            gatewayRouteDao.count();
        } catch (BadSqlGrammarException e) {
            gatewayRouteDao.createTable();
            gatewayRouteDao.createIndexCreateDate();
            gatewayRouteDao.createIndexOrderby();
            gatewayRouteDao.createIndexConfigType();
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    @Override
    public Integer insertOrUpdate(List<GatewayRoute> routes) {
        if (routes==null || routes.size()<=0) return 0;

        int re= gatewayRouteDao.insertOrUpdate(routes);

        return re;
    }


    @Override
    public Integer deleteLogic(List<String> ids) {
        if (ids==null || ids.size()==0) return 0;
        gatewayRouteDao.deleteLogic(ids);
         return 1;
    }


}
