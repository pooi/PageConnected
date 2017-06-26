package ga.pageconnected.pageconnected.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;

import ga.pageconnected.pageconnected.BaseActivity;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.fragment.ArticleFragment;
import ga.pageconnected.pageconnected.fragment.ColumnFragment;
import ga.pageconnected.pageconnected.fragment.PhotoFragment;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ProfileActivity extends BaseActivity {

    public final static int EDIT_PROFILE = 100;

    private String[] titles;

    private String userId;

    private ViewPager mViewPager;
    private NavigationTabStrip mNavigationTabStrip;
    private InfoFragment infoFragment;
    private ArticleFragment articleFragment;
    private ColumnFragment columnFragment;
    private PhotoFragment photoFragment;

    private RelativeLayout rl_profile;
    private TextView tv_name;
    private ImageView profileImg;
    private AVLoadingIndicatorView loadingName;
    boolean isProfileVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        userId = intent.getStringExtra("id");


        titles = new String[]{
                getResources().getString(R.string.info),
                getResources().getString(R.string.article),
                getResources().getString(R.string.column),
                getResources().getString(R.string.photo)
        };

        initUI();

    }

    private void initUI(){

        rl_profile = (RelativeLayout)findViewById(R.id.rl_profile);

        tv_name = (TextView)findViewById(R.id.tv_name);
        profileImg = (ImageView)findViewById(R.id.profileImg);
        loadingName = (AVLoadingIndicatorView)findViewById(R.id.loading_name);

        mNavigationTabStrip = (NavigationTabStrip) findViewById(R.id.nts_top);
        mNavigationTabStrip.setTitles(titles);
        mViewPager = (ViewPager) findViewById(R.id.vp);
        mViewPager.setOffscreenPageLimit(titles.length);
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                final int pattern = position % titles.length;
                Fragment f;

                switch (pattern){
                    case 0:
                        if(infoFragment == null){
                            infoFragment = new InfoFragment();
                            Bundle bdl = new Bundle(1);
                            bdl.putString("id", userId);
                            infoFragment.setArguments(bdl);
                        }
                        f = infoFragment;
                        break;
                    case 1:
                        if(articleFragment == null){
                            articleFragment = new ArticleFragment();
                            Bundle bdl = new Bundle(1);
                            bdl.putString("id", userId);
                            articleFragment.setArguments(bdl);
                        }
                        f = articleFragment;
                        break;
                    case 2:
                        if(columnFragment == null){
                            columnFragment = new ColumnFragment();
                            Bundle bdl = new Bundle(1);
                            bdl.putString("id", userId);
                            columnFragment.setArguments(bdl);
                        }
                        f = columnFragment;
                        break;
                    case 3:
                        if(photoFragment == null){
                            photoFragment = new PhotoFragment();
                            Bundle bdl = new Bundle(1);
                            bdl.putString("id", userId);
                            photoFragment.setArguments(bdl);
                        }
                        f = photoFragment;
                        break;
                    default:
                        f = new Fragment();
                        break;
                }

                return f;
            }

            @Override
            public int getCount() {
                return titles.length;
            }
        });

        mNavigationTabStrip.setViewPager(mViewPager, 0);
        mNavigationTabStrip.setTabIndex(0, true);

    }

    public void setProfileVisible(){
        isProfileVisible = !isProfileVisible;
        if(isProfileVisible){
            rl_profile.setVisibility(View.VISIBLE);
            tv_name.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            tv_name.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_gray));
        }else{
            rl_profile.setVisibility(View.GONE);
            tv_name.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            tv_name.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }
    }

    public void setProfile(String name, String img){

        tv_name.setText(name + "님");
        loadingName.hide();

        Picasso.with(getApplicationContext())
                .load(img)
                .transform(new CropCircleTransformation())
                .into(profileImg);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case EDIT_PROFILE:
                //InfoFragment fragment = (InfoFragment)((FragmentStatePagerAdapter)mViewPager.getAdapter()).ins;
                infoFragment.updateUI((HashMap<String, Object>) data.getSerializableExtra("item"));
                break;
            default:
                break;
        }

    }

}
