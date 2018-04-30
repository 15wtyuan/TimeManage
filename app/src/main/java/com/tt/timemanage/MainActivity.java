package com.tt.timemanage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tt.timemanage.model.UserData;
import com.tt.timemanage.model.UserDataJson;
import com.tt.timemanage.networkTools.HttpUtil;
import com.tt.timemanage.tools.StatusBarCompat;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends BaseActivity {

//    Button login_Button;
//    Button register_button;
    UserData userData = new UserData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LitePal.getDatabase();

//        register_button = (Button)findViewById(R.id.regist_Button);
//        login_Button = (Button) findViewById(R.id.login_Button);

        List<UserData> userLoginDatas = DataSupport.findAll(UserData.class);
        for(UserData temp:userLoginDatas){
            userData = temp;
        }

        if (userData.getLogin_id() == null){
            goLogin();
            //setButton();
            //showToase("没有登陆");
        }else {
            RequestBody requestBody = new FormBody.Builder()
                    .add("login_id",userData.getLogin_id())
                    .add("pw",userData.getPw())
                    .build();
            HttpUtil.sendOkHttpRequest("http://120.79.137.28/tp/public/index.php/index/User/login",requestBody,new okhttp3.Callback(){
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("test","链接成功");
                    String responseData = response.body().string();
                    parseJSON(responseData);
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    linkFailure();
                }
            });
        }

        StatusBarCompat.compat(this, Color.BLACK);//设置沉浸式颜色；

    }

    private void linkFailure(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToase("链接失败");
                goHome();
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
                    userData.save();

                    goHome();
                }else {
                    goLogin();
                    //setButton();
                }
            }
        });
    }

    private void goHome(){
        final Intent intent = new Intent(MainActivity.this,HomePageActivity.class);
        //通过一个时间控制函数Timer，在实现一个活动与另一个活动的跳转。
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(intent);
                finish();

            }
        }, 1000);//这里停留时间为1000=1s。
    }

    private void goLogin(){
        final Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        //通过一个时间控制函数Timer，在实现一个活动与另一个活动的跳转。
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(intent);
                finish();

            }
        }, 1000);//这里停留时间为1000=1s。
    }

}