package com.nx.logger.model.api.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BuryingPointLogDTO implements java.io.Serializable{
    /**
     * 全局唯一id
     */
    private String traceId;

    //操作系统
    private String clientOS;

    //操作系统-版本
    private String clientOSVer;

    //设备编号
    private String devId;

    //设备型号
    private String model;

    //工程-id
    @NotNull(message = "工程-id不能为空")
    private String projId;

    //工程--版本
    private String projIdVer;

    //渠道输入数字：1android_2ios_3H5_4小程序_5微信_6服务器后端_7QQ_100其它
    private String source;

    /**
     * 上传时间
     */
    private long upTime;
    /**
     * 事件发生时间,毫秒时间戳
     */
    private long accsTime;
    /**
     * 服务器时间,毫秒时间戳
     */
    private long servTime;

    // "view/click"
    private String eventType;
    //事件版本
    private String eventVer;

    //事件版本
    private String ip;

    //"56°75.343",
    private String longitude;

    //"143°07.230【非必填GPS关闭无法获取】",
    private String latitude;

    // "wifi/4G"
    private String netwkTyp;

    // "无埋点场景下所浏览页面的上一个页面的唯一标识",
    private String referId;

    //"页面浏览毫秒数，关闭页面时统计",
    private long duration;

    //"埋点自定义事件属性值",
    private String bannerId;

    private String bannerName;

    private String bannerType;

    private String bannerCity;

    // { "gid": "游客id", "uid": "登录账号", "province": ‘上海’】",
    private UsrProps usrProps;




    @Data
    public static class UsrProps{
        private String city;
        private String cityCode;
        private int age;
        //"1男_2女",
        private int sex;
        //13601197458
        private String phone;
    }

}
