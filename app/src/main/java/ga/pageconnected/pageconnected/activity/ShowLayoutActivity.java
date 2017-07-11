package ga.pageconnected.pageconnected.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import ga.pageconnected.pageconnected.BaseActivity;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.util.AdvancedImageView;
import ga.pageconnected.pageconnected.util.LayoutItem;
import ga.pageconnected.pageconnected.util.UpdateItem;

public class ShowLayoutActivity extends BaseActivity {

    public static final int UPDATE_HEART = 100;

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_UPDATE_HEART = 500;
    private final int MSG_MESSAGE_UPDATE_FAIL_HEART = 501;

    private RelativeLayout root;
    private AdvancedImageView[] ivList;
    private TextView tv_title;
    private TextView tv_content;
    private View line0;
    private TextView tv_reference;
    private RelativeLayout rl_interest;
    private ImageView img_heart;

    private LayoutItem layoutItem;
    private MaterialDialog progressDialog;

    private boolean testMode;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        layoutItem = (LayoutItem)intent.getSerializableExtra("item");
        testMode = intent.getBooleanExtra("testMode", false);
        position = intent.getIntExtra("position", -1);

        setContentView(layoutItem.getViewId());

        init();

    }

    private void init(){

        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .build();

        root = (RelativeLayout)findViewById(R.id.root);
        root.setBackgroundColor(getColorId(R.color.transparent));
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowLayoutActivity.this.finish();
            }
        });
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_content = (TextView)findViewById(R.id.tv_content);
        line0 = (View)findViewById(R.id.line0);
        tv_reference = (TextView)findViewById(R.id.tv_reference);
        rl_interest = (RelativeLayout)findViewById(R.id.rl_interest);
        img_heart = (ImageView)findViewById(R.id.img_heart);

        ivList = new AdvancedImageView[layoutItem.getMaxImageCount()];
        for(int i=0; i<layoutItem.getMaxImageCount(); i++){
            switch (i){
                case 0:
                    ivList[i] = (AdvancedImageView)findViewById(R.id.img0);
                    break;
                case 1:
                    ivList[i] = (AdvancedImageView)findViewById(R.id.img1);
                    break;
                case 2:
                    ivList[i] = (AdvancedImageView)findViewById(R.id.img2);
                    break;
            }
        }
        for(int i=layoutItem.getImageCount(); i<layoutItem.getMaxImageCount(); i++){
            ivList[i].setVisibility(View.GONE);
        }

        findViewById(R.id.tv_layout_pos).setVisibility(View.GONE);

        if(!testMode) {
            rl_interest.setVisibility(View.VISIBLE);
            rl_interest.setTag(false);
            rl_interest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean tag = (boolean) view.getTag();
                    updateHeart(tag);
//                    view.setTag(!tag);
//                    checkFillHeart(view);
                }
            });
        }

        fillContent();

    }

    private void checkFillHeart(View v){
        boolean tag = (boolean)v.getTag();
        if(tag){
            img_heart.setImageResource(R.drawable.ic_heart_outline_white_24dp);
        }else{
            img_heart.setImageResource(R.drawable.ic_heart_white_24dp);
        }
    }
    private void updateHeart(boolean tag){
        progressDialog.show();
        final int action;
        if(tag)
            action = 1;
        else
            action = -1;

        new UpdateItem(layoutItem.getTable(), "heart", layoutItem.getId(), action, getUserID(this), new UpdateItem.FinishAction() {
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
                            layoutItem.setHeartAble(false);
                        }else{
                            layoutItem.setHeartAble(true);
                        }
                        layoutItem.setHeart(Integer.parseInt(message));

                        Intent intent = new Intent();
                        intent.putExtra("position", position);
                        intent.putExtra("heart", layoutItem.getHeart());
                        intent.putExtra("heartAble", layoutItem.getHeartAble());
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
                    rl_interest.setTag(!((boolean)rl_interest.getTag()));
                    checkFillHeart(rl_interest);
                    break;
                case MSG_MESSAGE_UPDATE_FAIL_HEART:
                    progressDialog.hide();
                    break;
                default:
                    break;
            }
        }
    }

    private void fillContent(){
        tv_title.setText(layoutItem.getTitle());
        tv_content.setText(layoutItem.getContent());
        if(layoutItem.getReference().size() <= 0){
            line0.setVisibility(View.GONE);
            tv_reference.setVisibility(View.GONE);
        }else {
            line0.setVisibility(View.VISIBLE);
            tv_reference.setVisibility(View.VISIBLE);
            String reference = "";
            for (int i = 0; i < layoutItem.getReference().size(); i++) {
                reference += layoutItem.getReference().get(i);
                if (i < layoutItem.getReference().size() - 1) {
                    reference += "\n";
                }
            }
            tv_reference.setText(reference);
        }

        for(int i=0; i<layoutItem.getImageCount(); i++){
            final int pos = i;
            String path = layoutItem.getImageList().get(i);
            Picasso.with(getApplicationContext())
                    .load(path)
                    .resize(0, 800)
                    .into(ivList[pos]);
            ivList[pos].setImageList(layoutItem.getImageList(), pos, layoutItem.getTitle());
        }

        if(!testMode) {
            rl_interest.setTag((boolean) layoutItem.getItem().get("heartAble"));
            checkFillHeart(rl_interest);
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
