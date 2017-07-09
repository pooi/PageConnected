package ga.pageconnected.pageconnected.util;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import ga.pageconnected.pageconnected.R;

/**
 * Created by tw on 2017. 7. 1..
 */

public class LayoutItem implements Serializable {

    private String table;
    private int layoutNumber;
    private String title;
    private String content;
    private ArrayList<String> reference;
    private ArrayList<String> imageList;
    private HashMap<String, Object> item;

    public LayoutItem(String table, int layoutNumber, String title, String content, ArrayList<String> reference, ArrayList<String> imageList, HashMap<String, Object> item){

        this.table = table;
        this.layoutNumber = layoutNumber;
        this.title = title;
        this.content = content;
        this.reference = reference;
        this.imageList = imageList;
        this.item = item;

    }

    public String getTable(){
        return table;
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
    public HashMap<String, Object> getItem(){
        return item;
    }
    public String getId(){
        return (String)item.get("id");
    }
    public boolean getHeartAble(){
        return (boolean)item.get("heartAble");
    }
    public void setHeartAble(boolean b){
        item.put("heartAble", b);
    }
    public int getHeart(){
        return (int)item.get("heart");
    }
    public void setHeart(int heart){
        item.put("heart", heart);
    }

    public int getViewId(){
        return LayoutController.getLayoutId(layoutNumber);
    }

}
