package com.nx.auth.service.service;



public interface BaseService {
    String getProperty(String alias, String defaultValue);



//    @Override
//    public String getByAlias(String alias, String defaultValue) {
//        SysProperties sysProperties = getByAliasFromDb(alias);
//        if(BeanUtils.isEmpty(sysProperties)) {
//            return defaultValue;
//        }
//        String val = sysProperties.getValue();
//        if(StringUtil.isEmpty(val)) {
//            return defaultValue;
//        }
//        return val;
//    }
}
