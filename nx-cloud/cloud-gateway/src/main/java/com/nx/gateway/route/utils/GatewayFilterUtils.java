package com.nx.gateway.route.utils;

import com.google.common.base.Splitter;
import com.nx.gateway.route.model.GatewayFilterDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.gateway.filter.FilterDefinition;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GatewayFilterUtils {
    public static FilterDefinition parseFilterDefinition(Object filter){
        GatewayFilterDefinition definition = parseGatewayFilterDefinition(filter);
        if (definition!=null){
            FilterDefinition filterDefinition = new FilterDefinition();
            BeanUtils.copyProperties(definition,filterDefinition);
            return filterDefinition;
        }
        return null;
    }


    public static GatewayFilterDefinition parseGatewayFilterDefinition(Object filter){
        GatewayFilterDefinition definition = new GatewayFilterDefinition();
        if (filter instanceof Map){
            FilterEnum enums = FilterEnum.lookup(((Map) filter).get("name").toString());
            if (enums == null) return null;
            definition.setName(enums.name());
            Map<String, Object> map = (LinkedHashMap<String, Object>) ((Map) filter).get("args");
            Map<String, String> args=new LinkedHashMap<String,String>();
            for(String key:map.keySet()){
                if (key.equalsIgnoreCase("args")) continue;
                String value = map.get(key).toString();
                args.put(key, value);
            }
            definition.setArgs(args);
            return definition;
        }
        List<String> values = Splitter.on("=")
                .trimResults()
                .splitToList(filter.toString());

        if(values.size()>0){
            FilterEnum enums = FilterEnum.lookup(values.get(0));
            if (enums == null) return null;
            definition.setName(enums.name());
            if (values.size()>1){
                Map<String, String> args = parseArgs(enums, values.get(1).toString()) ;
                definition.setArgs(args);
            }
        }
        return definition;
    }

    private static Map<String, String> parseArgs(FilterEnum filterEnum,String text){
        Map<String,String> map = new LinkedHashMap<String, String>();
        String key = "";
        String value=  StringUtils.strip(text);;
        switch (filterEnum){
            case StripPrefix:
                key= "parts";
                map.put(key,value);
                break;
            case PrefixPath:
                key= "prefix";
                map.put(key,value);
                break;
            case AddRequestHeader:
            case AddRequestParameter:
            case AddResponseHeader:
            case SetRequestHeader:
            case SetResponseHeader:
                List<String> nameValues = Splitter.on(",")
                        .trimResults()
                        .splitToList(value);
                map.put("name",nameValues.get(0));
                map.put("value",nameValues.get(1));
                break;
            case MapRequestHeader:
                List<String> mapRequestHeader = Splitter.on(",")
                        .trimResults()
                        .splitToList(value);
                map.put("fromHeader",mapRequestHeader.get(0));
                map.put("toHeader",mapRequestHeader.get(1));
                break;
            case ModifyRequestBody:
                List<String> modifyRequestBody = Splitter.on(",")
                        .trimResults()
                        .splitToList(value);
                String inClass=modifyRequestBody.get(0);
                if (inClass.indexOf("#{@")>-1){
                    map.put("inClass",inClass);
                }else{
                    map.put("inClass", "#{T(" + inClass + ")}");
                }
                map.put("outClass",modifyRequestBody.get(1));
                map.put("contentType",modifyRequestBody.get(2));

                String rewriteFunction="";
                if (modifyRequestBody.size()==6){
                    map.put("inHints",modifyRequestBody.get(3));
                    map.put("outHints",modifyRequestBody.get(4));
                    rewriteFunction=modifyRequestBody.get(5);
                }else if(modifyRequestBody.size()==4){
                    rewriteFunction=modifyRequestBody.get(3);
                }
                if (StringUtils.isNotEmpty(rewriteFunction)) {
                    if (rewriteFunction.indexOf("#{@") > -1) {
                        map.put("rewriteFunction", rewriteFunction);
                    } else {
                        map.put("rewriteFunction", "#{@" + rewriteFunction + "}");
                    }
                }
                break;
            case ModifyResponseBody:
                List<String> modifyResponseBody = Splitter.on(",")
                        .trimResults()
                        .splitToList(value);
                String inClassResonse=modifyResponseBody.get(0);
                if (inClassResonse.indexOf("#{@")>-1){
                    map.put("inClass",inClassResonse);
                }else{
                    map.put("inClass", "#{T(" + inClassResonse + ")}");
                }
                map.put("outClass",modifyResponseBody.get(1));
                map.put("newContentType",modifyResponseBody.get(2));

                String rewriteFunctionResponse="";
                if (modifyResponseBody.size()==6){
                    map.put("inHints",modifyResponseBody.get(3));
                    map.put("outHints",modifyResponseBody.get(4));
                    rewriteFunctionResponse=modifyResponseBody.get(5);
                }else if(modifyResponseBody.size()==4){
                    rewriteFunctionResponse=modifyResponseBody.get(3);
                }
                if (StringUtils.isNotEmpty(rewriteFunctionResponse)) {
                    if (rewriteFunctionResponse.indexOf("#{@") > -1) {
                        map.put("rewriteFunction", rewriteFunctionResponse);
                    } else {
                        map.put("rewriteFunction", "#{@" + rewriteFunctionResponse + "}");
                    }
                }
                break;
            case DedupeResponseHeader:
                List<String> dedupeResponseHeader = Splitter.on(" ")
                        .splitToList(text);
                for(String v:dedupeResponseHeader){
                    if (StringUtils.isEmpty(v)) continue;
                    List<String> vList = Splitter.on(",")
                            .trimResults()
                            .splitToList(v);
                    if (vList.size()>1){
                        map.put(vList.get(0),vList.get(1));
                    }else{
                        map.put(vList.get(0),null);
                    }
                }
                break;
            case PreserveHostHeader:
                break;
            case RedirectTo:
                List<String> redirectTo = Splitter.on(",")
                        .trimResults()
                        .splitToList(text);
                map.put("status",redirectTo.get(0));
                map.put("url",redirectTo.get(1));
                break;
            case RemoveRequestHeader:
            case RemoveResponseHeader:
            case RemoveRequestParameter:
            case RequestHeaderToRequestUri:
                map.put("name",value);
                break;
            case RequestRateLimiter: //routeId
                List<String> requestRateLimiter = Splitter.on(",")
                        .trimResults()
                        .splitToList(text);

                String rateLimiter =requestRateLimiter.get(0);
                if (StringUtils.isNotEmpty(rateLimiter)) {
                    if (rateLimiter.indexOf("#{@") > -1) {
                        map.put("rewriteFunction", "#{@" + rateLimiter + "}");
                    } else {
                        map.put("rewriteFunction", rateLimiter);
                    }
                }
                String keyResolver =requestRateLimiter.get(1);
                if (StringUtils.isNotEmpty(keyResolver)) {
                    if (keyResolver.indexOf("#{@") > -1) {
                        map.put("rewriteFunction", "#{@" + keyResolver + "}");
                    } else {
                        map.put("rewriteFunction", keyResolver);
                    }
                }
                break;
            case RewritePath:
            case RewriteResponseHeader:
                List<String> rewritePath = Splitter.on(",")
                        .trimResults()
                        .splitToList(text);
                map.put("regexp",rewritePath.get(0));
                map.put("replacement",rewritePath.get(1));
                break;
            case Retry://短语方式无法表达
                break;
            case SetPath:
                map.put("template",value);
                break;
            case SecureHeaders:
                break;
            case  RewriteLocationResponseHeader:
                List<String> rewriteLocationResponseHeader = Splitter.on(",")
                        .trimResults()
                        .splitToList(text);
                map.put("stripVersion",rewriteLocationResponseHeader.get(0));
                map.put("locationHeaderName",rewriteLocationResponseHeader.get(1));
                map.put("hostValue",rewriteLocationResponseHeader.get(2));
                map.put("hostPortPattern ",rewriteLocationResponseHeader.get(3));
                map.put("hostPortVersionPattern ",rewriteLocationResponseHeader.get(4));
                map.put("protocols ",rewriteLocationResponseHeader.get(5));
                break ;
            case  SetStatus:
                map.put("status",value);
                break ;
            case  SaveSession:
                //map.put(SaveSessionGatewayFilterFactory.NAME_KEY)
                break;
            case RequestSize:
            case RequestHeaderSize:
                map.put("maxSize",value);
                break;
        }
        map.put(key,value);
        return map;
    }

}
