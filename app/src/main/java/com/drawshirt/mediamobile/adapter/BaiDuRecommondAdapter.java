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
public class BaiDuRecommondAdapter extends RecyclerView.Adapter<BaiDuRecommondAdapter.ViewHolder> {

    private final List<BaiDuRecommend> list;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_video_title;
        TextView tv_video_rating;
        ImageView iv_video_photo;

        public ViewHolder(View view) {
            super(view);
            iv_video_photo = (ImageView) view.findViewById(R.id.iv_video_photo);
            tv_video_title = (TextView) view.findViewById(R.id.tv_video_title);
            tv_video_rating = (TextView) view.findViewById(R.id.tv_video_rating);

        }
    }

    public BaiDuRecommondAdapter(List<BaiDuRecommend> list) {
        this.list = list;
    }

    @Override
    public BaiDuRecommondAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_list_item_video, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(BaiDuRecommondAdapter.ViewHolder holder, int position) {
        final BaiDuRecommend video = list.get(position);
        holder.tv_video_title.setText(video.getTitle());
        String type=video.getWorks_type();
        if (type.equals("movie")){
            double rating = Integer.parseInt(video.getRating())*0.1;
            holder.tv_video_rating.setText(String.valueOf(new java.text.DecimalFormat("#.0").format(rating))+"分");
        }else{
            holder.tv_video_rating.setText(video.getUpdate());
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
