package org.nianxi.api.feign.fallback;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.nianxi.api.feign.BpmServiceApi;
import org.nianxi.api.feign.dto.bpm.BoDataModifyRecordDTO;
import org.nianxi.api.feign.dto.bpm.BpmBusLinkDTO;
import org.nianxi.api.feign.dto.bpm.StartFlowParamObjectDTO;
import org.nianxi.api.feign.dto.bpm.StartResultDTO;
import org.nianxi.api.model.CommonResult;
import org.nianxi.api.model.exception.EmptyFeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


@Component
public class BpmServiceApiFallbackFactory implements FallbackFactory<BpmServiceApi> {
    @Override
    public BpmServiceApi create(Throwable e) {
        e.printStackTrace();
        return new BpmServiceApi(){

            @Override
            public List<String> getApprovalByDefKeyAndTypeId(String defKey, Optional<String> typeId, Optional<String> userId) {
                throw new EmptyFeignException();
            }

            @Override
            public CommonResult<String> executeTaskReminderJob() {
                throw new EmptyFeignException();
            }

            @Override
            public CommonResult<Boolean> isBoBindFlowCheck(String boCode, String formKey) {
                throw new EmptyFeignException();
            }

            @Override
            public Set<String> getMyOftenFlowKey() {
                throw new EmptyFeignException();
            }

            @Override
            public List<Map<String, String>> bpmDefinitionData(String alias) {
                throw new EmptyFeignException();
            }


            @Override
            public StartResultDTO start(StartFlowParamObjectDTO dto)  {
                throw new EmptyFeignException();
            }

            @Override
            public List<String> getBusLink(ObjectNode startFlowParamObject)  {
                throw new EmptyFeignException();
            }

            @Override
            public BpmBusLinkDTO getByBusinesKey(String businessKey, String formIdentity, Boolean isNumber)  {
                throw new EmptyFeignException();
            }

            @Override
            public List<ObjectNode> getDataByDefId(String defId)  {
                throw new EmptyFeignException();
            }

            @Override
            public Boolean isSynchronize(String instId, String nodeIds, String status, String lastStatus, String lastNodeIds)  {
                throw new EmptyFeignException();
            }

            @Override
            public List<Map<String, Object>> getFlowFieldListByJsonNode(JsonNode queryFilter)  {
                throw new EmptyFeignException();
            }

            @Override
            public CommonResult<String> getSubDataSqlByFk(ObjectNode boEnt, Object fkValue, String defId, String nodeId, String parentDefKey) {
                throw new EmptyFeignException();
            }

            @Override
            public ObjectNode defAutoStart()  {
                throw new EmptyFeignException();
            }

            @Override
            public ObjectNode printBoAndFormKey(String defId, String nodeId, String procInstId)  {
                throw new EmptyFeignException();
            }

            @Override
            public CommonResult<String> handleBoDateModify(Map<String, Object> params)  {
                throw new EmptyFeignException();
            }

            @Override
            public BoDataModifyRecordDTO getModifyById(String id)  {
                throw new EmptyFeignException();
            }

            @Override
            public List<ObjectNode> getTaskListByTenantId(String tenantId) {
                throw new EmptyFeignException();
            }
        };
    }
}
