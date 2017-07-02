package ga.pageconnected.pageconnected.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import ga.pageconnected.pageconnected.BaseActivity;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.util.LayoutItem;

public class ShowLayoutActivity extends BaseActivity {

    private RelativeLayout root;
    private ImageView[] ivList;
    private TextView tv_title;
    private TextView tv_content;
    private TextView tv_reference;

    private LayoutItem layoutItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        layoutItem = (LayoutItem)intent.getSerializableExtra("item");

        setContentView(layoutItem.getViewId());

        init();

    }

    private void init(){

        root = (RelativeLayout)findViewById(R.id.root);
        root.setBackgroundColor(getColorId(R.color.transparent));
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowLayoutActivity.this.onBackPressed();
            }
        });
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_content = (TextView)findViewById(R.id.tv_content);
        tv_reference = (TextView)findViewById(R.id.tv_reference);

        ivList = new ImageView[layoutItem.getMaxImageCount()];
        for(int i=0; i<layoutItem.getMaxImageCount(); i++){
            switch (i){
                case 0:
                    ivList[i] = (ImageView)findViewById(R.id.img0);
                    break;
                case 1:
                    ivList[i] = (ImageView)findViewById(R.id.img1);
                    break;
                case 2:
                    ivList[i] = (ImageView)findViewById(R.id.img2);
                    break;
            }
        }
        for(int i=layoutItem.getImageCount(); i<layoutItem.getMaxImageCount(); i++){
            ivList[i].setVisibility(View.GONE);
        }

        findViewById(R.id.tv_layout_pos).setVisibility(View.GONE);

        fillContent();

    }

    private void fillContent(){
        tv_title.setText(layoutItem.getTitle());
        tv_content.setText(layoutItem.getContent());
        String reference = "";
        for(int i=0; i<layoutItem.getReference().size(); i++){
            reference += layoutItem.getReference().get(i);
            if(i < layoutItem.getReference().size()-1){
                reference += "\n";
            }
        }
        tv_reference.setText(reference);

        for(int i=0; i<layoutItem.getImageCount(); i++){
            final int pos = i;
            String path = layoutItem.getImageList().get(i);
            Picasso.with(getApplicationContext())
                    .load(path)
                    .resize(0, 800)
                    .into(ivList[pos]);
        }
    }



}
