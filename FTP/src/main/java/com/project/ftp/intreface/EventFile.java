package com.project.ftp.intreface;

import com.project.ftp.common.DateUtilities;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.obj.BackendConfig;
import com.project.ftp.parser.TextFileParser;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventFile implements EventInterface {
    private final static Logger logger = LoggerFactory.getLogger(EventFile.class);
    private final AppConfig appConfig;
    private final DateUtilities dateUtilities = new DateUtilities();
    public EventFile(final AppConfig appConfig) {
        this.appConfig = appConfig;
    }
    private String getEventDataFileName() {
        String configPath = appConfig.getFtpConfiguration().getConfigDataFilePath();
        BackendConfig backendConfig = appConfig.getFtpConfiguration().getBackendConfig();
        String filename = AppConstant.EVENT_DATA_FILENAME;
        if (backendConfig != null) {
            String format = backendConfig.getEventDataFilenamePattern();
            if (format != null) {
                filename = dateUtilities.getDateStrFromPattern(format);
            }
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
        TextFileParser textFileParser = new TextFileParser(this.getEventDataFileName(), false, true);
        textFileParser.addText(eventLog);
        logger.info("Event added: {}", eventLog);
    }

    public void addTextV2(String username, String event, String status, String reason, String comment) {
        this.addText(username, event, status, reason, comment);
    }
}
