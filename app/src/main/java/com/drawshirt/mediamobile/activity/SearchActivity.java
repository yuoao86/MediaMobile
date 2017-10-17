package com.drawshirt.mediamobile.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.adapter.MovieAdapter;
import com.drawshirt.mediamobile.bean.SearchData;
import com.drawshirt.mediamobile.common.AppNetConfig;
import com.drawshirt.mediamobile.common.BaseActivity;
import com.drawshirt.mediamobile.utils.UIUtils;
import com.drawshirt.mediamobile.utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.Bind;

import static com.drawshirt.mediamobile.R.id.iamge;
import static com.drawshirt.mediamobile.R.id.searchButton;

public class SearchActivity extends BaseActivity {


    @Bind(R.id.edit_search)
    EditText mEditSearch;
    @Bind(searchButton)
    ImageView mSearchButton;
    @Bind(R.id.layout_search_bar)
    LinearLayout mLayoutSearchBar;
    @Bind(R.id.view_main_tab_my_listview)
    ListView searchListview;
    @Bind(iamge)
    ImageView mIamge;
    private ArrayList<SearchData> searchData;
    private MovieAdapter myAdapter;
    private String mySearchText;

    @Override
    protected String getUrl() {
        return null;
    }

    @Override
    protected RequestParams getParams() {
        return null;
    }

    @Override
    protected void initData(String content) {

        myAdapter = new MovieAdapter(SearchActivity.this, searchData);
        searchListview.setAdapter(myAdapter);
        mEditSearch.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() != 0) {
                    mIamge.setVisibility(View.GONE);
                    mySearchText = s.toString();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {
            }
        });
        mSearchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {

                    if(mySearchText !=null &&  !mySearchText.equals("")){
                        mySearchText  = URLEncoder.encode(mySearchText, "utf-8") ;
                        if(UIUtils.hasNetwork()){
                            Utils.startWaitingDialog(SearchActivity.this);
                            String url = "http://search.shouji.baofeng.com/msearch_type.php?query="+mySearchText+"&offset=0&limit=10&mtype=normal&ver="+ AppNetConfig.versionBaoFeng;
                            client.get(url,new AsyncHttpResponseHandler(){
                                @Override
                                public void onSuccess(String content) {
                                    super.onSuccess(content);

                                    ArrayList<SearchData> object = parserSearch(content);

                                    if(object!=null){
                                        searchData =object;
                                        myAdapter.setData(searchData);
                                        myAdapter.notifyDataSetChanged();
                                        Utils.closeWaitingDialog();
                                    }
                                }

                                @Override
                                public void onFailure(Throwable error, String content) {
                                    super.onFailure(error, content);
                                }
                            });
                        }else{
                            UIUtils.toast(SearchActivity.this.getText(R.string.tip_network).toString(),false);
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }


    private ArrayList<SearchData>  parserSearch(String s){
        ArrayList<SearchData> resultArr = new ArrayList<SearchData>();
        try {
            JSONObject js = new JSONObject(s);
            JSONArray resultArray = js.optJSONArray("result");
            for(int i=0;i<resultArray.length();i++){
                JSONObject resultObject = resultArray.optJSONObject(i);
                SearchData searchData = new SearchData();

                JSONArray actorsNameJsonArray = resultObject.optJSONArray("actors_name");
                ArrayList<String> actorsName = new ArrayList<String>();
                for(int j=0;j<actorsNameJsonArray.length();j++){
                    actorsName.add(actorsNameJsonArray.optString(j));
                }
                searchData.setActors_name(actorsName);

                searchData.setArea_l(resultObject.optString("area_l"));
                searchData.setCover_url(resultObject.optString("cover_url"));

                JSONArray directorsNameJsonArray = resultObject.optJSONArray("directors_name");
                ArrayList<String> directorsName = new ArrayList<String>();
                for(int j=0;j<directorsNameJsonArray.length();j++){
                    actorsName.add(directorsNameJsonArray.optString(j));
                }

                searchData.setDirectors_name(directorsName);

                searchData.setId(resultObject.optInt("id"));
                searchData.setLast_seq(resultObject.optString("last_seq"));
                searchData.setMax_site(resultObject.optString("max_site"));
                searchData.setScore(resultObject.optDouble("score"));
                searchData.setStyle_l(resultObject.optString("style_l"));
                searchData.setTitle(resultObject.optString("title"));
                searchData.setTotal(resultObject.optString("total"));
                searchData.setType_l(resultObject.optString("type_l"));
                searchData.setUpdate_time(resultObject.optString("update_time"));

                resultArr.add(searchData);
            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return resultArr;
    }

}
