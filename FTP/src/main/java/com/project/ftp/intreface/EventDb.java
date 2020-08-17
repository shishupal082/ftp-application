package com.project.ftp.intreface;

import com.project.ftp.mysql.DbDAO;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventDb implements EventInterface {
    final static Logger logger = LoggerFactory.getLogger(EventDb.class);
    final DbDAO dbDAO;
    final int usernameMaxLength = 255;
    final int eventMaxLength = 127;
    final int statusMaxLength = 63;
    final int reasonMaxLength = 255;
    final int commentMaxLength = 511;
    public EventDb(final DbDAO dbDAO) {
        this.dbDAO = dbDAO;
    }

    public void addText(String username, String event, String status, String reason, String comment) {
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
        username = StaticService.truncateString(username, usernameMaxLength);
        event = StaticService.truncateString(event, eventMaxLength);
        status = StaticService.truncateString(status, statusMaxLength);
        reason = StaticService.truncateString(reason, reasonMaxLength);
        comment = StaticService.truncateString(comment, commentMaxLength);
        dbDAO.insertEvent(username, event, status, reason, comment);
        String eventLog = "";
        eventLog += username;
        eventLog += "," + event;
        eventLog += "," + status;
        eventLog += "," + reason;
        eventLog += "," + comment;
        logger.info("Event added: {}", eventLog);
    }
}
