package com.project.ftp.mysql;

import com.project.ftp.jdbc.MysqlConnection;
import com.project.ftp.obj.EventDBParameters;
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
    private final String FindByEmail;
    private final SessionFactory sessionFactory;
    private final MysqlConnection mysqlConnection;
    public DbDAO(final SessionFactory sessionFactory, final DataSourceFactory dataSourceFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
        this.mysqlConnection = new MysqlConnection(dataSourceFactory);
        FindAllUser = "MysqlUser.findAll";
        FindByUsername = "MysqlUser.findByUsername";
        FindByEmail = "MysqlUser.findByEmail";
    }
    public List<MysqlUser> findAll() {
        List<MysqlUser> list = new ArrayList<>();
        try {
            list = (List<MysqlUser>) namedQuery(FindAllUser).getResultList();
            logger.info("result count: {}", list.size());
        } catch (Exception e) {
            logger.info("error in query: findAll: {}, {}", FindAllUser, e.getMessage());
//            e.printStackTrace();
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
            logger.info("error in query: findUserByName: {}, {}", queryName, e.getMessage());
//            e.printStackTrace();
        }
        return list;
    }
    public List<MysqlUser> findUserByEmail(String email) {
        StringBuilder builder = new StringBuilder("%");
        builder.append(email).append("%");
        List<MysqlUser> list = new ArrayList<>();
        String queryName = FindByEmail;
        try {
            list = (List<MysqlUser>) namedQuery(queryName).setParameter("email", builder.toString()).getResultList();
            logger.info("result count: {}", list.size());
        } catch (Exception e) {
            logger.info("error in query: findUserByEmail: {}, {}", queryName, e.getMessage());
//            e.printStackTrace();
        }
        return list;
    }
//    public boolean updatePassword(DataSourceFactory dataSourceFactory,
//                                  MysqlUser mysqlUser) {
//        MysqlConnection mysqlConnection = new MysqlConnection(dataSourceFactory);
//        String query = "UPDATE users SET password='"+mysqlUser.getPassword();
//        query += "',method='change_password',change_password_count='"+
//                mysqlUser.getChangePasswordCount()+
//                "' WHERE username='"+mysqlUser.getUsername()+"';";
//        return mysqlConnection.updateQuery(query);
//    }
//    public boolean setPassword(DataSourceFactory dataSourceFactory,
//                               MysqlUser mysqlUser) {
//        MysqlConnection mysqlConnection = new MysqlConnection(dataSourceFactory);
//        String query = "UPDATE users SET password='"+mysqlUser.getPassword();
//        query += "',method='register',name='"+ mysqlUser.getName()+
//                "',passcode='',change_password_count=0"+
//                " WHERE username='"+mysqlUser.getUsername()+"';";
//        return mysqlConnection.updateQuery(query);
//    }
    public void insertEvent(EventDBParameters eventDBParameters) {
        String query = "INSERT INTO event_data (username, event, status, reason, comment)" +
                " VALUES(:username,:event,:status,:reason,:comment)";
        try {
            sessionFactory.getCurrentSession()
                    .createSQLQuery(query)
                    .setParameter("username", eventDBParameters.getUsername())
                    .setParameter("event", eventDBParameters.getEvent())
                    .setParameter("status", eventDBParameters.getStatus())
                    .setParameter("reason", eventDBParameters.getReason())
                    .setParameter("comment", eventDBParameters.getComment())
                    .executeUpdate();
        } catch (Exception e) {
            logger.info("error in query: insertEvent: {}, {}", query, e.getMessage());
//            e.printStackTrace();
        }
    }
    public void insertEventV2(EventDBParameters eventDBParameters) {
        String query = "INSERT INTO event_data (username, event, status, reason, comment)" +
                " VALUES(?,?,?,?,?)";
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(eventDBParameters.getUsername());
        parameters.add(eventDBParameters.getEvent());
        parameters.add(eventDBParameters.getStatus());
        parameters.add(eventDBParameters.getReason());
        parameters.add(eventDBParameters.getComment());
        try {
            mysqlConnection.updateQueryV2(query, parameters);
        } catch (Exception e) {
            logger.info("error in query: insertEventV2: {}, {}", query, e.getMessage());
//            e.printStackTrace();
        }
    }
}
