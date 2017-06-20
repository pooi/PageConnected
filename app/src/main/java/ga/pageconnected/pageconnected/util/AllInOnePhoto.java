package ga.pageconnected.pageconnected.util;

import android.net.Uri;
import android.view.View;

import java.io.Serializable;

/**
 * Created by tw on 2017. 6. 17..
 */

public class AllInOnePhoto implements Serializable {

    private Uri uri;
    private String path;
    private View view;


    public AllInOnePhoto(Uri uri, String path, View v){
        this.uri = uri;
        this.path = path;
        this.view = v;
    }

    public Uri getUri(){
        return uri;
    }
    public String getPath(){
        return path;
    }
    public View getView(){
        return view;
    }

}
