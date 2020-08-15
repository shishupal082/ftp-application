package com.project.ftp.intreface;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.parser.TextFileParser;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventFile implements EventInterface {
    final static Logger logger = LoggerFactory.getLogger(EventFile.class);
    final String eventDataFileName;
    public EventFile(final AppConfig appConfig) {
        this.eventDataFileName = appConfig.getFtpConfiguration().getConfigDataFilePath() + AppConstant.EVENT_DATA_FILENAME;
    }
    public void addText(String username, String apiName, String status, String reason, String comment) {
        String timestamp = StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat6);
        if (StaticService.isInValidString(username)) {
            username = null;
        }
        if (StaticService.isInValidString(apiName)) {
            apiName = null;
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
        eventLog += username;
        eventLog += "," + apiName;
        eventLog += "," + status;
        eventLog += "," + timestamp;
        eventLog += "," + StaticService.encodeComma(reason);
        eventLog += "," + StaticService.encodeComma(comment);
        TextFileParser textFileParser = new TextFileParser(eventDataFileName);
        textFileParser.addText(eventLog);
        logger.info("Event added: {}", eventLog);
    }
}
