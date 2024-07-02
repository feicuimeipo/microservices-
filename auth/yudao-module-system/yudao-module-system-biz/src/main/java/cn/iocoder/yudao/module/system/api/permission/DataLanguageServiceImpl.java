package cn.iocoder.yudao.module.system.api.permission;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import cn.iocoder.yudao.module.system.dal.redis.datalang.DataLangRedisDAO;
import cn.iocoder.yudao.module.system.enums.oauth2.OAuth2ClientConstants;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DataLanguageServiceImpl implements DataLanguagePermission {
    @Autowired
    private DataLangRedisDAO dataLangRedisDAO;

    @Autowired
    private DictDataService dictDataService;

    @Autowired
    private AdminUserMapper adminUserMapper;


    @Override
    public List<String> scopes(String client) {
        //数据权限
        List<DataLangKV> dataLangList = listByClientId(client);
        List<String> finalScope =
                dataLangList.stream().map(item->{
                    return dataLang_oAuth2_scope_prefix+item.getValue();
                }).collect(Collectors.toList());
        return finalScope;
    }


    @Override
    public String getCurrentDataLanguageByUserId(Long userId) {
        String dataLang = this.getCacheCurrent(userId);
        if (StringUtils.isEmpty(dataLang)){
            AdminUserDO adminUserDO =  adminUserMapper.selectById(userId);
            if (adminUserDO!=null){
                this.setCacheCurrent(userId,adminUserDO.getCurrentDataLanguage());
                return adminUserDO.getCurrentDataLanguage();
            }
        }
        return dataLang;
    }


    @Override
    public void setCurrentDataLanguage(Long userId, String dataLanguage) {
        AdminUserDO adminUserDO =  adminUserMapper.selectById(userId);
        if (adminUserDO!=null){
            this.setCacheCurrent(userId,dataLanguage);
            adminUserDO.setCurrentDataLanguage(dataLanguage);
        }
    }


    @Override
    public List<DataLangKV> listByUserId(Long userId) {
        //数据权限
        String dataLanguages = this.getCacheList(userId);
        if (StringUtils.isEmpty(dataLanguages) || dataLanguages.equals("[]")){
            AdminUserDO adminUserDO = adminUserMapper.selectById(userId);
            if (adminUserDO == null) {
                return null;
            }
            dataLanguages = adminUserDO.getDataLanguages();
            this.setCacheList(userId,dataLanguages);
        }

        List<DataLangKV> originalDataLangList = listByClientId(OAuth2ClientConstants.CLIENT_ID_DEFAULT);

        Set<String> dataLanguageSet = DataLanguagePermission.stringValueSet(dataLanguages);

        List<DataLangKV> result = dataLanguageSet.stream().map(item->{
           Optional<DataLangKV> opt = originalDataLangList.stream().filter(cur->{
               return cur.getValue().equals(item);
           }).findFirst();
           return opt.isPresent()? opt.get():null;
        }).collect(Collectors.toList());

        List<DataLangKV> resultList = result.stream().filter(item->item!=null).collect(Collectors.toList());

        return resultList;
    }

    @Override
    public List<DataLangKV> listByClientId(String client) {

        client = OAuth2ClientConstants.CLIENT_ID_DEFAULT;
        String dataLanguages = getCacheListByClientId(client);
        //数据权限
        if (StringUtils.isEmpty(dataLanguages) || dataLanguages.equals("[]")){
            List<DictDataDO> dataLangList = dictDataService.getDictDatas(new DictDataExportReqVO(dataLang_dict_type));
            List<DataLangKV> result = dataLangList.stream().map(item->{
                return new DataLangKV(item.getValue(),item.getLabel());
            }).collect(Collectors.toList());


            if (result==null || result.size()==0) {
                //mock数据
                result.add(new DataLangKV("zh_en", "汉_英"));
                result.add(new DataLangKV("zh_fr", "汉_法"));
                result.add(new DataLangKV("zh_es", "汉_西"));
                result.add(new DataLangKV("zh_pq", "汉_葡"));
                result.add(new DataLangKV("zh_ru", "汉_俄"));
                result.add(new DataLangKV("zh_jp", "汉_日"));
                result.add(new DataLangKV("zh_kr", "汉_韩"));
            }

            this.setCacheListByClientId(client, JSONUtil.toJsonStr(result));

            return result;
        }else{
            JSONArray jsonArray = JSONUtil.parseArray(dataLanguages);
            List<DataLangKV> result =  jsonArray.toList(DataLangKV.class);
            return result;
        }
    }


   //@Override
    public String getCacheList(Long userId) {
       return dataLangRedisDAO.get(userId.toString());
    }


    @Override
    public void setCacheList(Long userId, String dataLang) {
        if (userId==null) return;
        dataLangRedisDAO.set(userId.toString(),dataLang);
    }


    //@Override
    public String getCacheListByClientId(String client) {
        return dataLangRedisDAO.get("byClientId"+client);
    }


    @Override
    public void setCacheListByClientId(String client, String dataLang) {
        if (client==null) return;
        dataLangRedisDAO.set("byClientId"+client,dataLang);
    }


    //@Override
    public String getCacheCurrent(Long userId) {
        return dataLangRedisDAO.get("current_"+userId.toString());
    }

    @Override
    public void setCacheCurrent(Long userId, String dataLang) {
        dataLangRedisDAO.set("current_"+userId.toString(),dataLang);
    }

}
