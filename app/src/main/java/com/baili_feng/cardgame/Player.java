package com.baili_feng.cardgame;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by baili_feng on 2015/8/26.
 */
public class Player {
    private static final String TAG = "Player";

    final public static int ACTION_UNKNOWN = -1;
    final public static int ACTION_READY = 0;
    final public static int ACTION_ACK = 100;
    final public static int ACTION_MAIZHUANG = 101;
    final public static int ACTION_ZHEZHANG = 102;
    final public static int ACTION_CHUPAI = 103;
    final public static int ACTION_CHI = 200;
    final public static int ACTION_PENG = 201;
    final public static int ACTION_GANG = 202;
    final public static int ACTION_TING = 203;
    final public static int ACTION_HU = 204;

    public List <Card> mCardList = new ArrayList<>();
    public List <Card> mTiaoList = new ArrayList<>();
    public List <Card> mBingList = new ArrayList<>();
    public List <Card> mWanList = new ArrayList<>();
    public List <Card> mBaidaList = new ArrayList<>();
    public Card mBaida = null;
    // last card from table
    public Card mLastCard = null;
    // tmp card from opponent
    public Card mTmpCard = null;
    public boolean mZhezhang = false;
    public AI mAI = null;

    public Handler mHandler;
    public int mIdx;

    public Player(Handler handler, int idx) {
        mHandler = handler;
        mIdx = idx;
    }

    public void attachAI(AI ai) {
        mAI = ai;
        mAI.setPlayer(this);
    }

    public void addCard(Card card) {
        if(card == null) {
            mAI.addCard();
            return;
        }
        if(card.mValue < 0 || card.mValue > 9) return;

        mCardList.add(card);
        if(isBaida(card))
        {
            int idx = 0;
            for(Card c : mBaidaList) {
                if(c.mValue < card.mValue) {
                    idx ++;
                }
            }
            mBaidaList.add(idx, card);
        }
        else{
            List <Card> list = null;
            switch(card.mType) {
                case Card.CARD_TYPE_TIAO:
                    list = mTiaoList;
                    break;
                case Card.CARD_TYPE_BING:
                    list = mBingList;
                    break;
                case Card.CARD_TYPE_WAN:
                    list = mWanList;
                    break;
                default:
                    break;
            }
            if(list != null) {
                int idx = 0;
                for(Card c : list) {
                    if(c.mValue < card.mValue) {
                        idx ++;
                    }
                }
                list.add(idx, card);
            }
        }
        mCardList.clear();
        mCardList.addAll(mTiaoList);
        mCardList.addAll(mBingList);
        mCardList.addAll(mWanList);
        mCardList.addAll(mBaidaList);
        mAI.addCard();
    }

    public void reset(){
        mCardList.clear();
        mTiaoList.clear();
        mBingList.clear();
        mWanList.clear();
        mBaidaList.clear();
        mBaida = null;
        mLastCard = null;
        mTmpCard = null;
    }

    public void setBaida(Card card) {
        //Log.i(TAG, "setBaida: " + card.mValue + "/" + card.mType);
        mBaida = card;
        reorder();
        mAI.setBaida();
    }

    public boolean isBaida(Card card){
        if(mBaida == null) {
            return false;
        }
        int value1 = mBaida.mValue;
        int value2 = value1 - 1;
        if(value2 < 1) value2 = 9;
        return card.mType == mBaida.mType && (card.mValue == value1 || card.mValue == value2);
    }

    public void addLastCard(Card card) {
        mLastCard = card;
        mAI.addLastCard();
    }

    public void addTmpCard(Card card) {
        mTmpCard = card;
        mAI.addTmpCard();
    }

    public boolean rmCard(Card card) {
        mCardList.remove(card);
        reorder();
        mAI.rmCard();
        return true;
    }

    public Card rmCard(int idx) {
        Card card = new Card();
        if(idx < 0 || idx > mCardList.size()) return null;
        if(idx < mCardList.size()) {
            if(isBaida(mCardList.get(idx))) return null;
            card.mType = mCardList.get(idx).mType;
            card.mValue = mCardList.get(idx).mValue;
            mCardList.remove(idx);
        } else if(idx == mCardList.size() && mLastCard != null) {
            if(isBaida(mLastCard)) return null;
            card.mType = mLastCard.mType;
            card.mValue = mLastCard.mValue;
            mLastCard = null;
        } else {
            return null;
        }
        reorder();
        mAI.rmCard();
        return card;
    }

    public void playCard(){
        mAI.playCard();
    }

    public boolean checkHu(){
        return false;
    }

    private void reorder() {
        mTiaoList.clear();
        mBingList.clear();
        mWanList.clear();
        mBaidaList.clear();
        for (Iterator<Card> it = mCardList.iterator(); it.hasNext();) {
            Card card = it.next();
            if(isBaida(card)) {
                mBaidaList.add(card);
            }
            else if(card.mType == Card.CARD_TYPE_TIAO)
            {
                mTiaoList.add(card);
            }
            else if(card.mType == Card.CARD_TYPE_BING)
            {
                mBingList.add(card);
            }
            else if(card.mType == Card.CARD_TYPE_WAN)
            {
                mWanList.add(card);
            }
        }
        mCardList.clear();
        mCardList.addAll(mTiaoList);
        mCardList.addAll(mBingList);
        mCardList.addAll(mWanList);
        mCardList.addAll(mBaidaList);
    }

    public void onTest() {
        Log.i(TAG, "onTest");
        Message message = new Message();
        message.what = ACTION_ACK;
        //mHandler.sendMessageDelayed(message,300);
        mHandler.sendMessage(message);
    }
}
