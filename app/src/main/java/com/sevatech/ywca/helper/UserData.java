package com.sevatech.ywca.helper;

public class UserData {
    private String userID;
    private String userName;
    private String userEmail;
    private String userContact;
    private String userType;

    public UserData() {
    }

    public UserData(String userID, String userType) {
        this.userID = userID;
        this.userType = userType;
    }

    public UserData(String userID, String userName, String userEmail, String userContact, String userType) {
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userContact = userContact;
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserContact() {
        return userContact;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
