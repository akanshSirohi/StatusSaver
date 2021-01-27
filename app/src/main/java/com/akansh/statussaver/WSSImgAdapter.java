package com.akansh.statussaver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WSSImgAdapter extends RecyclerView.Adapter<WSSImgAdapter.MyViewHolder> {

    Context ctx;
    List<WAStatus> statusList;

    WSSItemListener wssItemListener;

    public interface WSSItemListener {
        void onClickItem(View v,int position);
        void onSaveItem(View v,int position);
        void onShareItem(View v,int position);
    }

    public WSSImgAdapter(Context ctx, List<WAStatus> statusList) {
        this.ctx = ctx;
        this.statusList = statusList;
    }

    public void setWssItemListener(WSSItemListener wssItemListener) {
        this.wssItemListener = wssItemListener;
    }

    public void addItem(WAStatus status) {
        statusList.add(statusList.size(),status);
        notifyDataSetChanged();
    }

    public WAStatus getItem(int position) {
        return statusList.get(position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(ctx).inflate(R.layout.wss_item_img,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        WAStatus status=this.statusList.get(position);
        if(status.getThumbnail()==null) {
            holder.thumb.setImageDrawable(ctx.getDrawable(R.drawable.ic_launcher));
        }else{
            holder.thumb.setImageBitmap(status.getThumbnail());
        }
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumb;
        public ImageButton save,share;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.img_thumb);
            save = itemView.findViewById(R.id.img_save);
            share = itemView.findViewById(R.id.img_share);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wssItemListener.onSaveItem(v,getAdapterPosition());
                }
            });
            thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wssItemListener.onClickItem(v,getAdapterPosition());
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wssItemListener.onShareItem(v,getAdapterPosition());
                }
            });
        }
    }
    public void clearAll() {
        statusList.clear();
        notifyDataSetChanged();
    }
}
