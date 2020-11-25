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
    public Roles getRolesConfigByConfigPath(String rolesConfigPath) {
        RolesFileParser rolesFileParser = new RolesFileParser();
        Roles roles = rolesFileParser.getRolesFileData(rolesConfigPath);
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
