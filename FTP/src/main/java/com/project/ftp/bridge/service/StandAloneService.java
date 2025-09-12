package com.project.ftp.bridge.service;

import com.project.ftp.bridge.standalone.obj.ApiDetail;
import com.project.ftp.bridge.standalone.obj.Sequence;
import com.project.ftp.bridge.standalone.obj.StandAloneConfig;
import com.project.ftp.bridge.standalone.obj.StandAloneConfigObj;
import com.project.ftp.parser.YamlFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class StandAloneService {
    final static Logger logger = LoggerFactory.getLogger(StandAloneService.class);
    private final YamlFileParser yamlFileParser = new YamlFileParser();
    private boolean mergeStandAloneConfig(StandAloneConfig standAloneConfig,
                                       StandAloneConfigObj standAloneConfigObj2) {
        if (standAloneConfig == null) {
            return false;
        }
        if (standAloneConfigObj2 == null) {
            return false;
        }
        StandAloneConfig standAloneConfig2 = standAloneConfigObj2.getStandAloneConfig();
        if (standAloneConfig2 == null) {
            return false;
        }
        String currentSequence = standAloneConfig2.getCurrentSequence();
        HashMap<String, ApiDetail> apiDetailHashMap = standAloneConfig2.getApiList();
        HashMap<String, ArrayList<Sequence>> sequence = standAloneConfig2.getSequence();
        boolean isValid = false;
        if (currentSequence != null && !currentSequence.isEmpty()) {
            standAloneConfig.setCurrentSequence(currentSequence);
            isValid = true;
        }
        if (sequence != null) {
            standAloneConfig.setSequence(sequence);
            isValid = true;
        }
        if (apiDetailHashMap != null) {
            standAloneConfig.setApiList(apiDetailHashMap);
            isValid = true;
        }
        return isValid;
    }

    public StandAloneConfig getStandaloneConfig(ArrayList<String> standAloneConfigPathList) {
        if (standAloneConfigPathList == null) {
            return null;
        }
        StandAloneConfig standAloneConfig = new StandAloneConfig();
        StandAloneConfigObj standAloneConfigObjTemp;
        boolean isValidAtLeast1 = false;
        for (String str: standAloneConfigPathList) {
            standAloneConfigObjTemp = yamlFileParser.getStandAloneConfigObj(str);
            if (mergeStandAloneConfig(standAloneConfig, standAloneConfigObjTemp)) {
                isValidAtLeast1 = true;
            }
        }
        if (!isValidAtLeast1) {
            standAloneConfig = null;
        }
        return standAloneConfig;
    }
    public ArrayList<ApiDetail> getApiList(StandAloneConfig standAloneConfig) {
        if (standAloneConfig == null) {
            return null;
        }
        String currentSequence = standAloneConfig.getCurrentSequence();
        if (currentSequence == null || currentSequence.isEmpty()) {
            return null;
        }
        HashMap<String, ArrayList<Sequence>> sequence = standAloneConfig.getSequence();
        if (sequence == null) {
            return null;
        }
        HashMap<String, ApiDetail> apiList = standAloneConfig.getApiList();
        if (apiList == null) {
            return null;
        }
        ArrayList<Sequence> currentApiSequence = sequence.get(currentSequence);
        if (currentApiSequence == null) {
            logger.info("Current sequence: {}, not found in config.", currentSequence);
            return null;
        }
        ArrayList<ApiDetail> result = new ArrayList<>();
        String id;
        Boolean confirmationRequired;
        ApiDetail tempApiDetail;
        for (Sequence sequence1: currentApiSequence) {
            if (sequence1 == null) {
                continue;
            }
            id = sequence1.getId();
            confirmationRequired = sequence1.getConfirmationRequired();
            if (id == null || id.isEmpty()) {
                continue;
            }
            tempApiDetail = apiList.get(id);
            if (tempApiDetail == null) {
                continue;
            }
            if (confirmationRequired != null) {
                tempApiDetail.setConfirmationRequired(confirmationRequired);
            }
            result.add(tempApiDetail);
        }
        if (result.isEmpty()) {
            logger.info("Invalid standAloneConfig for currentSequence: {}, {}", currentSequence, standAloneConfig);
            return null;
        }
        return result;
    }
}
