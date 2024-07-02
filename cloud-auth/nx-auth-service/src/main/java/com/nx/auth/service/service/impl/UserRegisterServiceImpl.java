package com.nx.auth.service.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nx.api.model.R;
import com.nx.auth.service.model.entity.Demension;
import com.nx.auth.service.model.entity.Org;
import com.nx.auth.service.model.entity.OrgUser;
import com.nx.auth.service.model.entity.User;
import com.nx.auth.service.service.UserRegisterService;
import com.nx.boot.id.UniqueIdUtil;
import com.nx.utils.BeanUtils;
import com.nx.auth.service.dao.DemensionDao;
import com.nx.auth.service.dao.OrgDao;
import com.nx.auth.service.dao.OrgUserDao;
import com.nx.auth.service.dao.UserDao;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserRegisterServiceImpl implements UserRegisterService {
    @Autowired
    OrgDao orgDao;

    @Autowired
    DemensionDao demensionDao;


    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OrgUserDao orgUserDao;

    @Autowired
    UserDao userDao;

    /**
     * 添加部门
     * @param org
     * @return
     */
    @Override
    public R<String> addOrgFromExterUni(Org org) {
        if (BeanUtils.isEmpty(org.getName())) {
            return R.FAIL("添加组织失败，组织名称【name】不能为空！");
            //throw new RuntimeException("添加组织失败，组织名称【name】不能为空！");
        }
        if (BeanUtils.isEmpty(org.getCode())) {
            return R.FAIL("添加组织失败，组织编码【code】不能为空！");
            //throw new RuntimeException("添加组织失败，组织编码【code】不能为空！");
        }
        if (org.getCode().contains(",")) {
            return R.FAIL("组织编码中不能包含英文逗号‘,’");
        }
        if (BeanUtils.isEmpty(org.getDemId())) {
            Demension defaultDem =  demensionDao.getDefaultDemension();//demensionService.getDefaultDemension();
            if(BeanUtils.isEmpty(defaultDem)) {
                return R.FAIL("添加组织失败，本系统无默认维度");
            }
            org.setDemId(defaultDem.getId());
        }else {
            Demension dem = demensionDao.selectById(org.getDemId()) ;//demensionService.get(orgVo.getDemId());
            if (BeanUtils.isEmpty(dem)) {
                return R.FAIL("添加组织失败，根据输入的demId[" + org.getDemId() + "]没有找到对应的维度信息！");
            }
        }

        Org o = orgDao.getByCode(org.getCode());
        if (BeanUtils.isNotEmpty(o)) {
            return R.FAIL("添加组织失败，组织编码[" + org.getCode() + "]已存在！");
        }
        //Org pOrg = null;

        o = new Org();
        o.setPathName("/" + org.getName());
        o.setPath(org.getDemId() + "." + o.getId() + ".");
        if (!"0".equals(org.getParentId()) && !StringUtil.isNullOrEmpty(org.getParentId())) {
            Org pOrg =  orgDao.selectById(org.getParentId());
            if (pOrg==null) {
                return R.FAIL("添加组织失败，根据输入的parentId[" + org.getParentId() + "]没有找到对应的组织信息！");
            }
            if (pOrg!=null && !pOrg.getDemId().equals(org.getDemId())) {
                return R.FAIL("添加组织失败，根据输入demId与所输入的父组织所对应的维度id不一致！");
            }
            o.setPath(pOrg.getPath() + o.getId() + ".");
            o.setPathName(pOrg.getPathName() + "/" + org.getName());
        }
        o.setId(org.getId());
        o.setCode(org.getCode());
        o.setName(org.getName());
        o.setDemId(org.getDemId());
        o.setOrderNo(org.getOrderNo());
        if (com.alibaba.druid.util.StringUtils.isEmpty(org.getParentId())) {
            o.setParentId("0");
        } else {
            o.setParentId(org.getParentId());
        }
        orgDao.insert(o);
        //orgDao.create(o);
        return R.OK("添加组织成功！");
        //return new CommonResult<String>(true, "添加组织成功！", "");
    }




    // 检测邮箱格式是否正确
    private boolean checkEmail(String email){
        try {
            String check = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            return matcher.matches();
        } catch (Exception e) {}
        return false;
    }

    @Override
    public R<String> addUser(User user) {
        if(StringUtils.isEmpty(user.getAccount())){
            return R.FAIL("添加用户失败，用户帐号【account】必填！");
        }
        if(StringUtils.isEmpty(user.getFullname())){
            return R.FAIL("添加用户失败，用户名称【fullname】必填！");
        }
        if(StringUtils.isEmpty(user.getPassword())){
           return R.FAIL("添加用户失败，登录密码【password】必填！");
        }
        if(userDao.getCountByAccount(user.getAccount())>0){
            return  R.FAIL("添加用户失败，帐号【"+user.getAccount()+"】已存在，请重新输入！");
        }

        User u = userDao.getByNumber(user.getUserNumber());
        if(BeanUtils.isNotEmpty(u)){
            return R.FAIL("添加用户失败，工号【"+user.getUserNumber()+"】已存在，请重新输入！");
        }

        if(StringUtils.hasText(user.getMobile())){
            u = userDao.getByMobile(user.getMobile());
            if(BeanUtils.isNotEmpty(u)){
                R.FAIL("添加用户失败，手机号【"+user.getMobile()+"】已存在，请重新输入！");
            }
        }
        if (StringUtils.hasText(user.getEmail()) && !checkEmail(user.getEmail())){
            R.FAIL("添加用户失败，邮箱格式不正确！");
        }

        //User newUser = UserVo.parser(user);
        user.setStatus(User.STATUS_NORMAL);
        user.setId(UniqueIdUtil.getSuid());
        user.setCreateTime(LocalDateTime.now());
        user.setFrom(User.FROM_RESTFUL);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPwdCreateTime(LocalDateTime.now());
        Integer status = BeanUtils.isNotEmpty(user.getStatus())? user.getStatus():1;
        if(status!=1&&status!=-1&&status!=-2&&status!=0){
            status = 1;
        }
        user.setStatus(status);
        userDao.insert(user);
        return R.OK();
    }

    private List<OrgUser> getListByOrgIdUserId(String orgId, String userId) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("orgId", orgId);
        map.put("userId", userId);
        return orgUserDao.getByParms(map);
    }

    @Override
    public R<String> addUsersForOrg(String orgCode, String accounts){
        //OrgUserManager orgUserManager = AppUtil.getBean(OrgUserManager.class);

        String[] accountArr = accounts.split(",");
        Org o = orgDao.getByCode(orgCode);
        for (String a : accountArr) {
            User u = userDao.getByAccount(a);
            if (BeanUtils.isEmpty(u)) {
                return R.FAIL("帐号【" + a + "】不存在");
            }
            List<OrgUser> list = getListByOrgIdUserId(o.getId(), u.getId());
            if (list!=null && list.size()>0) {
                continue;
            }
            OrgUser ou = new OrgUser();
            ou.setId(UniqueIdUtil.getSuid());
            ou.setIsCharge(0);
            ou.setIsMaster(0);
            ou.setOrgId(o.getId());
            ou.setUserId(u.getId());
            ou.setVersion("1");
            ou.setIsRelActive(1);
            //orgUserService.create(ou);
            orgUserDao.insert(ou);
        }
        return R.OK();
    }

    @Override
    public List<Org> getOrgListByParentId(String parentId) {
        //List<Org> rtnList = new ArrayList<Org>();
        //List<Org> allList = orgDao.getAll();
        List<Org> orgList = orgDao.selectList(Wrappers.emptyWrapper());
        return getOrgListByParentId(orgList, parentId, new ArrayList<Org>());
    }
    private List<Org> getOrgListByParentId(List<Org> orgList,String parentId,List<Org> rtnList){
        //List<Org> allList = orgDao.getAll();
        for (Org org : orgList) {
            // 遍历出父id等于参数的id，add进子节点集合
            if (parentId.equals(org.getParentId())) {
                // 递归遍历下一级
                getOrgListByParentId(orgList, org.getId(), rtnList);
                rtnList.add(org);
            }
        }
        return rtnList;
    }
}
