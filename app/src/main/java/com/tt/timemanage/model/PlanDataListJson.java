package com.tt.timemanage.model;

import java.util.List;

/**
 * Created by TT on 2018/4/27.
 */

public class PlanDataListJson extends BaseJson {

    List<PlanData> data;

    public List<PlanData> getData() {
        return data;
    }

    public void setData(List<PlanData> data) {
        this.data = data;
    }
}
