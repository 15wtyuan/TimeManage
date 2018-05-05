package com.tt.timemanage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tt.timemanage.model.BaseJson;
import com.tt.timemanage.model.UserData;
import com.tt.timemanage.model.UserDataJson;
import com.tt.timemanage.networkTools.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddFriendActivity extends BaseActivity{

    private EditText search_edit;
    private TextView search_button;

    private String msg;

    private String uid1;

    private UserData friendData = null;

    private AlertDialog dialog;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        //取出intent的数据
        Intent intent = getIntent();
        uid1 = intent.getStringExtra("uid1");

        search_button = (TextView)findViewById(R.id.search_button);
        search_edit = (EditText)findViewById(R.id.search_edit);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search_edit.getText().toString().equals("")){
                    showToase("请输入账号");
                }else {
                    search();
                }
            }
        });

        builder = new AlertDialog.Builder(this);
    }

    private void search(){
        RequestBody requestBody = new FormBody.Builder()
                .add("login_id",search_edit.getText().toString())
                .build();
        HttpUtil.sendOkHttpRequest("http://120.79.137.28/tp/public/index.php/index/User/getFriends",requestBody,new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Gson gson = new GsonBuilder().serializeNulls().create();
                UserDataJson userDataJson = gson.fromJson(responseData, UserDataJson.class);
                if (userDataJson.getCode()==200){
                    friendData = userDataJson.getData();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String a = "是否添加"+friendData.getName()+"为朋友？";
                            builder.setMessage(a);
                            //点击对话框以外的区域是否让对话框消失
                            builder.setCancelable(true);
                            //设置正面按钮
                            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    addFriend();
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
                            dialog.show();
                        }
                    });
                }else {
                    msg = userDataJson.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToase("查无此人");
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

    private void addFriend(){
        if (friendData!=null){
            RequestBody requestBody = new FormBody.Builder()
                    .add("uid1",uid1)
                    .add("uid2",Integer.toString(friendData.getUid()))
                    .add("name",friendData.getName())
                    .add("sex",Integer.toString(friendData.getSex()))
                    .build();
            HttpUtil.sendOkHttpRequest("http://120.79.137.28/tp/public/index.php/index/Friendship/addFriendship",requestBody,new okhttp3.Callback(){
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    BaseJson baseJson = gson.fromJson(responseData, BaseJson.class);//使用Gson处理获取的json
                    if (baseJson.getCode()==200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToase("添加成功");
                            }
                        });
                    }else {
                        msg = baseJson.getMessage();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToase("你们已经是朋友关系了，不用再添加");
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
    }

    private void linkFailure(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToase("链接失败");
            }
        });
    }

    public static void actionStart(Context context,String uid1){
        Intent intent = new Intent(context,AddFriendActivity.class);
        intent.putExtra("uid1",uid1);
        context.startActivity(intent);
    }
}
