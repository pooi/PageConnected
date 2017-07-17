package ga.pageconnected.pageconnected.util;

import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.R;

/**
 * Created by tw on 2017. 6. 30..
 */

public class LayoutController implements Serializable {


    public static int getLayoutId(int position){

        int id;

        switch (position){
            case -1:
                id = R.layout.fragment_layout_item_photo;
                break;
            case 0:
                id = R.layout.fragment_layout_item_01;
                break;
            case 1:
                id = R.layout.fragment_layout_item_02;
                break;
            case 2:
                id = R.layout.fragment_layout_item_03;
                break;
            case 3:
                id = R.layout.fragment_layout_item_04;
                break;
            case 4:
                id = R.layout.fragment_layout_item_05;
                break;
            default:
                id = R.layout.fragment_layout;
                break;
        }

        return id;

    }

    public static int getMaxImageCount(int position){

        int count;

        switch (position){
            case -1:
                count = 1;
                break;
            case 0:
                count = 1;
                break;
            case 1:
                count = 2;
                break;
            case 2:
                count = 1;
                break;
            case 3:
                count = 3;
                break;
            case 4:
                count = 0;
                break;
            default:
                count = 0;
                break;
        }

        return count;

    }

    public static LayoutItem buildLayoutItemObject(String table, int layoutNumber, String title, String content, ArrayList<String> reference, ArrayList<AllInOnePhoto> imageList, HashMap<String, Object> item){

        return new LayoutItem(table, layoutNumber, title, content, reference, AdditionalFunc.AllInOnePhotoToPhotoList(imageList), new HashMap<String, Object>());

    }

}
