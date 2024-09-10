package com.project.ftp.bridge.mysqlTable;

import com.project.ftp.jdbc.JdbcQueryStatus;
import com.project.ftp.obj.yamlObj.TableConfiguration;

import java.util.ArrayList;
import java.util.HashMap;

public interface TableDb {

    ArrayList<HashMap<String, String>> getByMultipleParameter(TableConfiguration tableConfiguration,
                                                                     HashMap<String, ArrayList<String>> requestFilterParameter,
                                                                     boolean logQuery);
    ArrayList<HashMap<String, String>> getAll(TableConfiguration tableConfiguration);
    JdbcQueryStatus updateTableEntry(TableConfiguration tableConfiguration, HashMap<String, String> data,
                                     HashMap<String, ArrayList<String>> requestFilterParameter);
    JdbcQueryStatus addTableEntry(TableConfiguration tableConfiguration, HashMap<String, String> data);
    JdbcQueryStatus addEntry(TableConfiguration tableConfiguration, HashMap<String, String> data, Integer entryCount0);
    ArrayList<HashMap<String, String>> searchData(TableConfiguration tableConfiguration, HashMap<String, String> data);
    int getEntryCount(TableConfiguration tableConfiguration, HashMap<String, String> data);
    JdbcQueryStatus updateEntry(TableConfiguration tableConfiguration, HashMap<String, String> data, Integer entryCount0);
    void closeIfOracle(TableConfiguration tableConfiguration);
}
