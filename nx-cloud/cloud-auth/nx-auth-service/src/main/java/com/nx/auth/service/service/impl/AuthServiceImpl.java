/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.nx.api.model.R;
import com.nx.auth.context.BootConstant;
import com.nx.auth.exception.UsernameNotFoundException;
import com.nx.auth.api.dto.*;
import com.nx.auth.service.dao.*;
import com.nx.auth.service.service.AuthService;
import com.nx.auth.service.model.entity.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.security.cert.CertificateException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 * <pre> 
 * 描述：角色权限配置 处理实现类
 * 构建组：x6
 * 作者:liyg
 * 邮箱:liygui@jee-soft.cn
 * 日期:2018-06-29 14:27:46
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
    SysRoleAuthDao sysRoleAuthDao;

	@Autowired
    OrgDao orgDao;

	@Autowired
    UserDao userDao;

	@Autowired
    TenantManageDao tenantManageDao;

	@Autowired
    UserParamsDao userParamsDao;

	@Autowired
    OrgPostDao orgPostDao;

	@Override
	public User getUserByOpenId(String openId){
		return userDao.getUserByOpenId(openId);
	}


	@Override
	public List<SysRoleAuth> getSysRoleAuthAll() {
		ArrayList<Map<String,String>> result = new ArrayList<>();
		List<SysRoleAuth> sysRoleAuths = (ArrayList<SysRoleAuth>) sysRoleAuthDao.getSysRoleAuthAll();
		return sysRoleAuths;
	}

	@Override
	public UserFacadeDTO loadUserByUsername(String username) throws CertificateException {
		UserFacadeDTO userFacadeDTO  = loadUserByUsernameFromDB(username);
		//IUser user =  convertUserDetails2User(loadUserByUsernameFromDB(username));
		Assert.notNull(userFacadeDTO, "UserManagerDetailsServiceImpl.loadUserByUsernameFromDB "
				+ " returned null for username " + username + ". "
				+ "This is an interface contract violation");
		return userFacadeDTO;
	}
	/**
	 * 从数据库中获取用户的认证信息
	 * @param username
	 * @return
	 */
	private UserFacadeDTO loadUserByUsernameFromDB(String username) throws CertificateException {
		User user = userDao.getByAccount(username);
		if(user!=null) {
			throw new UsernameNotFoundException("用户信息找不到！");
		}
		//判断租户状态，非正常状态租户不允许其下用户
		if(StringUtils.isNotEmpty(user.getTenantId()) && !BootConstant.PLATFORM_TENANT_ID.equals(user.getTenantId())){
			TenantManage tenant = tenantManageDao.selectById(user.getTenantId());
			if(tenant!=null){
				throw new CertificateException("未获取到用户租户信息。");
			}else if(!TenantManage.STATUS_ENABLE.equals(tenant.getStatus())){
				throw new CertificateException("用户所属租户未启用。");
			}
		}


		UserFacadeDTO dto = new UserFacadeDTO();
		BeanUtils.copyProperties(user,dto);
		dto.setAdmin(user.isAdmin());

		UserDetailDTO userDetailDTO = getUserDetialDTO(user);
		dto.setUserDetail(userDetailDTO);


		List<UserRole> userRoles = getUserRoleListByUserId(user.getId());
		List<UserRoleDTO> userRoleList = new ArrayList<>();
		for (UserRole userRole : userRoles) {
			UserRoleDTO userRoleDTO = new UserRoleDTO();
			BeanUtils.copyProperties(userRole,userRoleDTO);
			userRoleList.add(userRoleDTO);
		}
		dto.setUserRoles(userRoleList);

		//得到主orgMain
		setOrgIdByUserId(user.getId(),dto);

		return dto;
	}


	public UserDetailDTO getUserDetialDTO(User user) {

		UserDetailDTO userDetailDTO = new UserDetailDTO();
		BeanUtils.copyProperties(user,userDetailDTO);

		List<UserParams> userParamsList = userParamsDao.getByUserId(user.getId());
		List<UserParamsDTO> userParamsDTOList = userParamsList.stream().map(item->{
			UserParamsDTO d = new UserParamsDTO();
			BeanUtils.copyProperties(item,d);
			return d;
		}).collect(Collectors.toList());
		userDetailDTO.setParams(userParamsDTOList);


		String userId = user.getId();
		String demId  = null;
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("userId", userId);
		if(StringUtils.isNotEmpty(demId)){
			map.put("demId", demId);
		}
		List<OrgPost> listPost = this.orgPostDao.getRelListByParam(map);

		List<OrgPostDTO> orgPostDTOList = listPost.stream().map(item->{
			OrgPostDTO d = new OrgPostDTO();
			BeanUtils.copyProperties(item,d);
			return d;

		}).collect(Collectors.toList());

		userDetailDTO.setOrgPosts(orgPostDTOList);

		return userDetailDTO;
	}

	@Autowired
	UserRoleDao userRoleDao;

	public List<UserRole> getUserRoleListByUserId(String userId) {
		Map<String,Object> params=new HashMap<String,Object>(){{
			put("userid",userId);
		}};

		QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
		Map<String, Object> paramNameValuePairs = wrapper.getParamNameValuePairs();
		paramNameValuePairs.putAll(params);

		return userRoleDao.queryByParams(wrapper);
	}



	private void setOrgIdByUserId(String userId,UserFacadeDTO dto){

		Map<String, String> map = new HashMap<String, String>();
		// 查询用户所在组织及该组织是否为主组织


		List<Org> orgList = orgDao.getOrgMapByUserId(userId);
		Set<OrgDTO> orgs = new HashSet<OrgDTO>();
		Set<String> orgIds = new HashSet<>();
		orgList.forEach(orgIdMap -> {
			String orgId = orgIdMap.getId();// MapUtil.getString(orgIdMap, "orgId");
			int isMaster = orgIdMap.getIsMaster() ;//MapUtil.getString(orgIdMap, "isMaster");

			OrgDTO orgDTO = new OrgDTO();
			BeanUtils.copyProperties(orgIdMap,orgDTO);
			if(StringUtils.isNotEmpty(orgId)){
				orgIds.add(orgId);
				orgs.add(orgDTO);
			}

			if(isMaster==1) {
				dto.setMainOrg(orgDTO);
				//map.put(AuthenticationUtil.CURRENT_USER_MAIN_ORGID, orgId);
			}
		});

		if(!orgs.isEmpty()){
			dto.setOrgs(orgs.toArray(new OrgDTO[]{}));
			//map.put(AuthenticationUtil.CURRENT_USER_ORGIDS,String.join(",", orgIds));
		}

		// 查询所在组织的下级组织
		List<Org> subOrgs = orgDao.getSubOrgByIds(orgIds);
		List<OrgDTO> subOrgDTOs = new ArrayList<>();
		subOrgs.forEach(item->{
			OrgDTO orgDTO = new OrgDTO();
			BeanUtils.copyProperties(item,orgDTO);
			subOrgDTOs.add(orgDTO);
		});
		dto.setSubOrgs(subOrgDTOs.toArray(new OrgDTO[]{}));
	}


	@Override
	public R<String> changePassword(String account, String oldPassword, String newpassword){
		//TODO: 修改密码
		return R.OK();
	}

	@Override
	public R<String> updatePhotoUrl(String account, String photoUrl){
		//TODO: 修改密码
		return R.OK();
	}


}
