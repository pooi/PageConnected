package ga.pageconnected.pageconnected.activity.add;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.BaseActivity;
import ga.pageconnected.pageconnected.Information;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.util.AdditionalFunc;
import ga.pageconnected.pageconnected.util.CustomViewPager;
import ga.pageconnected.pageconnected.util.PagerContainer;

public class AddArticleActivity extends BaseActivity implements SelectListener{


    // Input Frame
    private RelativeLayout rl_inputLayout;
    private TextView tv_layout;
    private MaterialEditText editTitle;
    private MaterialEditText editContent;
    private LinearLayout li_referenceField;
    private TextView addReferenceBtn;
    private LinearLayout li_photoField;
    private TextView addPhotoBtn;
    private TextView selectDayBtn;
    private Button addBtn;

    private int layoutNumber = -1;
    private ArrayList<String> referenceList;
    private String day = "";

    // Select Layout Frame
    private RelativeLayout rl_selectLayout;
    private PagerContainer viewPagerContainer;
    private CustomViewPager viewPager;
    private NavigationAdapter pagerAdapter;

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
                viewPager.setCurrentItem(layoutNumber);
                rl_selectLayout.setVisibility(View.VISIBLE);
                setFadeInAnimation(rl_selectLayout);
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
                new MaterialDialog.Builder(AddArticleActivity.this)
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
                // TODO
                showSnackbar("TODO");
            }
        });
        selectDayBtn = (TextView)findViewById(R.id.select_day_btn);
        selectDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(AddArticleActivity.this)
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


        // Select Layout Frame
        rl_selectLayout = (RelativeLayout)findViewById(R.id.rl_select_layout);
        rl_selectLayout.setVisibility(View.VISIBLE);
        viewPagerContainer = (PagerContainer)findViewById(R.id.view_pager_container);
        viewPager = (CustomViewPager) findViewById(R.id.view_pager);
        pagerAdapter = new NavigationAdapter(getSupportFragmentManager(), this, Information.LAYOUT_COUNT);
        viewPager.setOffscreenPageLimit(Information.LAYOUT_COUNT);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageMargin(30);
        viewPager.setClipChildren(false);


    }

    private void add(){

        //TODO
        showSnackbar("TODO");

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

    @Override
    public void selected(int position) {

        layoutNumber = position;

        tv_layout.setText("Layout " + (position+1));
        rl_inputLayout.setVisibility(View.VISIBLE);

        rl_selectLayout.setVisibility(View.GONE);
        setFadeOutAnimation(rl_selectLayout);
        checkAddable();

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


    private static class NavigationAdapter extends FragmentPagerAdapter {

        private int size;
        private SelectListener listener;

        public NavigationAdapter(FragmentManager fm, SelectListener listener, int size){
            super(fm);
            this.size = size;
            this.listener = listener;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f;
            final int pattern = position % size;

            f = new LayoutFragment();
            Bundle bdl = new Bundle(1);
            bdl.putInt("position", pattern);
            bdl.putSerializable("listener", listener);
            f.setArguments(bdl);

            return f;
        }

        @Override
        public int getCount(){
            return size;
        }


    }
}