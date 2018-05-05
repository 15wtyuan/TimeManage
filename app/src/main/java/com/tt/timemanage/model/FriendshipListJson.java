package com.tt.timemanage.model;

import java.util.List;

/**
 * Created by TT on 2018/5/1.
 */

public class FriendshipListJson extends BaseJson {
    List<Friendship> data;

    public List<Friendship> getData() {
        return data;
    }

    public void setData(List<Friendship> data) {
        this.data = data;
    }
}
