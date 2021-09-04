package com.project.ftp.obj;

import com.project.ftp.service.StaticService;

import java.util.ArrayList;

public class TableRowResponse {
    private String sNo;
    private String entryTime;
    private String addedBy;
    private String tableName;
    private String tableUniqueId;
    private String uiEntryTime;
    private String text;

    public TableRowResponse(ArrayList<String> row) {
        if (row == null) {
            return;
        }
        if (row.size() > 0) {
            sNo = row.get(0);
        }
        if (row.size() > 1) {
            entryTime = row.get(1);
        }
        if (row.size() > 2) {
            addedBy = row.get(2);
        }
        if (row.size() > 3) {
            tableName = row.get(3);
        }
        if (row.size() > 4) {
            tableUniqueId = row.get(4);
        }
        if (row.size() > 5) {
            uiEntryTime = row.get(5);
        }
        StringBuilder temp = new StringBuilder();
        if (row.size() > 6) {
            for(int i=6; i<row.size(); i++) {
                if (i==6) {
                    temp = new StringBuilder(row.get(i));
                    continue;
                }
                temp.append(",").append(row.get(i));
            }
        }
        text = temp.toString();
    }
    public boolean isValid() {
        return StaticService.isValidString(sNo);
    }
    public String getsNo() {
        return sNo;
    }

    public void setsNo(String sNo) {
        this.sNo = sNo;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableUniqueId() {
        return tableUniqueId;
    }

    public void setTableUniqueId(String tableUniqueId) {
        this.tableUniqueId = tableUniqueId;
    }

    public String getUiEntryTime() {
        return uiEntryTime;
    }

    public void setUiEntryTime(String uiEntryTime) {
        this.uiEntryTime = uiEntryTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "TableRowResponse{" +
                "sNo='" + sNo + '\'' +
                ", entryTime='" + entryTime + '\'' +
                ", addedBy='" + addedBy + '\'' +
                ", tableName='" + tableName + '\'' +
                ", tableUniqueId='" + tableUniqueId + '\'' +
                ", uiEntryTime='" + uiEntryTime + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
