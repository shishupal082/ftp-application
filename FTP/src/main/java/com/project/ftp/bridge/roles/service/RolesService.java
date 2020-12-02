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
        ArrayList<String> combineUserGroup = new ArrayList<>();
        combineUserGroup.add(user1);
        if (!combineUserGroup.contains(user2)) {
            combineUserGroup.add(user2);
        }
        if (user1Group == null) {
            user1Group = new ArrayList<>();
        }
        if (user2Group == null) {
            user2Group = new ArrayList<>();
        }
        for(String str: user1Group) {
            if (!combineUserGroup.contains(str)) {
                combineUserGroup.add(str);
            }
        }
        for(String str: user2Group) {
            if (!combineUserGroup.contains(str)) {
                combineUserGroup.add(str);
            }
        }
        for(String str: combineUserGroup) {
            coRelatedUsers.put(str, combineUserGroup);
        }
        coRelatedUsers.put(user1, combineUserGroup);
        coRelatedUsers.put(user2, combineUserGroup);
    }
    private ArrayList<String> removeDuplicate(ArrayList<String> entry) {
        ArrayList<String> result = new ArrayList<>();
        if (entry == null) {
            return result;
        }
        for (String str: entry) {
            if (result.contains(str)) {
                continue;
            }
            result.add(str);
        }
        return result;
    }
    public Roles getRolesConfigByConfigPath(String rolesConfigPath) {
        RolesFileParser rolesFileParser = new RolesFileParser();
        Roles roles = rolesFileParser.getRolesFileData(rolesConfigPath);
        HashMap<String, ArrayList<String>> relatedUsers = null;
        HashMap<String, ArrayList<String>> coRelatedUsers = null;
        if (roles != null) {
            relatedUsers = roles.getRelatedUsers();
            coRelatedUsers = roles.getCoRelatedUsers();
        }
        String username;
        ArrayList<String> usernames;
        /*Mixing co-related user properly */
        HashMap<String, ArrayList<String>> tempCoRelatedUsers = new HashMap<>();
        if (coRelatedUsers != null) {
            for (Map.Entry<String, ArrayList<String>> el: coRelatedUsers.entrySet()) {
                username = el.getKey();
                usernames = el.getValue();
                if (username == null || usernames == null) {
                    continue;
                }
                if (!usernames.contains(username)) {
                    usernames.add(username);
                }
                for(String str: usernames) {
                    for(String str1: usernames) {
                        this.addEntry(tempCoRelatedUsers, str, str1);
                    }
                }
            }
        }
        logger.info("tempCoRelatedUsers:{}", tempCoRelatedUsers);
        /*Mixing co-related user properly end */
        if (relatedUsers == null) {
            relatedUsers = new HashMap<>();
        }
        /* Combining relatedUsers and coRelatedUsers */
        ArrayList<String> relatedUsersGroup;
        for (Map.Entry<String, ArrayList<String>> el: tempCoRelatedUsers.entrySet()) {
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
        logger.info("finalRelatedUses before duplicate removal:{}", relatedUsers);
        /* Combining relatedUsers and coRelatedUsers end */
        String key;
        ArrayList<String> value;
        /* Remove duplicate entry and create relatedUsers properly */
        HashMap<String, ArrayList<String>> finalRelatedUsers = new HashMap<>();
        for (Map.Entry<String, ArrayList<String>> el: relatedUsers.entrySet()) {
            key = el.getKey();
            value = el.getValue();
            if (key == null || value == null) {
                continue;
            }
            value.add(key);
            value = this.removeDuplicate(value);
            finalRelatedUsers.put(key, value);
        }
        /* Remove duplicate entry end */
        logger.info("finalRelatedUses after duplicate removal:{}", finalRelatedUsers);
        if (roles != null) {
            roles.setRelatedUsers(finalRelatedUsers);
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
        if (BridgeConstant.IS_LOGIN.equals(role)) {
            if (isLogin) {
                return BridgeConstant.TRUE;
            } else {
                return BridgeConstant.FALSE;
            }
        }
        if (BridgeConstant.TRUE.equals(role)) {
            return BridgeConstant.TRUE;
        }
        if (BridgeStaticService.isInValidString(userName)) {
            logger.info("Invalid userName:{}", userName);
            return null;
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
