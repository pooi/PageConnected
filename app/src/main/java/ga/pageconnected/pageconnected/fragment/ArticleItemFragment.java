package ga.pageconnected.pageconnected.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.activity.ShowLayoutActivity;
import ga.pageconnected.pageconnected.profile.ProfileActivity;
import ga.pageconnected.pageconnected.util.AdditionalFunc;
import ga.pageconnected.pageconnected.util.LayoutItem;
import ga.pageconnected.pageconnected.util.UpdateItem;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by tw on 2017-06-01.
 */
public class ArticleItemFragment extends BaseFragment {

    // BASIC UI
    private View view;
    private Context context;

    // DATA
    private int position;
    private HashMap<String, Object> data;

    //// UI
    // profile
    private RelativeLayout rl_profile;
    private ImageView profileImg;
    private TextView tv_name;
    private TextView tv_email;
    // content
    private ImageView defaultImg;
    private TextView tv_imageCount;
    private TextView tv_title;
    private TextView tv_content;
    private TextView tv_date;
    private TextView tv_heart;
    private TextView tv_hit;



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt("position");
        data = (HashMap<String, Object>)getArguments().getSerializable("data");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_article_item, container, false);
        context = container.getContext();

        initData();
        initUI();

        return view;

    }

    private void initData(){


    }

    private void redirectShowLayoutActivity(){

        Intent intent = new Intent(context, ShowLayoutActivity.class);
        intent.putExtra("item", new LayoutItem(
                (int)data.get("layout"),
                (String)data.get("title"),
                (String)data.get("content"),
                (ArrayList<String>)data.get("url"),
                (ArrayList<String>)data.get("picture")
        ));
        startActivity(intent);
        if((boolean)data.get("hitAble")) {
            new UpdateItem("article", "hit", (String) data.get("id"), 1, getUserID(this), new UpdateItem.FinishAction() {
                @Override
                public void afterAction(String data) {
                    try {
                        JSONObject jObj = new JSONObject(data);
                        String status = jObj.getString("status");

                        System.out.println(status);
                        if ("success".equals(status)) {
                            //handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SUCCESS));
                            ArticleItemFragment.this.data.put("hitAble", false);
                        } else {
                            //handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FAIL));
                        }

                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }).start();
        }

    }

    private void initUI(){

        view.findViewById(R.id.cv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectShowLayoutActivity();
            }
        });

        rl_profile = (RelativeLayout)view.findViewById(R.id.rl_profile);
        profileImg = (ImageView)view.findViewById(R.id.profileImg);
        tv_name = (TextView)view.findViewById(R.id.tv_name);
        tv_email = (TextView)view.findViewById(R.id.tv_email);
        defaultImg = (ImageView) view.findViewById(R.id.img_default);
        tv_imageCount = (TextView)view.findViewById(R.id.tv_img_count);
        tv_title = (TextView)view.findViewById(R.id.tv_title);
        tv_content = (TextView)view.findViewById(R.id.tv_content);
        tv_date = (TextView)view.findViewById(R.id.tv_date);
        tv_heart = (TextView)view.findViewById(R.id.tv_heart);
        tv_hit = (TextView)view.findViewById(R.id.tv_hit);

        rl_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("id", (String)data.get("userId"));
                startActivity(intent);
            }
        });
        Picasso.with(context)
                .load((String)data.get("img"))
                .transform(new CropCircleTransformation())
                .into(profileImg);
        tv_name.setText((String)data.get("name"));
        tv_email.setText((String)data.get("email"));

        ArrayList<String> pictureList = (ArrayList<String>)data.get("picture");
        if(pictureList.size() <= 0){
            defaultImg.setVisibility(View.GONE);
            tv_imageCount.setVisibility(View.GONE);
        }else{
            defaultImg.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(pictureList.get(0))
                    .resize(500, 0)
                    .into(defaultImg);
            if(pictureList.size() > 1) {
                tv_imageCount.setVisibility(View.VISIBLE);
                tv_imageCount.setText("+" + (pictureList.size()-1));
            }else{
                tv_imageCount.setVisibility(View.GONE);
            }
        }

        tv_title.setText((String)data.get("title"));
        tv_content.setText((String)data.get("content"));

        tv_date.setText(AdditionalFunc.parseDateString((String)data.get("date"), (String)data.get("time")));

        tv_heart.setText((int)data.get("heart") + "");
        tv_hit.setText((int)data.get("hit") + "");

    }

}
