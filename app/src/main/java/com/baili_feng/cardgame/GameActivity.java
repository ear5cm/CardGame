package com.baili_feng.cardgame;

import android.app.Activity;
import android.graphics.Color;
import android.os.Message;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.content.Context;
import android.widget.FrameLayout;

public class GameActivity extends Activity {
    private static final String TAG = "GameActivity";

    private GameView mGameView;
    private Game mGame;
    public Handler mHandler = null;
    private int color = Color.BLUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        mHandler = new GameHandler();
        mGame = new Game(mHandler);
        mGameView = (GameView)findViewById(R.id.gameView);
        mGameView.setGame(mGame);
        mGameView.setHandler(mHandler);
        mGameView.setTouchListener();
        mGameView.configGameZone(getWidth(this), getHeight(this));
    }
    public static int getWidth(Context context){
        WindowManager wm=(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getHeight(Context context){
        WindowManager wm=(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public class GameHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Player.ACTION_ACK:
                    mGameView.postInvalidate();
                    break;
                case Player.ACTION_CHUPAI:
                    mGame.handleMessage(msg);
                    break;
                default:
                    break;
            }
        }
    };

}
