package com.nx.xxx.yyy.service;

import com.nx.api.model.query.PageInfo;
import com.nx.api.model.query.PageList;
import com.nx.mybatis.support.manager.BaseManager;
import com.nx.xxx.yyy.model.UserBo;
import com.nx.xxx.yyy.model.UserVo;
import com.nx.xxx.yyy.model.entity.UcUser;
import java.util.List;

/**
 * 
 * <pre> 
 * 描述：用户管理 处理接口
 * 构建组：x7
 * 作者:heyf
 * 邮箱:xlnian@163.com
 * 日期:2022-06-10 10:32:45
 * 版权：nx
 * </pre>
 */
public interface UcUserService extends BaseManager<UcUser> {

    /**
     * 查询
     * @param pageInfo
     * @param userBo
     * @return
     */
    PageList<UserVo> findPageListByUserBo(PageInfo pageInfo, UserBo userBo);


    PageList<UserVo> pageListByCondition(PageInfo pageInfo, UserBo bo);


    public List<UserVo> listByCondition(PageInfo pageInfo, UserBo bo);
}
