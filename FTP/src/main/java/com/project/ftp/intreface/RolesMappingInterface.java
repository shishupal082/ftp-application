package com.project.ftp.intreface;

import com.project.ftp.bridge.obj.yamlObj.SkipRowCriteria;
import com.roles_mapping.RolesMappingApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class RolesMappingInterface {
    private final static Logger logger = LoggerFactory.getLogger(RolesMappingInterface.class);
    private final static RolesMappingApp rolesMappingApp = new RolesMappingApp();
    public RolesMappingInterface() {}

    public static Boolean isValidCondition(String cellData, SkipRowCriteria skipRowCriteria) {
        if (skipRowCriteria == null) {
            return null;
        }
        Boolean isEmpty = skipRowCriteria.getIs_empty();
        String regex = skipRowCriteria.getRegex();
        ArrayList<String> range = skipRowCriteria.getRange();
        ArrayList<String> notInRange = skipRowCriteria.getNotInRange();
        return rolesMappingApp.isValidCondition(cellData,range,notInRange,isEmpty,regex);
    }
    public static boolean isPatternMatching(String str, String pattern, boolean exactMatch) {
        return rolesMappingApp.isPatternMatching(str, pattern, exactMatch);
    }
}
