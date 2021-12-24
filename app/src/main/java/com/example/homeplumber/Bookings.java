package com.example.homeplumber;

public class Bookings {
    private int orderid;
    private String workerid;
    private String workername;
    private String date;
    private String phone;
    private String status;


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public String getWorkerid() {
        return workerid;
    }

    public void setWorkerid(String workerid) {
        this.workerid = workerid;
    }

    public String getWorkername() {
        return workername;
    }

    public void setWorkername(String workername) {
        this.workername = workername;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
