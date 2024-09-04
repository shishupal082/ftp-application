package com.project.ftp.jdbc;

import com.project.ftp.config.AppConstant;
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
            logger.info("MysqlConnection: Error in closing connection: {}", e.toString());
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
            logger.info("MysqlConnection: Connection fail: {}", e.toString());
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
            logger.info("MysqlConnection: error in update query execution: {}, {}", query, e.toString());
        }
        this.close();
        return status;
    }
    public JdbcQueryStatus updateQueryV2(String query, ArrayList<String> parameters) {
        if (!this.isConnected()) {
            this.connect();
        }
        JdbcQueryStatus jdbcQueryStatus;
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            for(int i=0; i<parameters.size(); i++) {
                preparedStatement.setString(i+1, parameters.get(i));
            }
            preparedStatement.executeUpdate();
            jdbcQueryStatus = new JdbcQueryStatus(AppConstant.SUCCESS);
        } catch (Exception e) {
            logger.info("MysqlConnection: error in updateQueryV2 execution: {}, parameter: {}, {}",
                    query, parameters, e.toString());
            jdbcQueryStatus = new JdbcQueryStatus(AppConstant.FAILURE);
            jdbcQueryStatus.setQuery(query);
            jdbcQueryStatus.setParameter(parameters);
            jdbcQueryStatus.setReason(e.toString());
        }
        return jdbcQueryStatus;
    }
}
