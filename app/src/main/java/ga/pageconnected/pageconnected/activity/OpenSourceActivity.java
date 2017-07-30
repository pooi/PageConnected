package ga.pageconnected.pageconnected.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.util.OpenSourceItem;


public class OpenSourceActivity extends Activity {

    private Toolbar toolbar;
    private TextView closeBtn;
    private ScrollView sv;
    private LinearLayout li_content;

    private ArrayList<OpenSourceItem> list;

    private final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_source);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        closeBtn = (TextView) findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sv = (ScrollView) findViewById(R.id.sv);
        li_content = (LinearLayout) findViewById(R.id.li_content);

//        sv.setOnCustomScrollChangeListener(new OnCustomScrollChangeListener() {
//            @Override
//            public void scrollChanged(int scrollX, int scrollY, int dx, int dy) {
//                if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
//                    hideToolbar();
//                    controlsVisible = false;
//                    scrolledDistance = 0;
//                } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
//                    showToolbar();
//                    controlsVisible = true;
//                    scrolledDistance = 0;
//                }
//
//                if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
//                    scrolledDistance += dy;
//                }
//            }
//        });

        makeList();
        makeLayout();
        makeAdditionLayout();

    }

    private void makeList() {

        list = new ArrayList<>();
        list.add(new OpenSourceItem("PageConnected", "pooi", "https://github.com/pooi/PageConnected", OpenSourceItem.APACHE2_0));
        list.add(new OpenSourceItem("PDFMaker", "pooi", "https://github.com/pooi/PDFMaker", OpenSourceItem.APACHE2_0));
        list.add(new OpenSourceItem("KenBurnsView", "flavioarfaria", "https://github.com/flavioarfaria/KenBurnsView", OpenSourceItem.APACHE2_0));
        list.add(new OpenSourceItem("picasso", "square", "https://github.com/square/picasso", OpenSourceItem.APACHE2_0));
        list.add(new OpenSourceItem("picasso-transformations", "wasabeef", "https://github.com/wasabeef/picasso-transformations", OpenSourceItem.APACHE2_0));
        list.add(new OpenSourceItem("MaterialEditText", "rengwuxian", "https://github.com/rengwuxian/MaterialEditText", OpenSourceItem.APACHE2_0));
        list.add(new OpenSourceItem("material-dialogs", "afollestad", "https://github.com/afollestad/material-dialogs", OpenSourceItem.MIT));
        list.add(new OpenSourceItem("AVLoadingIndicatorView", "81813780", "https://github.com/81813780/AVLoadingIndicatorView", OpenSourceItem.APACHE2_0));
        list.add(new OpenSourceItem("NavigationTabStrip", "Devlight", "https://github.com/Devlight/NavigationTabStrip", OpenSourceItem.APACHE2_0 + ", " + OpenSourceItem.MIT));
        list.add(new OpenSourceItem("android-floating-action-button", "futuresimple", "https://github.com/futuresimple/android-floating-action-button", OpenSourceItem.APACHE2_0));
        list.add(new OpenSourceItem("VolleyPlus", "DWorkS", "https://github.com/DWorkS/VolleyPlus", OpenSourceItem.APACHE2_0));
        list.add(new OpenSourceItem("PhotoView", "chrisbanes", "https://github.com/chrisbanes/PhotoView", OpenSourceItem.APACHE2_0));

    }

    private void makeLayout() {

        for (OpenSourceItem item : list) {

            View v = item.getCustomView(getApplicationContext());//OpenSourceItem.makeView(getApplicationContext(), item);
            li_content.addView(v);

        }

    }

    private void makeAdditionLayout() {

        {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.open_source_custom_item, null, false);

            CardView cardView = (CardView) v.findViewById(R.id.cv);
            cardView.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), OpenSourceItem.APACHE_COLOR));

            TextView title = (TextView) v.findViewById(R.id.tv_title);
            TextView url = (TextView) v.findViewById(R.id.tv_url);
            TextView license = (TextView) v.findViewById(R.id.tv_license);

            title.setText(OpenSourceItem.APACHE2_0);
            url.setText(R.string.apache2_0_license);
            license.setText(OpenSourceItem.APACHE2_0);

            li_content.addView(v);
        }

        {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.open_source_custom_item, null, false);

            CardView cardView = (CardView) v.findViewById(R.id.cv);
            cardView.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), OpenSourceItem.MIT_COLOR));

            TextView title = (TextView) v.findViewById(R.id.tv_title);
            TextView url = (TextView) v.findViewById(R.id.tv_url);
            TextView license = (TextView) v.findViewById(R.id.tv_license);

            title.setText(OpenSourceItem.MIT);
            url.setText(R.string.mit_license);
            license.setText(OpenSourceItem.MIT);

            li_content.addView(v);
        }

    }

    public void hideToolbar() {
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    public void showToolbar() {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    public void showSnackbar(String msg) {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.snackbar_color));
        snackbar.show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
