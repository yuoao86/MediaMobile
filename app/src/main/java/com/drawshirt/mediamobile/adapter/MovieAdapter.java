package com.drawshirt.mediamobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.activity.MediaSearchActivity;
import com.drawshirt.mediamobile.bean.SearchData;

import java.util.ArrayList;


public class MovieAdapter extends BaseAdapter {

	private  ArrayList<SearchData> items;
	private LayoutInflater mInflater;	

	Context context;

//	private  String url ;
//	private String flag;
	public MovieAdapter(Context context, ArrayList<SearchData> items ) {
		super();
		this.context = context;
		this.items = items; 

		this.mInflater = LayoutInflater.from(context); 

	 
	}
	
	public void setData(ArrayList<SearchData> items){
		this.items = items;
	}
	
	@Override
	public int getCount() {
		if (this.items != null && this.items.size() > 0)
			return items.size();
		return 0;
	}

	public Object getItem(int position) { 
		return items.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try{ 
			if(items !=null&& items.size() >position){
				final SearchData voide = (SearchData) items.get(position);
				convertView = mInflater.inflate(R.layout.tab_home_listitem_video, null);
				final ImageView imageviewVideo = (ImageView) convertView .findViewById(R.id.imageview_video);
				TextView name = (TextView)convertView.findViewById(R.id.textview_name);
				TextView updatetip = (TextView)convertView.findViewById(R.id.textview_updatetip); 
				TextView act = (TextView)convertView.findViewById(R.id.textview_act);
				name.setText(voide.getTitle()+"");
				
				
				ArrayList<String> actors_name = voide.getActors_name();
				String actorsName = "";
				for(int i=0;i<actors_name.size();i++){
					actorsName = actorsName + actors_name.get(i) + "  ";
				}
				if(!actorsName.equals("")){
					act.setText("演员："+actorsName); 
				}
				
				ArrayList<String> directors_name = voide.getDirectors_name();
				String directorsName = "";
				for(int i=0;i<directors_name.size();i++){
					directorsName = directorsName + directors_name.get(i) + "  ";
				}
				if(!directorsName.equals("")){
					updatetip.setText("导演："+directorsName); 
				}
			

				Glide.with(context).load(voide.getCover_url())
						.placeholder(R.drawable.bg_list_default)
						.error(R.drawable.bg_list_default)
						.into(imageviewVideo);
				convertView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(voide!=null ){
							 SearchData baiDuRecommend = voide;
							 Intent intent = new Intent();
							 intent.putExtra("BaiDuRecommend",baiDuRecommend);
				             intent.setClass(context,MediaSearchActivity.class);
				             context.startActivity(intent);
						 } 
					}
				});

			}
			
		}catch (Exception e) {

			e.printStackTrace();
		}
		return convertView;
		
	} 

}
