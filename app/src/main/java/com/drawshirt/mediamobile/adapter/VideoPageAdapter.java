package com.drawshirt.mediamobile.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.bean.LocalMediaItem;
import com.drawshirt.mediamobile.utils.UIUtils;
import com.drawshirt.mediamobile.utils.Utils;

import java.io.File;
import java.util.ArrayList;


/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class VideoPageAdapter extends BaseAdapter {

    private ArrayList<LocalMediaItem> mediaItems;
    private Context context;
    private Utils utils;
    private boolean isVideo;

    public VideoPageAdapter(Context context, ArrayList mediaItems, boolean isVideo ){
        this.mediaItems=mediaItems;
        this.context=context;
        utils=new Utils();
        this.isVideo=isVideo;
    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView==null){
            convertView = View.inflate(context, R.layout.list_block, null);
            holder=new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.block_name);
            holder.tv_duration = (TextView) convertView.findViewById(R.id.block_duration);
            holder.tv_size = (TextView) convertView.findViewById(R.id.block_size);
            holder.item_icon=(ImageView) convertView.findViewById(R.id.block_thumbnail);
            holder.item_audio_icon=(ImageView) convertView.findViewById(R.id.iv_audio_icon);
            holder.rl_block=(RelativeLayout) convertView.findViewById(R.id.rl_block);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        LocalMediaItem mediaItem=mediaItems.get(i);
        holder.tv_name.setText(mediaItem.getName());
        holder.tv_size.setText(android.text.format.Formatter.formatFileSize(context,mediaItem.getSize()));
        holder.tv_duration.setText(UIUtils.stringForTime((int) mediaItem.getDuration()));

        if (!isVideo){
            holder.item_audio_icon.setVisibility(View.VISIBLE);
        }else {
            holder.rl_block.setVisibility(View.VISIBLE);
            Glide.with(context).load(Uri.fromFile(new File(mediaItem.getData())))
                    .placeholder(R.drawable.bg_default)
                    .into(holder.item_icon);
        }

        return convertView;
    }

    class ViewHolder{
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
        ImageView item_icon;
        ImageView item_audio_icon;
        RelativeLayout rl_block;
    }
}
