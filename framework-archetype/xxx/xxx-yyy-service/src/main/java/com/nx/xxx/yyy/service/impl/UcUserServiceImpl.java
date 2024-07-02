package com.nx.xxx.yyy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nx.api.model.query.PageInfo;
import com.nx.api.model.query.PageList;
import com.nx.mybatis.support.manager.impl.BaseManagerImpl;
import com.nx.xxx.yyy.dao.UcUserDao;
import com.nx.xxx.yyy.model.UserBo;
import com.nx.xxx.yyy.model.UserVo;
import com.nx.xxx.yyy.model.entity.UcUser;
import com.nx.xxx.yyy.service.UcUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * <pre> 
 * 描述：用户管理 处理实现类
 * 构建组：x7
 * 作者:heyf
 * 邮箱:xlnian@163.com
 * 日期:2022-06-10 10:32:45
 * 版权：nx
 * </pre>
 */
@Service("ucUserManager")
public class UcUserServiceImpl extends BaseManagerImpl<UcUserDao, UcUser> implements UcUserService {

    @Autowired
    private UcUserDao ucUserDao;

    @Override
    public PageList<UserVo> findPageListByUserBo(PageInfo pageInfo, UserBo bo) {
        /**
         * 数据库查询
         */
        UcUser ucUser = new UcUser();
        BeanUtils.copyProperties(bo,ucUser); ;

        QueryWrapper queryWrapper = new QueryWrapper<UcUser>();
        if (!StringUtils.isEmpty(bo.getAccount())){
            queryWrapper.eq("ACCOUNT_",bo.getAccount());
        }

        Page page = new Page(pageInfo.getCurrent(), pageInfo.getSize());
        IPage<UcUser> pages= ucUserDao.selectPage(page, queryWrapper);


        PageList<UserVo> pageList = new PageList();
        BeanUtils.copyProperties(
                pages.convert(u -> {
                    UserVo vo = new UserVo();
                    BeanUtils.copyProperties(u,vo);
                    return vo;
                }),pageList);


        return pageList;
    }

    @Override
    public PageList<UserVo> pageListByCondition(PageInfo pageInfo, UserBo bo) {
        QueryWrapper queryWrapper = new QueryWrapper<UcUser>();
        if (!StringUtils.isEmpty(bo.getAccount())){
            queryWrapper.eq("ACCOUNT_",bo.getAccount());
        }
        IPage<UcUser> pages = ucUserDao.pageListByCondition(new Page(pageInfo.getCurrent(), pageInfo.getSize()),queryWrapper);

        PageList<UserVo> pageList = new PageList();
        BeanUtils.copyProperties(
                pages.convert(u -> {
            UserVo vo = new UserVo();
            BeanUtils.copyProperties(u,vo);
            return vo;
        }),pageList);

        return pageList;
    }

    @Override
    public List<UserVo> listByCondition(PageInfo pageInfo, UserBo bo) {
        QueryWrapper queryWrapper = new QueryWrapper<UcUser>();
        if (!StringUtils.isEmpty(bo.getAccount())){
            queryWrapper.eq("ACCOUNT_",bo.getAccount());
        }
        List<UcUser> list = ucUserDao.listByCondition(queryWrapper);


        List<UserVo> voList = list.stream().map(u->{
            UserVo vo = new UserVo();
            BeanUtils.copyProperties(u,vo);
            return vo;
        }).collect(Collectors.toList());

        return voList;
    }


}
