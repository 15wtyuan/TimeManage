package com.tt.timemanage.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tt.timemanage.BaseActivity;
import com.tt.timemanage.R;
import com.tt.timemanage.model.UserData;

import org.litepal.crud.DataSupport;

import java.util.Arrays;
import java.util.List;

/**
 * Created by TT on 2018/4/18.
 */
//侧边栏打开，看个人设置个人信息等，用Drawer layout 和RecycleView实现
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {

    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_HEADER = 2;
    private BaseActivity activity;

    public DrawerAdapter(BaseActivity activity){
        this.activity =activity;
    }

    private List<DrawerItem> dataList = Arrays.asList(
            new DrawerItemHeader(),
            new DrawerItemNormal(R.mipmap.pengyou, "我的朋友"),
            new DrawerItemNormal(R.mipmap.news,"我的消息"),
            new DrawerItemNormal(R.mipmap.guanbi, "退出登陆")
    );


    @Override
    public int getItemViewType(int position) {
        DrawerItem drawerItem = dataList.get(position);
        if (drawerItem instanceof DrawerItemHeader) {
            return TYPE_HEADER;
        } else if (drawerItem instanceof DrawerItemNormal) {
            return TYPE_NORMAL;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return (dataList == null || dataList.size() == 0) ? 0 : dataList.size();
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DrawerViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_HEADER:
                viewHolder = new HeaderViewHolder(inflater.inflate(R.layout.nav_header, parent, false));
                break;
            case TYPE_NORMAL:
                viewHolder = new NormalViewHolder(inflater.inflate(R.layout.my_item, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder holder, int position) {
        final DrawerItem item = dataList.get(position);
        if (holder instanceof NormalViewHolder) {
            NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
            final DrawerItemNormal itemNormal = (DrawerItemNormal) item;
            normalViewHolder.iv.setBackgroundResource(itemNormal.iconRes);
            normalViewHolder.tv.setText(itemNormal.name);

            normalViewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        listener.itemClick(itemNormal);

                    }
                }
            });
        }else if(holder instanceof HeaderViewHolder){
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            List<UserData> userDatas = DataSupport.findAll(UserData.class);
            UserData userData = new UserData();
            for (UserData temp:userDatas){
                userData = temp;
            }
            headerViewHolder.my_name.setText(userData.getName());
            String temp = "";
            try{
                temp = userData.getSculpture();
            }catch (Exception e) {
                e.printStackTrace();
            }
            Glide
                    .with(activity)
                    .load(temp)
                    .centerCrop()
                    .placeholder(R.drawable.jiazai)
                    .into(headerViewHolder.head_portrait);

            headerViewHolder.view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.showToase("我的");
                        }
                    });
                }
            });
        }

    }

    public OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void itemClick(DrawerItemNormal drawerItemNormal);
    }




    //-------------------------item数据模型------------------------------
    // drawerlayout item统一的数据模型
    public interface DrawerItem {
    }


    //有图片和文字的item
    public class DrawerItemNormal implements DrawerItem {
        public int iconRes;
        public String name;

        public DrawerItemNormal(int iconRes, String name) {
            this.iconRes = iconRes;
            this.name = name;
        }

    }


    //头部item
    public class DrawerItemHeader implements DrawerItem{
        public DrawerItemHeader() {
        }
    }



    //----------------------------------ViewHolder数据模型---------------------------
    //抽屉ViewHolder模型
    public class DrawerViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public DrawerViewHolder(View itemView) {
            super(itemView);
        }
    }

    //有图标有文字ViewHolder
    public class NormalViewHolder extends DrawerViewHolder {

        public TextView tv;
        public ImageView iv;
        public ImageButton ib;

        public NormalViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv = (TextView) itemView.findViewById(R.id.my_item_name);
            iv = (ImageView) itemView.findViewById(R.id.my_item_image);
            ib = (ImageButton) itemView.findViewById(R.id.my_item_button);
        }
    }


    //头部ViewHolder
    public class HeaderViewHolder extends DrawerViewHolder {

        private ImageView head_portrait;
        private TextView my_name;
        private ImageButton imageButton;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            head_portrait = (ImageView)itemView.findViewById(R.id.head_portrait);
            my_name = (TextView)itemView.findViewById(R.id.my_name);
            imageButton = (ImageButton)itemView.findViewById(R.id.goto_mypage_button);
        }
    }
}
