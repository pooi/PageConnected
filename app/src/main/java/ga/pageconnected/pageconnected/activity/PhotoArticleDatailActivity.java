package ga.pageconnected.pageconnected.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.BaseActivity;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.util.AdditionalFunc;
import ga.pageconnected.pageconnected.util.UserInfo;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class PhotoArticleDatailActivity extends BaseActivity {

    private RelativeLayout rl_profile;
    private TextView tv_name;
    private TextView tv_email;
    private ImageView profileImg;
    private TextView tv_date;
    private TextView tv_content;
    private TextView tv_hit;

    private LinearLayout li_photoField;

    private HashMap<String, Object> item;
    private UserInfo userInfo;
    private String content;
    private String hit;
    private ArrayList<HashMap<String, Object>> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_article_datail);

        Intent intent = getIntent();

        item = (HashMap<String, Object>)intent.getSerializableExtra("item");

        userInfo = new UserInfo(
                (String)item.get("userId"),
                (String)item.get("name"),
                (String)item.get("email"),
                (String)item.get("img")
        );
        content = (String)item.get("content");
        hit = (String)item.get("hit");
        imageList = (ArrayList<HashMap<String, Object>>)item.get("imageList");

        init();

    }

    private void init(){

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
        tv_hit.setText(hit);

        li_photoField = (LinearLayout)findViewById(R.id.li_photo_field);

        makeList();

    }

    private void makeList(){

        li_photoField.removeAllViews();

        for(HashMap<String, Object> map : imageList){

            View v = getLayoutInflater().from(this).inflate(R.layout.photo_simple_list_custom_item, null, false);
            v.setTag((String)map.get("id"));

            ImageView imageView = (ImageView)v.findViewById(R.id.img_default);
            TextView tv_heart = (TextView)v.findViewById(R.id.tv_heart);

            Picasso.with(getApplicationContext())
                    .load((String)map.get("photo"))
                    .resize(500, 0)
                    .into(imageView);
            tv_heart.setText((String)map.get("heart"));

            li_photoField.addView(v);

        }

    }
}
