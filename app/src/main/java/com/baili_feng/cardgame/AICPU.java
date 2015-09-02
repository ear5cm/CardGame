package com.baili_feng.cardgame;

import android.os.Message;
import android.util.Log;

/**
 * Created by baili_feng on 2015/8/26.
 */
public class AICPU extends AI {
    private static final String TAG = "AICPU";

    void onChooseAction() {
        Message message = new Message();
        message.what = Player.ACTION_HU;
        message.arg1 = mPlayer.mIdx;
        //mHandler.sendMessageDelayed(message,300);
        mPlayer.mHandler.sendMessage(message);
        Log.i(TAG, "Player 0" + " send Action " + Player.ACTION_HU);
    }

    void onPlayCard(){
        Message message = new Message();
        message.what = Player.ACTION_CHUPAI;
        message.arg1 = mPlayer.mIdx;
        message.arg2 = 5;
        mPlayer.mHandler.sendMessageDelayed(message,500);
        //mPlayer.mHandler.sendMessage(message);
    }
    void printName() {}
}
