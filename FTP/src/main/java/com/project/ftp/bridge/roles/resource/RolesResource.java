package com.project.ftp.bridge.roles.resource;

import com.project.ftp.bridge.BridgeTracking;
import com.project.ftp.bridge.roles.service.RolesService;

import java.util.ArrayList;
import java.util.HashMap;

public class RolesResource {
    private final RolesService rolesService;
    private final BridgeTracking bridgeTracking;
    public RolesResource(RolesService rolesService, BridgeTracking bridgeTracking) {
        this.rolesService = rolesService;
        this.bridgeTracking = bridgeTracking;
    }
    public boolean isRoleAuthorised(String apiName, String userName, boolean isLogin) {
        return rolesService.isRoleAuthorised(apiName, userName, isLogin);
    }
    public ArrayList<String> getAllRoles() {
        return rolesService.getAllRoles();
    }

    public ArrayList<String> getRelatedUsers(String username) {
        return rolesService.getRelatedUsers(username);
    }

    public void trackRelatedUser() {
        HashMap<String, ArrayList<String>> allRelatedUsers = rolesService.getAllRelatedUsers();
        bridgeTracking.trackAllRelatedUsers(allRelatedUsers);
    }
    public Object getRolesConfig() {
        return rolesService.getRolesConfig();
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
