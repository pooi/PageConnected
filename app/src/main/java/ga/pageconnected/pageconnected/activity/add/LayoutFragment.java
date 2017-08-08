package ga.pageconnected.pageconnected.activity.add;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;

import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.fragment.BaseFragment;
import ga.pageconnected.pageconnected.util.LayoutController;

/**
 * Created by tw on 2017-06-06.
 */
public class LayoutFragment extends BaseFragment implements Serializable{

    // BASIC UI
    private View view;
    private Context context;

    // DATA
    private int position;
    private SelectListener listener;

    // UI
    private ImageView img;
    private TextView tv_layout_pos;
    private TextView tv_select;



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt("position");
        listener = (SelectListener)getArguments().getSerializable("listener");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment_layout, container, false);
        view = inflater.inflate(LayoutController.getLayoutId(position), container, false);
        context = container.getContext();

        initData();
        initUI();

        return view;

    }

    private void initData(){


    }

    private void initUI(){


        tv_layout_pos = (TextView)view.findViewById(R.id.tv_layout_pos);
        tv_layout_pos.setText("layout " + (position+1));

        switch (position){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                initETC();
                break;
            default:
                init00();
                break;
        }

    }

    private void init00(){

        img = (ImageView)view.findViewById(R.id.img);
        Picasso.with(context)
                .load(R.drawable.columns)
                .into(img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.selected(position, 0);
            }
        });

    }

    private void initETC(){

        (view.findViewById(R.id.cv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.selected(position, LayoutController.getMaxImageCount(position));
            }
        });
        tv_select = (TextView)view.findViewById(R.id.tv_select);
        tv_select.setVisibility(View.VISIBLE);
        tv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.selected(position, LayoutController.getMaxImageCount(position));
            }
        });


    }

}
