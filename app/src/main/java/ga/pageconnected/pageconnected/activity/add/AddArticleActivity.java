package ga.pageconnected.pageconnected.activity.add;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.BaseActivity;
import ga.pageconnected.pageconnected.Information;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.util.CustomViewPager;
import ga.pageconnected.pageconnected.util.PagerContainer;

public class AddArticleActivity extends BaseActivity implements SelectListener{


    // Input Frame
    private RelativeLayout rl_inputLayout;
    private TextView tv_layout;

    private int layoutNumber;

    // Select Layout Frame
    private RelativeLayout rl_selectLayout;
    private PagerContainer viewPagerContainer;
    private CustomViewPager viewPager;
    private NavigationAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);

        init();

    }

    private void init(){

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

        // Select Layout Frame
        rl_selectLayout = (RelativeLayout)findViewById(R.id.rl_select_layout);
        viewPagerContainer = (PagerContainer)findViewById(R.id.view_pager_container);
        viewPager = (CustomViewPager) findViewById(R.id.view_pager);
        pagerAdapter = new NavigationAdapter(getSupportFragmentManager(), this, Information.LAYOUT_COUNT);
        viewPager.setOffscreenPageLimit(Information.LAYOUT_COUNT);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageMargin(20);
        viewPager.setClipChildren(false);


    }



    @Override
    public void selected(int position) {

        layoutNumber = position;

        tv_layout.setText("Layout " + (position+1));
        rl_inputLayout.setVisibility(View.VISIBLE);

        rl_selectLayout.setVisibility(View.GONE);
        setFadeOutAnimation(rl_selectLayout);

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
