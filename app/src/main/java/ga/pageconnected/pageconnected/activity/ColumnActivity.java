package ga.pageconnected.pageconnected.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.BaseActivity;
import ga.pageconnected.pageconnected.Information;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.adapter.ColumnListCustomAdapter;
import ga.pageconnected.pageconnected.fragment.ArticleItemFragment;
import ga.pageconnected.pageconnected.util.AdditionalFunc;
import ga.pageconnected.pageconnected.util.CustomViewPager;
import ga.pageconnected.pageconnected.util.DividerItemDecoration;
import ga.pageconnected.pageconnected.util.OnAdapterSupport;
import ga.pageconnected.pageconnected.util.OnLoadMoreListener;
import ga.pageconnected.pageconnected.util.PagerContainer;
import ga.pageconnected.pageconnected.util.ParsePHP;
import ga.pageconnected.pageconnected.util.UpdateItem;

public class ColumnActivity extends BaseActivity implements OnAdapterSupport {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_MAKE_ENDLESS_LIST = 501;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;


    private TextView tv_msg;
    private TextView toolbarTitle;
    private AVLoadingIndicatorView loading;
    private MaterialDialog progressDialog;

    private String userId;
    private String day;
    private int page = 0;
    private String search;
    private ArrayList<HashMap<String, Object>> tempList;
    private ArrayList<HashMap<String, Object>> list;

    // Recycle View
    private RecyclerView rv;
    private LinearLayoutManager mLinearLayoutManager;
    private ColumnListCustomAdapter adapter;
    private boolean isLoadFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_column);

        day = getIntent().getStringExtra("day");
        userId = getIntent().getStringExtra("userId");
        search = getIntent().getStringExtra("search");

        list = new ArrayList<>();
        tempList = new ArrayList<>();

        initUI();

        getPhotoList();

    }

    private void initUI(){
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

        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(mLinearLayoutManager);
        rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));

        list = new ArrayList<>();
        tempList = new ArrayList<>();

        loading = (AVLoadingIndicatorView)findViewById(R.id.loading);
        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .build();

    }


    private void initLoadValue(){
        page = 0;
        isLoadFinish = false;
    }

    private void getPhotoList(){
        if(!isLoadFinish) {
            loading.show();
            HashMap<String, String> map = new HashMap<>();
            map.put("service", "getColumn");
            map.put("userId", userId);
            map.put("requestUserId", getUserID(this));
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

                        list = AdditionalFunc.getColumnList(data);

                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));
                    } else {

                        tempList.clear();
                        tempList = AdditionalFunc.getColumnList(data);
                        if (tempList.size() < 30) {
                            isLoadFinish = true;
                        }
                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_ENDLESS_LIST));

                    }

                }
            }.start();
        }else{
            if(adapter != null){
                adapter.setLoaded();
            }
        }
    }

    public void makeList(){

        adapter = new ColumnListCustomAdapter(getApplicationContext(), list, rv, this, this);

        rv.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page+=1;
                getPhotoList();
            }
        });

        adapter.notifyDataSetChanged();

    }

    private void addList(){

        for(int i=0; i<tempList.size(); i++){
            list.add(tempList.get(i));
            adapter.notifyItemInserted(list.size());
        }

        adapter.setLoaded();

    }

    @Override
    public void showView() {

    }

    @Override
    public void hideView() {

    }

    @Override
    public void redirectActivityForResult(Intent intent) {
        startActivityForResult(intent, 0);
    }

    @Override
    public void redirectActivity(Intent intent) {

        startActivity(intent);
    }

    public void redirectActivityWithUpdateItem(Intent intent, String updateId, final int position){

        if((boolean)list.get(position).get("hitAble")) {
            new UpdateItem("column", "hit", updateId, 1, getUserID(this), new UpdateItem.FinishAction() {
                @Override
                public void afterAction(String data) {
                    try {
                        JSONObject jObj = new JSONObject(data);
                        String status = jObj.getString("status");
                        String message = jObj.getString("message");


//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                        System.out.println(status);
                        if ("success".equals(status)) {
                            //handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SUCCESS));
                            list.get(position).put("hitAble", false);
                            list.get(position).put("hit", Integer.parseInt(message));
                            adapter.notifyItemChanged(position);
                        } else {
                            //handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FAIL));
                        }

                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }).start();
        }


        startActivityForResult(intent, ShowLayoutActivity.UPDATE_HEART);

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
                        adapter.notifyItemChanged(pos);
                    }
                }
                break;
            default:
                break;
        }

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
                default:
                    break;
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

}
