package com.nx.boot.launch.env;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;


@Data
public class NxValueProperties {
    private List<String> keys;
    private String defaultValue="";
    private String flag = "-1-n-i-a-n-x-i-2-";
    public static final String PLACEHOLDER_PREFIX = "${";
    public static final String PLACEHOLDER_SUFFIX = "}";

    public NxValueProperties(String keyword){
        if (keyword.startsWith("${") && keyword.endsWith("}") && keyword.length()>2){
            keyword = keyword.substring(2,keyword.length()-1);
        }

        String key = "";
        String[] ary = keyword.split(":");
        if (ary.length>1){
            key  = ary[0];
            defaultValue = ary[1].replace("'","");
            defaultValue = defaultValue.replace("\"","");
        }else if(ary.length == 1){
            key  = ary[0];
            defaultValue = null;
        }
        keys = parseKey(key);
    }

    private List<String> parseKey(String key){
        String[] ary = key.split(PLACEHOLDER_SUFFIX);
        List<String> keys = new LinkedList<>();

        for (String s : ary) {
            //int index = s.indexOf("$);
            int index1 = s.indexOf(PLACEHOLDER_PREFIX);
            if (index1==-1){
                keys.add(s);
            }else {
                int index2 = s.lastIndexOf(PLACEHOLDER_PREFIX);
                if (index1 != index2) {
                    throw new RuntimeException(key + "格式错误！");
                }

                String start = s.substring(0, index1);
                String end = s.substring(index1 + 2);
                keys.add(start);
                keys.add("${" + end + "}");
            }

        }

        return keys;
    }

    public static void main(String[] args) {
        String v = "os${config.server-addrbbb}999";
        String reg = "\\{*}";
        v.replaceAll(reg,"");
        new NxValueProperties(v);
    }
}
