package com.nx.gateway.route.utils;

import com.google.common.base.Splitter;
import com.nx.gateway.route.model.GatewayPredicateDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GatewayPredicateUtils {
    public static PredicateDefinition parsePredicateDefinition(Object predicate) {
        GatewayPredicateDefinition predicateDefinition = parseGatewayPredicateDefinition(predicate);
        if (predicateDefinition!=null){
            PredicateDefinition definition = new PredicateDefinition();
            BeanUtils.copyProperties(predicateDefinition,definition);
            return definition;
        }
        return null;
    }
    public static GatewayPredicateDefinition parseGatewayPredicateDefinition(Object predicate) {
        GatewayPredicateDefinition definition = new GatewayPredicateDefinition();
        if (predicate instanceof Map){
            String name = ((Map) predicate).get("name").toString();
            if (PredicateEnum.lookup(name) == null) return null;
            definition.setName(name);
            Map<String, Object> map = (LinkedHashMap<String, Object>) ((Map) predicate).get("args");
            Map<String, String> args=new LinkedHashMap<String,String>();
            for(String key:map.keySet()){
                if (key.equalsIgnoreCase("args")) continue;
                args.put(key,map.get(key).toString());
            }
            definition.setArgs(args);

            return definition;
        }

        List<String> values = Splitter.on("=")
                .trimResults()
                .splitToList(predicate.toString());

         if(values.size()>0){
             PredicateEnum enums = PredicateEnum.lookup(values.get(0));
             if (enums == null) return null;
             definition.setName(enums.name());
             if (values.size()>1){
                 Map<String, String> args = parseArgs(enums, values.get(1).toString()) ;
                 definition.setArgs(args);
             }
        }
        return definition;

    }

    private static Map<String, String> parseArgs(PredicateEnum predicateEnum,String text){
        Map<String,String> map = new LinkedHashMap<String, String>();
        String key= "patterns";
        String value= StringUtils.strip(text);;
        switch (predicateEnum){
            case Path:
            case Host:
                map.put(key,value);
                break;
            case Before:
            case After:
                key= "datetime";
                map.put(key,value);
                break;
            case Between:
                List<String> values = Splitter.on(",")
                        .trimResults()
                        .splitToList(value);
                map.put("datetime1",values.get(0));
                map.put("datetime2",values.get(1));
                break;
            case CloudFoundryRouteService: //指示是否来云路由
//                String cloudHeader = Joiner.on(",").join(X_CF_FORWARDED_URL,X_CF_FORWARDED_URL,X_CF_FORWARDED_URL);
//                map.put("header",cloudHeader);
//                map.put("regexp",value);
                break;
            case Cookie:
                List<String> cookie = Splitter.on(",")
                        .trimResults()
                        .splitToList(value);
                map.put("name",cookie.get(0));
                map.put("regexp",cookie.get(0));
                break;
            case Header:
                List<String> header = Splitter.on(",")
                        .trimResults()
                        .splitToList(value);
                map.put("header",header.get(0));
                if (header.size()>1){
                    map.put("regexp",header.get(1));
                }
                break;
            case Method:
                map.put("method",value);
                break;
            case Query:
                List<String> query = Splitter.on(",")
                        .trimResults()
                        .splitToList(value);
                map.put("param",query.get(0));
                if (query.size()>1) {
                    map.put("regexp",query.get(1));
                }
                break;
            case  ReadBody:
                //request attribute缓存在ServerWebExchange中，进行多次读取
                List<String> readBody = Splitter.on(",")
                        .trimResults()
                        .splitToList(value);

                if ( readBody.get(0).indexOf("#{@")>-1){
                    map.put("inClass", readBody.get(0));
                }else{
                    map.put("inClass", "#{T(" + readBody.get(0) + ")}");
                }
                if ( readBody.get(1).indexOf("#{@")>-1){
                    map.put("predicate", readBody.get(1));
                }else{
                    map.put("predicate", "#{@" + readBody.get(1) + "}");
                }
                if (readBody.size()>2){
                    map.put("hints",readBody.get(2));
                }
                break;
            case RemoteAddr:
                map.put("sources",value);
                break;
            case Weight://routeId未设置
                List<String> weight = Splitter.on(",")
                        .trimResults()
                        .splitToList(value);
                map.put("group",weight.get(0));
                if (weight.size()>1) {
                    map.put("weight",weight.get(1));
                }
                break;
        }
        return map;
    }
}
