package com.nx.logger.model.api.dto;

import com.nx.logger.enums.LoginLogTypeEnum;
import com.nx.logger.enums.LoginResultEnum;
import lombok.Data;

/**
 * 登录日志表
 *
 * 注意，包括登录和登出两种行为
 *
 * @author 芋道源码
 */
@Data
public class LoginLogDTO  implements java.io.Serializable{

    /**
     * 日志主键
     */
    private Long id;
    /**
     * 日志类型
     *
     * 枚举 {@link LoginLogTypeEnum}
     */
    private LoginLogTypeEnum loginLogType;

    private Integer logType;

    /**
     * 链路追踪编号
     */
    private String traceId;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 用户类型
     *
     * 枚举 {@link com.nx.common.context.spi.LoginUser.UserTypeEnum}
     */
    private String userType;
    /**
     * 用户账号
     *
     * 冗余，因为账号可以变更
     */
    private String username;
    /**
     * 登录结果
     *
     * 枚举 {@link LoginResultEnum}
     */
    private Integer result;
    /**
     * 用户 IP
     */
    private String userIp;
    /**
     * 浏览器 UA
     */
    private String userAgent;


    public Integer getlogType(){
        if (logType!=null || logType.intValue()>0){
            return this.logType;
        }else if(loginLogType!=null){
            return loginLogType.getType();
        }
        return null;
    }


}
