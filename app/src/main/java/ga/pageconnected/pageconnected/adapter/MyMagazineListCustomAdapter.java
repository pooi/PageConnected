package ga.pageconnected.pageconnected.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import net.sf.andpdf.nio.ByteBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.Information;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.fragment.MyMagazineFragment;
import ga.pageconnected.pageconnected.util.LoadBitmap;
import ga.pageconnected.pageconnected.util.OnAdapterSupport;
import ga.pageconnected.pageconnected.util.OnLoadMoreListener;


/**
 * Created by tw on 2017-06-14.
 */
public class MyMagazineListCustomAdapter extends RecyclerView.Adapter<MyMagazineListCustomAdapter.ViewHolder> {

    // UI
    private Context context;
    private MyMagazineFragment fragment;

    //    private MaterialNavigationDrawer activity;
    private OnAdapterSupport onAdapterSupport;

    public ArrayList<HashMap<String, String>> list;

    // 무한 스크롤
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean loading = false;

    // 생성자
    public MyMagazineListCustomAdapter(Context context, ArrayList<HashMap<String, String>> list, RecyclerView recyclerView, OnAdapterSupport listener, MyMagazineFragment fragment) {
        this.context = context;
        this.list = list;
        this.onAdapterSupport = listener;
        this.fragment = (MyMagazineFragment) fragment;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.magazine_list_custom_item,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final HashMap<String,String> item = list.get(position);
        final int pos = position;

        String title = item.get("title");

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = item.get("file");
                File dir = new File(Environment.getExternalStorageDirectory(), "PageConnected/mymagazine");
                File pdfFile = new File(dir, fileName);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if(Build.VERSION.SDK_INT >= 24) {
                    Uri pdfURI = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", pdfFile);
                    System.out.println(context.getPackageName());
                    intent.setDataAndType(pdfURI, "application/pdf");
                }else{
                    intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                onAdapterSupport.redirectActivity(intent);
            }
        });
        holder.cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String fileName = item.get("file");
                fragment.removeMagazine(pos, fileName);
//                File dir = new File(Environment.getExternalStorageDirectory(), "PageConnected/mymagazine");
//                new File(dir, fileName).delete();
//                list.remove(pos);
//                notifyItemRemoved(pos);
                return false;
            }
        });

//        Picasso.with(context)
//                .load((String)item.get("coverImg"))
//                .into(holder.img);

        holder.tv_day.setText(title);

        new LoadBitmap(holder.img, item.get("file")).start();
//        Bitmap bitmap = getBitmap(item.get("file"));
//        if(bitmap!=null){
//            holder.img.setImageBitmap(bitmap);
//        }


    }

    private Bitmap getBitmap(String fileName){

        byte[] bytes;
        try {

            File dir = new File(Environment.getExternalStorageDirectory(), "PageConnected/mymagazine");
            File file = new File(dir, fileName);
            FileInputStream is = new FileInputStream(file);

            // Get the size of the file
            long length = file.length();
            bytes = new byte[(int) length];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
            ByteBuffer buffer = ByteBuffer.NEW(bytes);
            String data = Base64.encodeToString(bytes, Base64.DEFAULT);
            PDFFile pdf_file = new PDFFile(buffer);
            PDFPage page = pdf_file.getPage(0, true);

            RectF rect = new RectF(0, 0, (int) page.getBBox().width(),
                    (int) page.getBBox().height());

            Bitmap image = page.getImage((int)rect.width(), (int)rect.height(), rect);
//            File file1 = new File(dir, "sixth.jpg");
//            FileOutputStream os = new FileOutputStream(file1);
//            image.compress(Bitmap.CompressFormat.PNG, 100, os);
            //((ImageView) findViewById(R.id.testView)).setImageBitmap(image);
            return image;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

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
        TextView tv_day;
        ImageView img;

        public ViewHolder(View v) {
            super(v);
            cv = (CardView)v.findViewById(R.id.cv);
            tv_day = (TextView) v.findViewById(R.id.tv_day);
            img = (ImageView)v.findViewById(R.id.img);
        }
    }

}
