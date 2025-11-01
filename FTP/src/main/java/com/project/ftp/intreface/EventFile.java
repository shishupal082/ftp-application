package com.project.ftp.intreface;

import com.project.ftp.common.DateUtilities;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.config.FtpConfigItemsV2;
import com.project.ftp.obj.yamlObj.EventConfig;
import com.project.ftp.parser.TextFileParser;
import com.project.ftp.service.FileService;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class EventFile implements EventInterface {
    private final static Logger logger = LoggerFactory.getLogger(EventFile.class);
    private final AppConfig appConfig;
    private final DateUtilities dateUtilities = new DateUtilities();
    private final FileService fileService;
    public EventFile(final AppConfig appConfig) {
        this.appConfig = appConfig;
        this.fileService = new FileService();
    }
    private String getEventDataFileName(String configPath) {
        EventConfig eventConfig = appConfig.getFtpConfiguration().getEventConfig();
        String filename = AppConstant.EVENT_DATA_FILENAME;
        String format = null;
        boolean isStaticDir = false;
        if (eventConfig != null) {
            format = eventConfig.getEventDataFilenamePattern();
            isStaticDir = eventConfig.isStaticDir();
        }
        if (format != null) {
            filename = dateUtilities.getDateStrFromPattern(format, AppConstant.EmptyStr);
        }
        if (isStaticDir) {
            return filename;
        }
        return configPath + filename;
    }
    @Override
    public void addText(String configDataFilePath, String username, String event, String status, String reason, String comment) {
        String timestamp = StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat6);
        if (StaticService.isInValidString(username)) {
            username = null;
        }
        if (StaticService.isInValidString(event)) {
            event = null;
        }
        if (StaticService.isInValidString(status)) {
            status = null;
        }
        if (StaticService.isInValidString(reason)) {
            reason = null;
        }
        if (StaticService.isInValidString(comment)) {
            comment = null;
        }
        String eventLog = "";
        eventLog += StaticService.encodeComma(username);
        eventLog += "," + event;
        eventLog += "," + status;
        eventLog += "," + timestamp;
        eventLog += "," + StaticService.encodeComma(reason);
        eventLog += "," + StaticService.encodeComma(comment);
        String eventDataFilepath = this.getEventDataFileName(configDataFilePath);
        fileService.createNewFile(eventDataFilepath);
        TextFileParser textFileParser = new TextFileParser();
        textFileParser.writeTextData(eventDataFilepath, eventLog, false);
        logger.info("Event added: {}", eventLog);
    }
    @Override
    public void addTextV2(String configDataFilePath, String username, String event, String status, String reason, String comment) {
        this.addText(configDataFilePath, username, event, status, reason, comment);
    }
}
