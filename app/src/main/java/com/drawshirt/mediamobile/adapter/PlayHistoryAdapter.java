package com.drawshirt.mediamobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.activity.SystemVideoPlayerActivity;
import com.drawshirt.mediamobile.bean.PlayHistoryInfo;
import com.drawshirt.mediamobile.utils.FormatUtil.TimeFormat;

import java.util.List;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class PlayHistoryAdapter extends RecyclerView.Adapter<PlayHistoryAdapter.ViewHolder> {

    private final List<PlayHistoryInfo> list;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_video_title;
        TextView tv_video_time;
        ImageView iv_video_photo;
        TextView tv_video_position;

        public ViewHolder(View view) {
            super(view);
            iv_video_photo = (ImageView) view.findViewById(R.id.iv_history_photo);
            tv_video_title = (TextView) view.findViewById(R.id.tv_history_title);
            tv_video_time = (TextView) view.findViewById(R.id.tv_history_time);
            tv_video_position = (TextView) view.findViewById(R.id.tv_history_position);

        }
    }

    public PlayHistoryAdapter(List<PlayHistoryInfo> historyList) {
        this.list=historyList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.new_playhistory_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final PlayHistoryInfo playHistoryInfo = list.get(position);
        holder.tv_video_title.setText(playHistoryInfo.getTitle());
        if (!playHistoryInfo.getPosition().isEmpty()){
            holder.tv_video_position.setVisibility(View.VISIBLE);
            holder.tv_video_position.setText(playHistoryInfo.getPosition());
        }else{
            holder.tv_video_position.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=null;

                if (playHistoryInfo.getIs_net()==1){
                    intent = new Intent(mContext, SystemVideoPlayerActivity.class);
                    intent.setData(Uri.parse(list.get(position).getSource_url()));
                }else{
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(list.get(position).getSource_url()));
                }

                mContext.startActivity(intent);
            }
        });

        Long time = System.currentTimeMillis() - playHistoryInfo.getPlay_time();
        if (time < TimeFormat.DAY) {
            holder.tv_video_time.setText(TimeFormat.relative(mContext,time));
        } else {
            holder.tv_video_time.setText(TimeFormat.absolute(playHistoryInfo.getPlay_time()));
        }
        Glide.with(mContext).load(playHistoryInfo.getImage_url())
                .placeholder(R.drawable.bg_list_default)
                .error(R.drawable.bg_list_default)
                .fitCenter()
                .into(holder.iv_video_photo);

    }

    @Override
    public int getItemCount() {
        return list.size() <= 0 ? 0 : list.size();
    }




}
