package com.tt.timemanage;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tt.timemanage.widget.CustomDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddPlanMyselfActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout begin, end;
    private TextView current_begin, current_end;
    private CustomDatePicker customDatePicker1, customDatePicker2;

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan_myself);

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
        }
    }

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        current_begin.setText(now);
        current_end.setText(now);

        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                current_begin.setText(time);
            }
        }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(true); // 不显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动

        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                current_end.setText(time);
            }
        }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
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
}
