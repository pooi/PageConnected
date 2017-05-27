package ga.pageconnected.pageconnected;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.FacebookException;
import com.facebook.Profile;
import com.flaviofaria.kenburnsview.KenBurnsView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import ga.pageconnected.pageconnected.profile.WtInfoActivity;
import ga.pageconnected.pageconnected.util.AdditionalFunc;
import ga.pageconnected.pageconnected.util.FacebookLogin;
import ga.pageconnected.pageconnected.util.FacebookLoginSupport;
import ga.pageconnected.pageconnected.util.ParsePHP;


public class StartActivity extends BaseActivity implements FacebookLoginSupport {

    public static final String FACEBOOK_LOGIN = "facebook";

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_SHOW_LOGIN = 500;
    private final int MSG_MESSAGE_SUCCESS = 501;
    private final int MSG_MESSAGE_FAIL_FB = 502;
    private final int MSG_MESSAGE_FACEBOOK_EMPTY = 505;

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;

    // Facebook
    private FacebookLogin facebookLogin;
    private ImageView fbLogin;

    // UI
    private KenBurnsView kenBurnsView;
    private RelativeLayout rl_background;
    private LinearLayout li_login;

    private String USER_ID = "";
    public static HashMap<String, Object> USER_DATA = new HashMap<>();
    public static String USER_SCHOOL = "";

    // ToWtInfo
    private String wt_id;
    private String wt_email;
    private String wt_img;
    private String wt_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookLogin = new FacebookLogin(this, this);
        setContentView(R.layout.activity_start);

        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();

        init();

        checkAlreadyLogin();

    }

    private void init(){

        kenBurnsView = (KenBurnsView)findViewById(R.id.image);
        kenBurnsView.setImageResource(R.drawable.loading);
//        Picasso.with(getApplicationContext())
//                .load(R.drawable.loading)
//                .into(kenBurnsView);

        rl_background = (RelativeLayout) findViewById(R.id.rl_background);
        rl_background.setVisibility(View.INVISIBLE);
        li_login = (LinearLayout)findViewById(R.id.li_login);

        fbLogin = (ImageView)findViewById(R.id.fb_login);
        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLogin.login();
            }
        });

    }

    private void checkAlreadyLogin(){

        String login = setting.getString("login", null);

        if(null != login){

            if (FACEBOOK_LOGIN.equals(login)) {

                if(facebookLogin.isAlreadyLogin()){

                    USER_ID = facebookLogin.getID();
                    editor.putString("userId", USER_ID);
                    editor.commit();
                    checkLogin(MSG_MESSAGE_SUCCESS, MSG_MESSAGE_FAIL_FB);

                    return;

                }

            }

        }

        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SHOW_LOGIN));

    }

    private void checkLogin(final int success, final int fail) {
        HashMap<String, String> map = new HashMap<>();
        map.put("service", "getUserInfo");
        map.put("id", USER_ID);
        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map){
            @Override
            protected void afterThreadFinish(String data) {
                USER_DATA = AdditionalFunc.getUserInfo(data);
                if(USER_DATA.isEmpty()){
                    handler.sendMessage(handler.obtainMessage(fail));
                }else{
                    handler.sendMessage(handler.obtainMessage(success));
                }
            }
        }.start();
    }

    private class MyHandler extends Handler {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_SHOW_LOGIN:
//                    li_login.setVisibility(View.VISIBLE);
                    rl_background.setVisibility(View.VISIBLE);
                    break;
                case MSG_MESSAGE_SUCCESS:
                    USER_SCHOOL = (String)USER_DATA.get("school");
                    redirectMainActivity();
                    break;
                case MSG_MESSAGE_FAIL_FB:
                    facebookLogin.login();
                    break;
                case MSG_MESSAGE_FACEBOOK_EMPTY:
                    redirectWtInfoActivity();
                    break;
                default:
                    break;
            }
        }
    }

    // =================== Login Method ============================

    // ================== Facebook ==================
    @Override
    public void afterFBLoginSuccess(Profile profile, HashMap<String, String> data) {
        // System.out.println(profile.getId() + ", " + profile.getName());
        editor.putString("login", FACEBOOK_LOGIN);
        editor.commit();

        USER_ID = profile.getId();
        editor.putString("userId", USER_ID);
        editor.commit();

        wt_id = profile.getId();
        wt_img = profile.getProfilePictureUri(500, 500).toString();
        wt_email = data.get("email");
        wt_name = profile.getName();

        checkLogin(MSG_MESSAGE_SUCCESS, MSG_MESSAGE_FACEBOOK_EMPTY);
    }

    @Override
    public void afterFBLoginCancel() {
        showSnackbar("Facebook Login Cancel");
    }

    @Override
    public void afterFBLoginError(FacebookException error) {
        showSnackbar("Facebook Login Error : " + error.getCause().toString());
    }

    @Override
    public void afterFBLogout() {
        showSnackbar("Facebook Logout");
    }




    public void redirectWtInfoActivity() {
        Intent intent = new Intent(this, WtInfoActivity.class);
        intent.putExtra("id", wt_id);
        intent.putExtra("img", wt_img);
        intent.putExtra("email", wt_email);
        intent.putExtra("name", wt_name);
        startActivity(intent);
        finish();
    }

    public void redirectMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookLogin.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 0:
                break;
            default:
                break;
        }
    }


    private void printKeyHash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "ga.pageconnected.pageconnected",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
//                showSnackbar(Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
