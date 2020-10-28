package com.project.ftp.bridge.roles.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Roles {
    private HashMap<String, ArrayList<String>> roleAccess;
    private HashMap<String, String> apiRolesMapping;

    public HashMap<String, ArrayList<String>> getRoleAccess() {
        return roleAccess;
    }

    public void setRoleAccess(HashMap<String, ArrayList<String>> roleAccess) {
        this.roleAccess = roleAccess;
    }

    public HashMap<String, String> getApiRolesMapping() {
        return apiRolesMapping;
    }

    public void setApiRolesMapping(HashMap<String, String> apiRolesMapping) {
        this.apiRolesMapping = apiRolesMapping;
    }

    @Override
    public String toString() {
        return "Roles{" +
                "roleAccess=" + roleAccess +
                ", apiRolesMapping=" + apiRolesMapping +
                '}';
    }
}
