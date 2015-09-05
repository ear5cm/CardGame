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
    final public static int ACTION_READY = 1;
    final public static int ACTION_ACK = 1<<1;
    final public static int ACTION_MAIZHUANG = 1<<2;
    final public static int ACTION_ZHEZHANG = 1<<3;

    final public static int ACTION_CHUPAI = 1<<4;
    final public static int ACTION_CHI = 1<<5;
    final public static int ACTION_PENG = 1<<6;
    final public static int ACTION_GANG = 1<<7;

    final public static int ACTION_TING = 1<<9;
    final public static int ACTION_HU = 1<<10;
    final public static int ACTION_CANCEL = 1<<11;

    public List <Card> mCardList = new ArrayList<>();
    public List <Card> mHistory = new  ArrayList<>();
    public List <Card> mHuList = null;
    public List <Ting> mTingList = null;
    public List <Action> mActionList = new ArrayList<>();
    public Card mAdvise = null;

    public class Ting {
        Card card;
        List<Card> hu;
    }

    public class Action {
        int action;
        List<Card> list = new ArrayList<>();
    }

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
    public int mScore;

    public Player(Handler handler, int idx) {
        mHandler = handler;
        mIdx = idx;
        mScore = 500000;
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

    public void checkTing() {
        mTingList = checkTing(mCardList);
        if(mTingList != null) {
            Log.i(TAG, "TING: " + mTingList);
        }
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

        // c_m_2 means card minus 2
        // c_m_1 means card plus 1
        // c_count is the number of cards in mCardList
        Card c_m_2 = null;
        Card c_m_1 = null;
        Card c_p_1 = null;
        Card c_p_2 = null;
        int c_count = 0;

        for(Card c : mCardList) {
            if(c.mType == card.mType){
                if(c.mValue == card.mValue-2) {
                    c_m_2 = c;
                }else if(c.mValue == card.mValue-1) {
                    c_m_1 = c;
                }else if(c.mValue == card.mValue) {
                    c_count++;
                }else if(c.mValue == card.mValue+1) {
                    c_p_1 = c;
                }else if(c.mValue == card.mValue+2) {
                    c_p_2 = c;
                }
            }
        }

        mActionList.clear();
        if(owner != mIdx) {
            // check CHI PENG if only we are not the owner of the card
            if(c_m_2 != null && c_m_1 != null) {
                Action action = new Action();
                action.action = ACTION_CHI;
                action.list.add(c_m_2);
                action.list.add(c_m_1);
                action.list.add(card);
                mActionList.add(action);
                mWaitAction |= action.action;
                Log.i(TAG, "[TEST] chi abx");
            }
            if(c_m_1 != null && c_p_1 != null) {
                Action action = new Action();
                action.action = ACTION_CHI;
                action.list.add(c_m_1);
                action.list.add(card);
                action.list.add(c_p_1);
                mActionList.add(action);
                mWaitAction |= action.action;
                Log.i(TAG, "[TEST] chi axc");
            }
            if(c_p_1 != null && c_p_2 != null) {
                Action action = new Action();
                action.action = ACTION_CHI;
                action.list.add(c_p_1);
                action.list.add(c_p_2);
                action.list.add(card);
                mActionList.add(action);
                mWaitAction |= action.action;
                Log.i(TAG, "[TEST] chi xab");
            }
            if(c_count != 0) {
                Action action = new Action();
                action.action = ACTION_CHI;
                action.list.add(card);
                action.list.add(card);
                mActionList.add(action);
                mWaitAction |= action.action;
                Log.i(TAG, "[TEST] chi ax");
            }
            if(c_count >= 2) {
                Action action = new Action();
                action.action = ACTION_PENG;
                action.list.add(card);
                action.list.add(card);
                action.list.add(card);
                mActionList.add(action);
                mWaitAction |= action.action;
                Log.i(TAG, "[TEST] peng");
            }
            if(c_count == 3) {
                Action action = new Action();
                action.action = ACTION_GANG;
                action.list.add(card);
                action.list.add(card);
                action.list.add(card);
                action.list.add(card);
                mActionList.add(action);
                mWaitAction |= action.action;
                Log.i(TAG, "[TEST] gang");
            }
        }

        mWaitAction = ACTION_NONE;
        List <Card> list = new ArrayList<>();
        //list is a temporary copy of mCardList
        list.addAll(mCardList);
        list.add(card);
        reorder(list);
        mHuList = huToCardList(checkHu(list), list);
        if(mHuList != null){
            mWaitAction |= ACTION_HU;
            Action action = new Action();
            action.action = ACTION_HU;
            action.list.addAll(mHuList);
        }

        return mWaitAction;
    }

    public int chooseAction(Card card, int owner) {
        Log.i(TAG, "makeAction: " + mIdx);
        if(mWaitAction == ACTION_NONE) {
            // no action we can make, so we must play a card
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
        } else if(action == ACTION_HU) {
        }
        mAI.makeAction();
        return 0;
    }

    private List <Ting> checkTing(List<Card> cardList) {
        List <Ting> tingList = null;
        if(cardList == null || cardList.size() < 1) {
            return null;
        }
        List<Card> testList = new ArrayList<>();
        int i, j;
        for(i = Card.CARD_TYPE_TIAO; i <= Card.CARD_TYPE_WAN; i++){
            for (j = 1; j <= 9; j++) {
                Card card = new Card();
                card.mType = i;
                card.mValue = j;
                if(isBaida(card)) {
                    //ingonre baida when check ting
                    continue;
                }
                testList.clear();
                testList.addAll(cardList);
                testList.add(card);
                reorder(testList);
                List<Card> ret = huToCardList(checkHu(testList), testList);
                if(ret != null) {
                    Ting ting = new Ting();
                    ting.card = card;
                    ting.hu = ret;
                    if(tingList == null) {
                        tingList = new ArrayList<>();
                    }
                    tingList.add(ting);
                }
            }
        }
        return tingList;
    }

    private List<List<Integer>> checkHu(List <Card> cardList) {
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
        return  Rule.getInstance().checkHu(listlist, numBaida);
    }

    private List<Card> huToCardList(List<List<Integer>> hu, List<Card> cardList) {
        if(hu == null || cardList == null) {
            return null;
        }
        int i = 0;
        int j = 0;
        for(i = 0; i < cardList.size(); i++) {
            // find 1st baida
            if(isBaida(cardList.get(i))) {
                break;
            }
        }

        List <Card> huList = new ArrayList<>();
        for(List<Integer> rlist : hu) {
            for(int value : rlist) {
                Card card = new Card();
                if (value != -1) {
                    card.mValue = value;
                    card.mType = j;

                } else {
                    if(i < cardList.size()) {
                        card.mValue = cardList.get(i).mValue;
                        card.mType = cardList.get(i).mType;
                    } else {
                        Log.e(TAG, "huToCardList out of range: " + i + "/" + cardList.size());
                    }
                    i++;
                }
                huList.add(card);
            }
            j++;
        }
        if(i != cardList.size()) {
            // baida is not used up
            for(j = i; j < cardList.size(); j++) {
                Card card = new Card();
                card.mValue = cardList.get(j).mValue;
                card.mType = cardList.get(j).mType;
                huList.add(card);
            }
        }
        return huList;
    }

    public Card getAdvise(List <Card> cardList) {
        List <Card> list = new ArrayList<>();
        int tingSize = 0;
        Card ret = null;
        Card last = new Card();
        last.mValue = -1;
        last.mType = -1;
        int i = 0;
        reorder(cardList);
        for(i = 0; i < cardList.size(); i++) {
            Card card = cardList.get(i);
            if(isBaida(card) || (card.mValue == last.mValue && card.mType == last.mType)) {
                //ignore baida and card already checked before
                continue;
            }
            last.mValue = card.mValue;
            last.mType = card.mType;
            list.clear();
            list.addAll(cardList);
            list.remove(i);
            reorder(list);
            List<Ting>  tingList = checkTing(list);
            if(tingList != null && tingList.size() > tingSize) {
                tingSize = tingList.size();
                ret = card;
                Log.i(TAG, "new tingSize: " + tingSize);
            }

        }
        if(ret != null)
            Log.i(TAG, "advise: " + ret.mValue + "/" + ret.mType);
        return ret;
    }

    public Card getAdvise_old(List <Card> cardList) {
        if(false)
            return null;
        List <Card> list = new ArrayList<>();
        List<List<Integer>> hu = null;
        int baida = 100;
        Card ret = null;
        Card last = new Card();
        last.mValue = -1;
        last.mType = -1;
        int i = 0;
        reorder(cardList);
        for(i = 0; i < cardList.size(); i++) {
            Card card = cardList.get(i);
            int numBaida = 0;
            if(isBaida(card) || (card.mValue == last.mValue && card.mType == last.mType)) {
                //ignore baida and card already checked before
                continue;
            }
            last.mValue = card.mValue;
            last.mType = card.mType;
            list.clear();
            list.addAll(cardList);
            //replace i with baida
            list.remove(i);
            list.add(i, mBaida);
            //add more baida
            for(int j = 0; j < 20; j++) {
                list.add(mBaida);
            }
            reorder(list);
            hu = checkHu(list);
            if(hu != null) {
                for(List<Integer> l : hu) {
                    for(int v : l) {
                        if(v == -1) {
                            numBaida++;
                        }
                    }
                }
                Log.i(TAG, "check " + card.mValue + "/" + card.mType + " baida: " + numBaida);
                if(numBaida < baida) {
                    baida = numBaida;
                    ret = card;
                }
            }
        }
        Log.i(TAG, "advise baida: " + baida + " ret: " + ret.mValue + "/" + ret.mType);
        return ret;
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
        mHistory.clear();
        mActionList.clear();
        mBaida = null;
        mLastCard = null;
        mTmpCard = null;
        mWaitAction = ACTION_NONE;
        mTingList = null;
        mHuList = null;
        mAdvise = null;
    }

    public void onTest() {
        Log.i(TAG, "onTest");
        Message message = new Message();
        message.what = ACTION_ACK;
        //mHandler.sendMessageDelayed(message,300);
        mHandler.sendMessage(message);
    }
}
