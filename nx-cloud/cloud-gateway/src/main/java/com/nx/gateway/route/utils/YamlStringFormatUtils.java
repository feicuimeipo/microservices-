package com.nx.gateway.route.utils;

/**
 * @ClassName YamlNode
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/18 3:16
 * @Version 1.0
 **/

import org.yaml.snakeyaml.Yaml;
import sun.security.action.GetPropertyAction;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class YamlStringFormatUtils {
    public static Yaml yaml = new Yaml();
    public static int tree = 0;
    public static  String space = "  ";

    // 测试用例
    public static void main(String[] args) {
       String content =
                "routes: \n"+
                "  - id: baidu \n"+
                "    uri: https://www.baidu.com \n"+
                "    order: 1 \n"+
                "    predicates: \n"+
                "      - Path=/baidu/** \n"+
                "      - name: Cookie \n"+
                "        args: \n"+
                "      - name: mycookie \n"+
                "        args: \n"+
                "          - regexp: mycookievalue \n"+
                "      - name: Header \n"+
                "        args: \n"+
                "          - name: Authorization\n"+
                "          - value: nianxi\n"+
                "    filters:\n"+
                "      - StripPrefix=1 \n"+
                "      - SwaggerHeaderFilter \n"+
                "      - AddRequestHeader=X-Request-id, 99999\n"+
                "      - AddRequestHeader=X-Request-author, kevin\n"+
                "      - AddRequestParameter=param-id,99999\n"+
                "      - AddRequestParameter=param-author, kevin\n"+
                "      - AddResponseHeader=rep-id, 99999\n"+
                "      - AddResponseHeader=req-author,kevin\n"+
                "      - name: Filter1\n"+
                "        args:\n"+
                "          - name: Filter1Name\n"+
                "          - regexp: Filter1Value\n"+
                "      - name: Filter2\n"+
                "        args:\n"+
                "          - name: Filter2Name\n"+
                "          - value: Filter2Value\n";



        String lineSeparator = AccessController.doPrivileged(new GetPropertyAction("line.separator"));
        content = content.replace("\\r", "");
        content = content.replace("\\n", lineSeparator);
        Object map = yaml.load(content);


        List<String> stringBuilder = new ArrayList<>();
        format(map,stringBuilder,0,(map instanceof List?true:false));

        for (String s : stringBuilder) {
            System.out.print(s+"\n");
        }
       // System.out.println(result);
    }




    public static String format(Object object){
        List<String> stringList = new ArrayList<>();
        format(object,stringList,0,(stringList instanceof List?false:true));
        StringBuffer result = new StringBuffer();
        for (String s : stringList) {
            if (result.length()>0) result.append("\n");
            result.append(s);
        }
        //System.out.println(result.toString());
        return result.toString();
    }

    private static List<String> format(Object yamlObject, List<String> stringBuilder,int tree,boolean isListFirst) {
        if (stringBuilder==null){
            stringBuilder =  new ArrayList<>();
        }

        String tabspace = "";
        for(int i=0;i<tree;i++){
            tabspace +=  space;
        }

        if (yamlObject instanceof Map){
            for (String key : ((LinkedHashMap<String, ?>) yamlObject).keySet()) {
                key = filter(key);
                Object value =  ((LinkedHashMap<?, ?>) yamlObject).get(key);
                if (value instanceof Map){
                    if (isListFirst){
                        stringBuilder.add(String.format("%s- %s: ",tabspace, key));
                        isListFirst=false;
                    }else{
                        stringBuilder.add(String.format("%s  %s: ",tabspace, key));
                   }
                    format(value,stringBuilder,tree+1,false);
                }else if(value instanceof List){
                    if (isListFirst){
                        stringBuilder.add(String.format("%s- %s: ",tabspace, key));
                        isListFirst = false;
                    }else{
                        stringBuilder.add(String.format("%s  %s: ",tabspace, key));//key:args
                    }
                    format(value, stringBuilder, tree + 1,true);
                }else{
                    String valueStr = yaml.dump(value);
                    valueStr = filter(valueStr);
                    if (isListFirst){
                        stringBuilder.add(String.format("%s- %s: %s", tabspace, key, valueStr));//--name
                        isListFirst=false;
                    }else{
                        stringBuilder.add(String.format("%s  %s: %s", tabspace, key, valueStr));//root与args
                    }
                }

            }
        }else if (yamlObject instanceof  List){
            for (Object obj : ((List<?>) yamlObject)) {
                if (obj instanceof Map){
                    format(obj,stringBuilder,tree+1,isListFirst);
                }else if (obj instanceof List){
                    format(obj,stringBuilder,tree+1,isListFirst);
                }else{
                    String valueStr = yaml.dump(obj);
                    valueStr = filter(valueStr);
                    if(valueStr.indexOf(":")<=0 || valueStr.indexOf("=")>-1) {
                        stringBuilder.add(String.format("  %s- %s", tabspace, valueStr));//- StripPrefix=1/ - SwaggerHeaderFilter
                    }else if (isListFirst) {
                        stringBuilder.add(String.format("%s- %s", tabspace, valueStr));
                        isListFirst  = false;
                    }else{
                        stringBuilder.add(String.format("%s  %s", tabspace, valueStr));
                    }
                }
            }
        }else{
            String valueStr = yaml.dump(yamlObject);
            valueStr = filter(valueStr);
            stringBuilder.add(String.format("%s%s",tabspace,valueStr));
        }
        //return "";
        return stringBuilder;

    }

    private static String filter(String value){
        value = value.replace("null","");
        value = value.replace("\r", "");
        value = value.replace("\n", "");
        value = value.replace("\t", "");
        return value;
    }

}