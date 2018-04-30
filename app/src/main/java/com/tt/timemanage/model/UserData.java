package com.tt.timemanage.model;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

/**
 * Created by TT on 2018/4/16.
 */

public class UserData extends DataSupport {

    @SerializedName("id")
    private int uid;
    private String login_id;
    private String pw;
    private String name;
    private int sex;
    private String sculpture;

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSculpture() {
        return sculpture;
    }

    public void setSculpture(String sculpture) {
        this.sculpture = sculpture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUid() {

        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }
}
