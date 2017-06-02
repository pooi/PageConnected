package ga.pageconnected.pageconnected.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.Information;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.util.AdditionalFunc;
import ga.pageconnected.pageconnected.util.CustomViewPager;
import ga.pageconnected.pageconnected.util.PagerContainer;
import ga.pageconnected.pageconnected.util.ParsePHP;

public class ArticleFragment extends BaseFragment {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_MAKE_ENDLESS_LIST = 501;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;
    private final int MSG_MESSAGE_SHOW_LOADING = 503;

    // UI
    private View view;
    private Context context;


    private AVLoadingIndicatorView loading;
    private MaterialDialog progressDialog;
    private TextView tv_msg;

    private PagerContainer viewPagerContainer;
    private CustomViewPager viewPager;
    private NavigationAdapter pagerAdapter;

    private int page = 0;
    private String search;
    private boolean isLoadFinish;
    private ArrayList<HashMap<String, Object>> list;
    private ArrayList<HashMap<String, Object>> tempList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        if(getArguments() != null) {

        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_article, container, false);
        context = container.getContext();

        list = new ArrayList<>();
        tempList = new ArrayList<>();

        init();

        return view;
    }

    private void init(){

        tv_msg = (TextView)view.findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);

        viewPagerContainer = (PagerContainer)view.findViewById(R.id.view_pager_container);
        viewPager = (CustomViewPager) view.findViewById(R.id.view_pager);

        loading = (AVLoadingIndicatorView)view.findViewById(R.id.loading);
        progressDialog = new MaterialDialog.Builder(context)
                .content("잠시만 기다려주세요.")
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

        pagerAdapter = new NavigationAdapter(getFragmentManager(), list);
        viewPager.setAdapter(pagerAdapter);
//        viewPager.setPageMargin(0);
//        viewPager.setClipChildren(false);

    }

    private void getArticleList(){
        if(!isLoadFinish) {
            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SHOW_LOADING));

            HashMap<String, String> map = new HashMap<>();
            map.put("service", "getArticle");
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


    private static class NavigationAdapter extends FragmentStatePagerAdapter {

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

            return f;
        }

        @Override
        public int getCount(){
            return list.size();
        }


    }


}
