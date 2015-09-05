package com.baili_feng.cardgame;

/**
 * Created by baili_feng on 2015/8/27.
 */
public class Card {
    private static final String TAG = "Card";
    public static final int CARD_TYPE_UNKNOWN = -1;
    public static final int CARD_TYPE_TIAO = 0;
    public static final int CARD_TYPE_BING = 1;
    public static final int CARD_TYPE_WAN = 2;

    public int mType;
    public int mValue;

    public Card() {
        this.mValue = 0;
        this.mType = 0;
    }

    public Card(int value, int type) {
        this.mValue = value;
        this.mType = type;
    }

    public String toString(){
        return Integer.toString(mType * 10) + Integer.toString(mValue);
    }

}
