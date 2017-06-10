package ga.pageconnected.pageconnected.activity.add;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.Serializable;

import ga.pageconnected.pageconnected.BaseActivity;
import ga.pageconnected.pageconnected.Information;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.util.CustomViewPager;
import ga.pageconnected.pageconnected.util.PagerContainer;

public class SelectLayoutActivity extends BaseActivity {

    // Select Layout Frame
    private RelativeLayout rl_selectLayout;
    private PagerContainer viewPagerContainer;
    private CustomViewPager viewPager;
    private NavigationAdapter pagerAdapter;

    private SelectListener selectListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_layout);

        selectListener = new SelectListener() {
            @Override
            public void selected(int position) {

                Intent intent = new Intent();
                intent.putExtra("layout", position);
                setResult(AddArticleActivity.SELECT_LAYOUT, intent);
                SelectLayoutActivity.this.finish();
//                layoutNumber = position;
//
//                tv_layout.setText("Layout " + (position+1));
//                rl_inputLayout.setVisibility(View.VISIBLE);
//
//                rl_selectLayout.setVisibility(View.GONE);
//                setFadeOutAnimation(rl_selectLayout);
//                checkAddable();

            }
        };

        init();
    }

    private void init(){


        // Select Layout Frame
        rl_selectLayout = (RelativeLayout)findViewById(R.id.rl_select_layout);
        rl_selectLayout.setVisibility(View.VISIBLE);
        viewPagerContainer = (PagerContainer)findViewById(R.id.view_pager_container);
        viewPager = (CustomViewPager) findViewById(R.id.view_pager);
        pagerAdapter = new NavigationAdapter(getSupportFragmentManager(), selectListener, Information.LAYOUT_COUNT);
        viewPager.setOffscreenPageLimit(Information.LAYOUT_COUNT);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageMargin(30);
        viewPager.setClipChildren(false);

    }

    private static class NavigationAdapter extends FragmentPagerAdapter implements Serializable {

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
