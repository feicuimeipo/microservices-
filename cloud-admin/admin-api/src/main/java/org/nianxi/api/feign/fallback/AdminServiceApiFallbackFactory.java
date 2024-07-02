package org.nianxi.api.feign.fallback;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pharmcube.api.context.AuthUser;
import com.pharmcube.api.context.CurrentRuntimeContext;
import com.pharmcube.api.model.R;
import com.pharmcube.api.model.page.PageList;
import lombok.extern.slf4j.Slf4j;
import org.nianxi.api.feign.AdminServiceApi;
import org.nianxi.api.feign.dto.*;
import org.nianxi.api.model.CommonResult;
import org.nianxi.api.model.exception.EmptyFeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


@Slf4j
@Component
public class AdminServiceApiFallbackFactory implements FallbackFactory<AdminServiceApi> {
    @Override
    public AdminServiceApi create(Throwable e) {
        e.printStackTrace();
        return new AdminServiceApi() {


            @Override
            public JsonNode loadUserByUsername(String account) {
                 throw new EmptyFeignException();
            }

            @Override
            public JsonNode getAllUser() {
                 throw new EmptyFeignException();
            }

            @Override
            public CommonResult<UserDTO> getUserById(String userId) {
                 throw new EmptyFeignException();
            }

            @Override
            public UserDTO getUser(String account, String userNumber) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<UserDTO> getUserByAccounts(String accounts) {
                 throw new EmptyFeignException();
            }

            @Override
            public CommonResult<String> postUserByAccount(String accounts, String openid) {
                 throw new EmptyFeignException();
            }

            @Override
            public PageList<UserDTO> getAllOrgUsersByJsonNode(JsonNode queryFilter) {
                 throw new EmptyFeignException();
            }

            @Override
            public PageList<UserDTO> getAllOrgUsersByOrgId(String orgId) {
                throw new EmptyFeignException();
            }

            @Override
            public List<UserDTO> getUsersByRoleCode(String codes) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<UserDTO> getUserByPost(String postCode) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<UserDTO> getUsersByJob(String codes) {
                 throw new EmptyFeignException();
            }

            @Override
            public ArrayNode getUserByEmail(String email) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<ObjectNode> getChargesByOrgId(String orgId, boolean isMain) {
                 throw new EmptyFeignException();
            }

            @Override
            public OrgDTO getOrgByIdOrCode(String orgId) {
                 throw new EmptyFeignException();
            }

            @Override
            public CommonResult<RoleDTO> getRoleByIdOrCode(String code) {
                 throw new EmptyFeignException();
            }

            @Override
            public CommonResult<OrgPostDTO> getPostByIdOrCode(String code) {
                 throw new EmptyFeignException();
            }

            @Override
            public CommonResult<OrgJobDTO> getJobByOrgCode(String code) {
                 throw new EmptyFeignException();
            }

            @Override
            public OrgJobDTO getJobByIdOrCode(String code) {
                 throw new EmptyFeignException();
            }

            @Override
            public OrgUserDTO getOrgUserMaster(String userId, String demId) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<UserDTO> getSuperUser(ObjectNode obj) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<RoleDTO> getAllRole() {
                 throw new EmptyFeignException();
            }

            @Override
            public List<RoleDTO> getRoleListByAccount(String account) {
                 throw new EmptyFeignException();
            }

            @Override
            public OrgDTO getMainGroup(String userId, String demId) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<OrgPostDTO> getPosListByAccount(String account) {
                 throw new EmptyFeignException();
            }

            @Override
            public ArrayNode getCurrentUserAuthOrgLayout(String userId) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<OrgDTO> getOrgListByUserId(String userId) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<ObjectNode> getByOrgRelDefCode(String jobCode, String orgCode) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<ObjectNode> getByOrgRelCode(String postCode, String orgCode) {
                 throw new EmptyFeignException();
            }

            @Override
            public JsonNode getCharges(String userId, Boolean isMain, Boolean isP) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<OrgJobDTO> getJobsByUserId(String userId) {
                 throw new EmptyFeignException();
            }

            @Override
            public boolean isSupOrgByCurrMain(String userId, String demId, Integer level) {
                return false;
            }

            @Override
            public ArrayNode getSuperFromUnder(String userId, String orgId, String demId) {
                 throw new EmptyFeignException();
            }

            @Override
            public ArrayNode getCustomLevelCharge(String userId, String level, boolean isMainCharge) {
                 throw new EmptyFeignException();
            }

            @Override
            public Set<ObjectNode> getCustomLevelPost(String userId, String level, String postCode) {
                 throw new EmptyFeignException();
            }

            @Override
            public Set<ObjectNode> getCustomLevelJob(String userId, String level, String jobCode) {
                 throw new EmptyFeignException();
            }

            @Override
            public String getStartOrgParam(String userId, String param) {
                 throw new EmptyFeignException();
            }

            @Override
            public ObjectNode getUserParamsById(String userId, String code) {
                 throw new EmptyFeignException();
            }

            @Override
            public ObjectNode getOrgParamsById(String orgId, String code) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<UserDTO> getUserByIdsOrAccounts(String ids) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<OrgDTO> getChildOrg(String parentId) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<OrgDTO> getOrgsByparentId(String parentId) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<Map<String, String>> getPathNames(List<String> userIds) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<UserDTO> getDepHeader(String userId, Boolean isMain) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<UserDTO> getDepHeaderByOrg(String orgId, Boolean isMain) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<GroupDTO> getGroupsByUidAndTypeForGroupDTO(String userId, String type) {
                 throw new EmptyFeignException();
            }

            @Override
            public Map<String, Set<String>> getChildrenIds(Map<String, String> ids) {
                 throw new EmptyFeignException();
            }

            @Override
            public List queryOrgUserRelByJsonNode(JsonNode queryFilter) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<ObjectNode> getUserByNameaAndEmal(String query) {
                 throw new EmptyFeignException();
            }

            @Override
            public Map<String, Object> calculateNodeUser(Map<String, Object> result) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<OrgDTO> getOrgListByDemId(String demId) {
                 throw new EmptyFeignException();
            }

            @Override
            public CommonResult<UserDTO> getUserByOpenId(String openId) {
                 throw new EmptyFeignException();
            }

            @Override
            public Map<String, Map<String, String>> getUserRightMapByIds(Set<String> ids) {
                 throw new EmptyFeignException();
            }

            @Override
            public ObjectNode getDefaultDem() {
                 throw new EmptyFeignException();
            }

            @Override
            public OrgUserDTO getMainPostOrOrgByUserId(String userId) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<ObjectNode> getAllDems() {
                 throw new EmptyFeignException();
            }

            @Override
            public Map<String, Object> getDetailByAccountOrId(String account) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<UserDTO> getUserByIds(String ids) {
                 throw new EmptyFeignException();
            }

            @Override
            public CommonResult<String> addOrgFromExterUni(OrgDTO orgVo) {
                 throw new EmptyFeignException();
            }

            @Override
            public CommonResult<UserDTO> getUserByMobile(String mobile) {
                 throw new EmptyFeignException();
            }

            @Override
            public CommonResult<String> addUserFromExterUni(UserDTO newUser) {
                 throw new EmptyFeignException();
            }

            @Override
            public CommonResult<String> addUsersForOrg(String orgCode, String accounts) {
                 throw new EmptyFeignException();
            }

            @Override
            public ArrayNode getUserInfoBySignData(ArrayNode customSignDatas) {
                 throw new EmptyFeignException();
            }

            @Override
            public List<String> getIgnoreMenuCodes(String tenantId) {
                 throw new EmptyFeignException();
            }

            @Override
            public JsonNode getTenantById(String id) {
                 throw new EmptyFeignException();
            }

            @Override
            public CommonResult<OrgDTO> getFillOrg(String demId) {
                 throw new EmptyFeignException();
            }


            /**
             * portal
             */
            @Override
            public List<Map<String, String>> getMethodRoleAuth() {
                throw new EmptyFeignException();
            }

            @Override
            public String getByAlias(String alias, Optional<String> defaultValue) {
                throw new EmptyFeignException();
            }

            @Override
            public ObjectNode getSysTypeById(String id) {
                throw new EmptyFeignException();
            }

            @Override
            public ObjectNode getAllSysTypeByJsonNode(JsonNode queryFilter) {
                throw new EmptyFeignException();
            }

            @Override
            public Long getEndTimeByUser(CalendarDTO param) {
                throw new EmptyFeignException();
            }

            @Override
            public Long getWorkTimeByUser(CalendarDTO calendar) {
                throw new EmptyFeignException();
            }

            @Override
            public ObjectNode getSysTypeByIdOrKey(String id) {
                throw new EmptyFeignException();
            }

            @Override
            public ObjectNode getMessBoxInfo(String account) {
                throw new EmptyFeignException();
            }

            @Override
            public JsonNode getBeanByAlias(String alias) {
                throw new EmptyFeignException();
            }

            @Override
            public String getNextIdByAlias(String alias) {
                throw new EmptyFeignException();
            }

            @Override
            public ObjectNode getMessageNewsByJsonNode(JsonNode queryFilter) {
                throw new EmptyFeignException();
            }

            @Override
            public ObjectNode publicMsgNews(String array) {
                throw new EmptyFeignException();
            }

            @Override
            public Map<String, String> getSysLogsSettingStatusMap() {
                throw new EmptyFeignException();
            }

            @Override
            public List<String> getAuthorizeIdsByUserMap(String objType) {
                throw new EmptyFeignException();
            }

            @Override
            public String getToken(String type) {
                throw new EmptyFeignException();
            }

            @Override
            public CommonResult<String> initData(String tenantId) {
                throw new EmptyFeignException();
            }

            @Override
            public String getUserInfoUrl(String type, String code)  {
                throw new EmptyFeignException();
            }

            @Override
            public String wordPrint(ObjectNode objectNode) {
                throw new EmptyFeignException();
            }


        };

    }
}
