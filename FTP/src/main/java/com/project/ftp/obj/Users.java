package com.project.ftp.obj;

import com.project.ftp.mysql.MysqlUser;
import com.project.ftp.service.StaticService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Users {
    private HashMap<String, MysqlUser> userHashMap;
    private Integer userCount = 0;
    public Users(List<MysqlUser> userList) {
        if (userList != null) {
            userHashMap = new HashMap<>();
            for(MysqlUser mysqlUser: userList) {
                userHashMap.put(mysqlUser.getUsername(), mysqlUser);
                userCount++;
            }
        }
    }
    // maskPassword
    public Users(HashMap<String, MysqlUser> userHashMap) {
        if (userHashMap != null) {
            this.userHashMap = new HashMap<>();
            this.userCount = 0;
            MysqlUser mysqlUser;
            for(Map.Entry<String, MysqlUser> data: userHashMap.entrySet()) {
                mysqlUser = new MysqlUser(data.getValue());
                mysqlUser.setPassword("*****");
                this.userHashMap.put(data.getKey(), mysqlUser);
                this.userCount++;
            }
        }
    }
    public Users(ArrayList<ArrayList<String>> filedata) {
        if (filedata != null) {
            userHashMap = new HashMap<>();
            MysqlUser user;
            for(int i=filedata.size()-1; i>=0; i--) {
                user = new MysqlUser(filedata.get(i));
                if (StaticService.isValidString(user.getUsername())) {
                    if (userHashMap.get(user.getUsername()) == null) {
                        userHashMap.put(user.getUsername(), user);
                        userCount++;
                    }
                }
            }
        }
    }
    public HashMap<String, MysqlUser> getUserHashMap() {
        return userHashMap;
    }

    public void setUserHashMap(HashMap<String, MysqlUser> userHashMap) {
        this.userHashMap = userHashMap;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public MysqlUser searchUserByName(String username) {
        if (username == null) {
            return null;
        }
        if (userHashMap != null) {
            return userHashMap.get(username);
        }
        return null;
    }
    @Override
    public String toString() {
        return "Users{" +
                "userHashMap=" + userHashMap +
                ", userCount=" + userCount +
                '}';
    }
}
