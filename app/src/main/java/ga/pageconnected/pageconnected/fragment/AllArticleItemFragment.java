package ga.pageconnected.pageconnected.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.activity.ShowLayoutActivity;
import ga.pageconnected.pageconnected.profile.ProfileActivity;
import ga.pageconnected.pageconnected.util.AdditionalFunc;
import ga.pageconnected.pageconnected.util.AdvancedImageView;
import ga.pageconnected.pageconnected.util.LayoutController;
import ga.pageconnected.pageconnected.util.LayoutItem;
import ga.pageconnected.pageconnected.util.MyMagazineSelectListener;
import ga.pageconnected.pageconnected.util.UpdateItem;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by tw on 2017-06-01.
 */
public class AllArticleItemFragment extends BaseFragment {

    // BASIC UI
    private View view;
    private Context context;

    // DATA
    private MyMagazineSelectListener listener;
    private int position;
    private HashMap<String, Object> data;
    private int layout;

    // UI
    private RelativeLayout root;
    private AdvancedImageView[] ivList;
    private TextView tv_title;
    private TextView tv_content;
    private View line0;
    private TextView tv_reference;
    private LinearLayout li_funcField;
    private RelativeLayout rl_interest;
    private Button selectBtn;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt("position");
        data = (HashMap<String, Object>)getArguments().getSerializable("data");
        layout = (int)data.get("layout");
        listener = (MyMagazineSelectListener)getArguments().getSerializable("listener");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LayoutController.getLayoutId(layout), container, false);
        context = container.getContext();

        initUI();
        fillContent();

        return view;

    }

    private void initUI(){

        root = (RelativeLayout)view.findViewById(R.id.root);
//        root.setBackgroundColor(getColorId(context, R.color.transparent));
        if(layout >= 0) {
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            line0 = (View) view.findViewById(R.id.line0);
            tv_reference = (TextView) view.findViewById(R.id.tv_reference);
            li_funcField = (LinearLayout) view.findViewById(R.id.li_func_field);
            rl_interest = (RelativeLayout) view.findViewById(R.id.rl_interest);

            view.findViewById(R.id.tv_layout_pos).setVisibility(View.GONE);
        }

        ArrayList<String> imageList = (ArrayList<String>)data.get("picture");
        ivList = new AdvancedImageView[LayoutController.getMaxImageCount(layout)];
        for(int i=0; i<LayoutController.getMaxImageCount(layout); i++){
            switch (i){
                case 0:
                    ivList[i] = (AdvancedImageView)view.findViewById(R.id.img0);
                    break;
                case 1:
                    ivList[i] = (AdvancedImageView)view.findViewById(R.id.img1);
                    break;
                case 2:
                    ivList[i] = (AdvancedImageView)view.findViewById(R.id.img2);
                    break;
            }
        }
        for(int i=imageList.size(); i<LayoutController.getMaxImageCount(layout); i++){
            ivList[i].setVisibility(View.GONE);
        }

        selectBtn = (Button)view.findViewById(R.id.select_btn);
        selectBtn.setVisibility(View.VISIBLE);


//        if(!testMode) {
//            li_funcField.setVisibility(View.VISIBLE);
//            rl_interest.setVisibility(View.VISIBLE);
//            rl_interest.setTag(false);
//            rl_interest.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    boolean tag = (boolean) view.getTag();
//                    updateHeart(tag);
////                    view.setTag(!tag);
////                    checkFillHeart(view);
//                }
//            });
//
//            tv_generate.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    if(pdfFile == null){
//                        checkPermission(); // generate pdf
//                    }else{
//                        viewPdf();
//                    }
//                }
//            });
//
//        }else{
////            li_funcField.setVisibility(View.VISIBLE);
////            rl_interest.setVisibility(View.GONE);
////            findViewById(R.id.func_middle_line).setVisibility(View.GONE);
//        }

    }

    private void fillContent(){
        if(layout >= 0){
            tv_title.setText((String)data.get("title"));
            tv_content.setText((String)data.get("content"));
            ArrayList<String> referenceList = (ArrayList<String>)data.get("url");
            if(referenceList.size() <= 0){
                line0.setVisibility(View.GONE);
                tv_reference.setVisibility(View.GONE);
            }else {
                line0.setVisibility(View.VISIBLE);
                tv_reference.setVisibility(View.VISIBLE);
                String reference = "";
                for (int i = 0; i < referenceList.size(); i++) {
                    reference += referenceList.get(i);
                    if (i < referenceList.size() - 1) {
                        reference += "\n";
                    }
                }
                tv_reference.setText(reference);
            }
        }

        ArrayList<String> pictureList = (ArrayList<String>)data.get("picture");
        for(int i=0; i<pictureList.size(); i++){
            final int pos = i;
            String path = pictureList.get(i);
            Picasso.with(context)
                    .load(path)
                    .resize(0, 800)
                    .into(ivList[pos]);
//            ivList[pos].setImageList(layoutItem.getImageList(), pos, layoutItem.getTitle());
        }

        selectBtn.setTag(false);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean tag = (boolean)view.getTag();
                select(tag);
                view.setTag(!tag);
            }
        });

    }

    private void select(boolean state){
        if(state){
            selectBtn.setBackgroundColor(getColorId(context, R.color.dark_gray));
        }else{
            selectBtn.setBackgroundColor(getColorId(context, R.color.colorPrimary));
        }
        listener.select(position, !state);
    }

    public void setItem(HashMap<String, Object> item){
//        this.data = item;
    }


}
