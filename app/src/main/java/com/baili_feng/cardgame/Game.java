package com.baili_feng.cardgame;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baili_feng on 2015/8/26.
 */
public class Game {
    private static final String TAG = "Game";
    final public static int GAME_UNKNOWN = -1;
    final public static int GAME_READY = 0;
    final public static int GAME_PREPARE = 1;
    final public static int GAME_LOOP = 2;
    final public static int GAME_OVER = 3;

    private Handler mHandler;
    private Table mTable = new Table();;

    public List<Player> mPlayers = new ArrayList<>();
    public int mState = GAME_UNKNOWN;
    public Card mBaida = null;
    public Card mTmpCard = null;
    public int mZhuangPlayerIdx = 0;
    public int mCurrentPlayerInx = 0;
    public int mNumPlayers = 0;

    public GameWait mWait = null;

    public Game(Handler handler) {
        super();
        mHandler = handler;
        mWait = new GameWait();
        mWait.action = Player.ACTION_UNKNOWN;
        init();
    }

    public void init() {
        Player player0 = new Player(mHandler, 0);
        AI ai0 = new AIHuman();
        player0.attachAI(ai0);
        mPlayers.add(player0);
        mNumPlayers++;

        Player player1 = new Player(mHandler, 1);
        AI ai1 = new AICPU();
        player1.attachAI(ai1);
        mPlayers.add(player1);
        mNumPlayers++;

        reset();
    }
    public void reset() {
        mTable.reset();
        mState = GAME_READY;
    }

    public void start() {
        mState = GAME_PREPARE;
        prepare();
    }

    public void prepare() {
        if(mState != GAME_PREPARE) return;

        if(mPlayers.get(mZhuangPlayerIdx).mLastCard != null) {
            if(mBaida == null) {
                mBaida = mTable.getCard();
                if(mBaida == null) {
                    over();
                    return;
                }
                for (Player player : mPlayers) {
                    player.setBaida(mBaida);
                }
            } else {
                //check zhezhang here;
            }
            //reset current index to zhuang
            mCurrentPlayerInx = mZhuangPlayerIdx;
            mState = GAME_LOOP;
            return;
        }

        Card card = mTable.getCard();
        if(card == null) {
            over();
            return;
        }
        if(mPlayers.get(mCurrentPlayerInx).mCardList.size() == 13) {
            mPlayers.get(mCurrentPlayerInx).addLastCard(card);
        } else {
            mPlayers.get(mCurrentPlayerInx).addCard(card);
        }
        //Log.i(TAG, "Player " + mCurrentPlayerInx + " add Card: " + card.mValue + "/" + card.mType);
        mCurrentPlayerInx = (mCurrentPlayerInx + 1) % mNumPlayers;
    }

    public void loop() {
        Log.i(TAG, "looping...");
        Player player = mPlayers.get(mCurrentPlayerInx);
        if(player.mLastCard != null) {
            if(player.checkHu()) {
            } else {
                Log.i(TAG, "111");
                mWait.who = mCurrentPlayerInx;
                mWait.action = Player.ACTION_CHUPAI;
                mWait.value = 0;
                // ask player to play a move
                player.playCard();
            }
        } else if(mTmpCard != null) {
            Log.i(TAG, "333");
            if(player.checkHu()) {

            } else {
                Log.i(TAG, "222");
                mTmpCard = null;
                Card card = mTable.getCard();
                if(card == null) {
                    over();
                    return;
                }
                player.addLastCard(card);
                //mCurrentPlayerInx = (mCurrentPlayerInx+1)%mNumPlayers;
            }
        }else{Log.i(TAG, "44444");}
    }

    public void over(){
        mState = GAME_OVER;
        Message message = new Message();
        message.what = Player.ACTION_ACK;
        //mHandler.sendMessageDelayed(message,300);
        mHandler.sendMessage(message);
    }

    public void handleMessage(Message msg){
        if(mWait.action != msg.what || mWait.who != msg.arg1){
            return;
        }
        switch (msg.what) {
            case Player.ACTION_ACK:
                break;
            case Player.ACTION_CHUPAI:
                Player player = mPlayers.get(mWait.who);
                mTmpCard = player.rmCard(msg.arg2);
                if(mTmpCard != null){
                    Log.i(TAG, "555");
                    mWait.who = -1;
                    mWait.action = Player.ACTION_UNKNOWN;
                    player.addCard(player.mLastCard);
                    player.mLastCard = null;
                    mCurrentPlayerInx = (mCurrentPlayerInx+1)%mNumPlayers;
                }
                else{Log.i(TAG, "TMP CARD IS NULL");}
                break;
            default:
                break;
        }
    }

    public int getState() {
        return mState;
    }

    public class GameWait {
        public int action;
        public int value;
        public int who;
    }
}
