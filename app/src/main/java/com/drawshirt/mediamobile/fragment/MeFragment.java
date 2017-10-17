package com.drawshirt.mediamobile.fragment;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.activity.HistoryActivity;
import com.drawshirt.mediamobile.common.BaseFragment;
import com.drawshirt.mediamobile.utils.UIUtils;
import com.drawshirt.mediamobile.view.MainTitlebar;
import com.loopj.android.http.RequestParams;

import butterknife.Bind;
import cn.waps.AppConnect;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class MeFragment extends BaseFragment {


    @Bind(R.id.main_title)
    MainTitlebar mMainTitle;
    @Bind(R.id.view_main_tab_my_listview)
    ListView mListview;
    private String[] strArray;
    private int[] imageArray;

    @Override
    protected String getUrl() {
        return null;
    }

    @Override
    protected RequestParams getParams() {
        return null;
    }

    @Override
    public void initTitle() {
        mMainTitle.show("我的影视大全");
    }

    @Override
    public void initData(String content) {
        String[] arrayOfString = new String[5];
        arrayOfString[0] = "播放历史";
        arrayOfString[1] = "升级";
        arrayOfString[2] = "自己应用";
        arrayOfString[3] = "推荐应用";
        arrayOfString[4] = "意见反馈";

        this.strArray = arrayOfString;// R.drawable.ic_listitem_favorite, R.drawable.ic_listitem_record,
        int[] arrayOfInt = {R.drawable.ic_listitem_record, R.drawable.ic_listitem_upgrade, R.drawable.ic_listitem_about
                , R.drawable.ic_listitem_record, R.drawable.ic_listitem_feedback};
        this.imageArray = arrayOfInt;
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_me;
    }

    private void initView()
    {


        ListViewAdapter localListViewAdapter = new ListViewAdapter();
        mListview.setAdapter(localListViewAdapter);
        ItemOnClickListener localItemOnClickListener = new ItemOnClickListener();
        mListview.setOnItemClickListener(localItemOnClickListener);
    }


    class ListViewAdapter extends BaseAdapter {

        class ViewHolder
        {
            TextView tv;
        }

        public ListViewAdapter()
        {

        }

        public int getCount()
        {
            return strArray.length;
        }

        public Object getItem(int paramInt)
        {
            return strArray[paramInt];
        }

        public long getItemId(int paramInt)
        {
            return paramInt;
        }

        public View getView(int position, View view, ViewGroup paramViewGroup)
        {
            ViewHolder holder;
            if (view == null) {
                view = View.inflate(getActivity(),R.layout.listitem_main_tab_my, null);
                holder = new ViewHolder();
                holder.tv = (TextView)view.findViewById(R.id.listitem_main_tab_my_tv);
                TextView localTextView3 = holder.tv;
                view.setTag(holder);
            }else{
                holder = (ViewHolder)view.getTag();
            }
            holder.tv.setText(strArray[position]);
            holder.tv.setCompoundDrawablesWithIntrinsicBounds(imageArray[position],0,0,0);

            return view;
        }
    }


    private class ItemOnClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            switch (i){
                case 0:
                    Intent intent = new Intent(getActivity(), HistoryActivity.class);//观看记录
                    startActivity(intent);
                    break;
                case 1:
                    AppConnect.getInstance(getActivity()).checkUpdate(getActivity());
                    break;
                case 2:
                    UIUtils.toast("抱谦，暂时还没有其它应用！",false);
                    //AppConnect.getInstance(getActivity()).showMore(getActivity(), "03dae6f7698e2cbb6438692a7f0764cd");
                    break;
                case 3:
                    AppConnect.getInstance(getActivity()).showOffers(getActivity());
                   // AppConnect.getInstance(getActivity()).showOffers(getActivity(), "yuoao86");
                    break;
                case 4:
                    AppConnect.getInstance(getActivity()).showFeedback(getActivity());
                    break;
            }
        }
    }
}
