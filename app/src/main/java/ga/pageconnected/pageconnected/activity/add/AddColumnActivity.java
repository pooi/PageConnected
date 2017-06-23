package ga.pageconnected.pageconnected.activity.add;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.BaseActivity;
import ga.pageconnected.pageconnected.Information;
import ga.pageconnected.pageconnected.MyApplication;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.util.AdditionalFunc;
import ga.pageconnected.pageconnected.util.ParsePHP;

public class AddColumnActivity extends BaseActivity implements Serializable{

    private static final String TAG = "AddColumnActivity";
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    public static final int SELECT_LAYOUT = 10;

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_SUCCESS = 500;
    private final int MSG_MESSAGE_FAIL = 501;

    static final int PICK_IMAGE_REQUEST = 1;

    // Input Frame
    private RelativeLayout rl_inputLayout;
    private TextView tv_layout;
    private MaterialEditText editTitle;
    private MaterialEditText editContent;
    private LinearLayout li_referenceField;
    private TextView addReferenceBtn;
    private LinearLayout li_photoField;
    private TextView addPhotoBtn;
    private TextView deletePhotoBtn;
    private ImageView imgPhoto;
    private TextView selectDayBtn;
    private Button addBtn;

    private int layoutNumber = -1;
    private ArrayList<String> referenceList;
    private String filePath;
    private String day = "";

    private MaterialDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);

        referenceList = new ArrayList<>();

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

        // Input Frame
        rl_inputLayout = (RelativeLayout)findViewById(R.id.rl_input_layout);
        tv_layout = (TextView)findViewById(R.id.tv_layout);
        tv_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddColumnActivity.this, SelectLayoutActivity.class);
                startActivityForResult(intent, SELECT_LAYOUT);
            }
        });

        editTitle = (MaterialEditText)findViewById(R.id.edit_title);
        editTitle.addTextChangedListener(textWatcher);
        editContent = (MaterialEditText)findViewById(R.id.edit_content);
        editContent.addTextChangedListener(textWatcher);

        li_referenceField = (LinearLayout)findViewById(R.id.li_reference_field);
        addReferenceBtn = (TextView)findViewById(R.id.add_reference_btn);
        addReferenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(AddColumnActivity.this)
                        .title(R.string.input)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(getResources().getString(R.string.please_enter_url), null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if(!"".equals(input.toString())) {
                                    referenceList.add(AdditionalFunc.replaceNewLineString(input.toString()));
                                    makeReferenceLayout();
                                    checkAddable();
                                }
                            }
                        }).show();
            }
        });
        li_photoField = (LinearLayout)findViewById(R.id.li_photo_field);
        addPhotoBtn = (TextView)findViewById(R.id.add_photo_btn);
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageBrowse();
            }
        });
        deletePhotoBtn = (TextView)findViewById(R.id.delete_photo_btn);
        deletePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPhoto.setImageURI(null);
                filePath = "";
                imgPhoto.setVisibility(View.GONE);
                deletePhotoBtn.setVisibility(View.GONE);
                addPhotoBtn.setText(getResources().getString(R.string.add));
            }
        });
        deletePhotoBtn.setVisibility(View.GONE);
        imgPhoto = (ImageView)findViewById(R.id.img_photo);
        selectDayBtn = (TextView)findViewById(R.id.select_day_btn);
        selectDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(AddColumnActivity.this)
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
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if(requestCode == PICK_IMAGE_REQUEST){
                Uri picUri = data.getData();

                filePath = getPath(picUri);

                Log.d("picUri", picUri.toString());
                Log.d("filePath", filePath);

                addPhotoBtn.setText(getResources().getString(R.string.modify));
                imgPhoto.setImageURI(picUri);
                imgPhoto.setVisibility(View.VISIBLE);
                deletePhotoBtn.setVisibility(View.VISIBLE);

            }

        }else if(resultCode == SELECT_LAYOUT){

            layoutNumber = data.getIntExtra("layout", -1);
            tv_layout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            tv_layout.setText("Layout " + (layoutNumber+1));
            checkAddable();


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

    private void addWithImage(){

        progressDialog.show();

        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, Information.MAIN_SERVER_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            String status = jObj.getString("status");


//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                            if("success".equals(status)){
                                handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SUCCESS));
                            }else{
                                handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FAIL));
                            }

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        smr.addFile("image", filePath);
        smr.addStringParam("service", "saveColumnWithImage");
        smr.addStringParam("userId", getUserID(this));
        smr.addStringParam("layout", Integer.toString(layoutNumber));
        smr.addStringParam("day", day);
        smr.addStringParam("title", AdditionalFunc.replaceNewLineString(editTitle.getText().toString()));
        smr.addStringParam("content", AdditionalFunc.replaceNewLineString(editContent.getText().toString()));
        smr.addStringParam("url", AdditionalFunc.arrayListToString(referenceList));
        MyApplication.getInstance().addToRequestQueue(smr);

    }

    private void add(){

//        $userId = $_POST['userId'];
//        $layout = $_POST['layout'];
//        // $picture = $_POST['picture'];
//        $day = $_POST['day'];
//        $title = $_POST['title'];
//        $title = replaceSqlString($title);
//        $content = $_POST['content'];
//        $content = replaceSqlString($content);
//        $url = $_POST['url'];

        if(filePath != null && !"".equals(filePath)){

            checkPermission();

        }else{
            progressDialog.show();

            HashMap<String, String> map = new HashMap<>();
            map.put("service", "saveColumn");
            map.put("userId", getUserID(this));
            map.put("layout", Integer.toString(layoutNumber));
            map.put("day", day);
            map.put("title", AdditionalFunc.replaceNewLineString(editTitle.getText().toString()));
            map.put("content", AdditionalFunc.replaceNewLineString(editContent.getText().toString()));
            map.put("url", AdditionalFunc.arrayListToString(referenceList));

            new ParsePHP(Information.MAIN_SERVER_ADDRESS, map){
                @Override
                protected void afterThreadFinish(String data) {
                    try {
                        JSONObject jObj = new JSONObject(data);
                        String status = jObj.getString("status");


//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                        if("success".equals(status)){
                            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SUCCESS));
                        }else{
                            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FAIL));
                        }

                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }.start();
        }

    }

    private void checkAddable(){

        boolean isTitle = editTitle.isCharactersCountValid();
        boolean isContent = editContent.isCharactersCountValid();
        boolean isDay = !day.equals("");
        boolean isLayout = layoutNumber >= 0;

        boolean setting = isTitle && isContent && isDay && isLayout;

        addBtn.setEnabled(setting);
        setButtonColor(addBtn, setting);


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



    private void makeReferenceLayout(){

        li_referenceField.removeAllViews();

        for(int i=0; i<referenceList.size(); i++){

            View v = getLayoutInflater().inflate(R.layout.add_field_custom_item, null, false);

            TextView tv_text = (TextView)v.findViewById(R.id.tv_text);
            tv_text.setText(referenceList.get(i));
            ImageView deleteBtn = (ImageView)v.findViewById(R.id.delete_btn);
            deleteBtn.setTag(i);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int)v.getTag();
                    referenceList.remove(index);
                    makeReferenceLayout();
                }
            });

            li_referenceField.addView(v);

        }

    }

    private class MyHandler extends Handler implements Serializable{

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_SUCCESS:
                    progressDialog.hide();
                    new MaterialDialog.Builder(AddColumnActivity.this)
                            .title(R.string.success_short)
                            .content(R.string.successfully_posted_column)
                            .positiveText(R.string.ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                    AddColumnActivity.this.finish();
                                }
                            })
                            .show();
                    break;
                case MSG_MESSAGE_FAIL:
                    progressDialog.hide();
                    new MaterialDialog.Builder(AddColumnActivity.this)
                            .title(R.string.fail_short)
                            .content(R.string.failed_register_column)
                            .positiveText(R.string.ok)
                            .show();
                    break;
                default:
                    break;
            }
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

            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant

        } else {
            // 다음 부분은 항상 허용일 경우에 해당이 됩니다.
//            writeFile();
//            imageUpload(filePath);
            addWithImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

//                    writeFile();
//                    imageUpload(filePath);
                    addWithImage();

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    Log.d(TAG, "Permission always deny");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
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
