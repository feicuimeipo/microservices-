package org.nianxi.api.feign.fallback;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pharmcube.api.model.page.PageList;
import lombok.extern.slf4j.Slf4j;
import org.nianxi.api.feign.FormServiceApi;
import org.nianxi.api.feign.dto.form.FormRestfulModelDTO;
import org.nianxi.api.model.CommonResult;
import org.nianxi.api.model.exception.EmptyFeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Component
public class FormServiceApiFallbackFactory implements FallbackFactory<FormServiceApi> {
    @Override
    public FormServiceApi create(Throwable e) {
        e.printStackTrace();
        return new FormServiceApi(){

            @Override
            public ObjectNode getMainBOEntByDefAliasOrId(String alias, String defId)  {
                throw new EmptyFeignException();
            }

            @Override
            public boolean getSupportDb(String alias) {
                return false;
            }

            @Override
            public List<ObjectNode> handlerBoData(FormRestfulModelDTO param)  {
                throw new EmptyFeignException();
            }

            @Override
            public ObjectNode getBodataByDefCode(String saveMode, String code)  {
                throw new EmptyFeignException();
            }

            @Override
            public ObjectNode getBodataById(FormRestfulModelDTO param)  {
                throw new EmptyFeignException();
            }

            @Override
            public ObjectNode getByFormKey(String formKey)  {
                throw new EmptyFeignException();
            }

            @Override
            public String getFormExportXml(String string)  {
                throw new EmptyFeignException();
            }

            @Override
            public String getBoDefExportXml(ObjectNode bodef)   {
                throw new EmptyFeignException();
            }

            @Override
            public String getFormRightExportXml(ObjectNode bodef)  {
                throw new EmptyFeignException();
            }

            @Override
            public CommonResult<String> importBo(String bodefXml)  {
                throw new EmptyFeignException();
            }

            @Override
            public ObjectNode importBoDef(List<ObjectNode> bos)  {
                throw new EmptyFeignException();
            }

            @Override
            public CommonResult<String> importForm(String formfXml)  {
                throw new EmptyFeignException();
            }

            @Override
            public CommonResult<String> importFormRights(String formRightsXml) {
                throw new EmptyFeignException();
            }

            @Override
            public ObjectNode getByFormId(String formId) {
                throw new EmptyFeignException();
            }

            @Override
            public String getInstPermission(ObjectNode param) {
                throw new EmptyFeignException();
            }

            @Override
            public String getStartPermission(ObjectNode param) {
                throw new EmptyFeignException();
            }

            @Override
            public String getPermission(ObjectNode param) {
                throw new EmptyFeignException();
            }

            @Override
            public List<ObjectNode> getFormRigthListByFlowKey(String formId) {
                throw new EmptyFeignException();
            }

            @Override
            public void removeFormRights(String flowKey, String parentFlowKey) {

            }

            @Override
            public ObjectNode getBodefByAlias(String alias) {
                throw new EmptyFeignException();
            }

            @Override
            public ObjectNode getBoJosn(String id) {
                throw new EmptyFeignException();
            }

            @Override
            public ObjectNode getBoEntByName(String name) {
                throw new EmptyFeignException();
            }

            @Override
            public void removeFormRightByFlowKey(ObjectNode param) {

            }

            @Override
            public void createFormRight(ObjectNode bpmFormRight) {

            }

            @Override
            public List<ObjectNode> queryFormRightByJsonNode(JsonNode queryFilter) {
                throw new EmptyFeignException();
            }

            @Override
            public List<ObjectNode> getFormBoLists(String formKey) {
                throw new EmptyFeignException();
            }

            @Override
            public Map<String, String> getFormAndBoExportXml(ObjectNode obj) {
                throw new EmptyFeignException();
            }

            @Override
            public CommonResult<String> importFormAndBo(ObjectNode obj) {
                throw new EmptyFeignException();
            }

            @Override
            public void removeDataByBusLink(JsonNode links) {

            }

            @Override
            public PageList getQueryPageForPageRowList(String alias) {
                throw new EmptyFeignException();
            }

            @Override
            public PageList doQueryForPageRowList(Optional<String> alias, Optional<Integer> page, Optional<String> queryData) {
                throw new EmptyFeignException();
            }

            @Override
            public List getCustomDialogs() {
                throw new EmptyFeignException();
            }

            @Override
            public ObjectNode getByClassName(String className) {
                throw new EmptyFeignException();
            }

            @Override
            public Map<String, Object> getFormData(String pcAlias, String mobileAlias) {
                throw new EmptyFeignException();
            }
        };
    }
}
