package ga.pageconnected.pageconnected.util;

import android.os.Handler;
import android.os.Message;

import java.io.Serializable;
import java.util.HashMap;

import ga.pageconnected.pageconnected.Information;

/**
 * Created by tw on 2017. 6. 26..
 */

public class UpdateItem extends Thread implements Serializable {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_EXECUTE_ACTION = 500;

    private FinishAction finishAction;

    private HashMap<String, String> map;

    private String data;

    public UpdateItem(String target, String type, String id, int action, String userId, FinishAction finishAction){

        this.finishAction = finishAction;

        map = new HashMap<>();
        map.put("service", "updateItem");
        map.put("target", target);
        map.put("type", type);
        map.put("id", id);
        map.put("action", Integer.toString(action));
        map.put("userId", userId);


    }

    public void run(){

        try{
            new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

                @Override
                protected void afterThreadFinish(String data) {

                    UpdateItem.this.data = data;
                    System.out.println(data);
                    handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_EXECUTE_ACTION));

                }
            }.start();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

    }

    private class MyHandler extends Handler {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_EXECUTE_ACTION:
                    finishAction.afterAction(data);
                    break;
                default:
                    break;
            }
        }
    }

    public interface FinishAction{
        void afterAction(String data);
    }

}
