package com.project.ftp.bridge.roles.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Roles {
    private HashMap<String, ArrayList<String>> roleAccess;
    private HashMap<String, String> roleAccessMapping;
    private HashMap<String, ArrayList<String>> relatedUsers;
    private ArrayList<String> coRelatedUsers;
    public Roles() {}
    public Roles(boolean isInit) {
        if (isInit) {
            roleAccess = new HashMap<>();
            roleAccessMapping = new HashMap<>();
            relatedUsers = new HashMap<>();
            coRelatedUsers = new ArrayList<>();
        }
    }
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

    public HashMap<String, ArrayList<String>> getRelatedUsers() {
        return relatedUsers;
    }

    public void setRelatedUsers(HashMap<String, ArrayList<String>> relatedUsers) {
        this.relatedUsers = relatedUsers;
    }

    public ArrayList<String> getCoRelatedUsers() {
        return coRelatedUsers;
    }

    public void setCoRelatedUsers(ArrayList<String> coRelatedUsers) {
        this.coRelatedUsers = coRelatedUsers;
    }

    @Override
    public String toString() {
        return "Roles{" +
                "roleAccess=" + roleAccess +
                ", roleAccessMapping=" + roleAccessMapping +
                ", relatedUsers=" + relatedUsers +
                ", coRelatedUsers=" + coRelatedUsers +
                '}';
    }
}
