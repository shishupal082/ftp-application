package com.project.ftp.bridge.roles.service;

import com.project.ftp.bridge.BridgeConstant;
import com.project.ftp.bridge.BridgeStaticService;
import com.project.ftp.bridge.config.BridgeConfig;
import com.project.ftp.bridge.roles.obj.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RolesService {
    final static Logger logger = LoggerFactory.getLogger(RolesService.class);
    private final BridgeConfig bridgeConfig;
    public RolesService(BridgeConfig bridgeConfig, ArrayList<String> rolesConfigPath) {
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
        result.sort(Collections.reverseOrder());
        return result;
    }
    public Roles getRolesConfigByConfigPath(ArrayList<String> rolesConfigPath) {
        RolesFileParser rolesFileParser = new RolesFileParser();
        Roles roles = rolesFileParser.getAllRolesFileData(rolesConfigPath);
        HashMap<String, ArrayList<String>> relatedUsers = null;
        HashMap<String, ArrayList<String>> rolesAccess = null;
        ArrayList<String> coRelatedUsers = null;
        if (roles != null) {
            rolesAccess = roles.getRoleAccess();
            relatedUsers = roles.getRelatedUsers();
            coRelatedUsers = roles.getCoRelatedUsers();
        }
        String username;
        ArrayList<String> usernames;
        /*Mixing co-related user properly */
        HashMap<String, ArrayList<String>> tempCoRelatedUsers = new HashMap<>();
        if (coRelatedUsers != null && rolesAccess != null) {
            ArrayList<String> groupUsers;
            for (String userGroupName: coRelatedUsers) {
                groupUsers = rolesAccess.get(userGroupName);
                if (groupUsers != null) {
                    for(String str: groupUsers) {
                        for(String str1: groupUsers) {
                            this.addEntry(tempCoRelatedUsers, str, str1);
                        }
                    }
                }
            }
        }
        logger.info("tempCoRelatedUsers: {}", tempCoRelatedUsers);
        /*Mixing co-related user properly end */
        if (relatedUsers == null) {
            relatedUsers = new HashMap<>();
        }
        /* Combining relatedUsers and coRelatedUsers */
        ArrayList<String> relatedUsersGroupNames;
        HashMap<String, ArrayList<String>> tempRelatedUsers = new HashMap<>();
        ArrayList<String> usernamesV2;
        if (rolesAccess != null) {
            for (Map.Entry<String, ArrayList<String>> el: relatedUsers.entrySet()) {
                usernamesV2 = new ArrayList<>();
                username = el.getKey();
                relatedUsersGroupNames = el.getValue();
                if (relatedUsersGroupNames != null) {
                    for (String groupName: relatedUsersGroupNames) {
                        usernames = rolesAccess.get(groupName);
                        if (usernames != null) {
                            usernamesV2.addAll(usernames);
                            usernamesV2.add(username);
                        }
                    }
                }
                if (usernamesV2.size() > 0) {
                    tempRelatedUsers.put(username, usernamesV2);
                }
            }
        }
        logger.info("tempRelatedUsers: {}", tempRelatedUsers);
        /* Combining tempRelatedUsers and tempCoRelatedUsers */
        for (Map.Entry<String, ArrayList<String>> el: tempRelatedUsers.entrySet()) {
            username = el.getKey();
            if (username == null) {
                continue;
            }
            usernames = el.getValue();
            if (usernames == null) {
                usernames = new ArrayList<>();
            }
            usernamesV2 = tempCoRelatedUsers.get(username);
            if (usernamesV2 == null) {
                usernamesV2 = new ArrayList<>();
            }
            for (String str: usernamesV2) {
                if (usernames.contains(str)) {
                    continue;
                }
                usernames.add(str);
            }
        }
        for (Map.Entry<String, ArrayList<String>> el: tempCoRelatedUsers.entrySet()) {
            username = el.getKey();
            if (username == null) {
                continue;
            }
            usernames = el.getValue();
            if (usernames == null || usernames.size() < 1) {
                continue;
            }
            usernamesV2 = tempRelatedUsers.get(username);
            if (usernamesV2 == null) {
                usernamesV2 = new ArrayList<>();
            }
            for (String str: usernames) {
                if (usernamesV2.contains(str)) {
                    continue;
                }
                usernamesV2.add(str);
            }
            tempRelatedUsers.put(username, usernamesV2);
        }
        logger.info("relatedUsers after merging: {}", tempRelatedUsers);
        HashMap<String, ArrayList<String>> finalRelatedUsers = new HashMap<>();
        for (Map.Entry<String, ArrayList<String>> el: tempRelatedUsers.entrySet()) {
            finalRelatedUsers.put(el.getKey(), this.removeDuplicate(el.getValue()));
        }
        logger.info("finalRelatedUsers after duplicate removal: {}", finalRelatedUsers);
        if (roles != null) {
            roles.setRelatedUsers(finalRelatedUsers);
        }
        logger.info("roles config data: {}", roles);
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
    public HashMap<String, ArrayList<String>> getAllRelatedUsers() {
        Roles roles = this.getRolesConfig();
        if (roles == null) {
            return null;
        }
        return roles.getRelatedUsers();
    }
    public ArrayList<String> getAllRelatedUsersName() {
        Roles roles = this.getRolesConfig();
        if (roles == null) {
            return null;
        }
        HashMap<String, ArrayList<String>> allRelatedUsers = roles.getRelatedUsers();
        if (allRelatedUsers == null) {
            return null;
        }
        ArrayList<String> result = new ArrayList<>();
        for(Map.Entry<String, ArrayList<String>> entry: allRelatedUsers.entrySet()) {
            if (entry.getKey() != null) {
                result.add(entry.getKey());
            }
            if (entry.getValue() != null) {
                result.addAll(entry.getValue());
            }
        }
        result = this.removeDuplicate(result);
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
        ArrayList<String> finalResult = new ArrayList<>();
        ArrayList<String> tempResult = relatedUsers.get(username);
        if (tempResult != null) {
            finalResult.addAll(tempResult);
        }
        return finalResult;
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
