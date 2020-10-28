package com.project.ftp.bridge.roles.resource;

import com.project.ftp.bridge.roles.service.RolesService;

public class RolesResource {
    private final RolesService rolesService;
//    private final RolesTracking rolesTracking;
    public RolesResource(RolesService rolesService) {
        this.rolesService = rolesService;
//        this.rolesTracking = new RolesTracking();
    }
    public boolean isApiAuthorised(String apiName, String userName) {
        return rolesService.isApiAuthorised(apiName, userName);
    }
    // /api/get/roles/config
    public Object getRolesConfig() {
        return null;
    }
    // /api/get/roles/allByUid
    public Object getAllRolesByUsersId() {
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
