package com.project.ftp.mysql;

import com.project.ftp.jdbc.MysqlConnection;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DbDAO extends AbstractDAO<MysqlUser> {
    final static Logger logger = LoggerFactory.getLogger(DbDAO.class);
    private final String FindAllUser;
    private final String FindByUsername;
    final SessionFactory sessionFactory;
    public DbDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
        FindAllUser = "MysqlUser.findAll";
        FindByUsername = "MysqlUser.findByUsername";
    }
    public List<MysqlUser> findAll() {
        List<MysqlUser> list = new ArrayList<>();
        try {
            list = (List<MysqlUser>) namedQuery(FindAllUser).getResultList();
            logger.info("result count: {}", list.size());
        } catch (Exception e) {
            logger.info("error in query: {}, {}", FindAllUser, e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    public List<MysqlUser> findUserByName(String name) {
        StringBuilder builder = new StringBuilder("%");
        builder.append(name).append("%");
        List<MysqlUser> list = new ArrayList<>();
        String queryName = FindByUsername;
        try {
            list = (List<MysqlUser>) namedQuery(queryName).setParameter("name", builder.toString()).getResultList();
            logger.info("result count: {}", list.size());
        } catch (Exception e) {
            logger.info("error in query: {}, {}", queryName, e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    public boolean updatePassword(DataSourceFactory dataSourceFactory,
                                  MysqlUser mysqlUser) {
        MysqlConnection mysqlConnection = new MysqlConnection(dataSourceFactory);
        String query = "UPDATE users SET password='"+mysqlUser.getPassword();
        query += "',method='change_password',change_password_count='"+
                mysqlUser.getChangePasswordCount()+
                "' WHERE username='"+mysqlUser.getUsername()+"';";
        return mysqlConnection.updateQuery(query);
    }
    public boolean setPassword(DataSourceFactory dataSourceFactory,
                               MysqlUser mysqlUser) {
        MysqlConnection mysqlConnection = new MysqlConnection(dataSourceFactory);
        String query = "UPDATE users SET password='"+mysqlUser.getPassword();
        query += "',method='register',name='"+ mysqlUser.getName()+
                "',passcode='',change_password_count=0"+
                " WHERE username='"+mysqlUser.getUsername()+"';";
        return mysqlConnection.updateQuery(query);
    }
    public void insertEvent(String username, String apiName, String status, String reason, String comment) {
        String query = "INSERT INTO event_data (username, api_name, status, reason, comment)" +
                " VALUES(:username,:api_name,:status,:reason,:comment)";
        try {
            sessionFactory.getCurrentSession()
                    .createSQLQuery(query)
                    .setParameter("username", username)
                    .setParameter("api_name", apiName)
                    .setParameter("status", status)
                    .setParameter("reason", reason)
                    .setParameter("comment", comment)
                    .executeUpdate();
        } catch (Exception e) {
            logger.info("error in query: {}, {}", query, e.getMessage());
            e.printStackTrace();
        }
    }
}
