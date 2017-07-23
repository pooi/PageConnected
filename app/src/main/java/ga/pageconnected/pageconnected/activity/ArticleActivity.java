package ga.pageconnected.pageconnected.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.BaseActivity;
import ga.pageconnected.pageconnected.Information;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.fragment.ArticleItemFragment;
import ga.pageconnected.pageconnected.util.AdditionalFunc;
import ga.pageconnected.pageconnected.util.CustomViewPager;
import ga.pageconnected.pageconnected.util.PagerContainer;
import ga.pageconnected.pageconnected.util.ParsePHP;

public class ArticleActivity extends BaseActivity {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_MAKE_ENDLESS_LIST = 501;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;
    private final int MSG_MESSAGE_SHOW_LOADING = 503;

    private AVLoadingIndicatorView loading;
    private MaterialDialog progressDialog;
    private TextView tv_msg;
    private TextView toolbarTitle;

    private PagerContainer viewPagerContainer;
    private CustomViewPager viewPager;
    private NavigationAdapter pagerAdapter;

    private int page = 0;
    private String userId;
    private String day;
    private String search;
    private boolean isLoadFinish;
    private ArrayList<HashMap<String, Object>> list;
    private ArrayList<HashMap<String, Object>> tempList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        day = getIntent().getStringExtra("day");
        userId = getIntent().getStringExtra("userId");
        search = getIntent().getStringExtra("search");

        list = new ArrayList<>();
        tempList = new ArrayList<>();

        init();

    }

    private void init(){
        String d;
        if(day.equals("0")){
            d = getResources().getString(R.string.before_the_competition);
        }else if(day.equals("%")) {
            d = getResources().getString(R.string.search_result);
        }else{
            d = String.format(getResources().getString(R.string.date_str), day.substring(0,4), day.substring(4,6), day.substring(6, 8));
        }
        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        toolbarTitle.setText(d);

        tv_msg = (TextView)findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);

        viewPagerContainer = (PagerContainer)findViewById(R.id.view_pager_container);
        viewPager = (CustomViewPager) findViewById(R.id.view_pager);

        loading = (AVLoadingIndicatorView)findViewById(R.id.loading);
        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .build();

        getArticleList();

    }

    private void initLoadValue(){
        page = 0;
        isLoadFinish = false;
    }

    private void loadViewPager(){

        pagerAdapter = new NavigationAdapter(getSupportFragmentManager(), list);
        viewPager.setOffscreenPageLimit(list.size());
        viewPager.setAdapter(pagerAdapter);
//        viewPager.setPageMargin(0);
        viewPager.setClipChildren(false);


    }

    private void getArticleList(){
        if(!isLoadFinish) {
            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SHOW_LOADING));

            HashMap<String, String> map = new HashMap<>();
            map.put("service", "getArticle");
            map.put("userId", userId);
            map.put("requestUserId", getUserID(this));
//            if(userId != null && !userId.equals("")){
//                map.put("userId", userId);
//            }
            map.put("day", day);
            map.put("page", Integer.toString(page));
            if (search != null && (!"".equals(search))) {
                map.put("search", search);
            }
            new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

                @Override
                protected void afterThreadFinish(String data) {

                    if (page <= 0) {
                        list.clear();

                        list = AdditionalFunc.getArticleList(data);

                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));

                    } else {

                        tempList.clear();
                        tempList = AdditionalFunc.getArticleList(data);
                        if (tempList.size() < 30) {
                            isLoadFinish = true;
                        }
                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_ENDLESS_LIST));

                    }

                }
            }.start();
        }else{
//            if(adapter != null){
//                adapter.setLoaded();
//            }
        }
    }

    private void checkMsg(){
        if(list.size()>0){
            tv_msg.setVisibility(View.GONE);
        }else{
            tv_msg.setVisibility(View.VISIBLE);
        }
    }

    public void makeList(){

        checkMsg();

        loadViewPager();

    }

    private void addList(){

        for(int i=0; i<tempList.size(); i++){
            list.add(tempList.get(i));
        }
        // TODO
//        adapter.setLoaded();

    }

    private class MyHandler extends Handler {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_MAKE_LIST:
                    progressDialog.hide();
                    loading.hide();
                    makeList();
                    break;
                case MSG_MESSAGE_MAKE_ENDLESS_LIST:
                    progressDialog.hide();
                    loading.hide();
                    addList();
                    break;
                case MSG_MESSAGE_PROGRESS_HIDE:
                    progressDialog.hide();
                    loading.hide();
                    break;
                case MSG_MESSAGE_SHOW_LOADING:
                    loading.show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case ShowLayoutActivity.UPDATE_HEART:
                if(data != null) {
                    int pos = data.getIntExtra("position", -1);
                    if (pos >= 0) {
                        boolean heartAble = data.getBooleanExtra("heartAble", false);
                        list.get(pos).put("heartAble", heartAble);
                        int heart = data.getIntExtra("heart", 0);
                        list.get(pos).put("heart", heart);
//                        ArticleItemFragment fragment = (ArticleItemFragment) pagerAdapter.getItem(pos);
//                        if(fragment != null) {
//                            fragment.setItem(list.get(pos));
//                        }
                    }
                }
                break;
            default:
                break;
        }

    }


    private static class NavigationAdapter extends FragmentPagerAdapter {

        private ArrayList<HashMap<String, Object>> list;

        public NavigationAdapter(FragmentManager fm, ArrayList<HashMap<String, Object>> list){
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f;
            final int pattern = position % list.size();

            f = new ArticleItemFragment();
            Bundle bdl = new Bundle(1);
            bdl.putInt("position", pattern);
            bdl.putSerializable("data", list.get(pattern));
            f.setArguments(bdl);
//            f = new ArticleItemFragment();

            return f;
        }

        @Override
        public int getCount(){
            return list.size();
        }


    }

}
