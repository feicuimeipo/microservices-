package cn.iocoder.yudao.module.system.api.permission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


public interface DataLanguagePermission {
    String dataLang_oAuth2_scope_prefix="data_language.";

    String dataLang_dict_type="language_data_role";

    List<String> scopes(String client);

    String getCurrentDataLanguageByUserId(Long userId);

    void setCurrentDataLanguage(Long userId, String dataLanguage);

    List<DataLangKV> listByUserId(Long userId);

    List<DataLangKV> listByClientId(String client);


    void setCacheList(Long userId, String dataLang);

    void setCacheListByClientId(String client, String dataLang);


    void setCacheCurrent(Long userId, String dataLang);

    public static String setToString(Set<String> dataLanguageValue){
        if (dataLanguageValue!=null && dataLanguageValue.size()>0){
            StringBuffer stringBuffer = new StringBuffer();
            for (String s : dataLanguageValue) {
                stringBuffer.append(","+s);
            }
            return stringBuffer.toString().substring(1);
        }
        return null;
    }



    public static Set<String> stringValueSet(String str){
        if (str!=null && StringUtils.hasText(str)){
            String[] array = str.split(",");
            List<String> list = Arrays.asList(array);
            Set sets = list.stream().filter(item->StringUtils.hasText(item)).collect(Collectors.toSet());
            return sets;
        }
        return new HashSet<>();
    }



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataLangKV{
        String value;
        String label;
    }

}

