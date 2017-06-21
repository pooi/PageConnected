package ga.pageconnected.pageconnected.util;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import ga.pageconnected.pageconnected.R;


/**
 * Created by tw on 2017-05-26.
 */

public class AdditionalFunc {

    public static void showSnackbar(Activity activity, String msg){
        Snackbar snackbar = Snackbar.make(activity.getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.snackbar_color));
        snackbar.show();
    }
    public static void showSnackbar(View v, Context context, String msg){
        Snackbar snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar_color));
        snackbar.show();
    }

    public static String replaceNewLineString(String s){

        String str = s.replaceAll("\n", "\\\\n");
        return str;

    }

    public static long getMilliseconds(int year, int month, int day){

        long days = 0;

        try {
            String cdate = String.format("%d%02d%02d", year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date date = sdf.parse(cdate);
            days = date.getTime();
            System.out.println(days);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;

    }

    public static long getTodayMilliseconds(){
        Calendar now = Calendar.getInstance();
        return getMilliseconds(now.get(Calendar.YEAR), now.get(Calendar.MONTH)+1, now.get(Calendar.DAY_OF_MONTH));
    }

    public static int getDday(long eTime){

        long cTime = System.currentTimeMillis();
        Date currentDate = new Date(cTime);
        Date finishDate = new Date(eTime);
//        System.out.println(cTime + ", " + eTime);

        DateFormat df = new SimpleDateFormat("yyyy");
        int currentYear = Integer.parseInt(df.format(currentDate));
        int finishYear = Integer.parseInt(df.format(finishDate));
        df = new SimpleDateFormat("MM");
        int currentMonth = Integer.parseInt(df.format(currentDate));
        int finishMonth = Integer.parseInt(df.format(finishDate));
        df = new SimpleDateFormat("dd");
        int currentDay = Integer.parseInt(df.format(currentDate));
        int finishDay = Integer.parseInt(df.format(finishDate));

//        System.out.println(currentYear + ", " + currentMonth + ", " + currentDay);
//        System.out.println(finishYear + ", " + finishMonth + ", " + finishDay);

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.set(currentYear, currentMonth, currentDay);
        end.set(finishYear, finishMonth, finishDay);

        Date startDate = start.getTime();
        Date endDate = end.getTime();

        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        long diffTime = endTime - startTime;
        long diffDays = diffTime / (1000 * 60 * 60 * 24);


        return (int)diffDays;
    }

    public static String getDateString(long time){

        Date currentDate = new Date(time);
        DateFormat df = new SimpleDateFormat("yyyy년 MM월 dd일");
        return df.format(currentDate);

    }

    public static ArrayList<String> stringToArrayList(String str){

        ArrayList<String> list = new ArrayList<>();

        for(String s : str.split(",")){
            if(!"".equals(s)){
                list.add(s);
            }
        }

        return list;
    }

    public static String[] arrayListToStringArray(ArrayList<String> list){
        String[] st = new String[list.size()];
        for(int i=0; i<list.size(); i++){
            st[i] = list.get(i);
        }
        return st;
    }

    public static String arrayListToString(ArrayList<String> list) {

        String str = "";
        for (int i = 0; i < list.size(); i++) {
            str += list.get(i);
            if (i + 1 < list.size()) {
                str += ",";
            }
        }

        return str;

    }

    public static String integerArrayListToString(ArrayList<Integer> list){

        String str = "";
        for(int i=0; i<list.size(); i++){
            str += list.get(i);
            if(i+1<list.size()){
                str += ",";
            }
        }
        return str;
    }


    public static HashMap<String, Object> getUserInfo(String data){

        HashMap<String, Object> item = new HashMap<>();

        try {
            // PHP에서 받아온 JSON 데이터를 JSON오브젝트로 변환
            JSONObject jObject = new JSONObject(data);
            // results라는 key는 JSON배열로 되어있다.
            JSONArray results = jObject.getJSONArray("result");
            String countTemp = (String)jObject.get("num_result");
            int count = Integer.parseInt(countTemp);

//                HashMap<String, String> hashTemp = new HashMap<>();
            for ( int i = 0; i < count; ++i ) {
                JSONObject temp = results.getJSONObject(i);

//                        HashMap<String, String> hashTemp = new HashMap<>();
                item.put("id", (String)temp.get("id"));
                item.put("name", (String)temp.get("name"));
                item.put("email", (String)temp.get("email"));
                item.put("img", (String)temp.get("img"));
                item.put("intro", (String)temp.get("intro"));

                ArrayList<String> in = new ArrayList<String>();
                String interest = (String)temp.get("interest");
                if(!interest.equals("")){
                    for(String s : interest.split(",")){
                        in.add(s);
                    }
                }
                item.put("interest", in);

            }

        } catch (JSONException e) {
            e.printStackTrace();
            item.clear();
        }

        return item;

    }

    public static ArrayList<HashMap<String, Object>> getArticleList(String data){

        ArrayList<HashMap<String, Object>> list = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(data);
            JSONArray results = jObject.getJSONArray("result");
            String countTemp = (String)jObject.get("num_result");
            int count = Integer.parseInt(countTemp);

            for ( int i = 0; i < count; ++i ) {
                JSONObject temp = results.getJSONObject(i);

                HashMap<String, Object> hashTemp = new HashMap<>();

                hashTemp.put("id", (String)temp.get("id"));
                hashTemp.put("userId", (String)temp.get("userId"));
                hashTemp.put("name", (String)temp.get("name"));
                hashTemp.put("email", (String)temp.get("email"));
                hashTemp.put("img", (String)temp.get("img"));
                hashTemp.put("layout", Integer.parseInt((String)temp.get("layout")));
                hashTemp.put("picture", (String)temp.get("picture"));
                hashTemp.put("title", (String)temp.get("title"));
                hashTemp.put("content", (String)temp.get("content"));
                hashTemp.put("date", (String)temp.get("date"));
                hashTemp.put("hit", Integer.parseInt((String)temp.get("hit")));
                hashTemp.put("heart", Integer.parseInt((String)temp.get("heart")));

                String urlTemp = (String)temp.get("url");
                ArrayList<String> urlList = new ArrayList<>();
                for(String s : urlTemp.split(",")){
                    urlList.add(s);
                }
                hashTemp.put("url", urlList);

                list.add(hashTemp);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;

    }

    public static ArrayList<HashMap<String, Object>> getPhotoList(String data){

        ArrayList<HashMap<String, Object>> list = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(data);
            JSONArray results = jObject.getJSONArray("result");
            String countTemp = (String)jObject.get("num_result");
            int count = Integer.parseInt(countTemp);

            for ( int i = 0; i < count; i++ ) {
                JSONObject temp = results.getJSONObject(i);

                HashMap<String, Object> hashTemp = new HashMap<>();

                hashTemp.put("id", (String)temp.get("id"));
                hashTemp.put("userId", (String)temp.get("userId"));
                hashTemp.put("name", (String)temp.get("name"));
                hashTemp.put("email", (String)temp.get("email"));
                hashTemp.put("img", (String)temp.get("img"));
                hashTemp.put("content", (String)temp.get("content"));
                hashTemp.put("day", (String)temp.get("day"));
                hashTemp.put("date", (String)temp.get("date"));
                hashTemp.put("time", (String)temp.get("time"));
                hashTemp.put("hit", (String)temp.get("hit"));

                JSONObject jObjectImg = (JSONObject)temp.get("imageList");
                JSONArray resultsImg = jObjectImg.getJSONArray("result");
                String countTempImg = (String)jObjectImg.get("num_image");
                int countImg = Integer.parseInt(countTempImg);

                ArrayList<HashMap<String, Object>> imageList = new ArrayList<>();
                for(int j=0; j<countImg; j++){

                    JSONObject tempImg = resultsImg.getJSONObject(j);

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", (String)tempImg.get("id"));
                    map.put("photo", (String)tempImg.get("photo"));
                    map.put("heart", (String)tempImg.get("heart"));

                    imageList.add(map);

                }

                hashTemp.put("imageList", imageList);

                list.add(hashTemp);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;

    }


}
