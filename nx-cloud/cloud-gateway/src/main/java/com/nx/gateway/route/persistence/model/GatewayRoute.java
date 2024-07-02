package com.nx.gateway.route.persistence.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.logging.log4j.util.Strings;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Date;


/**
 *   "(#{id},#{routes},#{version},#{creator},#{createDate},#{remarks})"+
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("gateway_dynamic_route")
public class GatewayRoute  implements java.io.Serializable{

    @Valid
    @NotEmpty
    protected String configType;

    @Valid
    @NotEmpty
    protected String id;

    @Valid
    @NotEmpty
    protected String uri;

    @Valid
    @NotEmpty
    protected Integer isDel=0;

    @Valid
    @NotEmpty
    protected Integer isEnabled=1;


    @TableField("orderby")
    protected int order = 1;

    /**
     * 路由断言集合配置
     *
     *  List<GatewayPredicateDefinition>
     */
    @Valid
    @NotEmpty
    protected String predicates= Strings.EMPTY;

    /**
     * List<GatewayFilterDefinition>
     * 路由过滤器集合配置
     */
    @Valid
    protected String filters =Strings.EMPTY;

    protected String metadata =Strings.EMPTY;

    private String version="1.0.0";

    private String creator="nacos";

    private Date createDate=new Date();

    private String remarks="nacos";


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
