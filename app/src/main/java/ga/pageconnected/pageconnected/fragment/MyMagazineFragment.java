package ga.pageconnected.pageconnected.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.Information;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.adapter.MyMagazineListCustomAdapter;
import ga.pageconnected.pageconnected.util.AdditionalFunc;
import ga.pageconnected.pageconnected.util.OnAdapterSupport;
import ga.pageconnected.pageconnected.util.ParsePHP;

public class MyMagazineFragment extends BaseFragment implements OnAdapterSupport {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;

    // UI
    private View view;
    private Context context;

    private TextView tv_msg;
    private AVLoadingIndicatorView loading;


//    private ArrayList<HashMap<String, String>> tempList;
    private ArrayList<HashMap<String, String>> list;

    private int page = 0;

    // Recycle View
    private RecyclerView rv;
    private LinearLayoutManager mLinearLayoutManager;
    private MyMagazineListCustomAdapter adapter;
    private boolean isLoadFinish;

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

        view = inflater.inflate(R.layout.fragment_my_magazine, container, false);
        context = container.getContext();

        list = new ArrayList<>();
//        tempList = new ArrayList<>();

        initUI();

        getMagazineList();

        return view;
    }

    private void initUI(){

        tv_msg = (TextView)view.findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);

        mLinearLayoutManager = new GridLayoutManager(context, 2);
        mLinearLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(mLinearLayoutManager);
//        rv.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));

        loading = (AVLoadingIndicatorView)view.findViewById(R.id.loading);
        loading.hide();

    }

    private void getMagazineList(){


        final File dir = new File(Environment.getExternalStorageDirectory(), "PageConnected/mymagazine");
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            int count = 0;
            for(int i=0; i<children.length; i++){
                if(children[i].endsWith(".pdf") || children[i].endsWith(".PDF")){
                    HashMap<String, String> map = new HashMap<>();
                    map.put("title", children[i]);
                    map.put("file", children[i]);
                    list.add(map);
                }
            }
        }
        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));

//        loading.show();
//        HashMap<String, String> map = new HashMap<>();
//        map.put("service", "getMagazineList");
//        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {
//
//            @Override
//            protected void afterThreadFinish(String data) {
//
//                    list.clear();
//
//                    list = AdditionalFunc.getMagazineList(data);
//
//                    handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));
//
//            }
//        }.start();

    }

    public void makeList(){

        if(list.size() > 0){
            tv_msg.setVisibility(View.GONE);
        }else{
            tv_msg.setVisibility(View.VISIBLE);
        }

        adapter = new MyMagazineListCustomAdapter(context, list, rv, this, this);

        rv.setAdapter(adapter);

//        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                page+=1;
//                getPhotoList();
//            }
//        });

        adapter.notifyDataSetChanged();

    }

    public void removeMagazine(final int pos, final String fileName){

        new MaterialDialog.Builder(context)
                .title(R.string.ok)
                .content(R.string.are_you_delete)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        File dir = new File(Environment.getExternalStorageDirectory(), "PageConnected/mymagazine");
                        new File(dir, fileName).delete();
                        list.remove(pos);
                        adapter.notifyItemRemoved(pos);
                    }
                })
                .show();

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

    private class MyHandler extends Handler {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_MAKE_LIST:
//                    progressDialog.hide();
                    loading.hide();
                    makeList();
                    break;
                default:
                    break;
            }
        }
    }


}
