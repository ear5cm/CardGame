package com.baili_feng.cardgame;

import android.os.Message;

/**
 * Created by baili_feng on 2015/8/26.
 */
abstract class AI {
    public Player mPlayer = null;

    public void setPlayer(Player player) {
        mPlayer = player;
    }

    public void addCard() {
        sendACK();
    }

    public void setBaida() {
        sendACK();
    }

    public void addLastCard() {
        sendACK();
    }

    public void addTmpCard() {

    }

    public void rmCard() {}

    public void playCard(){
        onPlayCard();
    }

    private void sendACK() {
        Message message = new Message();
        message.what = Player.ACTION_ACK;
        mPlayer.mHandler.sendMessageDelayed(message,300);
        //mPlayer.mHandler.sendMessage(message);
    }

    abstract void onPlayCard();
    abstract void printName();
}
