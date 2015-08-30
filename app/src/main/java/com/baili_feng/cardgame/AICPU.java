package com.baili_feng.cardgame;

import android.os.Message;

/**
 * Created by baili_feng on 2015/8/26.
 */
public class AICPU extends AI {
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
