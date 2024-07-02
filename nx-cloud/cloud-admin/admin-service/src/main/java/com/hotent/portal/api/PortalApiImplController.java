/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hotent.base.calendar.ICalendarService;
import com.hotent.file.persistence.FilePersistenceFactory;
import com.hotent.portal.model.MessageNews;
import com.hotent.portal.params.MessaboxVo;
import com.hotent.portal.persistence.manager.MessageNewsManager;
import com.hotent.portal.persistence.manager.SysMessageManager;
import com.hotent.portal.service.TenantService;
import com.hotent.sys.persistence.manager.*;
import com.hotent.sys.persistence.model.SysType;
import com.hotent.system.consts.DingTalkConsts;
import com.hotent.system.consts.WeChatOffAccConsts;
import com.hotent.system.consts.WeChatWorkConsts;
import com.hotent.system.enums.ExterUniEnum;
import com.hotent.system.util.DingTalkTokenUtil;
import com.hotent.system.util.WechatWorkTokenUtil;
import com.pharmcube.boot.annotation.ApiGroup;
import com.pharmcube.mybatis.support.query.FieldRelation;
import com.pharmcube.mybatis.support.query.PageList;
import com.pharmcube.mybatis.support.query.QueryFilter;
import com.pharmcube.mybatis.support.query.QueryOP;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.nianxi.api.enums.ResultCode2;
import org.nianxi.api.exception.BaseException;
import org.nianxi.api.feign.AdminServiceApi;
import org.nianxi.api.feign.constant.ApiGroupConsts;
import org.nianxi.api.model.CommonResult;
import org.nianxi.boot.support.AppUtil;
import org.nianxi.utils.BeanUtils;
import org.nianxi.utils.JsonUtil;
import org.nianxi.utils.StringUtil;
import org.nianxi.utils.time.DateFormatUtil;
import org.nianxi.x7.api.dto.portal.CalendarDTO;
import org.springframework.context.annotation.Primary;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;



@Primary
@Api(tags="feign接口")
@ApiGroup(group= {ApiGroupConsts.FEIGN_PORTAL})
@RestController
@RequestMapping("/")
@Slf4j
//public class PortalApiImplController implements PortalApi{
public class PortalApiImplController implements AdminServiceApi {
	/*@Resource
	SysRoleAuthController sysRoleAuthController;
	@Resource
	SysDataSourceController sysDataSourceController;
	@Resource
	SysPropertiesController sysPropertiesController;
	@Resource
	SysTypeController sysTypeController;
	@Resource
	JmsProducerController jmsProducerController;
	@Resource
	CalendarController calendarController;
	@Resource
	MessageReceiverController messageReceiverController;
	@Resource
	SysIdentityController sysIdentityController;
	@Resource
	MessageNewsController messageNewsController;
	@Resource
	SysLogsSettingsController sysLogsSettingsController;
	@Resource
	SysAuthUserController sysAuthUserController;
	@Resource
	SysExternalUniteController sysExternalUniteController;
	@Resource
	TenantController tenantController;
	@Resource
    FileInfoController fileInfoController;*/

	@Override
	public List<Map<String, String>> getMethodRoleAuth() {
		try {
			//return sysRoleAuthController.getMethodRoleAuth();
			SysRoleAuthManager sysRoleAuthManager = AppUtil.getBean(SysRoleAuthManager.class);
			return sysRoleAuthManager.getSysRoleAuthAll();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}

	/*@Override
	public String getPropertyByAlias(String alias) {
		try {
			//return sysPropertiesController.getByAlias(alias, Optional.empty());
			SysPropertiesManager sysPropertiesManager = AppUtil.getBean(SysPropertiesManager.class);
			return sysPropertiesManager.getByAlias(alias,"");
		} catch (Exception e) {
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}*/

	@Override
	public String getByAlias(String alias, Optional<String> defaultValue) {
		try {
			//return sysPropertiesController.getByAlias(alias, defaultValue);
			SysPropertiesManager sysPropertiesManager = AppUtil.getBean(SysPropertiesManager.class);
			String value = sysPropertiesManager.getByAlias(alias,defaultValue.orElse(""));
			return value;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}

	@Override
	public ObjectNode getSysTypeByIdOrKey(String id) {
		try {
			//SysType sysType = sysTypeController.getJson(id);

			SysTypeManager sysTypeManager = AppUtil.getBean(SysTypeManager.class);

			SysType sysType = new SysType();
			if (StringUtil.isNotEmpty(id)) {
				sysType = sysTypeManager.get(id);
				if(BeanUtils.isEmpty(sysType)){
					sysType = sysTypeManager.getByKey(id);
				}
			}
			return (ObjectNode) JsonUtil.toJsonNode(sysType).deepCopy();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}

	@Override
	public ObjectNode getSysTypeById(String id) {
		try {
			//SysType json = sysTypeController.getJson(id);

			SysTypeManager sysTypeManager = AppUtil.getBean(SysTypeManager.class);
			SysType sysType = new SysType();
			if (StringUtil.isNotEmpty(id)) {
				sysType = sysTypeManager.get(id);
				if(BeanUtils.isEmpty(sysType)){
					sysType = sysTypeManager.getByKey(id);
				}
			}
			return (ObjectNode)JsonUtil.toJsonNode(sysType).deepCopy();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}

	@Override
	public ObjectNode getAllSysTypeByJsonNode(JsonNode queryFilter) {
		try {
			SysTypeManager sysTypeManager = AppUtil.getBean(SysTypeManager.class);
			//PageRowList<ObjectNode> listJson = sysTypeController.getAllSysTypeByJsonNode(queryFilter);
			QueryFilter<SysType> queryFilterObj = JsonUtil.toBean(queryFilter,QueryFilter.class);
			PageList<SysType> list = sysTypeManager.query(queryFilterObj);
			return JsonUtil.toJsonNode(list).deepCopy();


		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}

	}

/*	@Override
	public CommonResult<String> sendNoticeToQueue(Notice notice) {
		try {
			TemplateService templateService  = AppUtil.getBean(TemplateService.class);
			templateService.sendNotice2Jms(notice);
			return new CommonResult<String>(true, "发送消息成功！");
			//return jmsProducerController.sendNoticeToQueue(notice);
		} catch (Exception e) {
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}*/

/*	@Override
	public CommonResult<String> sendToQueue(ObjectNode model) {
		try {
			return jmsProducerController.sendToQueue(model);
		} catch (Exception e) {
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}*/

	@Override
	public Long getEndTimeByUser(CalendarDTO calendar) {
		try {
			//return calendarController.getEndTimeByUser(JsonUtil.toBean(param, CalendarVo.class));
			ICalendarService iCalendarService = AppUtil.getBean(ICalendarService.class);
			String sTime = calendar.getStartTime();
			LocalDateTime endTime = null;
			if (StringUtil.isEmpty(sTime)){
				endTime = iCalendarService.getEndTimeByUser(calendar.getUserId(), Long.valueOf(calendar.getTime()));
			}else{
				endTime = iCalendarService.getEndTimeByUser(calendar.getUserId(), DateFormatUtil.parse(sTime), Long.valueOf(calendar.getTime()));
			}
			return Long.valueOf(DateFormatUtil.formaDatetTime(endTime));
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}

	@Override
	public Long getWorkTimeByUser(CalendarDTO calendar) {
		try {
			ICalendarService iCalendarService = AppUtil.getBean(ICalendarService.class);

			Long workTime = iCalendarService.getWorkTimeByUser(calendar.getUserId(), DateFormatUtil.parse(calendar.getStartTime()), DateFormatUtil.parse(calendar.getEndTime()));
			return workTime;

			//String workTimeByUser = calendarController.getWorkTimeByUser(JsonUtil.toBean(param, CalendarVo.class));
			//return Long.parseLong(workTimeByUser);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}

	@Override
	public ObjectNode getMessBoxInfo(String account) {
		try {
			//MessaboxVo messBoxInfo = messageReceiverController.getMessBoxInfo(account);
			SysMessageManager sysMessageManager = AppUtil.getBean(SysMessageManager.class);
			MessaboxVo messBoxInfo = sysMessageManager.getMessBoxInfo(account);
			return (ObjectNode)JsonUtil.toJsonNode(messBoxInfo).deepCopy();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}

	@Override
	public JsonNode getBeanByAlias(String alias) {
		try {
			//return sysDataSourceController.getBeanByAlias(alias);
			SysDataSourceManager sysDataSourceManager = AppUtil.getBean(SysDataSourceManager.class);
			return JsonUtil.toJsonNode(sysDataSourceManager.getByAlias(alias));
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}

	@Override
	public String getNextIdByAlias(String alias) {
		try {
			//CommonResult<String> nextIdByAlias = sysIdentityController.getNextIdByAlias(alias);
			SysIdentityManager identityManager = AppUtil.getBean(SysIdentityManager.class);
			CommonResult<String> nextIdByAlias = identityManager.getNextIdByAlias(alias);
			Assert.isTrue(nextIdByAlias.getState(), nextIdByAlias.getMessage());
			return nextIdByAlias.getValue();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}

	@Override
	public ObjectNode getMessageNewsByJsonNode(JsonNode queryFilter) {
		try {


			MessageNewsManager messageNewsManager = AppUtil.getBean(MessageNewsManager.class);

			QueryFilter<MessageNews> queryFilterObj = JsonUtil.toBean(queryFilter,QueryFilter.class);

			Optional<Boolean> isPublic  = Optional.empty();
			boolean isp = isPublic.orElse(false);
			if(isp){
				queryFilterObj.addFilter("FStatus", "2", QueryOP.EQUAL, FieldRelation.AND, "2");
			}
			PageList<MessageNews> list = messageNewsManager.query(queryFilterObj);
			return JsonUtil.toJsonNode(list).deepCopy();

			//PageList<MessageNews> list = messageNewsController.getMessageNewsByJsonNode(queryFilter, Optional.empty());
			//return JsonUtil.toJsonNode(list).deepCopy();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}

	@Override
	public ObjectNode publicMsgNews(String ids) {
		try {
			MessageNewsManager messageNewsManager = AppUtil.getBean(MessageNewsManager.class);
			CommonResult<String> publicMsgNews = messageNewsManager.publicMsgNews(ids);
			//CommonResult<String> publicMsgNews = messageNewsController.publicMsgNews(ids);
			return (ObjectNode)JsonUtil.toJsonNode(publicMsgNews).deepCopy();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}

/*	@Override
	public boolean calcPermssion(String permssionJson) {
		try {
			return sysAuthUserController.calcPermssion(permssionJson);
		} catch (Exception e) {
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}*/

	/*@Override
	public ObjectNode calcAllPermssion(String permssionJson) {
		try {
			return sysAuthUserController.calcAllPermssion(permssionJson);
		} catch (Exception e) {
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}*/

	@Override
	public List<String> getAuthorizeIdsByUserMap(String objType) {
		try {
			//return sysAuthUserController.getAuthorizeIdsByUserMap(objType);
			SysAuthUserManager sysAuthUserManager = AppUtil.getBean(SysAuthUserManager.class);
			return sysAuthUserManager.getAuthorizeIdsByUserMap(objType);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}

	@Override
	public String getToken(String type) {
		try {
			String token="";
			if(ExterUniEnum.WeChatWork.getKey().equals(type)){
				token= WechatWorkTokenUtil.getToken();
			}else if(ExterUniEnum.Dingtalk.getKey().equals(type)){
				token= DingTalkTokenUtil.getToken();
			}
			return token;
			//return sysExternalUniteController.getToken(type);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}

	@Override
	public CommonResult<String> initData(String tenantId) {
		try {
			//return tenantController.initData(tenantId);
			TenantService tenantService = AppUtil.getBean(TenantService.class);
			tenantService.initData(tenantId);
			return new CommonResult<String>("初始化数据成功");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}

	@Override
	public String getUserInfoUrl(String type, String code) {
		try {
			//return sysExternalUniteController.getUserInfoUrl(type,code);
			String url="";
			if(ExterUniEnum.WeChatWork.getKey().equals(type)){
				url= WeChatWorkConsts.getQyWxUserInfo(code);
			}else if(ExterUniEnum.Dingtalk.getKey().equals(type)){
				url= DingTalkConsts.getUserInfo(code);
			}else if(ExterUniEnum.WeChatOfficialAccounts.getKey().equals(type)){
				url= WeChatOffAccConsts.getWxAccessToken(code);
			}
			return url;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}

	@Override
	public String wordPrint(ObjectNode objectNode) {
		try {
			FilePersistenceFactory factory = AppUtil.getBean(FilePersistenceFactory.class);
			return factory.wordPrint(objectNode);
			//return fileInfoController.wordPrint(objectNode);
		} catch (Exception e) {
			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
		}
	}




//	该功能移动置配置文件里完成
//	@Override
//	public Map<String, String> getSysLogsSettingStatusMap() {
//		try {
//			//return sysLogsSettingsController.getSysLogsSettingStatusMap();
//			SysLogsSettingsManager sysLogsSettingsManager = AppUtil.getBean(SysLogsSettingsManager.class);
//			return sysLogsSettingsManager.getSysLogsSettingStatusMap();
//		} catch (Exception e) {
//			log.error(e.getMessage(),e);
//			throw new BaseException(ResultCode2.SERVICE_INVOKE_ERROR, ExceptionUtils.getRootCauseMessage(e));
//		}
//	}
}
