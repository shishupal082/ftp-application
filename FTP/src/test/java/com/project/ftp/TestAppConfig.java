package com.project.ftp;

import com.project.ftp.config.ApiIdentifier;
import com.project.ftp.config.ApiRoleAccess;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.resources.ApiResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TestAppConfig {
    public AppConfig getAppConfig(boolean isMysqlEnable) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(AppConstant.SERVER);
        if (isMysqlEnable) {
            arguments.add(AppConstant.TRUE);
        } else {
            arguments.add(AppConstant.FALSE);
        }
        arguments.add(AppConstant.FALSE);
        arguments.add("meta-data/app_env_config_mysql_db.yml");
        arguments.add("meta-data/app_env_config_2.yml");
        arguments.add("meta-data/app_env_config_4.yml");
        return AppConfig.getAppConfigFromCmdArgs(arguments, AppConstant.SOURCE_TEST);
    }
    public AppConfig getAppConfigV2() {
        //isMysqlEnable = false
        return this.getAppConfig(false);
    }
    public AppConfig getAppConfigV3() {
        //isMysqlEnable = true
        return this.getAppConfig(true);
    }
    @Test
    public void testAppConfigApiRoleMapping() {
        AppConfig appConfig = this.getAppConfig(false);
        Assert.assertEquals(AppConstant.AppVersion, appConfig.getAppVersion());
        Assert.assertEquals(55, appConfig.getFtpConfiguration().getApiAuthorisationConfig().size());
        Assert.assertEquals(56, appConfig.getApiRoleMappingList().size());
        Assert.assertEquals(2, appConfig.getApiRoleMappingList().get(ApiIdentifier.ADD_TEXT.getApiName()).size());
        Assert.assertEquals(ApiRoleAccess.IS_ADD_TEXT_ENABLE.getRoleAccessName(),
                appConfig.getApiRoleMappingList().get(ApiIdentifier.ADD_TEXT.getApiName()).get(0).getRole());
        Assert.assertEquals(AppConstant.roleAccessTypeDirect,
                appConfig.getApiRoleMappingList().get(ApiIdentifier.ADD_TEXT.getApiName()).get(0).getSource());
        Assert.assertEquals(ApiRoleAccess.IS_LOGIN.getRoleAccessName(),
                appConfig.getApiRoleMappingList().get(ApiIdentifier.ADD_TEXT.getApiName()).get(1).getRole());
        Assert.assertEquals(AppConstant.roleAccessTypeConfig,
                appConfig.getApiRoleMappingList().get(ApiIdentifier.ADD_TEXT.getApiName()).get(1).getSource());


        Assert.assertEquals(2, appConfig.getApiRoleMappingList().get(ApiIdentifier.GET_RELATED_USERS.getApiName()).size());
        Assert.assertEquals(ApiRoleAccess.IS_LOGIN.getRoleAccessName(),
                appConfig.getApiRoleMappingList().get(ApiIdentifier.GET_RELATED_USERS.getApiName()).get(0).getRole());
        Assert.assertEquals(AppConstant.roleAccessTypeDirect,
                appConfig.getApiRoleMappingList().get(ApiIdentifier.GET_RELATED_USERS.getApiName()).get(0).getSource());
        Assert.assertEquals("isAdminExternalConfig",
                appConfig.getApiRoleMappingList().get(ApiIdentifier.GET_RELATED_USERS.getApiName()).get(1).getRole());
        Assert.assertEquals(AppConstant.roleAccessTypeConfig,
                appConfig.getApiRoleMappingList().get(ApiIdentifier.GET_RELATED_USERS.getApiName()).get(1).getSource());

        Assert.assertEquals(2, appConfig.getApiRoleMappingList().get("404_file_path_check").size());
        Assert.assertEquals(ApiRoleAccess.IS_LOGIN.getRoleAccessName(),
                appConfig.getApiRoleMappingList().get("404_file_path_check").get(0).getRole());
        Assert.assertEquals(AppConstant.roleAccessTypeConfig,
                appConfig.getApiRoleMappingList().get("404_file_path_check").get(0).getSource());
        Assert.assertEquals("externalCheck",
                appConfig.getApiRoleMappingList().get("404_file_path_check").get(1).getRole());
        Assert.assertEquals(AppConstant.roleAccessTypeConfig,
                appConfig.getApiRoleMappingList().get("404_file_path_check").get(1).getSource());
    }
}
