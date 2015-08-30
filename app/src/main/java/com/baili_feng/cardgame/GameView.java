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
            color = Color.BLUE;
        } else if(card.mType == Card.CARD_TYPE_WAN) {
            color = Color.RED;
        }
        float stroke = 1.0f;
        float shadow = 5.0f;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        canvas.drawRect(x + shadow, y + shadow, x + w, y + h, paint);

        paint.setColor(0xffcccccc);
        canvas.drawRect(x, y, x + w - shadow, y + h - shadow, paint);

        drawRectLine(canvas, x, y, x + w - shadow, y + h - shadow, color, stroke);
        /*
        float[] pts = {x,y+stroke,x+w-shadow,y+stroke,
                        x+w-shadow-stroke,y+stroke,x+w-shadow-stroke,y+h-shadow-stroke,
                        x+w-shadow,y+h-shadow-stroke,x,y+h-shadow-stroke,
                        x+stroke,y+h-shadow-stroke,x+stroke,y+stroke};
        paint.setStrokeWidth(stroke * 2);
        paint.setColor(color);
        canvas.drawLines(pts, paint);
        */
        drawText(x + w / 2, y + h / 2.5f, canvas, (int) (h / 4), color, " " + card.mValue + " ", Paint.Align.CENTER);
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
        if(idx == 0) {
            RectF rect = getPlayerArea();
            drawRectLine(canvas, rect.left, rect.top, rect.right, rect.bottom, Color.MAGENTA, 5);
            /*
            float r = x + w * i * inc;
            if(player.mLastCard != null) {
                r += w*inc + offlast;
            }
            drawRectLine(canvas, x, y, r, y + h, Color.MAGENTA, 5);
            */
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //setBackgroundColor(color);
        //Log.i(TAG, "w: " + getWidth() + " h: " + getHeight());
        int state = mGame.getState();
        if(state == Game.GAME_READY) {
            drawText(640, 380, canvas, 100, Color.GREEN, "Touch screen to START!", Paint.Align.CENTER);
        } else if (state == Game.GAME_OVER) {
            drawText(640, 380, canvas, 100, Color.GREEN, "GAME OVER!", Paint.Align.CENTER);
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
            if(state == Game.GAME_PREPARE) {
                mGame.prepare();
            } else if(state == Game.GAME_LOOP) {
                mGame.loop();
            }
        }
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

    private void onTouchDown(MotionEvent event) {

    }
    private void onTouchUp(MotionEvent event) {
        int state = mGame.getState();
        if (state == Game.GAME_READY) {
            mGame.start();
        } else if (state == Game.GAME_PREPARE) {
            mGame.prepare();
        } else if (state == Game.GAME_LOOP) {
            float x = event.getX();
            float y = event.getY();
            int idx = getTouchIndex(x, y);
            Log.i(TAG, "idx is " + idx);
            Game.GameWait wait = mGame.mWait;
            if (idx != -1 && wait != null &&
                    wait.who == 0 &&
                    wait.action == Player.ACTION_CHUPAI) {
                Message message = new Message();
                message.what = Player.ACTION_CHUPAI;
                message.arg1 = 0;
                message.arg2 = idx;
                //mHandler.sendMessageDelayed(message,300);
                mHandler.sendMessage(message);
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
        mPlayerOffY1 = mCardHeight0/10;

        mBaidaWidth = mWidth/16;
        mBaidaHeight = mHeight/4;
        mBaidaOffX = mWidth/32;
        mBaidaOffY = mHeight/18;

        mLastCardOff = 0.2f;
    }

}
