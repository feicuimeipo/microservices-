package cn.iocoder.yudao.module.system.controller.admin.permission;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.api.permission.DataLanguagePermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.*;
import static cn.iocoder.yudao.module.system.api.permission.DataLanguagePermission.dataLang_oAuth2_scope_prefix;
import static cn.iocoder.yudao.module.system.enums.oauth2.OAuth2ClientConstants.CLIENT_ID_DEFAULT;

@Api(tags = "AI公共-数据语言")
@Slf4j
@RestController
@RequestMapping("/system/tm/dataLang")
public class DataLanguageController {

    @Autowired
    private DataLanguagePermission dataLanguagePermission;


    //语言角色字典
    @ApiOperation (value = "语言角色字典列表")
    @GetMapping("/listDataLanguageByUserId")
    public CommonResult<List<DataLanguagePermission.DataLangKV>> list(Long userId){
         List<DataLanguagePermission.DataLangKV> list = dataLanguagePermission.listByUserId(userId);
        return CommonResult.success(list);
    }


    //语言角色字典
    @ApiOperation(value = "得到当前用户的数据语言")
    @GetMapping("/currentListDataLanguage")
    public CommonResult<List<DataLanguagePermission.DataLangKV>> currentListDataLanguage(){
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser==null) return CommonResult.success((new ArrayList<>()));
        List<DataLanguagePermission.DataLangKV> list = loginUser.getDataLanguageList();
        if (list==null || list.size()==0){
            list = dataLanguagePermission.listByUserId(loginUser.getId());
        }
        return CommonResult.success(list);
    }

    //语言角色字典
    @ApiOperation(value = "得到当前用户的数据语言")
    @GetMapping("/currentDataLanguage")
    public CommonResult<String> currentDataLanguage(){
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser==null) return CommonResult.success("");
        String dataLang = loginUser.getCurrentDataLang();
        if (StringUtils.isEmpty(dataLang)){
            dataLang = dataLanguagePermission.getCurrentDataLanguageByUserId(loginUser.getId());
        }
        return CommonResult.success(dataLang);
    }

    //语言角色字典
    @ApiOperation (value = "语言角色字典列表")
    @GetMapping("/listDataLanguageByClientId")
    public CommonResult<List<DataLanguagePermission.DataLangKV>> listDataLanguageByClientId(){
        List<DataLanguagePermission.DataLangKV> list = dataLanguagePermission.listByClientId(CLIENT_ID_DEFAULT);
        return CommonResult.success(list);
    }


    @ApiOperation("设置当前用户的语言数据权限")
    @PutMapping("/setting")
    public CommonResult<String> setDataLanguage(@RequestParam(REQUEST_ATTRIBUTE_DATA_LANG) String dataLang) {
        if (StringUtils.isEmpty(dataLang)) {
            return CommonResult.error(500, "语言角色不能为空");
        }

        if (dataLang.indexOf("-") == -1
                && dataLang.indexOf("_") == -1
                && dataLang.indexOf(":") == -1
                && dataLang.indexOf(",") == -1){
            return CommonResult.error(500, "格式不对！");
        }

        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();

        if (loginUser==null){
            return CommonResult.error(401, "用户未登录！");
        }

        List<DataLanguagePermission.DataLangKV>  list = loginUser.getDataLanguageList();
        Optional<DataLanguagePermission.DataLangKV> opt= list.stream().filter(item->item.getValue().equals(dataLang)).findFirst();
        if (!opt.isPresent()){
            return CommonResult.error(500, "无效的数据语言权限！");
        }

        String value = opt.get().getValue();
        dataLanguagePermission.setCurrentDataLanguage(loginUser.getId(),value);

        return CommonResult.success("");
    }

}
