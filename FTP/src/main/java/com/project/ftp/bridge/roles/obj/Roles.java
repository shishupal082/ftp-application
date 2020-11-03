package com.project.ftp.bridge.roles.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Roles {
    private HashMap<String, ArrayList<String>> roleAccess;
    private HashMap<String, String> roleAccessMapping;

    public HashMap<String, ArrayList<String>> getRoleAccess() {
        return roleAccess;
    }

    public void setRoleAccess(HashMap<String, ArrayList<String>> roleAccess) {
        this.roleAccess = roleAccess;
    }

    public HashMap<String, String> getRoleAccessMapping() {
        return roleAccessMapping;
    }

    public void setRoleAccessMapping(HashMap<String, String> roleAccessMapping) {
        this.roleAccessMapping = roleAccessMapping;
    }

    @Override
    public String toString() {
        return "Roles{" +
                "roleAccess=" + roleAccess +
                ", roleAccessMapping=" + roleAccessMapping +
                '}';
    }
}
