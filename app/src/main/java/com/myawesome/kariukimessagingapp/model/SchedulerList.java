package com.myawesome.kariukimessagingapp.model;

public class SchedulerList {
    String name,pno,date_renew;

    public SchedulerList(String name, String pno, String date_renew) {
        this.name = name;
        this.pno = pno;
        this.date_renew = date_renew;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPno() {
        return pno;
    }

    public void setPno(String pno) {
        this.pno = pno;
    }

    public String getDate_renew() {
        return date_renew;
    }

    public void setDate_renew(String date_renew) {
        this.date_renew = date_renew;
    }
}
