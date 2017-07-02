package ga.pageconnected.pageconnected.util;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

import ga.pageconnected.pageconnected.R;

/**
 * Created by tw on 2017. 7. 1..
 */

public class LayoutItem implements Serializable {

    private int layoutNumber;
    private String title;
    private String content;
    private ArrayList<String> reference;
    private ArrayList<String> imageList;

    public LayoutItem(int layoutNumber, String title, String content, ArrayList<String> reference, ArrayList<String> imageList){

        this.layoutNumber = layoutNumber;
        this.title = title;
        this.content = content;
        this.reference = reference;
        this.imageList = imageList;

    }

    public int getLayoutNumber(){
        return layoutNumber;
    }
    public String getTitle(){
        return title;
    }
    public String getContent(){
        return content;
    }
    public ArrayList<String> getReference(){
        return reference;
    }
    public ArrayList<String> getImageList(){
        return imageList;
    }
    public int getImageCount(){
        return imageList.size();
    }
    public int getMaxImageCount(){
        return LayoutController.getMaxImageCount(layoutNumber);
    }

    public int getViewId(){
        return LayoutController.getLayoutId(layoutNumber);
    }

}
