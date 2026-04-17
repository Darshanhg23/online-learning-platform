package com.learningplatform.model;

import java.time.LocalDateTime;

public class ActivityLog {
    private int id;
    private Integer userId;       // nullable — for anonymous actions
    private String userEmail;
    private String userName;
    private String action;        // LOGIN, LOGOUT, REGISTER, ENROLL
    private String detail;        // extra context e.g. course name
    private String ipAddress;
    private LocalDateTime loggedAt;

    public ActivityLog() {}

    public ActivityLog(Integer userId, String userEmail, String userName,
                       String action, String detail, String ipAddress) {
        this.userId    = userId;
        this.userEmail = userEmail;
        this.userName  = userName;
        this.action    = action;
        this.detail    = detail;
        this.ipAddress = ipAddress;
    }

    // Getters & Setters
    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public Integer getUserId()                  { return userId; }
    public void setUserId(Integer userId)       { this.userId = userId; }

    public String getUserEmail()                { return userEmail; }
    public void setUserEmail(String e)          { this.userEmail = e; }

    public String getUserName()                 { return userName; }
    public void setUserName(String n)           { this.userName = n; }

    public String getAction()                   { return action; }
    public void setAction(String action)        { this.action = action; }

    public String getDetail()                   { return detail; }
    public void setDetail(String detail)        { this.detail = detail; }

    public String getIpAddress()                { return ipAddress; }
    public void setIpAddress(String ip)         { this.ipAddress = ip; }

    public LocalDateTime getLoggedAt()          { return loggedAt; }
    public void setLoggedAt(LocalDateTime t)    { this.loggedAt = t; }
}
