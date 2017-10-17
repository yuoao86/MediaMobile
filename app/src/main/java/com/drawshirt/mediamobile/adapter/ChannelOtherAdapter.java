package com.drawshirt.mediamobile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.bean.ChannelOtherVideos;

import java.util.ArrayList;
import java.util.List;

import static com.drawshirt.mediamobile.R.id.scoreId;


public class ChannelOtherAdapter extends RecyclerView.Adapter<ChannelOtherAdapter.ViewHolder>{

	private OnItemClickListener mOnItemClickListener = null;
	private List<ChannelOtherVideos> list;
	private Context mContext;

	public void setData(ArrayList<ChannelOtherVideos> array) {
		list=array;
	}

	public void addData(ArrayList<ChannelOtherVideos> array) {
		list.addAll(array);
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		TextView tv_video_title;
		TextView tv_video_rating;
		ImageView iv_video_photo;

		public ViewHolder(View view) {
			super(view);
			iv_video_photo = (ImageView) view.findViewById(R.id.imageView1);
			tv_video_title = (TextView) view.findViewById(R.id.name);
			tv_video_rating = (TextView) view.findViewById(scoreId);

		}
	}

	public ChannelOtherAdapter(List<ChannelOtherVideos> list) {
		this.list = list;
	}

	@Override
	public ChannelOtherAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (mContext == null) {
			mContext = parent.getContext();
		}
		View view = LayoutInflater.from(mContext).inflate(R.layout.channel_other_view, parent, false);
		final ViewHolder holder = new ViewHolder(view);
		//view.setOnClickListener(this);

		return holder;
	}

	public interface OnItemClickListener{
		void onItemClick(View view , int position);
	}
	public void setOnItemClickListener(OnItemClickListener listener) {
		        this.mOnItemClickListener = listener;
	}

	@Override
	public void onBindViewHolder(ChannelOtherAdapter.ViewHolder holder, final int position) {

		ChannelOtherVideos video = list.get(position);

		holder.tv_video_title.setText(video.getTitle());
		holder.tv_video_rating.setText(video.getDuration()+"");
		Glide.with(mContext).load(video.getImgv_url())
				.placeholder(R.drawable.bg_list_default)
				.error(R.drawable.bg_list_default)
				.into(holder.iv_video_photo);



		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mOnItemClickListener.onItemClick(view,position);

			}
		});
	}



	@Override
	public int getItemCount() {
		return list.size() <= 0 ? 0 : list.size();
	}
}
