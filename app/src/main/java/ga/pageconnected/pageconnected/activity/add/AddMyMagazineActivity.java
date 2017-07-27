package ga.pageconnected.pageconnected.activity.add;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.BaseActivity;
import ga.pageconnected.pageconnected.Information;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.activity.ArticleActivity;
import ga.pageconnected.pageconnected.activity.ShowLayoutActivity;
import ga.pageconnected.pageconnected.fragment.AllArticleItemFragment;
import ga.pageconnected.pageconnected.fragment.ArticleItemFragment;
import ga.pageconnected.pageconnected.util.AdditionalFunc;
import ga.pageconnected.pageconnected.util.CustomViewPager;
import ga.pageconnected.pageconnected.util.MyMagazineSelectListener;
import ga.pageconnected.pageconnected.util.PagerContainer;
import ga.pageconnected.pageconnected.util.ParsePHP;
import ga.pageconnected.pageconnected.util.pdf.DialogListener;
import ga.pageconnected.pageconnected.util.pdf.GeneratePDF;

public class AddMyMagazineActivity extends BaseActivity implements MyMagazineSelectListener, DialogListener {

    public final int MY_PERMISSION_REQUEST_STORAGE = 100;
    public final int FINISH_ACTIVITY = 101;

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_MAKE_ENDLESS_LIST = 501;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;
    private final int MSG_MESSAGE_SHOW_LOADING = 503;

    private final int MSG_MESSAGE_SHOW_PDF_FILE = 504;
    private final int MSG_MESSAGE_HIDE_PROGRESS = 505;
    private final int MSG_MESSAGE_FAIL_GENERATE_PDF = 506;
    private final int MSG_MESSAGE_CHANGE_DIALOG_CONTENT = 507;

    private AVLoadingIndicatorView loading;
    private MaterialDialog progressDialog;
    private TextView tv_msg;
    private TextView toolbarTitle;

    private PagerContainer viewPagerContainer;
    private CustomViewPager viewPager;
    private NavigationAdapter pagerAdapter;

    private int page = 0;
    private boolean isLoadFinish;
    private ArrayList<HashMap<String, Object>> list;
    private ArrayList<HashMap<String, Object>> tempList;
    private ArrayList<Integer> selectPosList;

    private Button createBtn;
    private File pdfFile = null;
    private String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_my_magazine);

        list = new ArrayList<>();
        tempList = new ArrayList<>();
        selectPosList = new ArrayList<>();

        init();

    }

    private void init(){
        tv_msg = (TextView)findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);

        viewPagerContainer = (PagerContainer)findViewById(R.id.view_pager_container);
        viewPager = (CustomViewPager) findViewById(R.id.view_pager);

        createBtn = (Button)findViewById(R.id.createBtn);
        createBtn.setEnabled(false);
        createBtn.setVisibility(View.GONE);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create();
            }
        });

        loading = (AVLoadingIndicatorView)findViewById(R.id.loading);
        resetProgressDialog();

        getAllArticleList();

    }

    private void initLoadValue(){
        page = 0;
        isLoadFinish = false;
    }

    private void loadViewPager(){

        pagerAdapter = new NavigationAdapter(getSupportFragmentManager(), list, this);
        viewPager.setOffscreenPageLimit(list.size());
        viewPager.setAdapter(pagerAdapter);
//        viewPager.setPageMargin(0);
        viewPager.setClipChildren(false);

    }

    private void getAllArticleList(){
        if(!isLoadFinish) {
            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SHOW_LOADING));

            HashMap<String, String> map = new HashMap<>();
            map.put("service", "getUserAllContents");
            map.put("userId", getUserID(this));
            new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

                @Override
                protected void afterThreadFinish(String data) {

                    if (page <= 0) {
                        list.clear();

                        list = AdditionalFunc.getAllArticleList(data);

                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));

                    } else {

                        tempList.clear();
                        tempList = AdditionalFunc.getAllArticleList(data);
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
        createBtn.setVisibility(View.VISIBLE);
        checkCreate();

        loadViewPager();

    }

    private void addList(){

        for(int i=0; i<tempList.size(); i++){
            list.add(tempList.get(i));
        }

    }

    @Override
    public void select(int fragmentPosition, boolean state) {
        if(state){
            selectPosList.add(fragmentPosition);
            showSnackbar(String.format(getResources().getString(R.string.select_pdf_page), selectPosList.size()));
        }else{
            try {
                selectPosList.remove((Integer) fragmentPosition);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        checkCreate();
    }

    private void create(){

        new MaterialDialog.Builder(AddMyMagazineActivity.this)
                .title(R.string.input)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getResources().getString(R.string.please_enter_title), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if(!"".equals(input.toString())) {
                            title = input.toString();
                            checkPermission();
                        }
                    }
                }).show();

    }

    private void createPdf(){

        ArrayList<HashMap<String, Object>> selectedList = new ArrayList<>();
        for(int index : selectPosList){
            selectedList.add(list.get(index));
        }

        try {
//            Date date = new Date();
//            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

            File pdfFolder = new File(Environment.getExternalStorageDirectory(), "PageConnected/mymagazine");
            if (!pdfFolder.exists()) {
                pdfFolder.mkdir();
            }
            pdfFile = new File(pdfFolder, title + ".pdf");

            OutputStream output = new FileOutputStream(pdfFile);

            progressDialog.show();
            new GeneratePDF(getApplicationContext(), output, selectedList, this){
                @Override
                protected void afterThreadFinish(boolean status) {

                    if(status){
                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SHOW_PDF_FILE));
                    }else{
                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FAIL_GENERATE_PDF));
                    }

                }
            }.start();

        }catch (Exception e){
            e.printStackTrace();
            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FAIL_GENERATE_PDF));
        }

    }

    private void viewPdf(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if(Build.VERSION.SDK_INT >= 24) {
            Uri pdfURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", pdfFile);
            System.out.println(getApplicationContext().getPackageName());
            intent.setDataAndType(pdfURI, "application/pdf");
        }else{
            intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
        }
        startActivity(intent);
    }

    @Override
    public void changeContent(int id) {
        String str = getResources().getString(id);
        Message msg = handler.obtainMessage(MSG_MESSAGE_CHANGE_DIALOG_CONTENT);
        Bundle bdl = new Bundle(1);
        bdl.putString("content", str);
        msg.setData(bdl);
        handler.sendMessage(msg);
//        progressDialog.setContent(id);
    }

    @Override
    public void changeContent(String content) {
//        progressDialog.setContent(content);
        Message msg = handler.obtainMessage(MSG_MESSAGE_CHANGE_DIALOG_CONTENT);
        Bundle bdl = new Bundle(1);
        bdl.putString("content", content);
        msg.setData(bdl);
        handler.sendMessage(msg);
    }

    private void resetProgressDialog(){

        if(progressDialog != null){
            progressDialog.dismiss();
        }
        progressDialog = new MaterialDialog.Builder(AddMyMagazineActivity.this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .cancelable(false)
                .build();
    }

    private void checkCreate(){
        if(selectPosList.size() <= 0){
            createBtn.setEnabled(false);
            createBtn.setBackgroundColor(getColorId(R.color.dark_gray));
        }else{
            createBtn.setEnabled(true);
            createBtn.setBackgroundColor(getColorId(R.color.colorPrimary));
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
                case MSG_MESSAGE_SHOW_LOADING:
                    loading.show();
                    break;
                case MSG_MESSAGE_SHOW_PDF_FILE:
                    progressDialog.hide();
                    resetProgressDialog();
//                    checkPDFText();

                    viewPdf();

                    break;
                case MSG_MESSAGE_HIDE_PROGRESS:
                    progressDialog.hide();
                    resetProgressDialog();
                    break;
                case MSG_MESSAGE_FAIL_GENERATE_PDF:
                    progressDialog.hide();
                    resetProgressDialog();
                    new MaterialDialog.Builder(AddMyMagazineActivity.this)
                            .title(R.string.error)
                            .content(R.string.fail_generate_pdf)
                            .positiveText(R.string.ok)
                            .show();
                    break;
                case MSG_MESSAGE_CHANGE_DIALOG_CONTENT:
                    Bundle bdl = msg.getData();
                    String content = bdl.getString("content");
                    progressDialog.setContent(content);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    createPdf();

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    Log.d("dd", "Permission always deny");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_STORAGE);

        } else {
            createPdf();
        }
    }

    private static class NavigationAdapter extends FragmentPagerAdapter {

        private ArrayList<HashMap<String, Object>> list;
        private AddMyMagazineActivity activity;

        public NavigationAdapter(FragmentManager fm, ArrayList<HashMap<String, Object>> list, AddMyMagazineActivity activity){
            super(fm);
            this.list = list;
            this.activity = activity;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f;
            final int pattern = position % list.size();

            f = new AllArticleItemFragment();
            Bundle bdl = new Bundle(1);
            bdl.putInt("position", pattern);
            bdl.putSerializable("data", list.get(pattern));
            bdl.putSerializable("listener", (MyMagazineSelectListener)activity);
            f.setArguments(bdl);

            return f;
        }

        @Override
        public int getCount(){
            return list.size();
        }


    }

}
