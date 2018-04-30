package com.tt.timemanage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tt.timemanage.model.UserData;
import com.tt.timemanage.model.UserDataJson;
import com.tt.timemanage.networkTools.HttpUtil;
import com.tt.timemanage.tools.StatusBarCompat;
import com.tt.timemanage.view.ToolBarHelper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity {

    private EditText login_id_edit;//用户账号输入框
    private EditText pw_edit;//密码输入框
    private EditText name_edit;//昵称
    private EditText qrpw_edit;//确认密码输入框

    private Button boy_button;//男孩选择按钮
    private Button girl_button;//女孩选择按钮
    private Button ensure_button;//确认提交按钮；

    private UserData userData;

    private String firstPassword;//存储用户输入的密码
    private String secondPassword;
    private String login_id;//存储用户账号
    private String name;//存储用户昵称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //设置toolbar信息
        Toolbar toolbar = (Toolbar) findViewById(R.id.white_toolbar);
        ToolBarHelper toolbarHelper = new ToolBarHelper(toolbar);
        toolbarHelper.setTitle("注册账号");
        toolbar = toolbarHelper.getToolbar();
        setSupportActionBar(toolbar);

        //绑定组建
        login_id_edit = (EditText)findViewById(R.id.register_edit_account);
        pw_edit = (EditText)findViewById(R.id.register_edit_password);
        qrpw_edit = (EditText)findViewById(R.id.register_confirm_password);
        name_edit = (EditText)findViewById(R.id.register_edit_name);

        boy_button = (Button)findViewById(R.id.boy_button);
        girl_button = (Button)findViewById(R.id.girl_button);
        ensure_button = (Button)findViewById(R.id.register_ensure_button);

        //初始化用户信息
        userData = new UserData();

        //设置男女按钮的监听
        boy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userData.setSex(0);
                boy_button.setBackgroundResource(R.drawable.orange_button);
                girl_button.setBackgroundResource(R.drawable.green_button);
            }
        });
        girl_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userData.setSex(1);
                boy_button.setBackgroundResource(R.drawable.green_button);
                girl_button.setBackgroundResource(R.drawable.orange_button);
            }
        });

        //设置确定按钮的监听
        ensure_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = name_edit.getText().toString();
                login_id = login_id_edit.getText().toString();
                //首先判断密码和确认密码的内容对不对 firstPassword = first_password_edit.getText().toString();
                secondPassword = qrpw_edit.getText().toString();
                firstPassword = pw_edit.getText().toString();
                if (firstPassword.equals(secondPassword)&&firstPassword!=null&&!firstPassword.equals("")) {
                    //如果一致，检查是否留空
                    if (name!=null&&!name.equals("")&&login_id!=null&&!login_id.equals("")){
                        //发送请求
                        send();
                    }else {
                        showToase("请不要留空");
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "密码输入不一致或为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        StatusBarCompat.compat(this, Color.WHITE);//设置沉浸式颜色；
    }

    //发送请求
    private void send(){
        RequestBody requestBody = new FormBody.Builder()
                .add("login_id",login_id)
                .add("pw",firstPassword)
                .add("name",name)
                .add("sex",Integer.toString(userData.getSex()))
                .build();
        HttpUtil.sendOkHttpRequest("http://120.79.137.28/tp/public/index.php/index/User/register",requestBody,new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                parseJSON(responseData);
            }
            @Override
            public void onFailure(Call call, IOException e) {
                linkFailure();
            }
        });
    }

    private void linkFailure(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToase("链接失败");
            }
        });
    }

    private void parseJSON(final String responseData){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new GsonBuilder().serializeNulls().create();
                UserDataJson userDataJson = gson.fromJson(responseData, UserDataJson.class);
                if (userDataJson.getMessage().equals("success")){

                    UserData userDataTemp = userDataJson.getData();
                    userData.setLogin_id(userDataTemp.getLogin_id());
                    userData.setPw(userDataTemp.getPw());
                    userData.setName(userDataTemp.getName());
                    userData.setSex(userDataTemp.getSex());
                    userData.setSculpture(userDataTemp.getSculpture());
                    userData.setUid(userDataTemp.getUid());
                    if (userData.save()){
                        Log.d("test","成功");
                    }

                    Intent intent = new Intent(RegisterActivity.this,HomePageActivity.class);
                    startActivity(intent);
                    finish();
                }else if(userDataJson.getCode()==302){
                    Toast.makeText(RegisterActivity.this, "服务器创建不了新用户", Toast.LENGTH_SHORT).show();
                }else if(userDataJson.getCode()==303){
                    Toast.makeText(RegisterActivity.this, "您已注册过，请直接登录", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                }
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
}
