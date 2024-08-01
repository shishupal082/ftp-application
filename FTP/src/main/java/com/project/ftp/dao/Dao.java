package com.project.ftp.dao;

import java.util.ArrayList;

public interface Dao<T> {
    T getById(long id);
    ArrayList<T> getAll();
    void add(T t);
    void addAll(ArrayList<T> ts);
    void updateById(T t);
    T findByData(T t);
}
