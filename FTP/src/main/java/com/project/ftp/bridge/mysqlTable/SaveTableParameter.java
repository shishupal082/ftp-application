package com.project.ftp.bridge.mysqlTable;

import com.project.ftp.obj.yamlObj.TableConfiguration;

import java.util.ArrayList;

public class SaveTableParameter {
    private final TableConfiguration tableConfiguration;
    private String startedTime;
    private boolean updateIfFound;
    private boolean maintainHistory;
    private ArrayList<String> maintainHistoryExcludedColumn;
    private int index;
    private int size;
    private int addEntryCount;
    private int updateEntryCount;
    private int skipEntryCount;
    private int addEntryErrorCount;
    private int updateEntryErrorCount;
    private int searchErrorCount;

    public SaveTableParameter(TableConfiguration tableConfiguration) {
        this.tableConfiguration = tableConfiguration;
        updateIfFound = false;
        maintainHistory = false;
        index = 1;
        size = 0;
        addEntryCount = 0;
        updateEntryCount = 0;
        skipEntryCount = 0;
        addEntryErrorCount = 0;
        updateEntryErrorCount = 0;
        searchErrorCount = 0;
    }

    public TableConfiguration getTableConfiguration() {
        return tableConfiguration;
    }

    public String getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(String startedTime) {
        this.startedTime = startedTime;
    }

    public boolean isUpdateIfFound() {
        return updateIfFound;
    }

    public void setUpdateIfFound(boolean updateIfFound) {
        this.updateIfFound = updateIfFound;
    }

    public boolean isMaintainHistory() {
        return maintainHistory;
    }

    public void setMaintainHistory(boolean maintainHistory) {
        this.maintainHistory = maintainHistory;
    }

    public ArrayList<String> getMaintainHistoryExcludedColumn() {
        return maintainHistoryExcludedColumn;
    }

    public void setMaintainHistoryExcludedColumn(ArrayList<String> maintainHistoryExcludedColumn) {
        this.maintainHistoryExcludedColumn = maintainHistoryExcludedColumn;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int incrementIndex() {
        this.index = this.index+1;
        return this.index;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getAddEntryCount() {
        return addEntryCount;
    }

    public void setAddEntryCount(int addEntryCount) {
        this.addEntryCount = addEntryCount;
    }

    public int incrementAddEntryCount() {
        this.addEntryCount = this.addEntryCount + 1;
        return this.addEntryCount;
    }

    public int getUpdateEntryCount() {
        return updateEntryCount;
    }

    public void setUpdateEntryCount(int updateEntryCount) {
        this.updateEntryCount = updateEntryCount;
    }
    public int incrementUpdateEntryCount() {
        this.updateEntryCount = this.updateEntryCount + 1;
        return this.updateEntryCount;
    }
    public int getSkipEntryCount() {
        return skipEntryCount;
    }

    public void setSkipEntryCount(int skipEntryCount) {
        this.skipEntryCount = skipEntryCount;
    }
    public int incrementSkipEntryCount() {
        this.skipEntryCount = this.skipEntryCount + 1;
        return this.skipEntryCount;
    }
    public int getAddEntryErrorCount() {
        return addEntryErrorCount;
    }

    public void setAddEntryErrorCount(int addEntryErrorCount) {
        this.addEntryErrorCount = addEntryErrorCount;
    }
    public int incrementAddEntryErrorCount() {
        this.addEntryErrorCount = this.addEntryErrorCount + 1;
        return this.addEntryErrorCount;
    }
    public int getUpdateEntryErrorCount() {
        return updateEntryErrorCount;
    }

    public void setUpdateEntryErrorCount(int updateEntryErrorCount) {
        this.updateEntryErrorCount = updateEntryErrorCount;
    }

    public int incrementUpdateEntryErrorCount() {
        this.updateEntryErrorCount = this.updateEntryErrorCount + 1;
        return this.updateEntryErrorCount;
    }

    public int getSearchErrorCount() {
        return searchErrorCount;
    }

    public void setSearchErrorCount(int searchErrorCount) {
        this.searchErrorCount = searchErrorCount;
    }
    public int incrementSearchErrorCount() {
        this.searchErrorCount = this.searchErrorCount + 1;
        return this.searchErrorCount;
    }
    public String getFinalUpdateSummary() {
        return "Final update summary,"+ addEntryCount + "/" + updateEntryCount + "/" +
                skipEntryCount + "/" + addEntryErrorCount + "/" + updateEntryErrorCount + "/" +
                searchErrorCount + "/" + size +
                ": Add, Update, Skip, AddError, UpdateError, SearchError, Total";
    }

    @Override
    public String toString() {
        return "SaveTableParameter{" +
                "startedTime='" + startedTime + '\'' +
                ", updateIfFound=" + updateIfFound +
                ", maintainHistory=" + maintainHistory +
                ", maintainHistoryExcludedColumn=" + maintainHistoryExcludedColumn +
                ", index=" + index +
                ", size=" + size +
                ", addEntryCount=" + addEntryCount +
                ", updateEntryCount=" + updateEntryCount +
                ", skipEntryCount=" + skipEntryCount +
                ", addEntryErrorCount=" + addEntryErrorCount +
                ", updateEntryErrorCount=" + updateEntryErrorCount +
                ", searchErrorCount=" + searchErrorCount +
                '}';
    }
}
