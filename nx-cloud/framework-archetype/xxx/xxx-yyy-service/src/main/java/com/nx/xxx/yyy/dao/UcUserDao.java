package com.nx.xxx.yyy.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nx.xxx.yyy.model.entity.UcUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 
 * <pre> 
 * 描述：用户管理 DAO接口
 * 构建组：x7
 * 作者:heyf
 * 邮箱:xlnian@163.com
 * 日期:2022-06-10 10:32:45
 * 版权：nx
 * </pre>
 */
@Mapper
public interface UcUserDao extends BaseMapper<UcUser> {

//    @Select("SELECT * FROM uc_user a LEFT JOIN tableB b on a.key = b.key ${ew.customSqlSegment}")
//    @Select("<script>"+
//            "select *  from uc_user s  "+
//            "<where>${ew.customSqlSegment}</where>"+
//            "</script>")
    @Select("<script>"+
            "select *  from uc_user s  "+
            "<where>${ew.sqlSegment}</where>"+
            "</script>")
    IPage<UcUser> pageListByCondition(IPage page, @Param(Constants.WRAPPER) QueryWrapper wrapper);

    //@Select("SELECT * FROM tableA a LEFT JOIN tableB b on a.key = b.key ${ew.customSqlSegment}")
    @Select("SELECT * FROM uc_user a ${ew.customSqlSegment}")
    List<UcUser> listByCondition(@Param(Constants.WRAPPER) QueryWrapper wrapper);
}
