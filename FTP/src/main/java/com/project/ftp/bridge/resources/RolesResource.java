package com.project.ftp.bridge.resources;

import com.project.ftp.FtpConfiguration;
import com.project.ftp.config.AppConfig;
import com.project.ftp.event.EventTracking;
import com.project.ftp.intreface.BridgeToApp;
import com.project.ftp.service.StaticService;
import com.roles_mapping.RolesMappingApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class RolesResource {
    private final static Logger logger = LoggerFactory.getLogger(RolesResource.class);
    private final AppConfig appConfig;
    private final EventTracking eventTracking;
    private final static RolesMappingApp rolesMappingApp = new RolesMappingApp();
    public RolesResource(final AppConfig appConfig, final EventTracking eventTracking) {
        this.appConfig = appConfig;
        this.eventTracking = eventTracking;
    }
    private BridgeTracking getBridgeTracking() {
        BridgeToAppInterface bridgeToAppInterface = new BridgeToApp(eventTracking);
        return new BridgeTracking(bridgeToAppInterface);
    }
    public void updateRoles() {
        String roleConfigDir = appConfig.getDirectoryService().getConfigPathDefault();
        ArrayList<String> rolesConfigPath = StaticService.getRolesConfigPath(appConfig);
        rolesMappingApp.updateRoleConfig(roleConfigDir, rolesConfigPath);
        this.trackRelatedUser();
    }
    public boolean isRoleAuthorised(String apiName, String userName) {
        return rolesMappingApp.isRoleAuthorised(apiName, userName);
    }
    public ArrayList<String> getActiveRoleIdByUserName(String username) {
        return rolesMappingApp.getActiveRoleIdByUserName(username);
    }

    public ArrayList<String> getRelatedUsers(String username) {
        return rolesMappingApp.getRelatedUsers(username);
    }
    public ArrayList<String> getAllUsersName() {
        return rolesMappingApp.getAllUsersName();
    }

    public void trackRelatedUser() {
        HashMap<String, ArrayList<String>> allRelatedUsers = rolesMappingApp.getAllRelatedUsers();
        this.getBridgeTracking().trackAllRelatedUsers(allRelatedUsers);
    }
    public Object getRolesConfig() {
        return rolesMappingApp.getRolesConfig();
    }
    public static String getAppVersion() {
        return rolesMappingApp.getAppVersion(FtpConfiguration.class);
    }
    // /api/get/roles/allByRid
    public Object getAllRolesByRolesId() {
        return null;
    }
    // /api/get/roles/u/{uid}
    public Object getAvailableRolesForUserId(String userId) {
        return null;
    }
    // /api/get/roles/r/{roleId}
    public Object getAvailableUsersForRoleId(String rId) {
        return null;
    }
}
