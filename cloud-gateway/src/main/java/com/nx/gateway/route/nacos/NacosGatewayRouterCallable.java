package com.nx.gateway.route.nacos;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.api.config.ConfigType;
import com.nx.gateway.route.core.DynamicRouteConfigType;
import com.nx.gateway.route.core.DynamicRouteManager;
import com.nx.gateway.route.utils.YamlStringFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;
import sun.security.action.GetPropertyAction;

import java.security.AccessController;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

import static com.nx.gateway.route.nacos.NacosRouteService.ROOT_JSON;
import static com.nx.gateway.route.nacos.NacosRouteService.ROOT_YAML;


/**
 * @ClassName nacosGatewayHandler
 * @Description TODO
 * @Author NianXiaoLing
 * @Date 2022/6/17 16:23
 * @Version 1.0
 **/
@Slf4j
public class NacosGatewayRouterCallable implements Callable<Boolean> {
    ReentrantLock lock = new ReentrantLock();
    private DynamicRouteManager dynamicRouteManager;
    private String configInfo;
    private String configType;
    public NacosGatewayRouterCallable(String configInfo, String configType, DynamicRouteManager dynamicRouteManager){
        log.info(configInfo);
        this.configInfo = configInfo;
        this.configType = configType;
        this.dynamicRouteManager = dynamicRouteManager;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            lock.lock();
            List<NacosGatewayRouteEntity> entityList =
                    configType.equals("json")?jsonContentHandler(configInfo): yamlContentHandler(configInfo);

            if (entityList!=null && entityList.size()>0) {
                dynamicRouteManager.push(entityList.toArray(new NacosGatewayRouteEntity[]{}));
            }
        }finally {
            lock.unlock();
        }
        return true;
    }


    /**
     * - aaa
     * - bb
     * - cc
     * @param configInfo
     */
    private static List<NacosGatewayRouteEntity> yamlContentHandler(String configInfo){
        List<NacosGatewayRouteEntity> entityList = new LinkedList<>();
        if (!StringUtils.hasText(configInfo) && configInfo.equals(ROOT_YAML) || configInfo.equals(ROOT_JSON)) return entityList;

        String lineSeparator = AccessController.doPrivileged(new GetPropertyAction("line.separator"));
        configInfo = configInfo.replace("\\r", "");
        configInfo = configInfo.replace("\\n", lineSeparator);
        Yaml yaml = new Yaml();
        List<Object> sourceMap = yaml.loadAs(configInfo, List.class);

        if (!Objects.isNull(sourceMap)) {
            for (Object o1 : sourceMap) {
                LinkedHashMap<String,Object> item = (LinkedHashMap<String, Object>) o1;
                if (item==null || !(item instanceof LinkedHashMap)) continue;
                try {
                    String id = item.get("id")!=null?item.get("id").toString().trim():"";
                    String uri = item.get("uri")!=null?item.get("uri").toString().trim():"";

                    if (! (StringUtils.hasText(id) && StringUtils.hasText(uri))
                        && item.get("predicates")!=null && item.get("predicates") instanceof  List
                    ){
                        continue;
                    }
                    int order = 1;
                    if (item.get("order") != null && !item.get("order").toString().trim().equals("")) {
                        order = Integer.parseInt(item.get("order").toString().trim());
                    }

                    String predicates = "";
                    if (item.containsKey("predicates") && item.get("predicates") instanceof  List){
                        List objList = (List) item.get("predicates");
                        String value = YamlStringFormatUtils.format(objList);
                        predicates = value;
                    }

                    String filters = "";
                    if (item.containsKey("filters") && item.get("filters") instanceof  List){
                        List objList = (List) item.get("filters");
                        String value = YamlStringFormatUtils.format(objList);
                        filters = value;
                    }

                    String metadata = "";
                    if (item.containsKey("metadata") && item.get("metadata") instanceof  List){
                        List objList = (List) item.get("metadata");
                        String value = YamlStringFormatUtils.format(objList);
                        metadata = value;
                    }


                    if (StringUtils.hasText(id) && StringUtils.hasText(uri) && StringUtils.hasText(predicates)){
                        NacosGatewayRouteEntity dto = new NacosGatewayRouteEntity();
                        dto.setId(id);
                        dto.setOrder(order);
                        dto.setUri(uri);
                        dto.setPredicates(predicates);
                        dto.setFilters(filters);
                        dto.setMetadata(metadata);
                        dto.setConfigType(ConfigType.YAML.getType());
                        entityList.add(dto);
                    }
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                }
            }
        }
        return entityList;
    }

    private static List<NacosGatewayRouteEntity> jsonContentHandler(String configInfo){
        List<NacosGatewayRouteEntity> entityList = new LinkedList<>();
        if (!StringUtils.hasText(configInfo) && configInfo.equals(ROOT_YAML) || configInfo.equals(ROOT_JSON)) return entityList;

        JSONArray jsonArray = JSONArray.parseArray(configInfo);
        if (jsonArray==null || jsonArray.size()<=0) {
            return entityList;
        }


        for (int i=0;i<jsonArray.size();i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            NacosGatewayRouteEntity dto = new NacosGatewayRouteEntity();
            dto.setId(jsonObject.get("id").toString());
            dto.setOrder(Integer.parseInt((String) jsonObject.get("order")));
            dto.setUri((String) jsonObject.get("uri"));
            dto.setPredicates(JSONObject.toJSONString(jsonObject.getJSONObject("predicates")));
            dto.setFilters(JSONObject.toJSONString(jsonObject.get("filters")));
            dto.setMetadata(JSONObject.toJSONString(jsonObject.get("metadata")));
            dto.setConfigType(DynamicRouteConfigType.JSON.getType());
            entityList.add(dto);
        }
        return entityList;
    }
}
