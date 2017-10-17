package com.drawshirt.mediamobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.activity.MediaActivity;
import com.drawshirt.mediamobile.activity.MediaShowActivity;
import com.drawshirt.mediamobile.bean.BaiDuRecommend;
import com.drawshirt.mediamobile.utils.UIUtils;

import java.util.List;


/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class ChannelMovieAdapter extends RecyclerView.Adapter<ChannelMovieAdapter.ViewHolder> {

    private List<BaiDuRecommend> list;
    private Context mContext;


    //private OnItemClickListener mOnItemClickListener = null;


    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_video_title;
        TextView tv_video_rating;
        ImageView iv_video_photo;

        public ViewHolder(View view) {
            super(view);
            iv_video_photo = (ImageView) view.findViewById(R.id.imageView1);
            tv_video_title = (TextView) view.findViewById(R.id.txt_loading);
            tv_video_rating = (TextView) view.findViewById(R.id.scoreId);

        }
    }

//    public static interface OnItemClickListener {
//        void onItemClick(View view , int position);
//    }

    public void addData(List<BaiDuRecommend> arrayList) {
        this.list.addAll(arrayList);
    }

    public void setData(List<BaiDuRecommend> arrayList){
        this.list=arrayList;
    }


    public ChannelMovieAdapter(List<BaiDuRecommend> list) {
        this.list = list;
    }

    @Override
    public ChannelMovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_gridview, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        //view.setOnClickListener(this);

        return holder;
    }

//    public void setOnItemClickListener(OnItemClickListener listener) {
//        this.mOnItemClickListener = listener;
//    }

    @Override
    public void onBindViewHolder(ChannelMovieAdapter.ViewHolder holder, int position) {
        final BaiDuRecommend video = list.get(position);
        holder.tv_video_title.setText(video.getTitle());
        String rating = video.getRating();
        String type=video.getWorks_type();
        if (type.equals("movie")){
            holder.tv_video_rating.setText(rating+"分");
        }else{
            holder.tv_video_rating.setText("");
        }

        Glide.with(mContext).load(video.getImg_url())
                .placeholder(R.drawable.bg_list_default)
                .error(R.drawable.bg_list_default)
                .into(holder.iv_video_photo);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UIUtils.hasNetwork()){
                    Intent intent;

                    if(video.getWorks_type().equals("tvshow")){
                        intent=new Intent(mContext,MediaShowActivity.class);
                        //intent.setClass(mContext,MediaShowActivity.class);
                    }else{
                        intent=new Intent(mContext,MediaActivity.class);
                    }
                    intent.putExtra("BaiDuRecommend",video);
                    mContext.startActivity(intent);
                }else{
                    UIUtils.toast(mContext.getText(R.string.tip_network).toString(),false);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size() <= 0 ? 0 : list.size();
    }
}
