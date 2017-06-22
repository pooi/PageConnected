package ga.pageconnected.pageconnected.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

import ga.pageconnected.pageconnected.profile.ProfileActivity;

/**
 * Created by tw on 2017. 6. 21..
 */

public class UserInfo implements Serializable {

    private String id;
    private String name;
    private String email;
    private String img;

    public UserInfo(String id, String name, String email, String img){

        this.id = id;
        this.name = name;
        this.email = email;
        this.img = img;

    }

    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getEmail(){
        return email;
    }
    public String getImg(){
        return img;
    }

    public void redirectProfileActivity(Context context){
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    public void redirectProfileActivity(Activity activity){
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra("id", id);
        activity.startActivity(intent);
    }

}
