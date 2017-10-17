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

import java.util.ArrayList;


public class RankListAdapter extends RecyclerView.Adapter<RankListAdapter.ViewHolder> {

	private ArrayList<BaiDuRecommend> items;
	private Context context;


	static class ViewHolder extends RecyclerView.ViewHolder {
		ImageView imageviewVideo;
		TextView name;
		TextView updatetip;
		TextView act;
		TextView status_day;


		public ViewHolder(View view) {
			super(view);
			imageviewVideo = (ImageView) view.findViewById(R.id.imageview_video);
			name = (TextView) view.findViewById(R.id.textview_name);
			updatetip = (TextView) view.findViewById(R.id.textview_updatetip);
			act = (TextView) view.findViewById(R.id.textview_act);
			status_day = (TextView) view.findViewById(R.id.status_day);

		}
	}

	public RankListAdapter(ArrayList<BaiDuRecommend> items ) {
		this.items = items;

	}

	public void setData(ArrayList<BaiDuRecommend> items){
		this.items = items;
	}


	@Override
	public RankListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (context == null) {
			context = parent.getContext();
		}
		View view = LayoutInflater.from(context).inflate(R.layout.tab_home_listitem_video, parent, false);
		final RankListAdapter.ViewHolder holder = new RankListAdapter.ViewHolder(view);

		return holder;
	}

	@Override
	public void onBindViewHolder(RankListAdapter.ViewHolder holder, int position) {
		final BaiDuRecommend voide = items.get(position);

		if(position==0){
			holder.status_day.setBackgroundResource(R.drawable.video_icon_ranking_1_bg);
		}else if(position==1){
			holder.status_day.setBackgroundResource(R.drawable.video_icon_ranking_2_3_bg);
		}else if(position==2){
			holder.status_day.setBackgroundResource(R.drawable.video_icon_ranking_2_3_bg);
		}else{
			holder.status_day.setBackgroundResource(R.drawable.video_icon_ranking_bg);
		}
		holder.status_day.setText((position+1)+"");
		if(voide.getWorks_type().equals("movie")){
			String rating = voide.getRating();
			if (rating.equals("0")){
				holder.act.setText("");
			}else{
				holder.act.setText(voide.getRating()+"分");
			}

			holder.name.setText(voide.getTitle()+"");
			holder.updatetip.setText(voide.getActor()+"");
		}else if(voide.getWorks_type().equals("tvplay")){
			holder.name.setText(voide.getTitle()+"");
			holder.act.setText(voide.getActor()+"");
			holder.updatetip.setText(voide.getUpdate()+"");
		}else if(voide.getWorks_type().equals("tvshow")){
			holder.name.setText(voide.getTitle()+"");
			holder.act.setText(voide.getActor()+"");
			holder.updatetip.setText(voide.getUpdate()+"");
		}else if(voide.getWorks_type().equals("comic")){
			holder.name.setText(voide.getTitle()+"");
			holder.act.setText(voide.getType()+"");
			holder.updatetip.setText(voide.getUpdate()+"");
		}

		Glide.with(context).load(voide.getImg_url())
				.placeholder(R.drawable.bg_list_default)
				.error(R.drawable.bg_list_default)
				.into(holder.imageviewVideo);


		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(UIUtils.hasNetwork()){
					//
					try {

						Intent intent = new Intent();
						intent.putExtra("BaiDuRecommend",voide);
						if(voide.getWorks_type().equals("tvshow")){
							intent.setClass(context,MediaShowActivity.class);
						}else{
							intent.setClass(context,MediaActivity.class);
						}
						context.startActivity(intent);


					} catch (Exception e) {
						// TODO: handle exception
					}

				}else{
					UIUtils.toast(context.getText(R.string.tip_network).toString(),false);
				}
			}
		}) ;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public int getItemCount() {
		return items.size() <= 0 ? 0 : items.size();
	}


}
