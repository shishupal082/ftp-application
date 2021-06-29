package com.project.ftp.intreface;

import com.project.ftp.common.DateUtilities;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.parser.TextFileParser;
import com.project.ftp.service.FileService;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventFile implements EventInterface {
    private final static Logger logger = LoggerFactory.getLogger(EventFile.class);
    private final AppConfig appConfig;
    private final DateUtilities dateUtilities = new DateUtilities();
    private final FileService fileService;
    public EventFile(final AppConfig appConfig) {
        this.appConfig = appConfig;
        this.fileService = new FileService();
    }
    private String getEventDataFileName() {
        String configPath = appConfig.getFtpConfiguration().getConfigDataFilePath();
        String format = appConfig.getFtpConfiguration().getEventDataFilenamePattern();
        String filename = AppConstant.EVENT_DATA_FILENAME;
        if (format != null) {
            filename = dateUtilities.getDateStrFromPattern(format);
        }
        return configPath + filename;
    }
    public void addText(String username, String event, String status, String reason, String comment) {
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
        String eventDataFilepath = this.getEventDataFileName();
        fileService.createNewFile(eventDataFilepath);
        TextFileParser textFileParser = new TextFileParser(eventDataFilepath, false);
        textFileParser.addText(eventLog);
        logger.info("Event added: {}", eventLog);
    }

    public void addTextV2(String username, String event, String status, String reason, String comment) {
        this.addText(username, event, status, reason, comment);
    }
}
