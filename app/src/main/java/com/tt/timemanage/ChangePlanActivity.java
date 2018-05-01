package com.tt.timemanage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tt.timemanage.model.BaseJson;
import com.tt.timemanage.networkTools.HttpUtil;
import com.tt.timemanage.widget.CustomDatePicker;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePlanActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout begin, end;
    private TextView current_begin, current_end;
    private CustomDatePicker customDatePicker1, customDatePicker2;

    private RelativeLayout delete;

    private Spinner spinner;
    private MyAdapter myAdapter;

    private Spinner spinner2;
    private MyAdapter myAdapter2;

    private List<String> dataList = Arrays.asList(
            "否",
            "是"
    );

    private List<String> dataList2 = Arrays.asList(
            "未完成",
            "已完成",
            "已过期"
    );

    private Toolbar toolbar;

    private EditText content_editText;

    private String begin_time,end_time;//存放开始时间和结束时间
    private String host_id,from_id,from_name,host_name, description, is_everyday, state,  begin_originally , end_originally , id;

    private String msg;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_plan);

        //取出intent的数据
        Intent intent = getIntent();
        host_id = intent.getStringExtra("host_id");
        from_id = intent.getStringExtra("from_id");
        from_name = intent.getStringExtra("from_name");
        host_name = intent.getStringExtra("host_name");
        description = intent.getStringExtra("description");
        is_everyday = intent.getStringExtra("is_everyday");
        state = intent.getStringExtra("state");
        begin_originally = intent.getStringExtra("begin");
        end_originally = intent.getStringExtra("end");
        id = intent.getStringExtra("id");
        Log.d("test",host_id+"  "+from_id+"  "+from_name+"  "+host_name+"  "+description+"  "+
                is_everyday+"  "+state+"  "+begin_originally+"  "+end_originally+"  "+id);

        //删除按钮
        delete = (RelativeLayout)findViewById(R.id.delete);
        delete.setOnClickListener(this);

        //时间选择器
        begin = (RelativeLayout)findViewById(R.id.begintime);
        begin.setOnClickListener(this);
        end = (RelativeLayout)findViewById(R.id.endtime) ;
        end.setOnClickListener(this);
        current_begin = (TextView)findViewById(R.id.current_begin);
        current_end = (TextView)findViewById(R.id.current_end) ;
        initDatePicker();

        //spinner 选择器,选择是否为每日计划
        spinner = (Spinner) findViewById(R.id.spinner);
        myAdapter = new MyAdapter(this, dataList);
        spinner.setAdapter(myAdapter);
        spinner.setSelection(Integer.valueOf(is_everyday),true);
        //spinner 选择器,选择状态如何
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        myAdapter2 = new MyAdapter(this, dataList2);
        spinner2.setAdapter(myAdapter2);
        spinner2.setSelection(Integer.valueOf(state),true);

        toolbar = (Toolbar) findViewById(R.id.white_toolbar_add);//标题栏的绑定
        setSupportActionBar(toolbar);
        Button add_to_net = toolbar.findViewById(R.id.add_to_net);//发布按钮
        add_to_net.setOnClickListener(this);

        //描述输入框
        content_editText = (EditText)findViewById(R.id.content_editText);
        content_editText.setText(description);

        //设置标识所属人和制定人的文字
        TextView hf_label = (TextView)findViewById(R.id.title_editText);
        String a = "from:"+from_name;
        String b = "  to:"+host_name;
        hf_label.setText(a + b);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("删除这条计划？");
        //点击对话框以外的区域是否让对话框消失
        builder.setCancelable(true);
        //设置正面按钮
        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletePlan();
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

    private void deletePlan(){
        RequestBody requestBody = new FormBody.Builder()
                .add("id",id)
                .build();
        HttpUtil.sendOkHttpRequest("http://120.79.137.28/tp/public/index.php/index/Plan/deletePlan",requestBody,new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Gson gson = new GsonBuilder().serializeNulls().create();
                BaseJson baseJson = gson.fromJson(responseData, BaseJson.class);//使用Gson处理获取的json
                if (baseJson.getCode()==200){
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            FragmentManager fm = getSupportFragmentManager();
                            if (fm != null && fm.getBackStackEntryCount() > 0) {
                                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            } else {
                                finish();
                            }
                        }
                    }, 500);//这里停留时间为1000=1s。
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

    private void linkFailure(){//网络错误后
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToase("链接失败");
            }
        });
    }

    private void send_to_net(){
        RequestBody requestBody = new FormBody.Builder()
                .add("id",id)
                .add("host_id",host_id)
                .add("from_id",from_id)
                .add("begin",begin_time)
                .add("end",end_time)
                .add("description",content_editText.getText().toString())
                .add("last_days","0")
                .add("is_everyday",Integer.toString(myAdapter.label))
                .add("state",Integer.toString(myAdapter2.label))
                .add("from_name",from_name)
                .build();
        HttpUtil.sendOkHttpRequest("http://120.79.137.28/tp/public/index.php/index/Plan/changePlanInformation",requestBody,new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Gson gson = new GsonBuilder().serializeNulls().create();
                BaseJson baseJson = gson.fromJson(responseData, BaseJson.class);//使用Gson处理获取的json
                if (baseJson.getCode()==200){
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            FragmentManager fm = getSupportFragmentManager();
                            if (fm != null && fm.getBackStackEntryCount() > 0) {
                                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            } else {
                                finish();
                            }
                        }
                    }, 500);//这里停留时间为1000=1s。
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
    /*
    * 将时间转换为时间戳
    */
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        ts = ts/1000;//java中时间戳精确到毫秒，所以会多出三位
        res = String.valueOf(ts);
        return res;
    }

    private static String TimeStamp2Date(String timestampString, String formats) {//将Unix时间截转换成能看懂的字符串
        if (TextUtils.isEmpty(formats))
            formats = "yyyy-MM-dd HH:mm";
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new java.text.SimpleDateFormat(formats, Locale.CHINA).format(new java.util.Date(timestamp));
        return date;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.begintime:
                // 日期格式为yyyy-MM-dd
                customDatePicker1.show(current_begin.getText().toString());
                break;

            case R.id.endtime:
                // 日期格式为yyyy-MM-dd HH:mm
                customDatePicker2.show(current_end.getText().toString());
                break;

            case R.id.add_to_net:
                String content =  content_editText.getText().toString();
                try {
                    begin_time = dateToStamp(begin_time);
                    end_time = dateToStamp(end_time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                send_to_net();
                break;
            case R.id.delete:
                dialog.show();
                break;
        }
    }

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        begin_time = TimeStamp2Date(begin_originally,"yyyy-MM-dd HH:mm");
        end_time = TimeStamp2Date(end_originally,"yyyy-MM-dd HH:mm");
        current_begin.setText(begin_time);
        current_end.setText(end_time);


        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                current_begin.setText(time);
                begin_time = time;
            }
        }, now, "2040-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(true); // 显示时和分
        customDatePicker1.setIsLoop(true); // 允许循环滚动

        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                current_end.setText(time);
                end_time = time;
            }
        }, now, "2040-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker2.showSpecificTime(true); // 显示时和分
        customDatePicker2.setIsLoop(true); // 允许循环滚动
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

    //自定义Spinner的适配器
    private class MyAdapter extends BaseAdapter {

        public int label=0;
        private Context mContext;
        private List<String> mList;

        public MyAdapter(Context context, List<String> list) {
            this.mContext = context;
            this.mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            label = position;//从这里获取到用户点击时哪个子项
            return position;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ChangePlanActivity.ViewHolder holder = null;

            if (convertView == null) {
                convertView = View.inflate(ChangePlanActivity.this, R.layout.memu_item, null);

                holder = new ChangePlanActivity.ViewHolder();

                holder.tv = (TextView) convertView.findViewById(R.id.tv);

                convertView.setTag(holder);
            } else {
                holder = (ChangePlanActivity.ViewHolder) convertView.getTag();
            }
            holder.tv.setText(mList.get(position));

            return convertView;
        }
    }

    public class ViewHolder {
        private TextView tv;
    }

    public static void actionStart(Context context, String host_id, String from_id, String host_name, String from_name,
                                   String description, String is_everyday, String state, String begin ,String end ,String id){
        Intent intent = new Intent(context,ChangePlanActivity.class);
        intent.putExtra("host_id",host_id);
        intent.putExtra("from_id",from_id);
        intent.putExtra("host_name",host_name);
        intent.putExtra("from_name",from_name);
        intent.putExtra("description",description);
        intent.putExtra("is_everyday",is_everyday);
        intent.putExtra("state",state);
        intent.putExtra("begin",begin);
        intent.putExtra("end",end);
        intent.putExtra("id",id);
        context.startActivity(intent);
    }
}
