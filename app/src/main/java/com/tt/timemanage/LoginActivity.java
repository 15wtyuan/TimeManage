package com.tt.timemanage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tt.timemanage.model.UserData;
import com.tt.timemanage.model.UserDataJson;
import com.tt.timemanage.networkTools.HttpUtil;
import com.tt.timemanage.tools.StatusBarCompat;
import com.tt.timemanage.view.ToolBarHelper;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText login_edit_account;
    private EditText login_edit_password;
    private UserData userData = new UserData();
    private Button regist_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //设置toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.white_toolbar_noback);
        ToolBarHelper toolbarHelper = new ToolBarHelper(toolbar);
        toolbarHelper.setTitle("登录");
        toolbar = toolbarHelper.getToolbar();
        setSupportActionBar(toolbar);

        //绑定组件
        Button login = (Button) findViewById(R.id.login_login_Button);
        TextView forget_text = (TextView)findViewById(R.id.textView03) ;
        login_edit_account = (EditText)findViewById(R.id.login_edit_account);
        login_edit_password = (EditText)findViewById(R.id.login_edit_password);
        login.setOnClickListener(this);
        //forget_text.setOnClickListener(this);

        //从数据库读取用户信息
        List<UserData> userDatas = DataSupport.findAll(UserData.class);
        if (userDatas != null){
            for (UserData temp:userDatas){
                userData = temp;
            }
        }

        //设置注册按钮
        regist_Button = (Button)findViewById(R.id.regist_Button);
        regist_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        StatusBarCompat.compat(this, Color.WHITE);//设置沉浸式颜色；
    }


    //设置组件的点击属性
    @Override
    public void onClick(View v) {

        //如果点击了登陆按钮
        if(v.getId()==R.id.login_login_Button){
            regist_Button.setEnabled(false);
            userData.setLogin_id(login_edit_account.getText().toString());
            userData.setPw(login_edit_password.getText().toString());
            RequestBody requestBody = new FormBody.Builder()
                    .add("login_id",userData.getLogin_id())
                    .add("pw",userData.getPw())
                    .build();
            HttpUtil.sendOkHttpRequest("http://120.79.137.28/tp/public/index.php/index/User/login",requestBody,new okhttp3.Callback(){
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

//        if(v.getId()==R.id.textView03){
//            Intent intent = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
//            startActivity(intent);
//        }
    }

    private void linkFailure(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                regist_Button.setEnabled(true);
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

                    Intent intent = new Intent(LoginActivity.this,HomePageActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    regist_Button.setEnabled(true);
                    showToase("账号或密码错误！");
                }
            }
        });
    }

//    //设置组件的点击属性
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                FragmentManager fm = getSupportFragmentManager();
//                if (fm != null && fm.getBackStackEntryCount() > 0) {
//                    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                } else {
//                    finish();
//                }
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}
