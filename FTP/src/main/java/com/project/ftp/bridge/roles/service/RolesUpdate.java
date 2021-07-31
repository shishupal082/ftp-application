package com.project.ftp.bridge.roles.service;

import com.project.ftp.bridge.roles.obj.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RolesUpdate {
    final static Logger logger = LoggerFactory.getLogger(RolesUpdate.class);
    private final RolesService rolesService;
    public RolesUpdate(RolesService rolesService) {
        this.rolesService = rolesService;
    }
    private void addRoleNameInUser(HashMap<String, ArrayList<String>> userRolesMapping,
                                   String username, String roleName) {
        if (userRolesMapping == null) {
            return;
        }
        userRolesMapping.computeIfAbsent(username, k -> new ArrayList<>());
        if (!userRolesMapping.get(username).contains(roleName)) {
            userRolesMapping.get(username).add(roleName);
        }
    }
    public void updateUserRolesMapping(){
        Roles roles = rolesService.getRolesConfig();
        if (roles == null) {
            return;
        }
        HashMap<String, ArrayList<String>> userRolesMapping = new HashMap<>();
        HashMap<String, String> roleAccessMapping = roles.getRoleAccessMapping();
        HashMap<String, ArrayList<String>> relatedUsers = roles.getRelatedUsers();
        HashMap<String, ArrayList<String>> roleAccess = roles.getRoleAccess();
        ArrayList<String> userNames;
        ArrayList<String> allUsername = new ArrayList<>();
        if (relatedUsers != null) {
            for (Map.Entry<String, ArrayList<String>> el: relatedUsers.entrySet()) {
                userNames = el.getValue();
                if (userNames == null) {
                    continue;
                }
                for(String userName: userNames) {
                    if (userName != null && !allUsername.contains(userName)) {
                        allUsername.add(userName);
                    }
                }
            }
        }
        if (roleAccess != null) {
            for (Map.Entry<String, ArrayList<String>> el: roleAccess.entrySet()) {
                userNames = el.getValue();
                if (userNames == null) {
                    continue;
                }
                for(String userName: userNames) {
                    if (userName != null && !allUsername.contains(userName)) {
                        allUsername.add(userName);
                    }
                }
            }
        }
        if (roleAccessMapping != null) {
            for(String tempUsername: allUsername) {
                for (Map.Entry<String, String> el1: roleAccessMapping.entrySet()) {
                    boolean result = rolesService.apiRolesIncludeUser(el1.getValue(), tempUsername);
                    if (result) {
                        this.addRoleNameInUser(userRolesMapping, tempUsername, el1.getKey());
                    }
                }
            }
        }
        roles.setUserRolesMapping(userRolesMapping);
        logger.info("userRolesMapping updated: {}", userRolesMapping);
    }
}
