package com.drawshirt.mediamobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.activity.SystemVideoPlayerActivity;
import com.drawshirt.mediamobile.bean.LocalMediaItem;
import com.drawshirt.mediamobile.bean.PlayHistoryInfo;
import com.drawshirt.mediamobile.dao.PlayHistoryDao;

import java.util.ArrayList;

/**
 * 作者：杨光福 on 2016/7/18 10:16
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：NetVideoPager的适配器
 */
public class NetVideoPagerAdapter extends RecyclerView.Adapter<NetVideoPagerAdapter.ViewHolder> {

    private  Context context;
    private final ArrayList<LocalMediaItem> mediaItems;
    private PlayHistoryDao dao = null;

    public NetVideoPagerAdapter(Context context,ArrayList<LocalMediaItem> mediaItems){
        this.mediaItems = mediaItems;
        this.context=context;
        dao=new PlayHistoryDao(context);
    }

    @Override
    public NetVideoPagerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_netvideo_pager, parent, false);
        final NetVideoPagerAdapter.ViewHolder holder = new NetVideoPagerAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final LocalMediaItem localMediaItem = mediaItems.get(position);

        holder.tv_name.setText(localMediaItem.getName());
        holder.tv_desc.setText(localMediaItem.getDesc());
        Glide.with(context).load(localMediaItem.getImageUrl())
                .placeholder(R.drawable.bg_default)
                .error(R.drawable.bg_default)
                .into(holder.iv_icon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //3.传递列表数据-对象-序列化
                Intent intent = new Intent(context,SystemVideoPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("videolist",mediaItems);
                intent.putExtras(bundle);
                intent.putExtra("position",position);

                final PlayHistoryInfo playHistoryInfo = new PlayHistoryInfo();
                playHistoryInfo.setTitle(localMediaItem.getName());
                playHistoryInfo.setPosition(localMediaItem.getDesc());
                playHistoryInfo.setImage_url(localMediaItem.getImageUrl());
                playHistoryInfo.setSource_url(localMediaItem.getData());
                playHistoryInfo.setIs_net(1);
                playHistoryInfo.setPlay_time(System.currentTimeMillis());
                if(dao!=null ) {
                    new Thread() {
                        @Override
                        public void run() {
                            dao.insert(playHistoryInfo);
                        }
                    }.start();
                }
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mediaItems.size() <= 0 ? 0 : mediaItems.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_desc = (TextView) itemView.findViewById(R.id.tv_desc);
        }
    }
}

