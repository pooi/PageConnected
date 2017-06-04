package ga.pageconnected.pageconnected.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ga.pageconnected.pageconnected.Information;
import ga.pageconnected.pageconnected.R;
import ga.pageconnected.pageconnected.activity.ArticleActivity;

public class ArticleFragment extends BaseFragment {


    // UI
    private View view;
    private Context context;

    private LinearLayout li_listField;
    private String userId = "";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        if(getArguments() != null) {
            userId = getArguments().getString("id");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_article, container, false);
        context = container.getContext();

        init();

        return view;
    }

    private void init(){

        li_listField = (LinearLayout)view.findViewById(R.id.li_list_field);
        makeList();

    }

    private void makeList(){

        li_listField.removeAllViews();

        for(int i=0; i<Information.DATE_LIST.length; i++){
            int day = i+1;
            final String date = Information.DATE_LIST[i];
            String title = String.format(getResources().getString(R.string.day_title), day, date.substring(0,4), date.substring(4,6), date.substring(6, 8));

            View v = LayoutInflater.from(context).inflate(R.layout.day_list_custom_item, null, false);

            TextView tv_title = (TextView)v.findViewById(R.id.tv_title);
            tv_title.setText(title);

            RelativeLayout root = (RelativeLayout)v.findViewById(R.id.root);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ArticleActivity.class);
                    intent.putExtra("date", date);
                    if(!userId.equals("")){
                        intent.putExtra("userId", userId);
                    }
                    startActivity(intent);
                }
            });

            li_listField.addView(v);

        }

    }


}
