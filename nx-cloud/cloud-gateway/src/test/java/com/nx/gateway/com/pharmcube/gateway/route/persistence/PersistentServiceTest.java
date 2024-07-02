package com.nx.gateway.com.pharmcube.gateway.route.persistence;

import com.nx.gateway.MyGatewayApplication;
import com.nx.gateway.route.core.DynamicRouteConfigType;
import com.nx.gateway.route.persistence.mapper.GatewayRouteDao;
import com.nx.gateway.route.persistence.model.GatewayRoute;
import com.nx.gateway.route.persistence.service.PersistentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName PersistentServiceTest
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/20 1:11
 * @Version 1.0
 **/
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = MyGatewayApplication.class,
        properties = "spring.main.web-application-type=reactive")
public class PersistentServiceTest {

    @Autowired
    PersistentService persistentService;

    @Autowired
    GatewayRouteDao gatewayRouteDao;

    @DisplayName("测试 @listRoutes()")
    @Test
    public void getList(){

        List<String> ids = new ArrayList<>();
        ids.add("baidu");
        ids.add("pharmcube");

        List<GatewayRoute>  list = persistentService.listRoutes(DynamicRouteConfigType.YAML.getType());

        list.addAll(list);
        int i=0;
        for (GatewayRoute gatewayRoute : list) {
            i++;
            if (i>1){
                gatewayRoute.setId("ss");
                gatewayRoute.setId("hh");
            }
        }


        int r = persistentService.insertOrUpdate(list);

        Assertions.assertEquals(2,r);
//        List<String> listIds = list.stream().map(GatewayRoute::getId).collect(Collectors.toList());
//
//        Assertions.assertArrayEquals(ids.toArray(new String[]{}),listIds.toArray(new String[]{}));
//
//        Assertions.assertEquals(2,list.size());
    }

    @Test
    public void delete(){
        List<String> ids = new ArrayList<>();
        ids.add("baidu");
        ids.add("pharmcube");

        int i = gatewayRouteDao.deleteLogic(ids);
        Assertions.assertEquals(2,i);

        List<GatewayRoute>  list = persistentService.listRoutes(DynamicRouteConfigType.YAML.getType());
        List<Integer> delIds = list.stream().map(GatewayRoute::getIsDel).collect(Collectors.toList());
        Assertions.assertEquals(new Integer[]{1,1},delIds.toArray(new Integer[]{}));

    }


}
