package com.baili_feng.cardgame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

/**
 * Created by baili_feng on 2015/8/27.
 */
public class Table {
    private static final String TAG = "Table";
    private static Table mInstance = null;

    private List<Card> mCardList = new ArrayList<>();

    public Table() {
    }

    public static Table getInstance() {
        if(mInstance == null) {
            mInstance = new Table();
        }
        return mInstance;
    }

    public int getCardNum() {
        return mCardList.size();
    }

    public void reset() {
        mCardList.clear();
        for(int value = 1; value <= 9; value++) {
            for(int type = Card.CARD_TYPE_TIAO; type <= Card.CARD_TYPE_WAN; type++) {
                for(int i = 0; i <4; i++) {
                    Card card = new Card(value, type);
                    mCardList.add(card);
                }
            }
        }
        Collections.shuffle(mCardList);
    }

    public Card getCard() {
        Card card = null;
        for (Iterator<Card> it = mCardList.iterator(); it.hasNext(); ) {
            card = it.next();
            if (card.mType != Card.CARD_TYPE_UNKNOWN) {
                it.remove();
                break;
            }
        }
        return card;
    }
}
