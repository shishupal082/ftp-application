package com.project.ftp.obj;

public class RelatedUserDataV2 {
    private String username;
    private boolean isValid;

    public RelatedUserDataV2(RelatedUserData relatedUserData) {
        if (relatedUserData == null) {
            return;
        }
        this.username = relatedUserData.getUsername();
        this.isValid = relatedUserData.isValid();
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    @Override
    public String toString() {
        return "RelatedUserDataV2{" +
                "username='" + username + '\'' +
                ", isValid=" + isValid +
                '}';
    }
}
