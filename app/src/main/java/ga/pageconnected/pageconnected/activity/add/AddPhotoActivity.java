package ga.pageconnected.pageconnected.activity.add;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import ga.pageconnected.pageconnected.BaseActivity;
import ga.pageconnected.pageconnected.Information;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.util.AdditionalFunc;
import ga.pageconnected.pageconnected.util.ParsePHP;

public class AddPhotoActivity extends BaseActivity implements Serializable{

    private static final String TAG = "AddPhotoActivity";
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_SUCCESS = 500;
    private final int MSG_MESSAGE_FAIL = 501;

    static final int PICK_IMAGE_REQUEST = 1;

    private MaterialEditText editContent;
    private LinearLayout li_photoField;
    private TextView selectDayBtn;
//    private TextView addPhotoBtn;
    private CardView cv_addPhoto;
    private ImageView addPhotoBtn;
    private Button addBtn;

    private MaterialDialog progressDialog;

    private ArrayList<View> imgViewList;
    private ArrayList<String> filePath;
    private String day = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        imgViewList = new ArrayList<>();
        filePath = new ArrayList<>();

        init();

    }

    private void init(){

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkAddable();
            }
        };
        editContent = (MaterialEditText)findViewById(R.id.edit_content);
        editContent.addTextChangedListener(textWatcher);

        li_photoField = (LinearLayout)findViewById(R.id.li_photo_field);
        cv_addPhoto = (CardView)findViewById(R.id.cv_add_photo);
//        addPhotoBtn = (TextView)findViewById(R.id.add_photo_btn);
        addPhotoBtn = (ImageView)findViewById(R.id.add_photo_btn);
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageBrowse();
            }
        });

        selectDayBtn = (TextView)findViewById(R.id.select_day_btn);
        selectDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(AddPhotoActivity.this)
                        .title(R.string.select_short)
                        .items(getDayString())
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                day = Information.DATE_LIST[position];
                                setPressedBtn(selectDayBtn, getDayString()[position]);
                                checkAddable();
                            }
                        })
                        .theme(Theme.LIGHT)
                        .positiveText(R.string.close)
                        .show();
            }
        });
        addBtn = (Button)findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });

        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .build();

    }

    private void imageBrowse() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
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

    private void addPhotoLayout(Uri uri){

        View v = getLayoutInflater().inflate(R.layout.add_photo_list_custom_item, null, false);

        ImageView img = (ImageView)v.findViewById(R.id.img);
        Button deleteBtn = (Button) v.findViewById(R.id.delete_btn);

        Picasso.with(getApplicationContext())
                .load(uri)
                .resize(0, 500)
                .into(img);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int pos = (int)view.getTag();
//                deletePhotoLayout(pos);
            }
        });

        li_photoField.removeView(cv_addPhoto);
        li_photoField.addView(v);
        li_photoField.addView(cv_addPhoto);

//        deleteBtn.setTag(imgViewList.size());
//        imgViewList.add(v);
        checkPhotoCount();
        checkAddable();
    }

    private void deletePhotoLayout(int position){

//        View v = imgViewList.get(position);
//        li_photoField.removeView(v);
//        imgViewList.remove(v);

        checkPhotoCount();
        checkAddable();

    }

    private void checkPhotoCount(){
        if(filePath.size() >= 5){
            cv_addPhoto.setVisibility(View.GONE);
        }
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if(requestCode == PICK_IMAGE_REQUEST){
                Uri picUri = data.getData();

                filePath.add(getPath(picUri));

                Log.d("picUri", picUri.toString());
                Log.d("filePath", filePath.get(filePath.size()-1));

//                imgPhoto.setImageURI(picUri);
                addPhotoLayout(picUri);

            }

        }

    }
    private String getPath(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

}
