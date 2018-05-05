package com.tt.timemanage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tt.timemanage.adapter.FriendListAdapter;
import com.tt.timemanage.model.BaseJson;
import com.tt.timemanage.model.Friendship;
import com.tt.timemanage.model.FriendshipListJson;
import com.tt.timemanage.model.UserData;
import com.tt.timemanage.networkTools.HttpUtil;
import com.tt.timemanage.view.ToolBarHelper;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FriendsActivity extends BaseActivity {

    private List<Friendship> friendshipList = new ArrayList<>();

    private FriendListAdapter adapter;

    public UserData userData = new UserData();

    private Toolbar toolbar;

    private AlertDialog dialog;

    private int position_temp;
    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        //获取用户资料，用于网络获取
        List<UserData> userLoginDatas = DataSupport.findAll(UserData.class);
        for(UserData temp:userLoginDatas){
            userData = temp;
        }

        RecyclerView friendList = (RecyclerView)findViewById(R.id.friendList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new FriendListAdapter(friendshipList);
        friendList.setLayoutManager(layoutManager);
        adapter.setOnItemClickListener(new OnItemClickListener());
        adapter.setLongOnItemClickListener(new LongOnItemClickListener());
        friendList.setAdapter(adapter);

        initFriendList();

        toolbar = (Toolbar) findViewById(R.id.orange_toolbar);//标题栏的绑定
        ToolBarHelper toolbarHelper = new ToolBarHelper(toolbar);
        toolbarHelper.setTitle("朋友列表");
        toolbarHelper.setTitlesColor(Color.WHITE);
        toolbar = toolbarHelper.getToolbar();
        setSupportActionBar(toolbar);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("删除这个朋友？");
        //点击对话框以外的区域是否让对话框消失
        builder.setCancelable(true);
        //设置正面按钮
        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFriend();
                dialog.dismiss();
            }
        });
        //设置反面按钮
        builder.setNegativeButton("不好", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
    }

    private void deleteFriend(){
        RequestBody requestBody = new FormBody.Builder()
                .add("uid1",friendshipList.get(position_temp).getUid1())
                .add("uid2",friendshipList.get(position_temp).getUid2())
                .build();
        HttpUtil.sendOkHttpRequest("http://120.79.137.28/tp/public/index.php/index/Friendship/deleteFriendship",requestBody,new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Gson gson = new GsonBuilder().serializeNulls().create();
                BaseJson baseJson = gson.fromJson(responseData, BaseJson.class);//使用Gson处理获取的json
                if (baseJson.getCode()==200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToase("删除成功");
                            initFriendList();
                        }
                    });
                }else {
                    msg = baseJson.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToase(msg);
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                linkFailure();
            }
        });
    }

    public void initFriendList(){
        friendshipList.clear();//清空所有消息
        Friendship head = new Friendship();
        head.setUid1("-10086");
        friendshipList.add(0,head);
        RequestBody requestBody = new FormBody.Builder()
                .add("id",Integer.toString(userData.getUid()))
                .build();
        HttpUtil.sendOkHttpRequest("http://120.79.137.28/tp/public/index.php/index/User/getFriendsList",requestBody,new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                parseIssueJSON(responseData);
            }
            @Override
            public void onFailure(Call call, IOException e) {
                linkFailure();
            }
        });
    }

    private void linkFailure(){//网络错误后
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToase("链接失败");
            }
        });
    }

    private void parseIssueJSON(final String responseData){//处理获取的json
        Gson gson = new GsonBuilder().serializeNulls().create();
        FriendshipListJson friendshipListJson = gson.fromJson(responseData, FriendshipListJson.class);//使用Gson处理获取的json
        Log.d("朋友",friendshipListJson.getMessage());
        if (friendshipListJson.getCode()==200){
            friendshipList.addAll(friendshipListJson.getData());
        }
        if (friendshipListJson.getCode()==500){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToase("你没有朋友");
                }
            });
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();//更新List的RecyclerView
            }
        });
    }

    //设置组件的点击属性
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if (fm != null && fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class OnItemClickListener implements FriendListAdapter.OnItemClickListener {//planlist 完成按钮的 监听

        @Override
        public void itemClick(final int position) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    AddPlanActivity.actionStart(FriendsActivity.this,friendshipList.get(position).getUid2(),Integer.toString(userData.getUid()),friendshipList.get(position).getName(),userData.getName(),"0");
                }
            }, 100);//这里停留时间为1000=1s。
        }
    }

    public class LongOnItemClickListener implements FriendListAdapter.LongOnItemClickListener {//planlist 完成按钮的 监听

        @Override
        public void itemClick(final int position) {
            position_temp = position;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });
        }
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context,FriendsActivity.class);
        context.startActivity(intent);
    }
}
