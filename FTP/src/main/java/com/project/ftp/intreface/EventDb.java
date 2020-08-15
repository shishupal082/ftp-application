package com.project.ftp.intreface;

import com.project.ftp.mysql.DbDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventDb implements EventInterface {
    final static Logger logger = LoggerFactory.getLogger(EventDb.class);
    final DbDAO dbDAO;
    public EventDb(final DbDAO dbDAO) {
        this.dbDAO = dbDAO;
    }

    public void addText(String username, String apiName, String status, String reason, String comment) {
        dbDAO.insertEvent(username, apiName, status, reason, comment);
    }
}
