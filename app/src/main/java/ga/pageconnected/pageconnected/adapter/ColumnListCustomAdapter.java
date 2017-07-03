package ga.pageconnected.pageconnected.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.activity.ColumnActivity;
import ga.pageconnected.pageconnected.activity.ShowLayoutActivity;
import ga.pageconnected.pageconnected.profile.ProfileActivity;
import ga.pageconnected.pageconnected.util.AdditionalFunc;
import ga.pageconnected.pageconnected.util.LayoutItem;
import ga.pageconnected.pageconnected.util.OnAdapterSupport;
import ga.pageconnected.pageconnected.util.OnLoadMoreListener;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;


/**
 * Created by tw on 2017-06-24.
 */
public class ColumnListCustomAdapter extends RecyclerView.Adapter<ColumnListCustomAdapter.ViewHolder> {

    // UI
    private Context context;
    private ColumnActivity activity;

    //    private MaterialNavigationDrawer activity;
    private OnAdapterSupport onAdapterSupport;

    public ArrayList<HashMap<String, Object>> list;

    // 무한 스크롤
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean loading = false;

    // 생성자
    public ColumnListCustomAdapter(Context context, ArrayList<HashMap<String, Object>> list, RecyclerView recyclerView, OnAdapterSupport listener, ColumnActivity activity) {
        this.context = context;
        this.list = list;
        this.onAdapterSupport = listener;
        this.activity = (ColumnActivity) activity;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            recyclerView.addOnScrollListener(new ScrollListener() {
                @Override
                public void onHide() {
                    hideViews();
                }

                @Override
                public void onShow() {
                    showViews();
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //recycler view에 반복될 아이템 레이아웃 연결
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.column_list_custom_item,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final HashMap<String,Object> item = list.get(position);
        final int pos = position;

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectShowLayoutActivity(item);
            }
        });

        // profile
        holder.tv_name.setText((String)item.get("name"));
        holder.tv_email.setText((String)item.get("email"));
        Picasso.with(context)
                .load((String)item.get("img"))
                .transform(new CropCircleTransformation())
                .into(holder.profileImg);
        holder.rl_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("id", (String)item.get("userId"));
                onAdapterSupport.redirectActivity(intent);
            }
        });

        // content
        String title = (String)item.get("title");
        holder.tv_title.setText(title);
        String content = (String)item.get("content");
        holder.tv_content.setText(content);

        ArrayList<String> pictureList = (ArrayList<String>)item.get("picture");
        if(pictureList.size() <= 0){
            holder.defaultImg.setVisibility(View.GONE);
            holder.tv_imageCount.setVisibility(View.GONE);
        }else{
            holder.defaultImg.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(pictureList.get(0))
                    .resize(500, 0)
                    .into(holder.defaultImg);
            if(pictureList.size() > 1) {
                holder.tv_imageCount.setVisibility(View.VISIBLE);
                holder.tv_imageCount.setText("+" + (pictureList.size()-1));
            }else{
                holder.tv_imageCount.setVisibility(View.GONE);
            }
        }

        String date = AdditionalFunc.parseDateString((String)item.get("date"), (String)item.get("time"));
        holder.tv_date.setText(date);

        holder.tv_hit.setText(((int)item.get("hit"))+"");
        holder.tv_heart.setText(((int)item.get("heart"))+"");


    }

    private void redirectShowLayoutActivity(HashMap<String, Object> data){

        Intent intent = new Intent(context, ShowLayoutActivity.class);
        intent.putExtra("item", new LayoutItem(
                (int)data.get("layout"),
                (String)data.get("title"),
                (String)data.get("content"),
                (ArrayList<String>)data.get("url"),
                (ArrayList<String>)data.get("picture")
        ));
        onAdapterSupport.redirectActivity(intent);

    }


    @Override
    public int getItemCount() {
        return this.list.size();
    }

    private void hideViews() {
        if (onAdapterSupport != null) {
            onAdapterSupport.hideView();
        }
    }

    private void showViews() {
        if (onAdapterSupport != null) {
            onAdapterSupport.showView();
        }
    }

    // 무한 스크롤
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }
    public void setLoaded() {
        loading = false;
    }

    public abstract class ScrollListener extends RecyclerView.OnScrollListener {
        private static final int HIDE_THRESHOLD = 30;
        private int scrolledDistance = 0;
        private boolean controlsVisible = true;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            totalItemCount = linearLayoutManager.getItemCount();
            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                // End has been reached
                // Do something
                loading = true;
                if (onLoadMoreListener != null) {
                    onLoadMoreListener.onLoadMore();
                }
            }
            // 여기까지 무한 스크롤

            if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                onHide();
                controlsVisible = false;
                scrolledDistance = 0;
            } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                onShow();
                controlsVisible = true;
                scrolledDistance = 0;
            }

            if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
                scrolledDistance += dy;
            }
            // 여기까지 툴바 숨기기
        }

        public abstract void onHide();
        public abstract void onShow();

    }

    public final static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        RelativeLayout rl_profile;
        ImageView profileImg;
        TextView tv_name;
        TextView tv_email;
        TextView tv_title;
        TextView tv_content;
        ImageView defaultImg;
        TextView tv_imageCount;
        TextView tv_date;
        TextView tv_heart;
        TextView tv_hit;

        public ViewHolder(View v) {
            super(v);
            cv = (CardView)v.findViewById(R.id.cv);
            rl_profile = (RelativeLayout)v.findViewById(R.id.rl_profile);
            profileImg = (ImageView)v.findViewById(R.id.profileImg);
            tv_name = (TextView)v.findViewById(R.id.tv_name);
            tv_email = (TextView)v.findViewById(R.id.tv_email);
            tv_content = (TextView)v.findViewById(R.id.tv_content);
            defaultImg = (ImageView)v.findViewById(R.id.img_default);
            tv_imageCount = (TextView)v.findViewById(R.id.tv_img_count);
            tv_title = (TextView)v.findViewById(R.id.tv_title);
            tv_date = (TextView)v.findViewById(R.id.tv_date);
            tv_heart = (TextView)v.findViewById(R.id.tv_heart);
            tv_hit = (TextView)v.findViewById(R.id.tv_hit);
        }
    }

}
