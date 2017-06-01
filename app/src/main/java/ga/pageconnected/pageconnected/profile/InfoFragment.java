package ga.pageconnected.pageconnected.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.Information;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.fragment.BaseFragment;
import ga.pageconnected.pageconnected.util.AdditionalFunc;
import ga.pageconnected.pageconnected.util.ParsePHP;


/**
 * Created by tw on 2017-05-29.
 */
public class InfoFragment extends BaseFragment {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_FILL_FORM = 500;


    // BASIC UI
    private View view;
    private Context context;

    private TextView tv_email;
    private AVLoadingIndicatorView loadingEmail;
    private LinearLayout interestField;
    private AVLoadingIndicatorView loadingInterest;
    private TextView tv_intro;
    private AVLoadingIndicatorView loadingIntro;

    private FloatingActionButton fabEdit;

    private String userId;
    private HashMap<String, Object> item;



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        userId = getArguments().getString("id");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_info, container, false);
        context = container.getContext();

        initUI();

        getUserInfo();

        return view;

    }

    private void initUI(){

        tv_email = (TextView)view.findViewById(R.id.tv_email);
        loadingEmail = (AVLoadingIndicatorView)view.findViewById(R.id.loading_email);
        interestField = (LinearLayout)view.findViewById(R.id.interest_field);
        loadingInterest = (AVLoadingIndicatorView)view.findViewById(R.id.loading_interest);
        tv_intro = (TextView)view.findViewById(R.id.tv_intro);
        loadingIntro = (AVLoadingIndicatorView)view.findViewById(R.id.loading_intro);

        fabEdit = (FloatingActionButton) view.findViewById(R.id.fab_edit);
        fabEdit.setTitle(getResources().getString(R.string.edit_short));
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WtInfoActivity.class);
                intent.putExtra("edit", true);
                intent.putExtra("id", getUserID(InfoFragment.this));
                startActivityForResult(intent, ProfileActivity.EDIT_PROFILE);
            }
        });
        if (getUserID(InfoFragment.this).equals(userId)) {
            fabEdit.setVisibility(View.VISIBLE);
        } else {
            fabEdit.setVisibility(View.GONE);
        }

    }

    public void updateUI(HashMap<String, Object> map){
        item = map;
        fillForm();
    }

    private void fillForm(){

        tv_email.setText((String)item.get("email"));
        loadingEmail.hide();

        tv_intro.setText((String)item.get("intro"));
        loadingIntro.hide();

        setInterestField();
        setParentActivityUI();

    }

    private void setParentActivityUI(){
        try {
            ((ProfileActivity) getActivity()).setProfile((String) item.get("name"), (String) item.get("img"));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void setInterestField(){

        try {
            final ArrayList<String> interest = (ArrayList<String>) item.get("interest");
            loadingInterest.hide();

            interestField.removeAllViews();

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            dpWidth -= 85;

            final float scale = getResources().getDisplayMetrics().density;
            int width = (int) (dpWidth * scale + 0.5f);
            int dp5 = (int) (5 * scale + 0.5f);
            int dp3 = (int) (3 * scale * 0.5f);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(dp3, dp3, dp3, dp3);
            int mButtonsSize = 0;
            Rect bounds = new Rect();

            boolean isAdd = false;
            TextView finalText = new TextView(context);
            finalText.setPadding(dp5 * 2, dp5, dp5 * 2, dp5);
            finalText.setBackgroundResource(R.drawable.round_button_blue);
            finalText.setTextColor(ContextCompat.getColor(context, R.color.white));
            finalText.setText("+" + interest.size());
            finalText.setLayoutParams(params);
            finalText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(context)
                            .title(R.string.interest_field)
                            .items(AdditionalFunc.arrayListToStringArray(interest))
                            .theme(Theme.LIGHT)
                            .positiveText(R.string.close)
                            .show();
                }
            });

            for (int i = 0; i < interest.size(); i++) {

                int remainCount = interest.size() - i;
                String remainString = "+" + remainCount;
                finalText.setText(remainString);

                String s = interest.get(i);
                TextView mBtn = new TextView(context);
                mBtn.setPadding(dp5 * 2, dp5, dp5 * 2, dp5);
                mBtn.setBackgroundResource(R.drawable.round_button_blue_line);
                mBtn.setTextColor(ContextCompat.getColor(context, R.color.pastel_blue));
                mBtn.setText(s);
                mBtn.setLayoutParams(params);

                Paint textPaint = mBtn.getPaint();
                textPaint.getTextBounds(s, 0, s.length(), bounds);
                int textWidth = bounds.width() + dp5 * 4 + dp3 * 2;

                Rect tempBounds = new Rect();
                textPaint = finalText.getPaint();
                textPaint.getTextBounds(remainString, 0, remainString.length(), tempBounds);
                int remainWidth = tempBounds.width() + dp5 * 4 + dp3 * 2;

                if (mButtonsSize + textWidth + remainWidth < width) {
                    interestField.addView(mBtn);
                    mButtonsSize += textWidth;
                } else {
                    interestField.addView(finalText);
                    isAdd = true;
                    break;
                }

            }

            if (!isAdd) {
                finalText.setText("+0");
                interestField.addView(finalText);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    private void getUserInfo(){

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "getUserInfo");
        map.put("id", userId);
        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map){
            @Override
            protected void afterThreadFinish(String data) {
                item = AdditionalFunc.getUserInfo(data);
                handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FILL_FORM));
            }
        }.start();

    }

    private class MyHandler extends Handler {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_FILL_FORM:
                    fillForm();
                    break;
                default:
                    break;
            }
        }
    }

}
