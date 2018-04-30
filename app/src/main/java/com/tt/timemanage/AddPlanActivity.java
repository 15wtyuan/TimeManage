package com.tt.timemanage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
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

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddPlanActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout begin, end;
    private TextView current_begin, current_end;
    private CustomDatePicker customDatePicker1, customDatePicker2;

    private Spinner spinner;
    private MyAdapter adapter;

    private List<String> dataList = Arrays.asList(
            "否",
            "是"
    );

    private Toolbar toolbar;

    private EditText content_editText;

    int label;//存放是否每日计划
    private String begin_time,end_time;//存放开始时间和结束时间
    private String host_id;
    private String from_id;
    private String from_name;
    private String host_name;

    private String type;//判断是给自己计划还是给朋友计划

    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);

        //取出intent的数据
        Intent intent = getIntent();
        host_id = intent.getStringExtra("host_id");
        from_id = intent.getStringExtra("from_id");
        from_name = intent.getStringExtra("from_name");
        host_name = intent.getStringExtra("host_name");
        type = intent.getStringExtra("type");

        //时间选择器
        begin = (RelativeLayout)findViewById(R.id.begintime);
        begin.setOnClickListener(this);
        end = (RelativeLayout)findViewById(R.id.endtime) ;
        end.setOnClickListener(this);
        current_begin = (TextView)findViewById(R.id.current_begin);
        current_end = (TextView)findViewById(R.id.current_end) ;
        initDatePicker();

        //spinner 选择器
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = new MyAdapter(this, dataList);
        spinner.setAdapter(adapter);

        toolbar = (Toolbar) findViewById(R.id.white_toolbar_add);//标题栏的绑定
        setSupportActionBar(toolbar);
        Button add_to_net = toolbar.findViewById(R.id.add_to_net);//发布按钮
        add_to_net.setOnClickListener(this);

        content_editText = (EditText)findViewById(R.id.content_editText);

        TextView hf_label = (TextView)findViewById(R.id.title_editText);
        String a = "from:"+from_name;
        String b = "  to:"+host_name;
        hf_label.setText(a + b);
    }

    private void linkFailure(){//网络错误后
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToase("链接失败");
            }
        });
    }

    private void send_to_net1(){
        RequestBody requestBody = new FormBody.Builder()
                .add("host_id",host_id)
                .add("from_id",from_id)
                .add("begin",begin_time)
                .add("end",end_time)
                .add("description",content_editText.getText().toString())
                .add("last_days","0")
                .add("is_everyday",Integer.toString(label))
                .add("state","0")
                .add("from_name",from_name)
                .build();
        HttpUtil.sendOkHttpRequest("http://120.79.137.28/tp/public/index.php/index/Plan/addPlanMyself",requestBody,new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Gson gson = new GsonBuilder().serializeNulls().create();
                BaseJson baseJson = gson.fromJson(responseData, BaseJson.class);//使用Gson处理获取的json
                if (baseJson.getCode()==200){

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

    private void send_to_net2(){
        RequestBody requestBody = new FormBody.Builder()
                .add("host_id",host_id)
                .add("from_id",from_id)
                .add("begin",begin_time)
                .add("end",end_time)
                .add("description",content_editText.getText().toString())
                .add("last_days","0")
                .add("is_everyday",Integer.toString(label))
                .add("state","0")
                .add("from_name",from_name)
                .build();
        HttpUtil.sendOkHttpRequest("http://120.79.137.28/tp/public/index.php/index/Plan/addPlanOther",requestBody,new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Gson gson = new GsonBuilder().serializeNulls().create();
                BaseJson baseJson = gson.fromJson(responseData, BaseJson.class);//使用Gson处理获取的json
                if (baseJson.getCode()==200){

                }else {
                    showToase(baseJson.getMessage());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.begintime:
                // 日期格式为yyyy-MM-dd
                customDatePicker1.show(current_begin.getText().toString());
                showToase("点击选择开始时间");
                break;

            case R.id.endtime:
                // 日期格式为yyyy-MM-dd HH:mm
                customDatePicker2.show(current_end.getText().toString());
                showToase("点击选择结束时间");
                break;

            case R.id.add_to_net:
                String content =  content_editText.getText().toString();
                try {
                    begin_time = dateToStamp(begin_time);
                    end_time = dateToStamp(end_time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (type.equals("0")){
                    send_to_net1();
                }else if (type.equals("1")){
                    send_to_net2();
                }

        }
    }

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        current_begin.setText(now);
        current_end.setText(now);
        begin_time = now;
        end_time = now;

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
                showToase("点击了返回");
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

            AddPlanActivity.ViewHolder holder = null;

            if (convertView == null) {
                convertView = View.inflate(AddPlanActivity.this, R.layout.memu_item, null);

                holder = new AddPlanActivity.ViewHolder();

                holder.tv = (TextView) convertView.findViewById(R.id.tv);

                convertView.setTag(holder);
            } else {
                holder = (AddPlanActivity.ViewHolder) convertView.getTag();
            }
            holder.tv.setText(mList.get(position));

            return convertView;
        }
    }

    public class ViewHolder {
        private TextView tv;
    }

    public static void actionStart(Context context,String host_id,String from_id,String host_name,String from_name,String type){
        Intent intent = new Intent(context,AddPlanActivity.class);
        intent.putExtra("host_id",host_id);
        intent.putExtra("from_id",from_id);
        intent.putExtra("host_name",host_name);
        intent.putExtra("from_name",from_name);
        intent.putExtra("type",type);
        context.startActivity(intent);
    }
}
