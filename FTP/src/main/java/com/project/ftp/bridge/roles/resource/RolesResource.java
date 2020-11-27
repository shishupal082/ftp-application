package com.project.ftp.bridge.roles.resource;

import com.project.ftp.bridge.roles.service.RolesService;

import java.util.ArrayList;

public class RolesResource {
    private final RolesService rolesService;
//    private final RolesTracking rolesTracking;
    public RolesResource(RolesService rolesService) {
        this.rolesService = rolesService;
//        this.rolesTracking = new RolesTracking();
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
    // /api/get/roles/config
    public Object getRolesConfig() {
        return null;
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
