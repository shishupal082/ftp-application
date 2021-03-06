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
    public void close() {
        if (con == null) {
            logger.info("MySQL connection not found");
            return;
        }
        try {
            con.close();
            logger.info("MySQL connection closed");
        } catch (Exception e) {
            logger.info("Error in closing connection");
            e.printStackTrace();
        }
    }
    public void Connect() {
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);
            logger.info("Connection success");
        } catch (Exception e) {
            logger.info("Connection fail");
            e.printStackTrace();
        }
    }
    public ResultSet query(String query) {
        ResultSet rs = null;
        try {
            Statement stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            logger.info("query executed");
        } catch (Exception e) {
            logger.info("error in query execution: {}", query);
            e.printStackTrace();
        }
        return rs;
    }
    public boolean updateQuery(String query) {
        boolean status = false;
        this.Connect();
        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
            status = true;
            logger.info("query executed");
        } catch (Exception e) {
            logger.info("error in query execution: {}", query);
            e.printStackTrace();
        }
        this.close();
        return status;
    }
    public boolean updateQueryV2(String query, ArrayList<String> parameters) {
        boolean status = false;
        this.Connect();
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            for(int i=0; i<parameters.size(); i++) {
                preparedStatement.setString(i+1, parameters.get(i));
            }
            preparedStatement.executeUpdate();
            status = true;
            logger.info("query executed");
        } catch (Exception e) {
            logger.info("error in query execution: {}", query);
            e.printStackTrace();
        }
        this.close();
        return status;
    }
}
