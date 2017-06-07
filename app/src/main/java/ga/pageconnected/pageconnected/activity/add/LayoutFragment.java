package ga.pageconnected.pageconnected.activity.add;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.fragment.BaseFragment;
import ga.pageconnected.pageconnected.profile.ProfileActivity;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by tw on 2017-06-06.
 */
public class LayoutFragment extends BaseFragment {

    // BASIC UI
    private View view;
    private Context context;

    // DATA
    private int position;
    private SelectListener listener;

    // UI
    private ImageView img;
    private TextView tv_title;



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt("position");
        listener = (SelectListener)getArguments().getSerializable("listener");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_layout, container, false);
        context = container.getContext();

        initData();
        initUI();

        return view;

    }

    private void initData(){


    }

    private void initUI(){

        img = (ImageView)view.findViewById(R.id.img);
        Picasso.with(context)
                .load(R.drawable.columns)
                .into(img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.selected(position);
            }
        });
        tv_title = (TextView)view.findViewById(R.id.tv_title);
        tv_title.setText("layout " + (position+1));

    }

}
