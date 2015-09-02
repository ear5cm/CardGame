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
    final public static int GAME_SCORE = 3;
    final public static int GAME_OVER = 4;

    private Handler mHandler;
    private Table mTable = null;
    private int betBase = 10000;
    private int aword = 0;

    public List<Player> mPlayers = new ArrayList<>();
    public int mState = GAME_UNKNOWN;
    public Card mBaida = null;
    public Card mTmpCard = null;
    public int mZhuangPlayerIdx = 0;
    public int mCurrentPlayerInx = 0;
    public int mHuPlayerIdx = 0;
    public int mNumPlayers = 0;

    public GameWait mWait = null;

    public Game(Handler handler) {
        super();
        mHandler = handler;
        mTable = Table.getInstance();
        mWait = new GameWait();
        mWait.action = Player.ACTION_NONE;
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
        for(Player player : mPlayers) {
            player.reset();
        }
        mTable.reset();
        mBaida = null;
        mTmpCard = null;
        mWait.action = Player.ACTION_NONE;
        mState = GAME_READY;
        mCurrentPlayerInx = mZhuangPlayerIdx;
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
            mPlayers.get(mCurrentPlayerInx).setLastCard(card);
        } else {
            mPlayers.get(mCurrentPlayerInx).addCard(card);
        }
        //Log.i(TAG, "Player " + mCurrentPlayerInx + " add Card: " + card.mValue + "/" + card.mType);
        mCurrentPlayerInx = (mCurrentPlayerInx + 1) % mNumPlayers;
    }

    public void loop() {
        Log.i(TAG, "looping...");
        Player player = mPlayers.get(mCurrentPlayerInx);
        if(player.mLastCard != null && mWait.action == Player.ACTION_NONE) {
            // wait player to make a decision.
            mWait.who = mCurrentPlayerInx;
            mWait.action = Player.ACTION_CHUPAI;
            // what can u do?
            mWait.action |= player.checkAction(player.mLastCard, mCurrentPlayerInx);
            Log.i(TAG, "Player " + mCurrentPlayerInx + " checkAction1 " + mWait.action);
            // make ur decision. AI will post a msg to main handler
            player.chooseAction(player.mLastCard, mCurrentPlayerInx);
            Log.i(TAG, "Player " + mCurrentPlayerInx + " chooseAction1 " + mWait.action);
        } else if(mTmpCard != null && mWait.action == Player.ACTION_NONE) {
            mWait.who = mCurrentPlayerInx;
            mWait.action |= player.checkAction(mTmpCard, mCurrentPlayerInx);
            Log.i(TAG, "Player " + mCurrentPlayerInx + " checkAction2 " + mWait.action);
            if(mWait.action != Player.ACTION_NONE) {
                player.chooseAction(mTmpCard, mCurrentPlayerInx);
                Log.i(TAG, "Player " + mCurrentPlayerInx + " chooseAction2 " + mWait.action);
            } else {
                mTmpCard = null;
                Card card = mTable.getCard();
                if(card == null) {
                    over();
                    return;
                }
                // prepare for next loop cycle
                player.setLastCard(card);
                Log.i(TAG, "Player " + mCurrentPlayerInx + " get a new card");
                //mCurrentPlayerInx = (mCurrentPlayerInx+1)%mNumPlayers;
            }
        }else {
            Log.i(TAG, "Player " + mCurrentPlayerInx + " do nothing");
        }
    }

    public void handleMessage(Message msg){
        if(mWait.who != msg.arg1){
            return;
        }
        Player player = mPlayers.get(mWait.who);
        switch (msg.what) {
            case Player.ACTION_ACK:
                break;
            case Player.ACTION_HU:
                Log.i(TAG, "Player " + mWait.who + " ACTION_HU");
                mState = GAME_SCORE;
                mHuPlayerIdx = mWait.who;
                int loser = (mHuPlayerIdx+1)%mPlayers.size();
                aword = betBase;
                if(player.mLastCard != null) {
                    aword *= 2;
                }
                player.mScore += aword;
                mPlayers.get(loser).mScore -= aword;
                if(mZhuangPlayerIdx == player.mIdx) {
                    mZhuangPlayerIdx = player.mIdx;
                }
                player.makeAction(Player.ACTION_HU, 0);
                break;
            case Player.ACTION_CANCEL:
                Log.i(TAG, "Player " + mWait.who + " ACTION_CANCEL");
                if(mTmpCard != null) {
                    mTmpCard = null;
                    Card card = mTable.getCard();
                    if(card == null) {
                        over();
                        return;
                    }
                    // prepare for next loop cycle
                    player.setLastCard(card);
                    Log.i(TAG, "Player " + mWait.who + " get a new card after cancel");
                }
                mWait.action &= ~Player.ACTION_HU;
                player.makeAction(Player.ACTION_CANCEL, 0);
                break;
            case Player.ACTION_CHUPAI:
                Log.i(TAG, "Player " + mWait.who + " ACTION_CHUPAI");
                mTmpCard = player.rmCard(msg.arg2);
                if(mTmpCard != null){
                    mWait.who = -1;
                    mWait.action = Player.ACTION_NONE;
                    // rm the selected card and add mLastcard to mLastCardList
                    player.addCard(player.mLastCard);
                    player.mLastCard = null;
                    // prepare for next loop cycle
                    Log.i(TAG, "Player " + mWait.who + " switch to next player");
                    mCurrentPlayerInx = (mCurrentPlayerInx+1)%mNumPlayers;
                }
                else{Log.i(TAG, "TMP CARD IS NULL");}
                break;
            default:
                break;
        }
    }

    public void over(){
        mState = GAME_OVER;
        Message message = new Message();
        message.what = Player.ACTION_ACK;
        //mHandler.sendMessageDelayed(message,300);
        mHandler.sendMessage(message);
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
