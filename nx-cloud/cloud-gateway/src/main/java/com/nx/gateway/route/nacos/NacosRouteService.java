package com.nx.gateway.route.nacos;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.nx.boot.launch.NxSpringBootApplicationBuilder;
import com.nx.boot.launch.conf.NxBootstrapConfig;
import com.nx.gateway.route.core.DynamicRouteConfig;
import com.nx.gateway.route.core.DynamicRouteConfigType;
import com.nx.gateway.route.core.DynamicRouteManager;
import com.nx.gateway.route.model.RouteConstant;
import com.nx.gateway.route.persistence.model.GatewayRoute;
import com.nx.gateway.route.persistence.service.PersistentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
/**
 * @author nianxl
 * @date   监听nacos的变化, 完成对指定路由的刷新操作
 */
@Slf4j
public class NacosRouteService {
    public static final String ROOT_YAML="-id: test";
    public static final String ROOT_JSON="[{}]";

    private final DynamicRouteConfig dynamicRouteConfig;
    private final DynamicRouteManager dynamicRouteManager;
    private static ConfigService configService;
    private final PersistentService persistentService;


    public NacosRouteService(DynamicRouteConfig dynamicRouteConfig, DynamicRouteManager dynamicRouteManager, PersistentService persistentService) {
        this.dynamicRouteConfig = dynamicRouteConfig;
        this.dynamicRouteManager = dynamicRouteManager;
        this.persistentService = persistentService;
    }

    public synchronized void start() {
        NxBootstrapConfig bootstrap = NxSpringBootApplicationBuilder.bootstrap();
        if (!bootstrap.isConfigEnabled() && StringUtils.hasText(bootstrap.getCloudServerAddr())){
            return;
        }
        String nacosHost = bootstrap.getCloudServerAddr();
        String namespace = bootstrap.getCloudNamespace();
        String groupId = bootstrap.getCloudGroup();
        String dataId = bootstrap.getConfigPrefix() + "-routes";
        long timeout = 3000;
        // 初始化网关路由 nacos conf
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, nacosHost);
        properties.setProperty(PropertyKeyConst.NAMESPACE, namespace);
        if (configService == null) {
            synchronized (NacosRouteService.class) {
                if (configService == null) {
                    try {
                        configService = NacosFactory.createConfigService(properties);
                    } catch (NacosException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }

            if (configService == null) return;
            String configInfo = "";
            /**
             * 监听Nacos下发的动态路由配置
             */
            try {
                configInfo = configService.getConfig( dataId,  groupId, timeout);

            } catch (NacosException e) {
                log.error(e.getMessage(), e);
            }

            if (StringUtils.hasText(configInfo) && !configInfo.equals(ROOT_YAML) && !configInfo.equals(ROOT_JSON)) {
                Callable<Boolean> call = new NacosGatewayRouterCallable(configInfo, dynamicRouteConfig.getConfigType(),dynamicRouteManager);
                FutureTask<Boolean> ft = new FutureTask<>(call);
                Thread t = new Thread(ft);
                t.start();
            }else{
                String content = dynamicRouteConfig.getConfigType().equals("json")?getContentFromLocalH2Json(): getContentFromLocalH2Yaml();                try {
                    configService.publishConfig(dataId, groupId, content,dynamicRouteConfig.getConfigType());
                } catch (NacosException e) {
                    log.error(e.getMessage(), e);
                }
            }

            try {
                configService.addListener(dataId, groupId, new Listener() {
                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        Callable<Boolean> call = new NacosGatewayRouterCallable(configInfo, dynamicRouteConfig.getConfigType(),dynamicRouteManager);
                        FutureTask<Boolean> ft = new FutureTask<>(call);
                        Thread t = new Thread(ft);
                        t.start();
                    }
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }
                });
            } catch (NacosException e) {
               log.error(e.getMessage(),e);
            }
        }
    }


    public String getContentFromLocalH2Yaml(){
        List<GatewayRoute> list =  persistentService.listRoutes(DynamicRouteConfigType.YAML.getType());
        if (list==null || list.size()<=0)  {
            return ROOT_YAML;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (GatewayRoute route : list) {
            try {
                if (stringBuffer.length()>0){
                    stringBuffer.append("\n");
                }
                stringBuffer.append(String.format("- %s: %s ", RouteConstant.ID,route.getId()));
                stringBuffer.append(String.format("\n  %s: %s ",RouteConstant.URI,route.getUri()));
                stringBuffer.append(String.format("\n  %s: %s ", RouteConstant.ORDER,route.getOrder()));

                if (StringUtils.hasText(route.getPredicates())) {
                    stringBuffer.append(String.format("\n  %s: ",RouteConstant.PREDICATES,route.getPredicates()));
                    String[] predicates = route.getPredicates().split("\n");
                    for (String predicate : predicates) {
                        stringBuffer.append(String.format("\n  %s ", predicate));
                    }
                }

                if (StringUtils.hasText(route.getFilters())) {
                    stringBuffer.append(String.format("\n  %s: ", RouteConstant.FILTERS));
                    String[] filters = route.getFilters().split("\n");
                    for (String filter : filters) {
                        stringBuffer.append(String.format("\n  %s ", filter));
                    }
                }
                if (StringUtils.hasText(route.getMetadata())) {
                    stringBuffer.append(String.format("\n  %s: \n%s ",RouteConstant.METADATA,route.getMetadata()));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        if (stringBuffer.length()<=0){
            stringBuffer.append(ROOT_YAML);
        }
        return stringBuffer.toString();
    }

    public String getContentFromLocalH2Json(){
        List<GatewayRoute> list =  persistentService.listRoutes(DynamicRouteConfigType.YAML.getType());
        if (list==null || list.size()<=0)  {
            return ROOT_YAML;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (GatewayRoute route : list) {
            try {
                if (stringBuffer.length()>0){
                    stringBuffer.append(",");
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id",route.getId());
                jsonObject.put("uri",route.getUri());
                jsonObject.put("order",route.getOrder());
                jsonObject.put("predicates",JSONObject.parseObject(route.getPredicates()));
                jsonObject.put("filters",JSONObject.parseObject(route.getFilters()));
                jsonObject.put("metadata",JSONObject.parseObject(route.getMetadata()));

                stringBuffer.append(String.format("\n %s",jsonObject.toJSONString()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        if (stringBuffer.length()<=0){
            stringBuffer.append(ROOT_YAML);
        }
        return String.format("[\n %s \n]",stringBuffer.toString());
    }

}
