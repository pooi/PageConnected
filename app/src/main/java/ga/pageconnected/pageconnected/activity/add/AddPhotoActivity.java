package ga.pageconnected.pageconnected.activity.add;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;


import java.io.Serializable;

import ga.pageconnected.pageconnected.BaseActivity;
import ga.pageconnected.pageconnected.Information;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.util.AdditionalFunc;
import ga.pageconnected.pageconnected.util.ParsePHP;

public class AddPhotoActivity extends BaseActivity implements Serializable{

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_SUCCESS = 500;
    private final int MSG_MESSAGE_FAIL = 501;


    private MaterialDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        init();

    }

    private void init(){




        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .build();

    }



    private void add(){


    }

    private void checkAddable(){

//        boolean isTitle = editTitle.isCharactersCountValid();
//        boolean isContent = editContent.isCharactersCountValid();
//        boolean isDay = !day.equals("");
//        boolean isLayout = layoutNumber >= 0;
//
//        boolean setting = isTitle && isContent && isDay && isLayout;
//
//        addBtn.setEnabled(setting);
//        setButtonColor(addBtn, setting);


    }

    private void setButtonColor(Button btn, boolean check){
        if(check){
            btn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        }else{
            btn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_gray));
        }
    }

    private void setPressedBtn(TextView tv, String text){

        tv.setText(text);
        tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_gray));
        tv.setTypeface(Typeface.DEFAULT);

    }

    private String[] getDayString(){
        String[] list = new String[Information.DATE_LIST.length];
        for(int i=0; i<Information.DATE_LIST.length; i++){
            int day = i;
            String date = Information.DATE_LIST[i];
            String title;
            if(date == "0"){ // 대회 시작전
                title = getResources().getString(R.string.before_the_competition);
            }else {
                title = String.format(getResources().getString(R.string.day_title), day, date.substring(0, 4), date.substring(4, 6), date.substring(6, 8));
            }
            list[i] = title;

        }
        return list;
    }


    private class MyHandler extends Handler implements Serializable{

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_SUCCESS:
                    progressDialog.hide();
                    new MaterialDialog.Builder(AddPhotoActivity.this)
                            .title(R.string.success_short)
                            .content(R.string.successfully_posted_article)
                            .positiveText(R.string.ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                    AddPhotoActivity.this.finish();
                                }
                            })
                            .show();
                    break;
                case MSG_MESSAGE_FAIL:
                    progressDialog.hide();
                    new MaterialDialog.Builder(AddPhotoActivity.this)
                            .title(R.string.fail_short)
                            .content(R.string.failed_register_article)
                            .positiveText(R.string.ok)
                            .show();
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
