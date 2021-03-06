package ga.pageconnected.pageconnected;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.activity.ArticleActivity;
import ga.pageconnected.pageconnected.activity.OpenSourceActivity;
import ga.pageconnected.pageconnected.activity.PhotoArticleActivity;
import ga.pageconnected.pageconnected.activity.add.AddArticleActivity;
import ga.pageconnected.pageconnected.activity.add.AddColumnActivity;
import ga.pageconnected.pageconnected.activity.add.AddMyMagazineActivity;
import ga.pageconnected.pageconnected.activity.add.AddPhotoActivity;
import ga.pageconnected.pageconnected.fragment.ArticleFragment;
import ga.pageconnected.pageconnected.fragment.ColumnFragment;
import ga.pageconnected.pageconnected.fragment.DayMagazineFragment;
import ga.pageconnected.pageconnected.fragment.MyMagazineFragment;
import ga.pageconnected.pageconnected.fragment.PhotoFragment;
import ga.pageconnected.pageconnected.profile.ProfileActivity;
import ga.pageconnected.pageconnected.util.AdditionalFunc;
import ga.pageconnected.pageconnected.util.AdvancedImageView;
import ga.pageconnected.pageconnected.util.FacebookLogin;
import ga.pageconnected.pageconnected.util.ParsePHP;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainActivity extends BaseActivity  implements NavigationView.OnNavigationItemSelectedListener {

    public final int MY_PERMISSION_REQUEST_STORAGE = 100;

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_FINISH = 500;

    private NavigationView navigationView;

    private String[] menuList = {"nav_article", "nav_column", "nav_photo", "nav_day_magazine", "nav_my_magazine", "nav_info", "nav_report", "nav_file", "nav_open_source"};
    private int currentSelectId;
    private boolean menuVisible = true;

    // Logout
    private MaterialDialog progressDialog;
    private FacebookLogin facebookLogin;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookLogin = new FacebookLogin(this);
        setContentView(R.layout.activity_main);

        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();
        login = setting.getString("login", null);

        progressDialog = new com.afollestad.materialdialogs.MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String name = (String)StartActivity.USER_DATA.get("name") + "님";
        String email = (String)StartActivity.USER_DATA.get("email");
        String img = (String)StartActivity.USER_DATA.get("img");

        View headerView = navigationView.getHeaderView(0);
        AdvancedImageView profileImg = (AdvancedImageView)headerView.findViewById(R.id.profileImg);
        Picasso.with(this)
                .load(img)
                .transform(new CropCircleTransformation())
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(profileImg);
        profileImg.setImage(img, name);

        TextView tv_name = (TextView)headerView.findViewById(R.id.tv_name);
        tv_name.setText(name);
        TextView tv_email = (TextView)headerView.findViewById(R.id.tv_email);
        tv_email.setText(email);

        currentSelectId = R.id.nav_article;
        showFragment("nav_article", new ArticleFragment());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.share_article);
        }
        navigationView.setCheckedItem(R.id.nav_article);

    }

    public Toolbar getToolbar(){
        return (Toolbar)findViewById(R.id.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.search:
                searchAction();
                break;
            case R.id.add:
                addAction();
                break;

        }
        return true;
    }

    private void searchAction(){
        switch (currentSelectId){
            case R.id.nav_article:
                new MaterialDialog.Builder(MainActivity.this)
                        .title(R.string.search_long)
                        .inputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                                InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                        .theme(Theme.LIGHT)
                        .positiveText(R.string.search)
                        .negativeText(R.string.cancel)
                        .neutralText(R.string.reset)
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                search = "";
//                                initLoadValue();
//                                progressDialog.show();
//                                getBoardList();
                            }
                        })
                        .input(getResources().getString(R.string.please_input_search_text), "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                String search = input.toString();

                                Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                                intent.putExtra("day", "%");
                                intent.putExtra("search", search);
                                startActivity(intent);

                            }
                        })
                        .show();
                break;
            case R.id.nav_column:
                showSnackbar("Search column");
                break;
            case R.id.nav_photo:
                new MaterialDialog.Builder(MainActivity.this)
                        .title(R.string.search_long)
                        .inputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                                InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                        .theme(Theme.LIGHT)
                        .positiveText(R.string.search)
                        .negativeText(R.string.cancel)
                        .neutralText(R.string.reset)
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                search = "";
//                                initLoadValue();
//                                progressDialog.show();
//                                getBoardList();
                            }
                        })
                        .input(getResources().getString(R.string.please_input_search_text), "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                String search = input.toString();

                                Intent intent = new Intent(MainActivity.this, PhotoArticleActivity.class);
                                intent.putExtra("day", "%");
                                intent.putExtra("search", search);
                                startActivity(intent);

                            }
                        })
                        .show();
                break;
            case R.id.nav_day_magazine:
                showSnackbar("Search magazine");
                break;
        }
    }

    private void addAction(){
        switch (currentSelectId){
            case R.id.nav_article: {
                Intent intent = new Intent(MainActivity.this, AddArticleActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_column: {
                Intent intent = new Intent(MainActivity.this, AddColumnActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_photo: {
                Intent intent = new Intent(MainActivity.this, AddPhotoActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_my_magazine:{
                Intent intent = new Intent(MainActivity.this, AddMyMagazineActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                break;
            }
            case R.id.nav_day_magazine:
                showSnackbar("Add magazine");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new MaterialDialog.Builder(this)
                    .title(R.string.ok)
                    .content(R.string.are_you_finish_app)
                    .positiveText(R.string.finish)
                    .negativeText(R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem search = menu.findItem(R.id.search);
        MenuItem add = menu.findItem(R.id.add);
        if(menuVisible)
        {
            add.setVisible(true);
            search.setVisible(true);
        }
        else
        {
            add.setVisible(false);
            search.setVisible(false);
        }
        return true;
    }

    //@SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        String title = null;

        if(id == R.id.nav_article){

            showFragment("nav_article", new ArticleFragment());
            title = getResources().getString(R.string.share_article);
            currentSelectId = id;
            menuVisible = true;

        }else if(id == R.id.nav_column){

            showFragment("nav_column", new ColumnFragment());
            title = getResources().getString(R.string.share_column);
            currentSelectId = id;
            menuVisible = true;

        }else if(id == R.id.nav_photo){

            showFragment("nav_photo", new PhotoFragment());
            title = getResources().getString(R.string.share_photo);
            currentSelectId = id;
            menuVisible = true;

        }else if(id == R.id.nav_day_magazine) {

            showFragment("nav_day_magazine", new DayMagazineFragment());
            title = getResources().getString(R.string.day_magazine);
            currentSelectId = id;
            menuVisible = false;

        }else if(id == R.id.nav_my_magazine){

            showFragment("nav_my_magazine", new MyMagazineFragment());
            title = getResources().getString(R.string.my_magazine);
            currentSelectId = id;
            menuVisible = true;

        }else if (id == R.id.nav_show_profile) {

            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("id", getUserID(this));
            startActivity(intent);

        } else if(id == R.id.nav_logout){

            if (StartActivity.FACEBOOK_LOGIN.equals(login)) {
                facebookLogin.logout();
            }

            editor.remove("login");
            editor.commit();

            redirectStartPage();

        } else if(id == R.id.nav_logout_delete){
            if (StartActivity.FACEBOOK_LOGIN.equals(login)) {

                facebookLogin.logout();
                editor.remove("login");
                editor.commit();
                removeUser(getUserID(this));

            }

        } else if(id == R.id.nav_info){

            String text = getResources().getString(R.string.app_name) + " " + getVersion() + "(build " + getVersionCode() + ")";

            new MaterialDialog.Builder(this)
                    .title(R.string.info)
                    .content(text)
                    .positiveText(R.string.ok)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

        } else if(id == R.id.nav_report){

            String text = getResources().getString(R.string.app_name) + " " + getVersion() + "(build " + getVersionCode() + ") 오류제보";

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{Information.ADMINISTRATOR_EMAIL});
            i.putExtra(Intent.EXTRA_SUBJECT, text);
            i.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.please_input_content));
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                showSnackbar(R.string.no_install_email_client);
            }

        } else if(id == R.id.nav_file){

//            showTempPdfFileList();
            checkPermission();

        } else if(id == R.id.nav_open_source){

            Intent intent = new Intent(getApplicationContext(), OpenSourceActivity.class);
            startActivity(intent);

        }

        if (getSupportActionBar() != null && title != null) {
            getSupportActionBar().setTitle(title);
        }

        navigationView.setCheckedItem(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        invalidateOptionsMenu();

        return true;
    }

    private void showTempPdfFileList(){

        final File dir = new File(Environment.getExternalStorageDirectory(), "PageConnected");
        boolean isShowAlert = false;
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            if(children != null) {
                int count = 0;
                for (int i = 0; i < children.length; i++) {
                    if (children[i].endsWith(".pdf") || children[i].endsWith(".PDF")) {
                        count += 1;
                    }
                }
                if (count > 0) {
                    isShowAlert = true;
                }
            }
//                        for (int i = 0; i < children.length; i++)
//                        {
//                            new File(dir, children[i]).delete();
//                        }
        }

        if(isShowAlert){
            ArrayList<String> list = new ArrayList<>();
            final String[] children = dir.list();
            for(int i=0; i<children.length; i++){
                if(children[i].endsWith(".pdf") || children[i].endsWith(".PDF")){
                    list.add(children[i]);
                }
            }
            new MaterialDialog.Builder(this)
                    .title(R.string.file_list)
                    .items(AdditionalFunc.arrayListToStringArray(list))
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                            viewPdf(text.toString());
                        }
                    })
                    .itemsLongCallback(new MaterialDialog.ListLongCallback() {
                        @Override
                        public boolean onLongSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                            new File(dir, text.toString()).delete();
                            return false;
                        }
                    })
                    .positiveText(R.string.ok)
                    .neutralText(R.string.delete_all)
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            for(int i=0; i<children.length; i++){
                                new File(dir, children[i]).delete();
                            }
                        }
                    })
                    .show();

        }else{
            new MaterialDialog.Builder(this)
                    .title(R.string.ok)
                    .content(R.string.there_is_no_file)
                    .positiveText(R.string.ok)
                    .show();
        }

    }

    private void viewPdf(String fileName){
        File dir = new File(Environment.getExternalStorageDirectory(), "PageConnected");
        File pdfFile = new File(dir, fileName);

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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showFragment(String tag, Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();

        if(fragmentManager.findFragmentByTag(tag) != null){
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(tag)).commit();
        }else{
            fragmentManager.beginTransaction().add(R.id.content_fragment_layout, fragment, tag).commit();
        }
        hideFragment(fragmentManager,tag);

    }

    private void hideFragment(FragmentManager fragmentManager, String name){

        for(String s : menuList){

            if(!s.equals(name)) {
                if (fragmentManager.findFragmentByTag(s) != null) {
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(s)).commit();
                }
            }

        }

    }

    private class MyHandler extends Handler {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_FINISH:
                    redirectStartPage();
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
            default:
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    showTempPdfFileList();

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
//                Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_STORAGE);

        } else {
            showTempPdfFileList();
        }
    }

    private void removeUser(String userId){
        HashMap<String, String> map = new HashMap<>();
        map.put("service", "deleteUser");
        map.put("id", userId);

        progressDialog.setContent(R.string.removing_data);
        progressDialog.show();
        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map){
            @Override
            protected void afterThreadFinish(String data) {
                handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FINISH));
            }
        }.start();
    }



    private String getVersionCode() {
        String version = "";
        try {
            PackageInfo i = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            version = Integer.toString(i.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return version;
    }

    private String getVersion() {
        String version = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (Exception e) {

        }
        return version;
    }

}
