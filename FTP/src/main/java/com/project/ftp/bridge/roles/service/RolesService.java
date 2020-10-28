package com.project.ftp.bridge.roles.service;

import com.project.ftp.bridge.BridgeConstant;
import com.project.ftp.bridge.BridgeStaticService;
import com.project.ftp.bridge.config.BridgeConfig;
import com.project.ftp.bridge.roles.obj.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class RolesService {
    final static Logger logger = LoggerFactory.getLogger(RolesService.class);
    private final BridgeConfig bridgeConfig;
    public RolesService(BridgeConfig bridgeConfig, String rolesConfigPath) {
        this.bridgeConfig = bridgeConfig;
        this.bridgeConfig.setRoles(this.getRolesConfigByConfigPath(rolesConfigPath));
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
            return roles.getApiRolesMapping();
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
    public boolean isApiAuthorised(String apiName, String userName) {
        logger.info("isApiAuthorised check request:{},{}", apiName, userName);
        if (BridgeStaticService.isInValidString(apiName)) {
            logger.info("Invalid apiName:{}", apiName);
            return false;
        }
        if (BridgeStaticService.isInValidString(userName)) {
            logger.info("Invalid userName:{}", userName);
            return false;
        }
        String apiRoles = this.getRolesByApiName(apiName);
        boolean result = this.apiRolesIncludeUser(apiRoles, userName);
        logger.info("isApiAuthorised check response:{}", result);
        return result;
    }
    private String getBooleanEquivalentToRole(String role, String userName) {
        if (BridgeStaticService.isInValidString(role)) {
            logger.info("Invalid role:{}", role);
            return null;
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
    private boolean apiRolesIncludeUser(String apiRoles, String userName) {
        if (BridgeStaticService.isInValidString(apiRoles)) {
            logger.info("Invalid apiRoles:{}", apiRoles);
            return false;
        }
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        ArrayList<String> tokens = evaluator.tokenize(apiRoles);
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
            tokens.set(i, this.getBooleanEquivalentToRole(token, userName));
        }
        Boolean finalResult = evaluator.evaluateBinaryExpression(String.join("", tokens));
        if (finalResult == null) {
            logger.info("Invalid finalResult:{}", finalResult);
            return false;
        }
        return finalResult;
    }
}
