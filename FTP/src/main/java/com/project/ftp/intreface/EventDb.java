package com.project.ftp.intreface;

import com.project.ftp.event.EventDBParameters;
import com.project.ftp.mysql.DbDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventDb implements EventInterface {
    private final static Logger logger = LoggerFactory.getLogger(EventDb.class);
    private final DbDAO dbDAO;
    public EventDb(final DbDAO dbDAO) {
        this.dbDAO = dbDAO;
    }

    public void addText(String username, String event, String status, String reason, String comment) {
        EventDBParameters eventDBParameters = new EventDBParameters(username, event, status, reason, comment);
        dbDAO.insertEvent(eventDBParameters);
        logger.info("Event added: {}", eventDBParameters);
    }

    public void addTextV2(String username, String event, String status, String reason, String comment) {
        EventDBParameters eventDBParameters = new EventDBParameters(username, event, status, reason, comment);
        dbDAO.insertEventV2(eventDBParameters);
        logger.info("Event added: {}", eventDBParameters);
    }
}
