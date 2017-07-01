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



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt("position");
        listener = (SelectListener)getArguments().getSerializable("listener");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        switch (position){
            case 0:
                view = inflater.inflate(R.layout.fragment_layout_item_01, container, false);
                break;
            case 1:
                view = inflater.inflate(R.layout.fragment_layout_item_02, container, false);
                break;
            case 2:
                view = inflater.inflate(R.layout.fragment_layout_item_03, container, false);
                break;
            case 3:
                view = inflater.inflate(R.layout.fragment_layout_item_04, container, false);
                break;
            case 4:
                view = inflater.inflate(R.layout.fragment_layout_item_05, container, false);
                break;
            default:
                view = inflater.inflate(R.layout.fragment_layout, container, false);
                break;
        }
//        view = inflater.inflate(R.layout.fragment_layout, container, false);
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
                listener.selected(position);
            }
        });

    }

    private void initETC(){

        (view.findViewById(R.id.cv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.selected(position);
            }
        });

    }

}
