package com.project.ftp.mysql;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.ftp.config.UserMethod;
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
                        + "where e.username like :name"),
        @NamedQuery(name = "MysqlUser.findByEmail",
                query = "select e from MysqlUser e "
                        + "where e.email like :email")
})

// it will automatically fire update query when ever MysqlUser changes

@JsonIgnoreProperties(ignoreUnknown = true)


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

    @Column(name = "create_password_otp")
    private String createPasswordOtp;

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
    // copy constructor
    public MysqlUser(MysqlUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.createPasswordOtp = user.getCreatePasswordOtp();
        this.passcode = user.getPasscode();
        this.name = user.getName();
        this.email = user.getEmail();
        this.mobile = user.getMobile();
        UserMethod userMethod = StaticService.getUserMethodValue(user.getMethod());
        if (userMethod != null) {
            this.method = userMethod.getUserMethod();
        }
        this.timestamp = user.getTimestamp();
        this.deleted = user.isDeleted();
        this.changePasswordCount = user.getChangePasswordCount();
    }
    public MysqlUser(ArrayList<String> arrayList) {
        if (arrayList != null) {
            if (arrayList.size() >= 1) {
                username = arrayList.get(0);
            }
            if (arrayList.size() >= 2) {
                password = arrayList.get(1);
            }
            if (arrayList.size() >= 3) {
                name = StaticService.decodeComma(arrayList.get(2));
            }
            if (arrayList.size() >= 4) {
                passcode = arrayList.get(3);
            }

            if (arrayList.size() >= 5) {
                mobile = arrayList.get(4);
            }

            if (arrayList.size() >= 6) {
                email = arrayList.get(5);
            }
            if (arrayList.size() >= 7) {
                createPasswordOtp = arrayList.get(6);
            }
            if (arrayList.size() >= 8) {
                changePasswordCount = StaticService.strToInt(arrayList.get(7));
            }
            if (arrayList.size() >= 9) {
                UserMethod userMethod = StaticService.getUserMethodValue(arrayList.get(8));
                if (userMethod != null) {
                    method = userMethod.getUserMethod();
                }
            }
            if (arrayList.size() >= 10) {
                timestamp = arrayList.get(9);
            }
            if (arrayList.size() >= 11) {
                deleted = "true".equals(arrayList.get(10));
            }
        }
    }
    public void incrementEntryCount() {
        changePasswordCount++;
    }

    public void truncateString() {
        int usernameLength = 255;
        int passwordMaxLength = 63;
        int mobileMaxLength = 15;
        int emailMaxLength = 255;
        int nameMaxLength = 255;
        int passcodeMaxLength = 15;
        int createPasswordOtpMaxLength = 15;
        int methodMaxLength = 255;
        int timestampMaxLength = 25;
        int maxValueOfChangePasswordCount = 32767;
        // No truncation for password and username
        mobile = StaticService.truncateString(mobile, mobileMaxLength);
        email = StaticService.truncateString(email, emailMaxLength);
        name = StaticService.truncateString(name, nameMaxLength);
        passcode = StaticService.truncateString(passcode, passcodeMaxLength);
        if (changePasswordCount > maxValueOfChangePasswordCount) {
            changePasswordCount = maxValueOfChangePasswordCount;
        }
        createPasswordOtp = StaticService.truncateString(createPasswordOtp, createPasswordOtpMaxLength);
        method = StaticService.truncateString(method, methodMaxLength);
        timestamp = StaticService.truncateString(timestamp, timestampMaxLength);
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

    public String getCreatePasswordOtp() {
        return createPasswordOtp;
    }

    public void setCreatePasswordOtp(String createPasswordOtp) {
        this.createPasswordOtp = createPasswordOtp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, mobile, email, name, passcode, createPasswordOtp,
                changePasswordCount, method, timestamp, deleted);
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
                ", createPasswordOtp='" + createPasswordOtp + '\'' +
                ", changePasswordCount=" + changePasswordCount +
                ", method='" + method + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
