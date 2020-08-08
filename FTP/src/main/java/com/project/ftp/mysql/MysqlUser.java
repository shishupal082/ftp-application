package com.project.ftp.mysql;


import com.project.ftp.config.AppConstant;
import com.project.ftp.service.StaticService;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

@Entity // Pojo is mapped to table
@Table(name="users")
@NamedQueries({
        @NamedQuery(name = "MysqlUser.findAll",
                query = "select e from MysqlUser e"),
        @NamedQuery(name = "MysqlUser.findByUsername",
                query = "select e from MysqlUser e "
                        + "where e.username like :name")
})



public class MysqlUser implements Serializable {
    /**
     * Entity's unique identifier.
     */
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "passcode")
    private String passcode;

    @Column(name = "change_password_count")
    private int changePasswordCount;

    @Column(name = "method")
    private String method;

    @Column(name = "timestamp")
    private String timestamp;

    @Column(name = "deleted")
    private boolean deleted;
    // Default constructor required for hibernate
    public MysqlUser() {}
    public MysqlUser(ArrayList<String> arrayList) {
        if (arrayList != null) {
            if (arrayList.size() >= 1) {
                changePasswordCount = 1;
                username = arrayList.get(0);
            }
            if (arrayList.size() >= 2) {
                password = arrayList.get(1);
            }
            if (arrayList.size() >= 3) {
                name = arrayList.get(2);
            }
            if (arrayList.size() >= 4) {
                passcode = arrayList.get(3);
            }
            if (arrayList.size() >= 5) {
                method = arrayList.get(4);
            }
            if (arrayList.size() >= 6) {
                timestamp = arrayList.get(5);
            }
        }
    }
    public MysqlUser incrementEntryCount() {
        changePasswordCount++;
        return this;
    }
    public String getAddTextResponse() {
        String text = "";
        if (username != null) {
            text += username + ",";
        } else {
            text += ",";
        }
        if (password != null) {
            text += StaticService.replaceComma(password) +",";
        } else {
            text += ",";
        }
        if (name != null) {
            text += StaticService.replaceComma(name) +",";
        } else {
            text += ",";
        }
        text += ","; // for passcode
        if (method != null) {
            text += method +",";
        } else {
            text += ",";
        }
        text += StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat6) + ",";
        return text;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getChangePasswordCount() {
        return changePasswordCount;
    }

    public void setChangePasswordCount(int changePasswordCount) {
        this.changePasswordCount = changePasswordCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, mobile, email, name, passcode, changePasswordCount, method, timestamp, deleted);
    }
    @Override
    public String toString() {
        return "MysqlUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + "*****" + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", passcode='" + passcode + '\'' +
                ", changePasswordCount='" + changePasswordCount + '\'' +
                ", method='" + method + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
