package com.project.ftp.jdbc;

import io.dropwizard.db.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

public class MysqlConnection {
    private static final Logger logger = LoggerFactory.getLogger(MysqlConnection.class);
    private final String driver;
    private final String url;
    private final String username;
    private final String password;
    private Connection con;
    public MysqlConnection(DataSourceFactory dataSourceFactory) {
        driver = dataSourceFactory.getDriverClass();
        url = dataSourceFactory.getUrl();
        username = dataSourceFactory.getUser();
        password = dataSourceFactory.getPassword();
    }
    private boolean isConnected() {
        try {
            if (con == null) {
                return false;
            }
            if (this.con.isClosed()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    public void close() {
        if (con == null) {
            return;
        }
        if (!this.isConnected()) {
            return;
        }
        try {
            con.close();
            logger.info("MysqlConnection: MySQL connection closed");
        } catch (Exception e) {
            logger.info("MysqlConnection: Error in closing connection");
            e.printStackTrace();
        }
    }
    public void connect() {
        if (this.isConnected()) {
            return;
        }
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);
            logger.info("MysqlConnection: Connection success");
        } catch (Exception e) {
            logger.info("MysqlConnection: Connection fail");
            e.printStackTrace();
        }
    }
    public ResultSet query(String query, ArrayList<String> parameters) {
        if (!this.isConnected()) {
            this.connect();
        }
        ResultSet rs = null;
        try {
//            Statement stmt = con.createStatement();
//            rs = stmt.executeQuery(query);
            PreparedStatement preparedStatement = con.prepareStatement(query);
            for(int i=0; i<parameters.size(); i++) {
                preparedStatement.setString(i+1, parameters.get(i));
            }
            rs = preparedStatement.executeQuery();
        } catch (Exception e) {
            logger.info("MysqlConnection: error in select query execution: {}", query);
//            e.printStackTrace();
        }
        return rs;
    }
    public boolean updateQuery(String query) {
        boolean status = false;
        this.connect();
        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
            status = true;
        } catch (Exception e) {
            logger.info("MysqlConnection: error in update query execution: {}", query);
            e.printStackTrace();
        }
        this.close();
        return status;
    }
    public boolean updateQueryV2(String query, ArrayList<String> parameters) {
        boolean status = false;
        if (!this.isConnected()) {
            this.connect();
        }
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            for(int i=0; i<parameters.size(); i++) {
                preparedStatement.setString(i+1, parameters.get(i));
            }
            preparedStatement.executeUpdate();
            status = true;
        } catch (Exception e) {
            logger.info("MysqlConnection: error in updateQueryV2 execution: {}, parameter: {}, {}",
                    query, parameters, e.toString());
        }
        return status;
    }
}
