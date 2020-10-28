package com.project.ftp.bridge.roles.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.project.ftp.bridge.roles.obj.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

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
}
