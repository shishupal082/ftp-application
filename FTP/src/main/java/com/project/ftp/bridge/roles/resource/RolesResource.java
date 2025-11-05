package com.project.ftp.bridge.roles.resource;

import com.project.ftp.bridge.BridgeToAppInterface;
import com.project.ftp.bridge.BridgeTracking;
import com.project.ftp.bridge.config.BridgeConfig;
import com.project.ftp.bridge.config.EmailConfig;
import com.project.ftp.bridge.roles.service.RolesService;
import com.project.ftp.config.AppConfig;
import com.project.ftp.event.EventTracking;
import com.project.ftp.intreface.BridgeToApp;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class RolesResource {
    private final static Logger logger = LoggerFactory.getLogger(RolesResource.class);
    private final AppConfig appConfig;
    private final EventTracking eventTracking;
    public RolesResource(final AppConfig appConfig, final EventTracking eventTracking) {
        this.appConfig = appConfig;
        this.eventTracking = eventTracking;
    }
    private BridgeTracking getBridgeTracking() {
        BridgeToAppInterface bridgeToAppInterface = new BridgeToApp(eventTracking);
        return new BridgeTracking(bridgeToAppInterface);
    }
    private RolesService getRolesService() {
        EmailConfig emailConfig = appConfig.getFtpConfiguration().getEmailConfig();
        BridgeConfig bridgeConfig = new BridgeConfig(emailConfig, appConfig.getFtpConfiguration().getCreatePasswordEmailConfig());
        ArrayList<String> rolesConfigPath = StaticService.getRolesConfigPath(appConfig);
        return new RolesService(bridgeConfig, rolesConfigPath);
    }
    public boolean isRoleAuthorised(String apiName, String userName) {
        return this.getRolesService().isRoleAuthorised(apiName, userName);
    }
    public ArrayList<String> getActiveRoleIdByUserName(String username) {
        return this.getRolesService().getActiveRoleIdByUserName(username);
    }

    public ArrayList<String> getRelatedUsers(String username) {
        return this.getRolesService().getRelatedUsers(username);
    }
    public ArrayList<String> getAllUsersName() {
        return this.getRolesService().getAllUsersName();
    }

    public void trackRelatedUser() {
        HashMap<String, ArrayList<String>> allRelatedUsers = this.getRolesService().getAllRelatedUsers();
        this.getBridgeTracking().trackAllRelatedUsers(allRelatedUsers);
    }
    public Object getRolesConfig() {
        return this.getRolesService().getRolesConfig();
    }
    public boolean updateRoles(ArrayList<String> rolesConfigPath) {
        boolean status = this.getRolesService().updateRoles(rolesConfigPath);
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
