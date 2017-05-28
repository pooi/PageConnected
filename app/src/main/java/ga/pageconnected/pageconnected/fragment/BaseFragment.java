package ga.pageconnected.pageconnected.fragment;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import ga.pageconnected.pageconnected.R;

/**
 * Created by tw on 2017-05-28.
 */

public class BaseFragment extends Fragment {

    public String getUserID(Fragment fragment){

        String userId = "";
        try {
            userId = fragment.getActivity().getSharedPreferences("setting", 0).getString("userId", null);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return userId;

    }

    public void showSnackbar(View g_view, Context context, String msg){
        Snackbar snackbar = Snackbar.make(g_view, msg, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar_color));
        snackbar.show();
    }

    public void showSnackbar(View g_view, Context context, int id){
        Snackbar snackbar = Snackbar.make(g_view, getResources().getString(id), Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar_color));
        snackbar.show();
    }

}
