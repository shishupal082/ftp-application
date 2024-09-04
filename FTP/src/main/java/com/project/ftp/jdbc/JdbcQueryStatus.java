package com.project.ftp.jdbc;

import com.project.ftp.config.AppConstant;

import java.util.ArrayList;

public class JdbcQueryStatus {
    private String status;
    private String query;
    private ArrayList<String> parameter;
    private String reason;
    public JdbcQueryStatus(String status) {
        if (status == null) {
            status = AppConstant.FAILURE;
        }
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ArrayList<String> getParameter() {
        return parameter;
    }

    public void setParameter(ArrayList<String> parameter) {
        this.parameter = parameter;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "JdbcQueryStatus{" +
                "status='" + status + '\'' +
                ", query='" + query + '\'' +
                ", parameter=" + parameter +
                ", reason='" + reason + '\'' +
                '}';
    }
}
