package com.nx.common.banner;

import com.nx.common.context.NxCommonConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BannerUtils {
    private static List<BannerInfo> enabledInfo = new ArrayList<>();

    public static void push(Class tClass,String modelName, String[] msg){
        if (msg==null){
            return;
        }
        if (modelName==null){
            modelName = "";
        }else{
            modelName = modelName + "_";
        }
        if (modelName.endsWith("-")){
            modelName.substring(0,modelName.lastIndexOf("-"));
        }

        String finalModelName = modelName;
        List<String> s = Arrays.stream(msg).map(o->{return finalModelName + o;}).collect(Collectors.toList());
        BannerInfo bannerInfo = new BannerInfo<>(tClass,"",s.toArray(new String[]{}));
        enabledInfo.add(bannerInfo);
    }

    public static void push(Class tClass,String... msg){
        if (msg==null){
            return;
        }

        BannerInfo bannerInfo = new BannerInfo<>(tClass,"",msg);
        enabledInfo.add(bannerInfo);
    }

    public static void push(int index,Class tClass,String... msg){
        if (msg==null){
            return;
        }

        BannerInfo bannerInfo = new BannerInfo<>(tClass,"",msg);
        enabledInfo.add(index,bannerInfo);
    }

    public static void push(BannerInfo bannerInfo){
        enabledInfo.add(bannerInfo);
    }

    //打印启动的组件
    public static void printEnabled(){
        List<String> msg = new ArrayList<>();
        for (BannerInfo bannerInfo : enabledInfo) {
            msg.addAll(bannerInfo.getMsgs());
        }
        printInfo(NxCommonConfiguration.class,msg.toArray(new String[]{}));
    }

    public static void printInfo(Class clz,String... msg){

        Logger logger = LoggerFactory.getLogger(clz);
        int max = "*********************************************************".length();
        for (String s : msg) {
            if (s.getBytes().length>max){
                max = s.getBytes().length;
            }
        }
        StringBuffer border=new StringBuffer("");
        for (int i = 0; i < (max); i++) {
            border.append("*");
        }
        logger.info("");
        logger.info(border.toString());
        for (String s : msg) {
            logger.info("* "+s);
        }
        logger.info(border.toString());
        logger.info("");
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BannerInfo<T>{

        private Class<T> tClass;
        private List<String> msgs=new ArrayList<>();
        private String url;


        public  BannerInfo(Class<T> tClass,String url,String[] msg){
            this.tClass = tClass;
            this.url = url;
            msgs.addAll(Arrays.asList(msg));
        }



    }
}
