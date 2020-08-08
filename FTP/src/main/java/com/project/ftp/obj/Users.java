package com.project.ftp.obj;

import com.project.ftp.mysql.MysqlUser;

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
    public Users(ArrayList<ArrayList<String>> filedata) {
        if (filedata != null) {
            userHashMap = new HashMap<>();
            MysqlUser user, updatedUser;
            for(int i=filedata.size()-1; i>=0; i--) {
                user = new MysqlUser(filedata.get(i));
                if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                    updatedUser = userHashMap.get(user.getUsername());
                    if (updatedUser == null) {
                        userHashMap.put(user.getUsername(), user);
                        userCount++;
                    } else {
                        userHashMap.put(user.getUsername(), updatedUser.incrementEntryCount());
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
    public void maskPassword() {
        if (userHashMap != null) {
            for(Map.Entry<String, MysqlUser> data: userHashMap.entrySet()) {
                data.getValue().setPassword("*****");
            }
        }
    }

    @Override
    public String toString() {
        return "Users{" +
                "userHashMap=" + userHashMap +
                ", userCount=" + userCount +
                '}';
    }
}
