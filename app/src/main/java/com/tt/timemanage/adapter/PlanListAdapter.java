package com.tt.timemanage.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tt.timemanage.HomePageActivity;
import com.tt.timemanage.R;
import com.tt.timemanage.model.PlanData;

import java.util.List;
import java.util.Locale;

/**
 * Created by TT on 2018/4/27.
 */

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.ViewHolder> {

    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_HEADER = 2;

    private List<PlanData> planDataList;
    private HomePageActivity activity;

    public PlanListAdapter(List<PlanData> planDataList,HomePageActivity activity){
        this.planDataList = planDataList;
        this.activity = activity;
    }

    //----------------------------------ViewHolder数据模型---------------------------
    //ViewHolder模型
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    //头部ViewHolder
    public class HeaderViewHolder extends ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class NormalViewHolder extends ViewHolder{

        TextView description;
        TextView from_id;
        TextView begin;
        TextView end;
        Button complete;
        Button edit;

        public NormalViewHolder(View view){
            super(view);
            description = (TextView)view.findViewById(R.id.description);
            from_id = (TextView)view.findViewById(R.id.from_id);
            begin = (TextView)view.findViewById(R.id.begin);
            end = (TextView)view.findViewById(R.id.end);
            complete = (Button)view.findViewById(R.id.complete);
            edit = (Button)view.findViewById(R.id.edit);
        }
    }

    @Override
    public int getItemViewType(int position) {
        PlanData planData = planDataList.get(position);
        if (planData.getPid()==-10086) {
            return TYPE_HEADER;
        } else{
            return TYPE_NORMAL;
        }
    }

    private static String TimeStamp2Date(String timestampString, String formats) {//将Unix时间截转换成能看懂的字符串
        if (TextUtils.isEmpty(formats))
            formats = "yyyy-MM-dd HH:mm";
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new java.text.SimpleDateFormat(formats, Locale.CHINA).format(new java.util.Date(timestamp));
        return date;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_HEADER:
                viewHolder = new HeaderViewHolder(inflater.inflate(R.layout.plan_list_head, parent, false));
                break;
            case TYPE_NORMAL:
                viewHolder = new NormalViewHolder(inflater.inflate(R.layout.plan_list_item, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PlanData planData = planDataList.get(position);
        if (holder instanceof NormalViewHolder) {
            NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
            normalViewHolder.description.setText(planData.getDescription());
            normalViewHolder.from_id.setText(planData.getFrom_name());
            normalViewHolder.begin.setText(TimeStamp2Date(Integer.toString(planData.getBegin()), "MM-dd HH:mm"));
            normalViewHolder.end.setText(TimeStamp2Date(Integer.toString(planData.getEnd()), "MM-dd HH:mm"));
        }else if (holder instanceof HeaderViewHolder){
            HeaderViewHolder headerViewHolder = (HeaderViewHolder)holder;
        }
    }

    @Override
    public int getItemCount() {
        return planDataList.size();
    }

}
