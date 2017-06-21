package ga.pageconnected.pageconnected.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.activity.PhotoArticleActivity;
import ga.pageconnected.pageconnected.fragment.PhotoFragment;
import ga.pageconnected.pageconnected.profile.ProfileActivity;
import ga.pageconnected.pageconnected.util.OnAdapterSupport;
import ga.pageconnected.pageconnected.util.OnLoadMoreListener;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;


/**
 * Created by tw on 2017-06-14.
 */
public class PhotoListCustomAdapter extends RecyclerView.Adapter<PhotoListCustomAdapter.ViewHolder> {

    // UI
    private Context context;
    private PhotoArticleActivity activity;

    //    private MaterialNavigationDrawer activity;
    private OnAdapterSupport onAdapterSupport;

    public ArrayList<HashMap<String, Object>> list;

    // 무한 스크롤
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean loading = false;

    // 생성자
    public PhotoListCustomAdapter(Context context, ArrayList<HashMap<String, Object>> list, RecyclerView recyclerView, OnAdapterSupport listener, PhotoArticleActivity activity) {
        this.context = context;
        this.list = list;
        this.onAdapterSupport = listener;
        this.activity = (PhotoArticleActivity) activity;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_list_custom_item,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final HashMap<String,Object> item = list.get(position);
        final int pos = position;

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
        String content = (String)item.get("content");
        if("".equals(content)){
            holder.tv_content.setVisibility(View.GONE);
        }else{
            holder.tv_content.setVisibility(View.VISIBLE);
            holder.tv_content.setText(content);
        }

        // image
        ArrayList<HashMap<String, Object>> imageList = (ArrayList<HashMap<String, Object>>)item.get("imageList");
        if(imageList.size() > 1){
            holder.tv_imageCount.setVisibility(View.VISIBLE);
            holder.tv_imageCount.setText("+" + (imageList.size()));
        }else{
            holder.tv_imageCount.setVisibility(View.GONE);
        }
        if(imageList.size() > 0){
            String photo = (String)imageList.get(0).get("photo");
            Picasso.with(context)
                    .load(photo)
                    .resize(500, 0)
                    .into(holder.defaultImg);
        }

        holder.tv_hit.setText((String)item.get("hit"));
        holder.tv_heart.setText((String)item.get("heart"));


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

        RelativeLayout rl_profile;
        ImageView profileImg;
        TextView tv_name;
        TextView tv_email;
        TextView tv_content;
        ImageView defaultImg;
        TextView tv_imageCount;
        TextView tv_heart;
        TextView tv_hit;

        public ViewHolder(View v) {
            super(v);
            rl_profile = (RelativeLayout)v.findViewById(R.id.rl_profile);
            profileImg = (ImageView)v.findViewById(R.id.profileImg);
            tv_name = (TextView)v.findViewById(R.id.tv_name);
            tv_email = (TextView)v.findViewById(R.id.tv_email);
            tv_content = (TextView)v.findViewById(R.id.tv_content);
            defaultImg = (ImageView)v.findViewById(R.id.img_default);
            tv_imageCount = (TextView)v.findViewById(R.id.tv_img_count);
            tv_heart = (TextView)v.findViewById(R.id.tv_heart);
            tv_hit = (TextView)v.findViewById(R.id.tv_hit);
        }
    }

}
