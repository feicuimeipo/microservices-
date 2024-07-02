/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.feign.dto.bpm;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;


/**
* 流程表单数据修改记录
* <pre>
* 描述：流程表单数据修改记录 实体对象
* 构建组：x7
* 作者:heyf
* 邮箱:heyf@jee-soft.cn
* 日期:2020-03-23 11:45:27
* 版权：广州宏天软件股份有限公司
* </pre>
*/
@Data
@ToString
public class BoDataModifyRecordDTO implements java.io.Serializable{

   private static final long serialVersionUID = 1L;

   @Schema(description="ID_")
   protected String id;

   @Schema(description="外键（表单记录主键）")
   protected String refId;

   @Schema(description="修改人id")
   protected String userId;

   @Schema(description="修改人姓名")
   protected String userName;

   @Schema(description="流程实例id")
   protected String instId;

   @Schema(description="任务id")
   protected String taskId;

   @Schema(description="任务名称")
   protected String taskName;

   @Schema(description="节点id")
   protected String nodeId;

   @Schema(description="修改时间")
   protected LocalDateTime modifyTime;

   @Schema(description="修改人ip")
   protected String ip;

   @Schema(description="修改详情")
   protected String detail;

   @Schema(description="修改原因")
   protected String reason;

   @Schema(description="表单数据")
   protected String data;


}