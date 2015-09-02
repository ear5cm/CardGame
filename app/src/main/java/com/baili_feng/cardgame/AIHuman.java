package com.baili_feng.cardgame;

/**
 * Created by baili_feng on 2015/8/26.
 */
public class AIHuman extends AI {

    void onChooseAction() {
        // do nothing here, just tell ui to update
        // wait user to choose an action.
        sendACK();
    }

    void onPlayCard(){
        //do nothing, wait GameView do all of the things
    }
    void printName() {}
}