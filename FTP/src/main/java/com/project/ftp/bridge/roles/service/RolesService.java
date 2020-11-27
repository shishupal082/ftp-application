package com.project.ftp.bridge.roles.service;

import com.project.ftp.bridge.BridgeConstant;
import com.project.ftp.bridge.BridgeStaticService;
import com.project.ftp.bridge.config.BridgeConfig;
import com.project.ftp.bridge.roles.obj.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RolesService {
    final static Logger logger = LoggerFactory.getLogger(RolesService.class);
    private final BridgeConfig bridgeConfig;
    public RolesService(BridgeConfig bridgeConfig, String rolesConfigPath) {
        this.bridgeConfig = bridgeConfig;
        if (bridgeConfig != null) {
            this.bridgeConfig.setRoles(this.getRolesConfigByConfigPath(rolesConfigPath));
        }
    }
    private void addEntry(Map<String, ArrayList<String>> coRelatedUsers, String user1, String user2) {
        if (coRelatedUsers == null || user1 == null || user2 == null) {
            return;
        }
        ArrayList<String> user1Group = coRelatedUsers.get(user1);
        ArrayList<String> user2Group = coRelatedUsers.get(user2);
        if (user1Group == null) {
            user1Group = new ArrayList<>();
        }
        if (user2Group == null) {
            user2Group = new ArrayList<>();
        }
        if (!user1Group.contains(user2)) {
            user1Group.add(user2);
            coRelatedUsers.put(user1, user1Group);
        }
        if (!user2Group.contains(user1)) {
            user2Group.add(user1);
            coRelatedUsers.put(user2, user2Group);
        }
    }
    public Roles getRolesConfigByConfigPath(String rolesConfigPath) {
        RolesFileParser rolesFileParser = new RolesFileParser();
        Roles roles = rolesFileParser.getRolesFileData(rolesConfigPath);
        HashMap<String, ArrayList<String>> relatedUsers = null;
        HashMap<String, ArrayList<String>> coRelatedUsers = null;
        HashMap<String, ArrayList<String>> tempRelatedUsers = new HashMap<>();
        if (roles != null) {
            relatedUsers = roles.getRelatedUsers();
            coRelatedUsers = roles.getCoRelatedUsers();
        }
        String username;
        ArrayList<String> usernames;
        if (coRelatedUsers != null) {
            for (Map.Entry<String, ArrayList<String>> el: coRelatedUsers.entrySet()) {
                username = el.getKey();
                usernames = el.getValue();
                for(String str: usernames) {
                    this.addEntry(tempRelatedUsers, username, str);
                }
            }
        }
        if (roles!= null && relatedUsers == null) {
            relatedUsers = new HashMap<>();
        }
        ArrayList<String> relatedUsersGroup;
        for (Map.Entry<String, ArrayList<String>> el: tempRelatedUsers.entrySet()) {
            username = el.getKey();
            usernames = el.getValue();
            relatedUsersGroup = relatedUsers.get(username);
            if (relatedUsersGroup == null) {
                relatedUsers.put(username, usernames);
                continue;
            }
            for(String str: usernames) {
                if (!relatedUsersGroup.contains(str)) {
                    relatedUsersGroup.add(str);
                }
            }
            relatedUsers.put(username, relatedUsersGroup);
        }
        if (roles != null) {
            roles.setRelatedUsers(relatedUsers);
        }
        logger.info("roles config data:{}", roles);
        return roles;
    }
    public Roles getRolesConfig() {
        if (bridgeConfig != null) {
            return bridgeConfig.getRoles();
        }
        return null;
    }
    public HashMap<String, ArrayList<String>> getRolesAccess() {
        Roles roles = this.getRolesConfig();
        if (roles != null) {
            return roles.getRoleAccess();
        }
        return null;
    }
    public HashMap<String, String> getApiRolesMapping() {
        Roles roles = this.getRolesConfig();
        if (roles != null) {
            return roles.getRoleAccessMapping();
        }
        return null;
    }
    public ArrayList<String> getRolesAccessByRoleId(String roleId) {
        if (BridgeStaticService.isInValidString(roleId)) {
            logger.info("Invalid roleId:{}", roleId);
            return null;
        }
        HashMap<String, ArrayList<String>> rolesAccess = this.getRolesAccess();
        ArrayList<String> roleAccess;
        if (rolesAccess != null && rolesAccess.get(roleId) != null) {
            roleAccess = rolesAccess.get(roleId);
            if (roleAccess != null) {
                return roleAccess;
            }
            logger.info("Invalid rolesAccess:{}, for roleId:{}", null, roleId);
        }
        return null;
    }
    public String getRolesByApiName(String apiName) {
        if (BridgeStaticService.isInValidString(apiName)) {
            logger.info("Invalid apiName:{}", apiName);
            return null;
        }
        HashMap<String, String> apiRolesMapping = this.getApiRolesMapping();
        String apiRole;
        if (apiRolesMapping != null) {
            apiRole = apiRolesMapping.get(apiName);
            if (apiRole != null) {
                return apiRole;
            }
            logger.info("Invalid apiRole:{}, for apiName:{}", null, apiName);
        }
        return null;
    }
    public boolean isRoleAuthorised(String roleName, String userName, boolean isLogin) {
        logger.info("isRoleAuthorised check request:"+roleName+",{},{}", userName, isLogin);
        if (BridgeStaticService.isInValidString(roleName)) {
            logger.info("Invalid roleName:{}", roleName);
            return false;
        }
        if (BridgeStaticService.isInValidString(userName)) {
            logger.info("Invalid userName:{}", userName);
            return false;
        }
        if (BridgeConstant.IS_LOGIN.equals(roleName)) {
            return isLogin;
        }
        String apiRoles = this.getRolesByApiName(roleName);
        boolean result = this.apiRolesIncludeUser(apiRoles, userName, isLogin);
        logger.info("isRoleAuthorised check response:{}", result);
        return result;
    }
    public ArrayList<String> getAllRoles() {
        HashMap<String, String> roleAccessMapping = this.getApiRolesMapping();
        ArrayList<String> result = new ArrayList<>();
        if (roleAccessMapping != null) {
            for (Map.Entry<String, String> el: roleAccessMapping.entrySet()) {
                result.add(el.getKey());
            }
        }
        return result;
    }
    public ArrayList<String> getRelatedUsers(String username) {
        if (username == null) {
            return null;
        }
        Roles roles = this.getRolesConfig();
        if (roles == null) {
            return null;
        }
        HashMap<String, ArrayList<String>> relatedUsers = roles.getRelatedUsers();
        if (relatedUsers == null) {
            return null;
        }
        return relatedUsers.get(username);
    }
    private String getBooleanEquivalentToRole(String role, String userName, boolean isLogin) {
        if (BridgeStaticService.isInValidString(role)) {
            logger.info("Invalid role:{}", role);
            return null;
        }
        if (BridgeStaticService.isInValidString(userName)) {
            logger.info("Invalid userName:{}", userName);
            return null;
        }
        if (BridgeConstant.IS_LOGIN.equals(role)) {
            if (isLogin) {
                return BridgeConstant.TRUE;
            } else {
                return BridgeConstant.FALSE;
            }
        }
        ArrayList<String> roleAccessUsers = this.getRolesAccessByRoleId(role);
        if (roleAccessUsers == null) {
            return null;
        }
        if (roleAccessUsers.contains(userName)) {
            return BridgeConstant.TRUE;
        }
        return BridgeConstant.FALSE;
    }
    private boolean apiRolesIncludeUser(String apiRoles, String userName, boolean isLogin) {
        if (BridgeStaticService.isInValidString(apiRoles)) {
            logger.info("Invalid apiRoles:{}", apiRoles);
            return false;
        }
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        ArrayList<String> tokens = evaluator.tokenizeBinary(apiRoles);
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(BridgeConstant.OPEN);
        parameters.add(BridgeConstant.CLOSE);
        parameters.add(BridgeConstant.NOT);
        parameters.add(BridgeConstant.AND);
        parameters.add(BridgeConstant.OR);
        String token;
        for (int i=0; i<tokens.size(); i++) {
            token = tokens.get(i);
            if (parameters.contains(token)) {
                continue;
            }
            tokens.set(i, this.getBooleanEquivalentToRole(token, userName, isLogin));
        }
        Boolean finalResult = evaluator.evaluateBinaryExpression(String.join("", tokens));
        if (finalResult == null) {
            logger.info("Invalid finalResult:{}", finalResult);
            return false;
        }
        return finalResult;
    }
}
