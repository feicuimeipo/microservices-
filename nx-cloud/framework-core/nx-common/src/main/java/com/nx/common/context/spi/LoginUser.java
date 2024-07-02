package com.nx.common.context.spi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nx.common.crypt.util.SimpleCryptUtils;
import com.nx.common.model.bo.IntArrayValuable;
import lombok.*;
import java.util.*;


/**
 * <br>
 * Class Name   : LoginUser
 *
 * @author nianxiaoling
 * @version 1.0.0
 * @date 2020年10月17日
 */
@Data
@ToString
@Generated
public class LoginUser {
    public static final String CONTACT_CHAR = "#";
    public static final String CONTACT_LIST_CHAR = "@";
    public static final String PLACEHOLDER_CHAR = "{-}";

    protected Long           id;
    private String           loginName;
    protected Long           tenantId;
    protected String         username;
    protected String         nickname;        //昵称
    protected UserTypeEnum   userType;
    protected UserStatusEnum userStatus;
    private   List<String>   scopes;

    //专门针对cas的
    protected String     principalId;
    protected String     ticketId;


    // ========== 上下文 ==========
    /**
     * 上下文字段，不进行持久化
     *
     * 1. 用于基于 LoginUser 维度的临时缓存
     */
    @JsonIgnore
    private Map<String, Object> context;

    public void setContext(String key, Object value) {
        if (context == null) {
            context = new HashMap<>();
        }
        context.put(key, value);
    }

    public <T> T getContext(String key, Class<T> type) {
        return context==null?null: (T) context.get(key);
    }


    public static LoginUser decode(String encodeString) {
        if (isBlank(encodeString)) return null;
        encodeString = SimpleCryptUtils.decrypt(encodeString);
        String[] splits = encodeString.split(CONTACT_CHAR);

        LoginUser loginUser = new LoginUser();
        loginUser.setId(placeHolderToNull(splits[0])==null?null:Long.parseLong(splits[0]));
        loginUser.setUsername(placeHolderToNull(splits[1])==null?null:splits[1]);
        loginUser.setTenantId(placeHolderToNull(splits[2])==null?null:Long.parseLong(splits[2]));
        loginUser.setUserType(UserTypeEnum.valueOf(placeHolderToNull(splits[3])));
        loginUser.setUserStatus(UserStatusEnum.valueOf(placeHolderToNull(splits[4])));
        loginUser.setNickname(placeHolderToNull(splits[1])==null?null:splits[5]);
        loginUser.setScopes(Arrays.asList(splits[6]!=null?splits[6].split(CONTACT_LIST_CHAR):new String[]{}));
        loginUser.setPrincipalId(placeHolderToNull(splits[7]));
        loginUser.setTicketId(placeHolderToNull(splits[8]));

        return loginUser;
    }

    public String encode() {
        StringBuilder builder = new StringBuilder();
        builder.append(id).append(CONTACT_CHAR);
        builder.append(username).append(CONTACT_CHAR);
        builder.append(trimToPlaceHolder(tenantId==null?null:tenantId.toString())).append(CONTACT_CHAR);
        builder.append(userType.getCode()).append(CONTACT_CHAR);
        builder.append(userStatus.getCode()).append(CONTACT_CHAR);
        builder.append(nickname).append(CONTACT_CHAR);
            StringBuilder sbScope = new StringBuilder();
            scopes.stream().forEach(i->sbScope.append(CONTACT_LIST_CHAR));
        builder.append(sbScope).append(CONTACT_CHAR);
        builder.append(trimToPlaceHolder(principalId)).append(CONTACT_CHAR);
        builder.append(trimToPlaceHolder(ticketId)).append(CONTACT_CHAR);

        return SimpleCryptUtils.encrypt(builder.toString());
    }

    private static String trimToPlaceHolder(String value) {
        return value == null ? PLACEHOLDER_CHAR : value.trim();
    }


    private static String placeHolderToNull(String value) {
        return PLACEHOLDER_CHAR.equals(value) ? null : value;
    }

    private static boolean isBlank(String traceId){
        return traceId ==null || traceId.trim().length()==0;
    }


    @Getter
    @AllArgsConstructor
    public enum UserStatusEnum implements IntArrayValuable {
        enabled(1),disabled(0),paused(2);

        private Integer code;

        public static int[] ARRAYS = Arrays.stream(values()).mapToInt(UserStatusEnum::getCode).toArray();

        public static UserStatusEnum valueOf(Integer code) {
            Optional<UserStatusEnum> opt = Arrays.stream(UserStatusEnum.values()).filter(o -> o.equals(code)).findFirst();
            return opt.isPresent()?opt.get():null;
        }


        @Override
        public int[] array() {
            return ARRAYS;
        }
    }


    @AllArgsConstructor
    public enum UserTypeEnum implements IntArrayValuable {
        Admin(1,"后台管理用户(员)"),
        Member(2,"用户(会员)"),
        //Member2B(3,"B端用户(会员)")
        ;
        @Getter
        private Integer code;
        private String desc;


        public static int[] ARRAYS = Arrays.stream(values()).mapToInt(UserTypeEnum::getCode).toArray();

        public static UserTypeEnum valueOf(Integer code) {
            Optional<UserTypeEnum> opt = Arrays.stream(UserTypeEnum.values()).filter(o -> o.equals(code)).findFirst();
            return opt.isPresent()?opt.get():null;
        }

        @Override
        public int[] array() {
            return ARRAYS;
        }
    }

//    @Getter
//    @AllArgsConstructor
//    public enum LoginTypeEnum implements IntArrayValuable {
//            LOGIN_USERNAME(1,"username"), // 使用账号登录
//            LOGIN_SOCIAL(2,"social"),     // 使用社交登录
//            LOGIN_MOBILE(3,"mobile"),     // 使用手机登陆
//            LOGIN_SMS(4,"sms"),           // 使用短信登陆
//            LOGOUT_SELF(5,"self"),        // 自己主动登出
//            LOGOUT_DELETE(6,"delete"),   // 强制退出
//        ;
//
//
//        private Integer code;
//        private String desc;
//
//
//        public static LoginTypeEnum valueOf(Integer code) {
//            Optional<LoginTypeEnum> opt = Arrays.stream(LoginTypeEnum.values()).filter(o -> o.equals(code)).findFirst();
//            return opt.isPresent()?opt.get():null;
//        }
//
//        public static int[] ARRAYS = Arrays.stream(values()).mapToInt(LoginTypeEnum::getCode).toArray();
//
//        @Override
//        public int[] array() {
//            return ARRAYS;
//        }
//    }
}
