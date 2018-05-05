package com.tt.timemanage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Util;
import com.tt.timemanage.adapter.DrawerAdapter;
import com.tt.timemanage.adapter.PlanListAdapter;
import com.tt.timemanage.model.BaseJson;
import com.tt.timemanage.model.PlanData;
import com.tt.timemanage.model.PlanDataListJson;
import com.tt.timemanage.model.UserData;
import com.tt.timemanage.networkTools.HttpUtil;
import com.tt.timemanage.tools.RecyclerViewScrollListener;
import com.tt.timemanage.tools.StatusBarCompat;
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

public class HomePageActivity extends BaseActivity {

    public UserData userData = new UserData();

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private BoomMenuButton bmb;

    private List<PlanData> planDataList = new ArrayList<>();

    private RecyclerView planList;

    private PlanListAdapter planListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //获取用户资料，用于网络获取
        List<UserData> userLoginDatas = DataSupport.findAll(UserData.class);
        for(UserData temp:userLoginDatas){
            userData = temp;
        }

        //绑定计划列表的组件
        planList = (RecyclerView)findViewById(R.id.planList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        planListAdapter = new PlanListAdapter(planDataList,this);
        planList.setLayoutManager(layoutManager);
//        planList.setLayoutManager(new
//                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        planListAdapter.setOnItemClickListener(new PlanOnItemClickListener());
        planList.setAdapter(planListAdapter);

        initPlanList();//初始化列表

        initBoomMenuButton();//悬浮按钮 添加 的初始化

        toolbar = (Toolbar) findViewById(R.id.green_toolbar_homepage);//标题栏的绑定
        ToolBarHelper toolbarHelper = new ToolBarHelper(toolbar);
        toolbarHelper.setTitle("今天的计划");
        toolbarHelper.setTitlesColor(Color.WHITE);
        toolbar = toolbarHelper.getToolbar();
        setSupportActionBar(toolbar);

        planList.addOnScrollListener(new RecyclerViewScrollListener() {//消息列表上滑悬浮按钮消失，下滑时出现
            @Override
            public void hide() {
                bmb.animate().translationY(250).setInterpolator(new AccelerateDecelerateInterpolator());
                toolbar.animate().translationY(0-toolbar.getHeight()).setInterpolator(new AccelerateDecelerateInterpolator());
            }

            @Override
            public void show() {
                bmb.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator());
                toolbar.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator());
            }
        });

        mDrawerLayout = (DrawerLayout)findViewById(R.id.home_page);//右滑菜单
        //mDrawerLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.my_item);//右滑菜单用RecyclerView实现
        LinearLayoutManager layoutManagerPlan = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManagerPlan);
        DrawerAdapter adapter = new DrawerAdapter(this);
        adapter.setOnItemClickListener(new MyOnItemClickListener());
        recyclerView.setAdapter(adapter);

        StatusBarCompat.compat(this, Color.parseColor("#FC911F"));//设置沉浸式颜色；


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initPlanList();
    }

    public void initPlanList(){
        planDataList.clear();//清空所有消息
        PlanData head = new PlanData();
        head.setPid(-10086);
        planDataList.add(0,head);
        RequestBody requestBody = new FormBody.Builder()
                .add("id",Integer.toString(userData.getUid()))
                .build();
        HttpUtil.sendOkHttpRequest("http://120.79.137.28/tp/public/index.php/index/Plan/getPlanList",requestBody,new okhttp3.Callback(){
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
        PlanDataListJson planDataListJson = gson.fromJson(responseData, PlanDataListJson.class);//使用Gson处理获取的json
        if (planDataListJson.getMessage().equals("success")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDrawerLayout.setBackgroundColor(Color.parseColor("#FC911F"));
                }
            });
            planDataList.addAll(planDataListJson.getData());
        }
        if (planDataListJson.getCode()==500){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToase("点击右下角加号添加计划");
                    mDrawerLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            });
        }
        //Log.d("test",Integer.toString(planDataList.size()));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                planListAdapter.notifyDataSetChanged();//更新planList的RecyclerView
            }
        });
    }

    private void initBoomMenuButton(){//初始化悬浮按钮
        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {//添加子按钮
            TextOutsideCircleButton.Builder builder = new TextOutsideCircleButton.Builder()
                    .listener(new OnBMClickListener() {//子按钮的监听
                        @Override
                        public void onBoomButtonClick(int index) {
                            // When the boom-button corresponding this builder is clicked.
                            //Toast.makeText(TextOutsideCircleButtonActivity.this, "Clicked " + index, Toast.LENGTH_SHORT).show();
                            switch (index){
                                case 0:
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            AddPlanActivity.actionStart(HomePageActivity.this,Integer.toString(userData.getUid()),Integer.toString(userData.getUid()),userData.getName(),userData.getName(),"0");
                                        }
                                    }, 300);//这里停留时间为1000=1s。

                                    break;
                                case 1:
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            FriendsActivity.actionStart(HomePageActivity.this);
                                        }
                                    }, 300);//这里停留时间为1000=1s。
                                    break;
                                case 2:
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            AddFriendActivity.actionStart(HomePageActivity.this,Integer.toString(userData.getUid()));
                                        }
                                    }, 300);//这里停留时间为1000=1s。
                                    break;
                            }
                        }
                    })
                    .shadowEffect(false)//子按钮的阴影
                    .normalColor(Color.parseColor("#00000000"))
                    .normalImageRes(addImageRes(i))//子按钮的背景图片
                    .normalText(addTextRes(i));//子按钮的文字描述
            bmb.addBuilder(builder);
        }
        bmb.setButtonRadius(Util.dp2px(30));
        bmb.setNormalColor(getResources().getColor(R.color.colorPrimaryDark));
        bmb.setUnableColor(getResources().getColor(R.color.white));
        bmb.setHighlightedColor(getResources().getColor(R.color.white));
        bmb.setDotRadius(0);
        bmb.setUse3DTransformAnimation(true);
        bmb.setShadowEffect(true);//设置大悬浮按钮的阴影是否显示
        bmb.setShadowColor(Color.parseColor("#55000000"));//大悬浮按钮阴影颜色
        bmb.setShadowOffsetX(-4);//阴影偏移
        bmb.setShadowOffsetY(4);
        bmb.setShadowRadius(4);//阴影的圆角
    }

    private int addImageRes(int i){//悬浮按钮的子按钮背景图片
        switch (i){
            case 0:
                return R.mipmap.jihua;
            case 1:
                return R.mipmap.help_friends;
            case 2:
                return R.mipmap.pengyou;
        }
        return R.mipmap.jihua;
    }

    private String addTextRes(int i){//悬浮按钮的子按钮文字描述
        switch (i){
            case 0:
                return "为自己制定计划";
            case 1:
                return "为朋友制定计划";
            case 2:
                return "添加好友";
        }
        return "";
    }

    public class MyOnItemClickListener implements DrawerAdapter.OnItemClickListener {//侧滑菜单的item 监听

        @Override
        public void itemClick(DrawerAdapter.DrawerItemNormal drawerItemNormal) {
            switch (drawerItemNormal.name) {
                case "我的朋友":
                    FriendsActivity.actionStart(HomePageActivity.this);
                    break;
                case "我的消息":
                    showToase("暂无");
                    break;
                case "退出登陆":
                    showToase("退出登陆");
                    List<UserData> userLoginDatas = DataSupport.findAll(UserData.class);
                    UserData userLoginData = new UserData();
                    for(UserData temp:userLoginDatas){
                        userLoginData = temp;
                    }
                    userLoginData.delete();
                    ActivityCollector.finishAll();
                    Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
            }
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public class PlanOnItemClickListener implements PlanListAdapter.OnItemClickListener {//planlist 完成按钮的 监听

        @Override
        public void itemClick(final int position) {
            RequestBody requestBody = new FormBody.Builder()
                    .add("id",Integer.toString(planDataList.get(position).getPid()))
                    .build();
            HttpUtil.sendOkHttpRequest("http://120.79.137.28/tp/public/index.php/index/Plan/finishPlan",requestBody,new okhttp3.Callback(){
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("完成计划","链接失败");
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    BaseJson baseJson = gson.fromJson(responseData, BaseJson.class);//使用Gson处理获取的json
                    if (baseJson.getCode()==300){
                        Log.d("完成计划","成功");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initPlanList();
                            }
                        });
                    }else {
                        final String msg = baseJson.getMessage();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToase(msg);
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//导航栏菜单的监听
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);//打开侧滑菜单
                break;
//            case R.id.alert_button://新消息
//                showToase("新消息");
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
