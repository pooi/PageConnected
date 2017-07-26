package ga.pageconnected.pageconnected.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import ga.pageconnected.pageconnected.BaseActivity;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.util.AdvancedImageView;
import ga.pageconnected.pageconnected.util.LayoutItem;
import ga.pageconnected.pageconnected.util.UpdateItem;
import ga.pageconnected.pageconnected.util.pdf.DialogListener;
import ga.pageconnected.pageconnected.util.pdf.GeneratePDF;

public class ShowLayoutActivity extends BaseActivity implements DialogListener{

    public static final int UPDATE_HEART = 100;
    public final int MY_PERMISSION_REQUEST_STORAGE = 100;

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_UPDATE_HEART = 500;
    private final int MSG_MESSAGE_UPDATE_FAIL_HEART = 501;
    private final int MSG_MESSAGE_SHOW_PDF_FILE = 502;
    private final int MSG_MESSAGE_HIDE_PROGRESS = 503;
    private final int MSG_MESSAGE_FAIL_GENERATE_PDF = 504;
    private final int MSG_MESSAGE_CHANGE_DIALOG_CONTENT = 505;

    private RelativeLayout root;
    private AdvancedImageView[] ivList;
    private TextView tv_title;
    private TextView tv_content;
    private View line0;
    private TextView tv_reference;
    private LinearLayout li_funcField;
    private RelativeLayout rl_interest;
    private TextView tv_generate;
    private ImageView img_heart;

    private LayoutItem layoutItem;
    private MaterialDialog progressDialog;

    private boolean testMode;
    private int position;

    private File pdfFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        layoutItem = (LayoutItem)intent.getSerializableExtra("item");
        testMode = intent.getBooleanExtra("testMode", false);
        position = intent.getIntExtra("position", -1);

        setContentView(layoutItem.getViewId());

        init();

        checkAlreadyExistFile();
        checkPDFText();

    }

    private void init(){

        resetProgressDialog();

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
        li_funcField = (LinearLayout)findViewById(R.id.li_func_field);
        rl_interest = (RelativeLayout)findViewById(R.id.rl_interest);
        img_heart = (ImageView)findViewById(R.id.img_heart);
        tv_generate = (TextView)findViewById(R.id.tv_generate);

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
            li_funcField.setVisibility(View.VISIBLE);
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

            tv_generate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(pdfFile == null){
                        checkPermission(); // generate pdf
                    }else{
                        viewPdf();
                    }
                }
            });

        }else{
//            li_funcField.setVisibility(View.VISIBLE);
//            rl_interest.setVisibility(View.GONE);
//            findViewById(R.id.func_middle_line).setVisibility(View.GONE);
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

    @Override
    public void changeContent(int id) {
        String str = getResources().getString(id);
        Message msg = handler.obtainMessage(MSG_MESSAGE_CHANGE_DIALOG_CONTENT);
        Bundle bdl = new Bundle(1);
        bdl.putString("content", str);
        msg.setData(bdl);
        handler.sendMessage(msg);
//        progressDialog.setContent(id);
    }

    @Override
    public void changeContent(String content) {
//        progressDialog.setContent(content);
        Message msg = handler.obtainMessage(MSG_MESSAGE_CHANGE_DIALOG_CONTENT);
        Bundle bdl = new Bundle(1);
        bdl.putString("content", content);
        msg.setData(bdl);
        handler.sendMessage(msg);
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
                case MSG_MESSAGE_SHOW_PDF_FILE:
                    progressDialog.hide();
                    resetProgressDialog();
                    checkPDFText();

                    viewPdf();

                    break;
                case MSG_MESSAGE_HIDE_PROGRESS:
                    progressDialog.hide();
                    resetProgressDialog();
                    break;
                case MSG_MESSAGE_FAIL_GENERATE_PDF:
                    progressDialog.hide();
                    resetProgressDialog();
                    new MaterialDialog.Builder(ShowLayoutActivity.this)
                            .title(R.string.error)
                            .content(R.string.fail_generate_pdf)
                            .positiveText(R.string.ok)
                            .show();
                    break;
                case MSG_MESSAGE_CHANGE_DIALOG_CONTENT:
                    Bundle bdl = msg.getData();
                    String content = bdl.getString("content");
                    progressDialog.setContent(content);
                    break;
                default:
                    break;
            }
        }
    }

    private void resetProgressDialog(){

        if(progressDialog != null){
            progressDialog.dismiss();
        }
        progressDialog = new MaterialDialog.Builder(ShowLayoutActivity.this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .cancelable(false)
                .build();
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

    private void checkAlreadyExistFile(){
        String fileName = layoutItem.getTable() + "_" + layoutItem.getId() + ".pdf";

        File dir = new File(Environment.getExternalStorageDirectory(), "PageConnected");
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for(int i=0; i<children.length; i++){
                if(fileName.equals(children[i])){
                    pdfFile = new File(dir, children[i]);
                    break;
                }
            }
//                        for (int i = 0; i < children.length; i++)
//                        {
//                            new File(dir, children[i]).delete();
//                        }

        }
    }
    private void checkPDFText(){
        if(pdfFile == null){
            tv_generate.setText(R.string.generate_pdf);
        }else{
            tv_generate.setText(R.string.show_pdf);
        }
    }

    private void createPdf(){

        try {
//            Date date = new Date();
//            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

            File pdfFolder = new File(Environment.getExternalStorageDirectory(), "PageConnected");
            if (!pdfFolder.exists()) {
                pdfFolder.mkdir();
            }
            pdfFile = new File(pdfFolder, layoutItem.getTable() + "_" + layoutItem.getId() + ".pdf");

            OutputStream output = new FileOutputStream(pdfFile);

            progressDialog.show();
            new GeneratePDF(getApplicationContext(), output, layoutItem.getItem(), this){
                @Override
                protected void afterThreadFinish(boolean status) {

                    if(status){
                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SHOW_PDF_FILE));
                    }else{
                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FAIL_GENERATE_PDF));
                    }

                }
            }.start();

        }catch (Exception e){
            e.printStackTrace();
            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FAIL_GENERATE_PDF));
        }

    }

    private void viewPdf(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setAction(android.content.Intent.ACTION_VIEW);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    createPdf();

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
                Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_STORAGE);

        } else {
            createPdf();
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
