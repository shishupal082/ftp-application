package com.project.ftp.bridge.roles.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.project.ftp.bridge.roles.obj.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RolesFileParser {
    final static Logger logger = LoggerFactory.getLogger(RolesFileParser.class);
    public RolesFileParser() {}
    public Roles getRolesFileData(String rolesFilePath) {
        if (rolesFilePath == null || rolesFilePath.isEmpty()) {
            logger.info("Roles file path is invalid : {}", rolesFilePath);
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        Roles roles = null;
        try {
            roles = objectMapper.readValue(new File(rolesFilePath), Roles.class);
        } catch (IOException ioe) {
            logger.info("IOE : for file : {}", rolesFilePath);
        }
        return roles;
    }
    private void updateRoleAccess(Roles preFinalRoles, Roles tempRoles) {
        if (preFinalRoles == null || preFinalRoles.getRoleAccess() == null) {
            logger.info("preFinalRoles or its roleAccess in null");
            return;
        }
        HashMap<String, ArrayList<String>> roleAccess1 = preFinalRoles.getRoleAccess();
        HashMap<String, ArrayList<String>> roleAccess2 = null;
        if (tempRoles != null) {
            roleAccess2 = tempRoles.getRoleAccess();
        }
        if (roleAccess2 == null) {
            return;
        }
        /*finding duplicate roleAccessGroup entry*/
        String key;
        ArrayList<String> value, temp;
        for (Map.Entry<String, ArrayList<String>> entry: roleAccess2.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            if (key == null || value == null) {
                continue;
            }
            temp = roleAccess1.get(key);
            if (temp != null) {
                logger.info("ERROR: '{}' roleAccessGroup, is found in multiple roles file", key);
                logger.info("preFinalRoles: {}", preFinalRoles);
                logger.info("tempRoles: {}", tempRoles);
                preFinalRoles.setRoleAccess(null);
                return;
            }
            roleAccess1.put(key, value);
        }
        /*end finding duplicate roleAccessGroup entry*/
        /*finding duplicate username entry in roleAccessGroup*/
        temp = new ArrayList<>();
        for (Map.Entry<String, ArrayList<String>> entry: roleAccess1.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            if (value != null) {
                for (String username: value) {
                    if (temp.contains(username)) {
                        logger.info("username: '{}', is also found in rollAccessGroup: {}", username, key);
                    }
                    temp.add(username);
                }
            }
        }
        /*end finding duplicate username entry in roleAccessGroup*/
    }
    private void updateRelatedUsers(Roles preFinalRoles, Roles tempRoles) {
        if (preFinalRoles == null || preFinalRoles.getRelatedUsers() == null) {
            logger.info("preFinalRoles or its relatedUsers in null");
            return;
        }
        HashMap<String, ArrayList<String>> relatedUsers1 = preFinalRoles.getRelatedUsers();
        HashMap<String, ArrayList<String>> relatedUsers2 = null;
        if (tempRoles != null) {
            relatedUsers2 = tempRoles.getRelatedUsers();
        }
        if (relatedUsers2 == null) {
            return;
        }
        String key;
        ArrayList<String> value, temp;
        for(Map.Entry<String, ArrayList<String>> entry: relatedUsers2.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            if (key == null || value == null) {
                continue;
            }
            temp = relatedUsers1.get(key);
            if (temp != null) {
                logger.info("ERROR: Username: {}, duplicate relatedUsers entry", key);
                logger.info("relatedUsers1: {}, relatedUsers2: {}", relatedUsers1, relatedUsers2);
                preFinalRoles.setRelatedUsers(null);
                return;
            }
            relatedUsers1.put(key, value);
        }
    }
    private void updateRoleAccessMapping(Roles preFinalRoles, Roles tempRoles) {
        if (preFinalRoles == null || preFinalRoles.getRoleAccessMapping() == null) {
            logger.info("preFinalRoles or its roleAccessMapping in null");
            return;
        }
        HashMap<String, String> roleAccessMapping1 = preFinalRoles.getRoleAccessMapping();
        HashMap<String, String> roleAccessMapping2 = null;
        if (tempRoles != null) {
            roleAccessMapping2 = tempRoles.getRoleAccessMapping();
        }
        if (roleAccessMapping2 == null) {
            return;
        }
        String key, value, temp;
        for(Map.Entry<String, String> entry: roleAccessMapping2.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            if (key == null || value == null) {
                continue;
            }
            temp = roleAccessMapping1.get(key);
            if (temp == null) {
                roleAccessMapping1.put(key, value);
                continue;
            }
            temp = "("+temp+"|"+value+")";
            logger.info("Merged rollAccessMapping: {}", temp);
            roleAccessMapping1.put(key, temp);
        }
    }
    private void updateCoRelatedUsers(Roles preFinalRoles, Roles tempRoles) {
        if (preFinalRoles == null || preFinalRoles.getCoRelatedUsers() == null) {
            logger.info("preFinalRoles or its coRelatedUsers in null");
            return;
        }
        ArrayList<String> coRelatedUsers1 = preFinalRoles.getCoRelatedUsers();
        ArrayList<String> coRelatedUsers2 = null;
        if (tempRoles != null) {
            coRelatedUsers2 = tempRoles.getCoRelatedUsers();
        }
        if (coRelatedUsers2 == null) {
            return;
        }
        for (String userGroup: coRelatedUsers2) {
            if (userGroup != null && !coRelatedUsers1.contains(userGroup)) {
                coRelatedUsers1.add(userGroup);
            }
        }
    }
    public Roles getAllRolesFileData(ArrayList<String> rolesFilePath) {
        if (rolesFilePath == null) {
            return null;
        }
        ArrayList<Roles> roleList = new ArrayList<>();
        Roles tempRole;
        for (String filepath: rolesFilePath) {
            tempRole = this.getRolesFileData(filepath);
            if (tempRole == null) {
                continue;
            }
            roleList.add(tempRole);
        }
        Roles preFinalRole = null;
        if (roleList.size() > 0) {
            preFinalRole = new Roles(true);
            for (Roles roles1: roleList) {
                this.updateRoleAccess(preFinalRole, roles1);
                this.updateRoleAccessMapping(preFinalRole, roles1);
                this.updateCoRelatedUsers(preFinalRole, roles1);
                this.updateRelatedUsers(preFinalRole, roles1);
            }
        }
        if (preFinalRole == null || preFinalRole.getRoleAccess() == null ||
                preFinalRole.getRelatedUsers() == null ||
                preFinalRole.getCoRelatedUsers() == null ||
                preFinalRole.getRoleAccessMapping() == null) {
            logger.info("ERROR found in roles file merging.");
            return null;
        }
        return preFinalRole;
    }
}
