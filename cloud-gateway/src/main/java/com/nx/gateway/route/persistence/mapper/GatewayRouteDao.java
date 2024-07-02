package com.nx.gateway.route.persistence.mapper;


import com.nx.gateway.route.persistence.model.GatewayRoute;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author nxl
 */
@Mapper
@Repository
public interface GatewayRouteDao {
    /**
     * 创建表结构
     */
    @Update(
        "CREATE TABLE gateway_dynamic_route (" +
                "id VARCHAR(64) not null  ,"+
                "predicates  VARCHAR(4096) not null  ,"+
                "filters  VARCHAR(4096) null  ,"+
                "uri  VARCHAR(128) not null  ,"+
                "orderby  INT default 0  ,"+
                "version VARCHAR(128) not null  ,"+
                "configType VARCHAR(12) not null  ,"+
                "creator VARCHAR(16) ,"+
                "remarks VARCHAR(256) ,"+
                "isDel INT  default 0,"+
                "isEnabled INT default 1,"+
                "createDate TIMESTAMP default current_timestamp,PRIMARY KEY(id));"
    )
    void createTable();

    @Update("CREATE INDEX orderby  ON  gateway_dynamic_route(orderby);")
    void createIndexOrderby();

    @Update("CREATE INDEX createDate  ON  gateway_dynamic_route(createDate);")
    void createIndexCreateDate();

    @Update("CREATE INDEX configType  ON  gateway_dynamic_route(configType);")
    void createIndexConfigType();


    /**
     * 统计数据
     * @return
     */
    @Select("select count(1) from gateway_dynamic_route where isDel=#{del}")
    int countByDelFlag(@Param("id") Integer del);

    /**
     * 统计数据
     * @return
     */
    @Select("select count(1) from gateway_dynamic_route")
    int count();



    /**
     * 插入一条记录
     * @return
     */
    @Insert({"<script>" +
            "insert into gateway_dynamic_route(id,predicates,filters,uri,orderby,isDel,isEnabled,version,configType,creator,createDate,remarks) values " +
            "<foreach collection='lists' item='record' separator=','>" +
            "(#{record.id},#{record.predicates},#{record.filters},#{record.uri},#{record.order},#{record.isDel},#{record.isEnabled},#{record.version},#{record.configType},#{record.creator},#{record.createDate},#{record.remarks})"+
            "</foreach>" +
            " on duplicate key update id=VALUES(id) "+
            "</script>"})
    public int  insertOrUpdate(@Param("lists") List<GatewayRoute> lists);




    @Update({"<script>" +
            "update gateway_dynamic_route set isDel=1 WHERE id IN (",
            "<foreach item='id' index='index' collection='ids' open='' separator=',' close=''>",
            "#{id}",
            "</foreach>)",
            "</script>"})
    public int  deleteLogic(@Param("ids") List<String> ids);



    /**
     *
     * @return
     */
    //@Select("select * from gateway_dynamic_route order by orderby,createDate desc")
    @Select({"<script>",
            "select * FROM gateway_dynamic_route  WHERE isDel=0 ",
            "<if test='configType != null and configType != \"\" '>",
                "and configType=#{configType}",
            "</if> order by orderby,createDate desc ",
            "</script>"})
    public List<GatewayRoute> listRoutes(@Param("configType") String configType) ;
}
