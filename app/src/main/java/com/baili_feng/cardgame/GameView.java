package com.baili_feng.cardgame;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.GenericArrayType;
import java.util.List;

public class GameView extends View {
    private static final String TAG = "GameView";

    private Game mGame;
    private Handler mHandler;
    private float mWidth = 0;
    private float mHeight = 0;
    private float mCardWidth0 = 0;
    private float mCardHeight0 = 0;
    private float mPlayerOffX0 = 0;
    private float mPlayerOffY0 = 0;

    private float mCardWidth1 = 0;
    private float mCardHeight1 = 0;
    private float mPlayerOffX1 = 0;
    private float mPlayerOffY1 = 0;

    private float mLastCardOff = 0;
    private float mBaidaWidth = 0;
    private float mBaidaHeight = 0;
    private float mBaidaOffX = 0;
    private float mBaidaOffY = 0;

    public void setGame(Game game) {
        mGame = game;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public GameView(Context context) {
        super(context);
        init(null, 0);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
    }

    private void drawRectLine(Canvas canvas, float x, float y, float w, float h, int color, float s) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float stroke = s/2;
        float[] pts = {x,y+stroke,w,y+stroke,
                w-stroke,y+stroke,w-stroke,h-stroke,
                w,h-stroke,x,h-stroke,
                x+stroke,h-stroke,x+stroke,y+stroke};
        paint.setStrokeWidth(s);
        paint.setColor(color);
        canvas.drawLines(pts, paint);
    }

    private void drawCard(Canvas canvas, Card card, float x, float y, float w, float h) {
        int color = Color.BLACK;
        if(card.mType == Card.CARD_TYPE_TIAO) {
            //color = Color.GREEN;
            color = 0xff008800;
        } else if(card.mType == Card.CARD_TYPE_BING) {
            color = 0xff000088;
        } else if(card.mType == Card.CARD_TYPE_WAN) {
            color = 0xff880000;
        }
        float stroke = 1.0f;
        float shadow = 5.0f;
        float round = 10;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.DKGRAY);
        canvas.drawRoundRect(new RectF(x + shadow, y + shadow, x + w, y + h), round, round, paint);

        paint.setColor(0xffcccccc);
        canvas.drawRoundRect(new RectF(x, y, x + w - shadow, y + h - shadow), round, round, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stroke);
        paint.setColor(color);
        canvas.drawRoundRect(new RectF(x, y, x + w - shadow, y + h - shadow), round, round, paint);
        //drawRectLine(canvas, x, y, x + w - shadow, y + h - shadow, color, stroke);
        drawText(x + w / 2, y + h / 2.5f, canvas, (int) (h / 4), color, " " + card.mValue + " ", Paint.Align.CENTER);
    }

    private void drawActionBar(Canvas canvas, int action) {
        float x = mWidth/10;
        float y = mHeight/10;
        float w = mWidth*8/10;
        float h = mHeight*8/10;
        float shadow = 5.0f;
        float round = 10;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0x33000000);
        canvas.drawRoundRect(new RectF(x + shadow, y + shadow, x + w, y + h), round, round, paint);

        paint.setColor(0x33cc00cc);
        canvas.drawRoundRect(new RectF(x, y, x + (w - shadow) / 2, y + h - shadow), round, round, paint);
        drawText(x + w / 4, y + h / 2, canvas, 70, Color.BLACK, "胡", Paint.Align.CENTER);

        paint.setColor(0x3300cccc);
        canvas.drawRoundRect(new RectF(x + (w - shadow) / 2, y, x + w - shadow, y + h - shadow), round, round, paint);
        drawText(x + w * 3 / 4, y + h / 2, canvas, 70, Color.BLACK, "取消", Paint.Align.CENTER);
    }

    private void drawPlayground(Canvas canvas) {
        float x = mWidth/60;
        float y = mHeight/30;
        float w = mWidth/3;
        float h = mHeight/3.5f;
        float shadow = 5.0f;
        float round = 10;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xcc666666);
        canvas.drawRoundRect(new RectF(x + shadow, y + shadow, x + w, y + h), round, round, paint);
        String str = null;
        paint.setColor(0xcccc0000);
        for(Player player : mGame.mPlayers) {
            if(player.mIdx == 0) {
                str = "玩家: " + player.mScore;
                if(mGame.mZhuangPlayerIdx == player.mIdx) {
                    canvas.drawOval(new RectF(x + w * 2/7, y+h/4 , x + w * 2/7+30, y+h/4+30), paint);
                }
                drawText(x + w * 3/7, y+h/4, canvas, 30, Color.BLACK, str, Paint.Align.LEFT);
            } else {
                str = "电脑: " + player.mScore;
                if(mGame.mZhuangPlayerIdx == player.mIdx) {
                    canvas.drawOval(new RectF(x + w * 2/7, y+h*2/4 , x + w * 2/7+30, y+h*2/4+30), paint);
                }
                drawText(x + w * 3/7, y+h*2/4, canvas, 30, Color.BLACK, str, Paint.Align.LEFT);
            }
        }
        str = "剩余: " + Table.getInstance().getCardNum();
        drawText(x + w * 3 / 7, y + h * 3 / 4, canvas, 30, Color.BLACK, str, Paint.Align.LEFT);
    }

    private void drawText(float left, float top, Canvas canvas, int textSize,
                            int col, String string, Paint.Align a_align) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(col);
        paint.setTextSize(textSize);
        paint.setTextAlign(a_align);
        //Log.i(TAG, "top: " + top + " ascent: " + paint.ascent() + " descent: " + paint.descent());
        canvas.drawText(string, left, top - (paint.ascent() + paint.descent()), paint);
    }

    private void drawPlayer(Canvas canvas, int idx) {
        Player player = mGame.mPlayers.get(idx);
        float x, y, w, h;
        float offlast;
        int inc = 0;
        if(idx == 0) {
            x = mPlayerOffX0;
            y = mPlayerOffY0;
            w = mCardWidth0;
            h = mCardHeight0;
            inc = 1;
            offlast = w*mLastCardOff;
        } else {
            x = mPlayerOffX1;
            y = mPlayerOffY1;
            w = mCardWidth1;
            h = mCardHeight1;
            inc = -1;
            offlast = w*mLastCardOff*inc;
        }
        int i;
        for(i = 0; i < player.mCardList.size(); i++) {
            drawCard(canvas, player.mCardList.get(i), x+w*i*inc, y, w, h);
        }
        if(player.mLastCard != null) {
            drawCard(canvas, player.mLastCard, x + w * i * inc + offlast, y, w, h);
        }
        /*
        if(idx == 0) {
            RectF rect = getPlayerArea();
            drawRectLine(canvas, rect.left, rect.top, rect.right, rect.bottom, Color.MAGENTA, 5);
        }
        */
    }

    private void drawScore(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xaa666666);
        canvas.drawRect(0, 0, mWidth, mHeight, paint);
        Player player = mGame.mPlayers.get(mGame.mHuPlayerIdx);
        drawText(640, 380, canvas, 100, Color.GREEN, "Touch to RESTART!", Paint.Align.CENTER);
        /*
        int i = 0;
        for(Card card : player.mHuList) {
            drawCard(canvas, card, mWidth/16+i*mCardWidth0, (mHeight-mCardHeight0)/2, mCardWidth0, mCardHeight0);
        }*/
        //drawText(640, 380, canvas, 100, Color.GREEN, "Touch to RESTART!", Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //setBackgroundColor(color);
        //Log.i(TAG, "w: " + getWidth() + " h: " + getHeight());
        drawPlayground(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xaa666666);
        int state = mGame.getState();
        if(state == Game.GAME_READY) {
            canvas.drawRect(0, 0, mWidth, mHeight, paint);
            drawText(640, 380, canvas, 100, Color.BLACK, "点击屏幕开始", Paint.Align.CENTER);
        } else if (state == Game.GAME_OVER) {
            canvas.drawRect(0, 0, mWidth, mHeight, paint);
            drawText(640, 380, canvas, 100, Color.DKGRAY, "游戏结束", Paint.Align.CENTER);
        } else {
            if(mGame.mBaida != null) {
                drawCard(canvas, mGame.mBaida, mBaidaOffX, mBaidaOffY, mBaidaWidth, mBaidaHeight);
            }
            int i, j;
            float x, y, w, h;
            float xinc = 0;
            for(i = 0; i < mGame.mPlayers.size(); i++) {
                drawPlayer(canvas, i);
            }
            if(mGame.mTmpCard != null) {
                drawCard(canvas, mGame.mTmpCard, (mWidth-mCardWidth0)/2, (mHeight-mCardHeight0)/2, mCardWidth0, mCardHeight0);
            }

            if(state == Game.GAME_PREPARE) {
                mGame.prepare();
            } else if(state == Game.GAME_LOOP) {
                if(mGame.mPlayers.get(0).mWaitAction != Player.ACTION_NONE) {
                    drawActionBar(canvas, Player.ACTION_HU | Player.ACTION_CANCEL);
                }
                mGame.loop();
            } else if(state == Game.GAME_SCORE) {
                drawScore(canvas);
            }
        }
    }

    private boolean hasAction(int cur, int action) {
        return (cur & action) == action;
    }

    private int setAction(int cur, int action) {
        return cur | action;
    }

    private int clearAction(int cur, int action) {
        return cur & ~action;
    }

    public void setTouchListener() {
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    onTouchDown(event);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    onTouchUp(event);
                }
                return true;
            }
        });
    }

    private void onTouchUp(MotionEvent event) {

    }
    private void onTouchDown(MotionEvent event) {
        int state = mGame.getState();
        if (state == Game.GAME_READY || state == Game.GAME_SCORE) {
            mGame.reset();
            mGame.start();
        } else if (state == Game.GAME_PREPARE) {
            mGame.prepare();
        } else if (state == Game.GAME_LOOP) {
            float x = event.getX();
            float y = event.getY();
            int idx;

            if(mGame.mPlayers.get(0).mWaitAction != Player.ACTION_NONE) {
                idx = getActionBarIndex(x, y);
                Message message = new Message();
                if(idx == 0) message.what = Player.ACTION_HU;
                if(idx == 1) message.what = Player.ACTION_CANCEL;
                message.arg1 = 0;
                //mHandler.sendMessageDelayed(message,300);
                mHandler.sendMessage(message);
                Log.i(TAG, "Player 0" + " send Action " + idx);
                return;
            }
            idx = getTouchIndex(x, y);
            Log.i(TAG, "idx is " + idx);
            Game.GameWait wait = mGame.mWait;
            if (idx != -1 && wait != null &&
                    wait.who == 0 &&
                    hasAction(wait.action, Player.ACTION_CHUPAI)
                    ) {
                Message message = new Message();
                message.what = Player.ACTION_CHUPAI;
                message.arg1 = 0;
                message.arg2 = idx;
                //mHandler.sendMessageDelayed(message,300);
                mHandler.sendMessage(message);
                Log.i(TAG, "Player 0" + " chupai " + idx);
            }
        }
    }

    private int getTouchIndex(float x, float y) {
        int idx = -1;
        Player player = mGame.mPlayers.get(0);
        RectF rect = getPlayerArea();
        if(x < rect.left || x > rect.right || y < rect.top || y > rect.bottom){
            return idx;
        }
        if(x - rect.left > mCardWidth0*player.mCardList.size()) {
            idx = player.mCardList.size();
        } else {
            idx = (int)((x - rect.left)/mCardWidth0);
        }
        return idx;
    }

    private int getActionBarIndex(float x, float y) {
        if(x > mWidth/2) return 1;
        return 0;
    }

    private RectF getPlayerArea() {
        Player player = mGame.mPlayers.get(0);
        float w = player.mCardList.size()*mCardWidth0;
        if(player.mLastCard != null) {
            w += mCardWidth0+mCardWidth0*mLastCardOff;
        }
        float h = mCardHeight0;
        return new RectF(mPlayerOffX0, mPlayerOffY0, mPlayerOffX0+w, mPlayerOffY0+h);
    }

    public void configGameZone(int w, int h) {
        mWidth = w;
        mHeight = h;
        mCardWidth0 = mWidth/16;
        mCardHeight0 = mHeight/4;
        mPlayerOffX0 = mWidth/32;
        mPlayerOffY0 = mHeight - mCardHeight0*11/10;

        mCardWidth1 = mWidth*2/16/3;
        mCardHeight1 = mHeight*2/12;
        mPlayerOffX1 = mWidth - mWidth/16;
        mPlayerOffY1 = mHeight/30;

        mBaidaWidth = mWidth/16;
        mBaidaHeight = mHeight/4;
        mBaidaOffX = mWidth/32;
        mBaidaOffY = mHeight/18;

        mLastCardOff = 0.2f;
    }

}
