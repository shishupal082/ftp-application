package com.project.ftp.bridge.roles.resource;

import com.project.ftp.bridge.BridgeToAppInterface;
import com.project.ftp.bridge.BridgeTracking;
import com.project.ftp.config.AppConfig;
import com.project.ftp.event.EventTracking;
import com.project.ftp.intreface.BridgeToApp;
import com.roles_mapping.RolesMappingApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class RolesResource {
    private final static Logger logger = LoggerFactory.getLogger(RolesResource.class);
    private final AppConfig appConfig;
    private final EventTracking eventTracking;
    private RolesMappingApp rolesMappingApp;
    public RolesResource(final AppConfig appConfig, final EventTracking eventTracking) {
        this.appConfig = appConfig;
        this.eventTracking = eventTracking;
    }
    private BridgeTracking getBridgeTracking() {
        BridgeToAppInterface bridgeToAppInterface = new BridgeToApp(eventTracking);
        return new BridgeTracking(bridgeToAppInterface);
    }
    public void setRolesMappingApp() {
        String roleConfigDir = appConfig.getDirectoryService().getConfigPathDefault();
        logger.info("rolesMappingApp set with roleConfigDir: {}", roleConfigDir);
        this.rolesMappingApp = new RolesMappingApp(roleConfigDir);
    }
    public boolean isRoleAuthorised(String apiName, String userName) {
        return this.rolesMappingApp.isRoleAuthorised(apiName, userName);
    }
    public ArrayList<String> getActiveRoleIdByUserName(String username) {
        return this.rolesMappingApp.getActiveRoleIdByUserName(username);
    }

    public ArrayList<String> getRelatedUsers(String username) {
        return this.rolesMappingApp.getRelatedUsers(username);
    }
    public ArrayList<String> getAllUsersName() {
        return this.rolesMappingApp.getAllUsersName();
    }

    public void trackRelatedUser() {
        HashMap<String, ArrayList<String>> allRelatedUsers = this.rolesMappingApp.getAllRelatedUsers();
        this.getBridgeTracking().trackAllRelatedUsers(allRelatedUsers);
    }
    public Object getRolesConfig() {
        return this.rolesMappingApp.getRolesConfig();
    }
    public boolean updateRoles(ArrayList<String> rolesConfigPath) {
        boolean status = this.rolesMappingApp.updateRoleConfig(rolesConfigPath);
        this.trackRelatedUser();
        return status;
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
