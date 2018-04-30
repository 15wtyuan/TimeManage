package com.tt.timemanage.model;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

/**
 * Created by TT on 2018/4/27.
 */

public class PlanData extends DataSupport {

    @SerializedName("id")
    private int pid;
    private String host_id;
    private String from_id;
    private String description;
    private int last_days;
    private int is_everyday;
    private int state;
    private int begin;
    private int end;
    private String creat_time;
    private String update_time;
    private String from_name;

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getHost_id() {
        return host_id;
    }

    public void setHost_id(String host_id) {
        this.host_id = host_id;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLast_days() {
        return last_days;
    }

    public void setLast_days(int last_days) {
        this.last_days = last_days;
    }

    public int getIs_everyday() {
        return is_everyday;
    }

    public void setIs_everyday(int is_everyday) {
        this.is_everyday = is_everyday;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getCreat_time() {
        return creat_time;
    }

    public void setCreat_time(String creat_time) {
        this.creat_time = creat_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }
}
