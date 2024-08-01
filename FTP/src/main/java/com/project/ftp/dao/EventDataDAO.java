package com.project.ftp.dao;

import com.project.ftp.obj.EventDBParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class EventDataDAO implements Dao<EventDBParameters> {
    private final static Logger logger = LoggerFactory.getLogger(EventDataDAO.class);
    private final ArrayList<EventDBParameters> eventDBParameterList = new ArrayList<>();
    public EventDataDAO() {}

    @Override
    public EventDBParameters getById(long id) {
        return null;
    }
    @Override
    public ArrayList<EventDBParameters> getAll() {
        return eventDBParameterList;
    }
    @Override
    public void add(EventDBParameters dbParameter) {
        if (dbParameter != null) {
            eventDBParameterList.add(dbParameter);
        }
    }

    @Override
    public void addAll(ArrayList<EventDBParameters> filepathDBParameters) {
        if (filepathDBParameters != null) {
            eventDBParameterList.addAll(filepathDBParameters);
        }
    }

    @Override
    public void updateById(EventDBParameters dbParameters) {
        return;
    }

    @Override
    public EventDBParameters findByData(EventDBParameters eventDBParameters) {
        return null;
    }
}
