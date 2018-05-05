package com.tt.timemanage.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tt.timemanage.R;
import com.tt.timemanage.model.Friendship;

import java.util.List;

/**
 * Created by TT on 2018/5/1.
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_HEADER = 2;

    private List<Friendship> friendshipList;

    public FriendListAdapter(List<Friendship> friendshipList){
        this.friendshipList = friendshipList;
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

        TextView friend_tv;
        LinearLayout item;

        public NormalViewHolder(View view){
            super(view);
            friend_tv = (TextView) view.findViewById(R.id.friend_tv);
            item = (LinearLayout) view.findViewById(R.id.fitem);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Friendship friendship = friendshipList.get(position);
        if (friendship.getUid1().equals("-10086")) {
            return TYPE_HEADER;
        } else{
            return TYPE_NORMAL;
        }
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
                viewHolder = new NormalViewHolder(inflater.inflate(R.layout.friend_list_item, parent, false));
                break;
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Friendship friendship = friendshipList.get(position);
        if (holder instanceof NormalViewHolder) {
            NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
            normalViewHolder.friend_tv.setText(friendship.getName());
            normalViewHolder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        listener.itemClick(position);
                    }
                }
            });
            normalViewHolder.item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(longListener != null){
                        longListener.itemClick(position);
                    }
                    return true;
                }
            });
        }else if (holder instanceof HeaderViewHolder){
            HeaderViewHolder headerViewHolder = (HeaderViewHolder)holder;
        }
    }

    public OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void itemClick(int position);
    }

    public LongOnItemClickListener longListener;

    public void setLongOnItemClickListener(LongOnItemClickListener longListener){
        this.longListener = longListener;
    }

    public interface LongOnItemClickListener{
        void itemClick(int position);
    }

    @Override
    public int getItemCount() {
        return friendshipList.size();
    }
}
