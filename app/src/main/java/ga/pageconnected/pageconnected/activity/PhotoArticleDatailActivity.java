package ga.pageconnected.pageconnected.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.BaseActivity;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.util.AdditionalFunc;
import ga.pageconnected.pageconnected.util.AdvancedImageView;
import ga.pageconnected.pageconnected.util.UpdateItem;
import ga.pageconnected.pageconnected.util.UserInfo;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static ga.pageconnected.pageconnected.activity.PhotoArticleActivity.UPDATE_HEART;

public class PhotoArticleDatailActivity extends BaseActivity {


    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_UPDATE_HEART = 500;
    private final int MSG_MESSAGE_UPDATE_FAIL_HEART = 501;

    private RelativeLayout rl_profile;
    private TextView tv_name;
    private TextView tv_email;
    private ImageView profileImg;
    private TextView tv_date;
    private TextView tv_content;
    private TextView tv_hit;

    private LinearLayout li_photoField;

    private int position;
    private HashMap<String, Object> item;
    private UserInfo userInfo;
    private String content;
    private int hit;
    private ArrayList<HashMap<String, Object>> imageList;

    private MaterialDialog progressDialog;

    private RelativeLayout temp_rl_interest;
    private ImageView temp_imgHeart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_article_datail);

        Intent intent = getIntent();

        item = (HashMap<String, Object>)intent.getSerializableExtra("item");
        position = intent.getIntExtra("position", -1);

        userInfo = new UserInfo(
                (String)item.get("userId"),
                (String)item.get("name"),
                (String)item.get("email"),
                (String)item.get("img")
        );
        content = (String)item.get("content");
        hit = (int)item.get("hit");
        imageList = (ArrayList<HashMap<String, Object>>)item.get("imageList");

        init();

    }

    private void init(){

        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .cancelable(false)
                .build();

        rl_profile = (RelativeLayout)findViewById(R.id.rl_profile);
        rl_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInfo.redirectProfileActivity(getApplicationContext());
            }
        });
        profileImg = (ImageView)findViewById(R.id.profileImg);
        Picasso.with(this)
                .load(userInfo.getImg())
                .transform(new CropCircleTransformation())
                .into(profileImg);
        tv_name = (TextView)findViewById(R.id.tv_name);
        tv_name.setText(userInfo.getName());
        tv_email = (TextView)findViewById(R.id.tv_email);
        tv_email.setText(userInfo.getEmail());
        tv_content = (TextView)findViewById(R.id.tv_content);
        if("".equals(content)){
            tv_content.setVisibility(View.GONE);
        }else{
            tv_content.setText(content);
            tv_content.setVisibility(View.VISIBLE);
        }

        tv_date = (TextView)findViewById(R.id.tv_date);
        String date = AdditionalFunc.parseDateString((String)item.get("date"), (String)item.get("time"));
        tv_date.setText(date);
        tv_hit = (TextView)findViewById(R.id.tv_hit);
        tv_hit.setText(hit + "");

        li_photoField = (LinearLayout)findViewById(R.id.li_photo_field);

        makeList();

    }

    private void makeList(){

        li_photoField.removeAllViews();
        ArrayList<String> al = getAllImageList();

        for(int i=0; i<imageList.size(); i++){
            final int pos = i;
            HashMap<String, Object> map = imageList.get(i);

            View v = getLayoutInflater().from(this).inflate(R.layout.photo_simple_list_custom_item, null, false);
            v.setTag((String)map.get("id"));

            AdvancedImageView imageView = (AdvancedImageView)v.findViewById(R.id.img_default);
            TextView tv_heart = (TextView)v.findViewById(R.id.tv_heart);

            Picasso.with(getApplicationContext())
                    .load((String)map.get("photo"))
                    .resize(500, 0)
                    .into(imageView);
            imageView.setImageList(al, i);
            tv_heart.setText((int)map.get("heart") + "");


            final RelativeLayout rl_interest = (RelativeLayout)v.findViewById(R.id.rl_interest);
            final ImageView img_heart = (ImageView)v.findViewById(R.id.img_heart);

            rl_interest.setTag((boolean) map.get("heartAble"));
            rl_interest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean tag = (boolean) view.getTag();
                    temp_imgHeart = img_heart;
                    temp_rl_interest = rl_interest;
                    updateHeart(tag, pos);
//                    checkFillHeart(img_heart);
                }
            });
            checkFillHeart(rl_interest, img_heart);

            li_photoField.addView(v);

        }

    }

    private void updateHeart(boolean tag, final int pos){
        progressDialog.show();
        final int action;
        if(tag)
            action = 1;
        else
            action = -1;

        new UpdateItem("photoDetail", "heart", (String)imageList.get(pos).get("id"), action, getUserID(this), new UpdateItem.FinishAction() {
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
                        if(action > 0){
                            imageList.get(pos).put("heartAble", false);
                        }else{
                            imageList.get(pos).put("heartAble", true);
                        }
                        imageList.get(pos).put("heart", Integer.parseInt(message));

                        Intent intent = new Intent();
                        intent.putExtra("position", position);
                        intent.putExtra("heart", (int)imageList.get(pos).get("heart"));
                        intent.putExtra("heartAble", (boolean)imageList.get(pos).get("heartAble"));
                        setResult(UPDATE_HEART, intent);

                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_UPDATE_HEART));

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

    private class MyHandler extends Handler {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_UPDATE_HEART:
                    progressDialog.hide();
                    if(temp_rl_interest != null) {
                        temp_rl_interest.setTag(!((boolean) temp_rl_interest.getTag()));
                        checkFillHeartForTemp(temp_rl_interest);
                    }
                    break;
                case MSG_MESSAGE_UPDATE_FAIL_HEART:
                    progressDialog.hide();
                    break;
                default:
                    break;
            }
        }
    }


    private ArrayList<String> getAllImageList(){
        ArrayList<String> al = new ArrayList<>();
        for(HashMap<String, Object> map : imageList){
            al.add((String)map.get("photo"));
        }
        return al;
    }

    private void checkFillHeart(RelativeLayout rl_interest, ImageView img_haert){
        boolean tag = (boolean)rl_interest.getTag();
        if(tag){
            img_haert.setImageResource(R.drawable.ic_heart_outline_grey600_24dp);
        }else{
            img_haert.setImageResource(R.drawable.ic_heart_grey600_24dp);
        }
    }

    private void checkFillHeartForTemp(View view){
        boolean tag = (boolean)view.getTag();
        if(tag){
            temp_imgHeart.setImageResource(R.drawable.ic_heart_outline_grey600_24dp);
        }else{
            temp_imgHeart.setImageResource(R.drawable.ic_heart_grey600_24dp);
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
