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

    final public static int ACTION_UNKNOWN = 0;
    final public static int ACTION_NONE = 0;
    final public static int ACTION_READY = 1<<1;
    final public static int ACTION_ACK = 1<<2;
    final public static int ACTION_MAIZHUANG = 1<<3;
    final public static int ACTION_ZHEZHANG = 1<<4;
    final public static int ACTION_CHUPAI = 1<<5;
    final public static int ACTION_CHI = 1<<6;
    final public static int ACTION_PENG = 1<<7;
    final public static int ACTION_GANG = 1<<8;
    final public static int ACTION_TING = 1<<9;
    final public static int ACTION_HU = 1<<10;
    final public static int ACTION_CANCEL = 1<<11;

    public List <Card> mCardList = new ArrayList<>();

    public Card mBaida = null;
    // last card from table
    public Card mLastCard = null;
    // tmp card from opponent
    public Card mTmpCard = null;
    public boolean mZhezhang = false;
    public AI mAI = null;

    public Handler mHandler;
    public int mIdx;
    public int mWaitAction = ACTION_NONE;

    public Player(Handler handler, int idx) {
        mHandler = handler;
        mIdx = idx;
    }

    public void attachAI(AI ai) {
        mAI = ai;
        mAI.setPlayer(this);
    }

    public void addCard(Card card) {
        Log.i(TAG, "Player" + mIdx + " add card, now card num: " + mCardList.size());
        if(card == null) {
            mAI.addCard();
            return;
        }
        if(card.mValue < 0 || card.mValue > 9) return;
        mCardList.add(card);
        reorder(mCardList);
        mAI.addCard();
        Log.i(TAG, "Player" + mIdx + " after add card, now card num: " + mCardList.size());
    }

    public Card rmCard(int idx) {
        Log.i(TAG, "Player" + mIdx + " rmCard: " + idx);
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
        reorder(mCardList);
        mAI.rmCard();
        Log.i(TAG, "Player" + mIdx + " after rmCard, now cards no: " + mCardList.size());
        return card;
    }

    public void setBaida(Card card) {
        mBaida = card;
        reorder(mCardList);
        mAI.setBaida();
    }

    public void setLastCard(Card card) {
        mLastCard = card;
        mAI.addLastCard();
    }

    public void setTmpCard(Card card) {
        mTmpCard = card;
        mAI.addTmpCard();
    }

    public void playCard(){
        mAI.playCard();
    }

    public boolean checkHu(){
        return false;
    }

    public int checkAction(Card card, int owner) {
        Log.i(TAG, "checkAction: " + mIdx);
        // if we can do nothing, the only action we could make is CHUPAI
        //mWaitAction = ACTION_CHUPAI;
        mWaitAction = ACTION_NONE;
        if(card == null) {
            return mWaitAction;
        }
        List <Card> list = new ArrayList<>();
        //list is a temporary copy of mCardList
        list.addAll(mCardList);
        list.add(card);
        reorder(list);
        if(checkHu(list) == 1){
            mWaitAction |= ACTION_HU;
        }
        return mWaitAction;
    }

    public int chooseAction(Card card, int owner) {
        Log.i(TAG, "makeAction: " + mIdx);
        if(mWaitAction == ACTION_NONE) {
            Log.i(TAG, "makeAction1: " + mIdx);
            mAI.playCard();
        } else {
            Log.i(TAG, "makeAction2: " + mIdx);
            mAI.chooseAction();
        }
        return 0;
    }

    public int makeAction(int action, int value) {
        if(action == ACTION_CANCEL) {
            mWaitAction = ACTION_NONE;
        }
        mAI.makeAction();
        return 0;
    }

    private int checkHu(List <Card> cardList) {
        List<Integer> tlist = new ArrayList<>();
        List<Integer> blist = new ArrayList<>();
        List<Integer> wlist = new ArrayList<>();
        int numBaida = 0;
        for(Card card : cardList) {
            if(isBaida(card)) {
                numBaida ++;
            } else if(card.mType == Card.CARD_TYPE_TIAO){
                tlist.add(card.mValue);
            } else if(card.mType == Card.CARD_TYPE_BING){
                blist.add(card.mValue);
            } else if(card.mType == Card.CARD_TYPE_WAN){
                wlist.add(card.mValue);
            }
        }
        List<List<Integer>> listlist = new ArrayList<>();
        listlist.add(tlist);
        listlist.add(blist);
        listlist.add(wlist);
        List<Integer> hu = Rule.getInstance().checkHu(listlist, numBaida);
        if(hu == null) {
            return 0;
        }
        return 1;
    }

    private void reorder(List <Card> cardList) {
        if(cardList == null) {
            return;
        }
        List <Card> tiaoList = new ArrayList<> ();
        List <Card> bingList = new ArrayList<> ();
        List <Card> wanList = new ArrayList<> ();
        List <Card> baidaList = new ArrayList<> ();
        for (Card card : cardList) {
            List <Card> list = null;
            if(isBaida(card)) {
                list = baidaList;
            } else {
                switch (card.mType) {
                    case Card.CARD_TYPE_TIAO:
                        list = tiaoList;
                        break;
                    case Card.CARD_TYPE_BING:
                        list = bingList;
                        break;
                    case Card.CARD_TYPE_WAN:
                        list = wanList;
                        break;
                    default:
                        break;
                }
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
        cardList.clear();
        cardList.addAll(tiaoList);
        cardList.addAll(bingList);
        cardList.addAll(wanList);
        cardList.addAll(baidaList);
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

    public void reset(){
        mCardList.clear();
        mBaida = null;
        mLastCard = null;
        mTmpCard = null;
        mWaitAction = ACTION_NONE;
    }

    public void onTest() {
        Log.i(TAG, "onTest");
        Message message = new Message();
        message.what = ACTION_ACK;
        //mHandler.sendMessageDelayed(message,300);
        mHandler.sendMessage(message);
    }
}
