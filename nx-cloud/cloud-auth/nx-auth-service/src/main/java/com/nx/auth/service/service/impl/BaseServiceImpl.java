package com.nx.auth.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nx.auth.service.dao.SysPropertiesDao;
import com.nx.auth.service.model.entity.SysProperties;
import com.nx.auth.service.service.BaseService;
import com.nx.utils.BeanUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class BaseServiceImpl implements BaseService {

    @Autowired
    SysPropertiesDao sysPropertiesDao;



    @Override
    public String getProperty(String alias, String defaultValue){

        QueryWrapper<SysProperties> queryWrapper = new QueryWrapper<SysProperties>();
        queryWrapper.and(wq->wq.eq("alias", alias));
        SysProperties sysProperties =  this.sysPropertiesDao.selectOne(queryWrapper);

        if(BeanUtils.isEmpty(sysProperties) || StringUtils.isEmpty(sysProperties.getValue())) {
            return defaultValue;
        }
        return sysProperties.getValue();
    }






}
