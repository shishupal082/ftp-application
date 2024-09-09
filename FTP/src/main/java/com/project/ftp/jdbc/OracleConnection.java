package com.project.ftp.jdbc;

import com.project.ftp.config.AppConstant;
import com.project.ftp.obj.yamlObj.OracleDatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

public class OracleConnection {
    private static final Logger logger = LoggerFactory.getLogger(OracleConnection.class);
    private final String driver;
    private final String url;
    private final String username;
    private final String password;
    private Connection con;
    public OracleConnection(OracleDatabaseConfig oracleDatabaseConfig) {
        if (oracleDatabaseConfig == null) {
            driver = null;
            url = null;
            username = null;
            password = null;
            return;
        }
        driver = oracleDatabaseConfig.getDriver();
        url = oracleDatabaseConfig.getUrl();
        username = oracleDatabaseConfig.getUsername();
        password = oracleDatabaseConfig.getPassword();
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
            logger.info("OracleConnection: MySQL connection closed");
        } catch (Exception e) {
            logger.info("OracleConnection: Error in closing connection: {}", e.toString());
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
            logger.info("OracleConnection: Connection success");
        } catch (Exception e) {
            logger.info("OracleConnection: Connection fail: {}", e.toString());
            e.printStackTrace();
        }
    }
    public ResultSet query(String query, ArrayList<String> parameters) {
        if (!this.isConnected()) {
            this.connect();
        }
        ResultSet rs = null;
        try {
            Statement stmt = con.createStatement();
            rs = stmt.executeQuery(query);
//            PreparedStatement preparedStatement = con.prepareStatement(query);
//            for(int i=0; i<parameters.size(); i++) {
//                preparedStatement.setString(i+1, "'"+parameters.get(i)+"'");
//            }
//            rs = preparedStatement.executeQuery();
        } catch (Exception e) {
            logger.info("OracleConnection: error in select query execution: {}, {}", query, e.toString());
            e.printStackTrace();
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
            logger.info("OracleConnection: error in update query execution: {}, {}", query, e.toString());
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
            logger.info("OracleConnection: error in updateQueryV2 execution: {}, parameter: {}, {}",
                    query, parameters, e.toString());
            jdbcQueryStatus = new JdbcQueryStatus(AppConstant.FAILURE);
            jdbcQueryStatus.setQuery(query);
            jdbcQueryStatus.setParameter(parameters);
            jdbcQueryStatus.setReason(e.toString());
            e.printStackTrace();
        }
        return jdbcQueryStatus;
    }
}
