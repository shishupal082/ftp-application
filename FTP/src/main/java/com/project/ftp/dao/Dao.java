package com.project.ftp.dao;

import java.util.ArrayList;

public interface Dao<T> {
    T getById(long id);
    ArrayList<T> getAll();
    void save(T t);
    void update();
    void updateById(T t);
}
