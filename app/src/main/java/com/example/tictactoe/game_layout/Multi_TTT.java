package com.example.tictactoe.game_layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.example.tictactoe.R;
import com.example.tictactoe.option_page;
import com.example.tictactoe.result_page;

public class Multi_TTT extends View {

    private boolean mPlayer1Turn;
    private ImageView mEmojiPicture;
    private int mTurns;
    private ImageView[][] emojis;
    private String mPl1, mPl2;
    private SoundPool mPlayer;
    private int mDraw, mP1win, mP2win, mActionP1, mActionP2;
    private char[][] mBoard;
    private final char nil = ' ', pl1 = 'X', pl2 = 'O';
    private Paint paint_line;

    public Multi_TTT(Context context) {
        super(context);
        init_sounds();
        init(null);
    }

    public Multi_TTT(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init_sounds();
        init(attrs);
    }

    public Multi_TTT(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init_sounds();
        init(attrs);
    }

    public Multi_TTT(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init_sounds();
        init(attrs);
    }

    public void init(@Nullable AttributeSet set) {
        mTurns = 0;
        init_sounds();
        mPlayer1Turn = true;
        emojis = new ImageView[3][3];
        mEmojiPicture = ((Activity) getContext()).findViewById(R.id.imageView_emoji);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String button_id = "image_" + i + j;
                int resID = getResources().getIdentifier(button_id, "id", getContext().getPackageName());
                emojis[i][j] = ((Activity) getContext()).findViewById(resID);
            }
        }

        paint_line = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_line.setColor(Color.WHITE);

        mBoard = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mBoard[i][j] = nil;
            }
        }
    }

    public void init_string(String a, String b) {
        mPl1 = a;
        mPl2 = b;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        drawLines(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value =  super.onTouchEvent(event);
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                int[] a = get_i_j(event.getX(), event.getY());
                if (a!=null) {
                    int i = a[0];
                    int j = a[1];
                    played(i,j);
                }
            }
        }
        return value;
    }

    private void drawLines(Canvas canvas) {
        paint_line.setStrokeWidth(20f);
        float x = getWidth();
        float y = getHeight()*23/20;
        for (int i = 1; i <= 2; i++) {
            float y_line = y/2 - x/6 + x/3*(i-1);
            canvas.drawLine(x*i/3, y/2 - x/2, x/3*i, y/2+x/2, paint_line);
            canvas.drawLine(0, y_line, x, y_line, paint_line);
        }
    }

    private int[] get_i_j (float x, float y) {
        int[] ij = new int[2];
        int i, j;

        float width = getWidth();
        float height = getHeight()*23/20;

        if (y < height/2 - width/2 || y > height/2 + width/2)
            return null;

        if (x<width/3) {
            j = 0;
            if (y<height/2 - width/6) {
                i = 0;
            }
            else if (y>height/2 + width/6) {
                i = 2;
            }
            else {
                i = 1;
            }
        }
        else if (x>width*2/3) {
            j = 2;
            if (y<height/2 - width/6) {
                i = 0;
            }
            else if (y>height/2 + width/6) {
                i = 2;
            }
            else {
                i = 1;
            }
        }
        else {
            j = 1;
            if (y<height/2 - width/6) {
                i = 0;
            }
            else if (y>height/2 + width/6) {
                i = 2;
            }
            else {
                i = 1;
            }
        }

        ij[0] = i;
        ij[1] = j;

        return ij;
    }

    private void played(int i, int j) {
        if (mBoard[i][j] == nil) {
            Animation image_animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
            mTurns++;
            change_image();
            if (mPlayer1Turn) {
                mBoard[i][j] = pl1;
                emojis[i][j].setVisibility(View.VISIBLE);
                emojis[i][j].setImageResource(R.drawable.x);
                emojis[i][j].startAnimation(image_animation);

                mPlayer.play(mActionP1, 1, 1, 0, 0, 1);
            }
            else {
                mBoard[i][j] = pl2;
                emojis[i][j].setVisibility(View.VISIBLE);
                emojis[i][j].setImageResource(R.drawable.o);
                emojis[i][j].startAnimation(image_animation);
                mPlayer.play(mActionP2, 1, 1, 0, 0, 1);
            }
            if(check_winner()) {
                return;
            }

            if (mTurns == 9) {
                Toast.makeText(getContext(), "Its a draw! :(", Toast.LENGTH_SHORT).show();
                mPlayer.play(mDraw, 1, 1, 0, 0, 1);
                getContext().startActivity(new Intent(getContext(), option_page.class));
                ((Activity)getContext()).finish();
            }
            mPlayer1Turn = !mPlayer1Turn;
        }
    }

    private boolean check_winner() {

        for (int i = 0; i < 3; i++) {
            if(mBoard[0][i] == mBoard[1][i] && mBoard[1][i] == mBoard[2][i] && mBoard[0][i]!=nil) {
                declare_winner(mBoard[1][i]);
                return true;
            }
        }
        for (int i = 0; i < 3; i++) {
            if(mBoard[i][0] == mBoard[i][1] && mBoard[i][1] == mBoard[i][2] && mBoard[i][0]!=nil) {
                declare_winner(mBoard[i][1]);
                return true;
            }
        }
        if(mBoard[0][0] == mBoard[1][1] && mBoard[1][1] == mBoard[2][2] && mBoard[1][1]!=nil) {
            declare_winner(mBoard[1][1]);
            return true;
        }
        if (mBoard[0][2] == mBoard[1][1] && mBoard[1][1] == mBoard[2][0] && mBoard[1][1]!=nil) {
            declare_winner(mBoard[1][1]);
            return true;
        }
        return false;
    }

    public void declare_winner(char winner){

        String declare = " won! :)";
        String winner_string;
        Intent i = new Intent(getContext(), result_page.class);
        if(winner == pl1) {
            Toast.makeText(getContext(), mPl1 + declare, Toast.LENGTH_SHORT).show();
            mPlayer.play(mP1win, 1, 1, 0, 0, 1);
            winner_string = mPl1;
        }
        else {
            Toast.makeText(getContext(), mPl2 + declare, Toast.LENGTH_SHORT).show();
            mPlayer.play(mP2win, 1, 1, 0, 0, 1);
            winner_string = mPl2;
        }
        i.putExtra("winner", winner_string);
        getContext().startActivity(i);
        ((Activity)getContext()).finish();
    }

    private void change_image() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
        if(mPlayer1Turn) {
            mEmojiPicture.startAnimation(animation);
            mEmojiPicture.setImageResource(R.drawable.scissor);
        }
        else {
            mEmojiPicture.startAnimation(animation);
            mEmojiPicture.setImageResource(R.drawable.sunglasses);
        }
    }

    private void init_sounds() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .build();
            mPlayer = new SoundPool.Builder()
                    .setMaxStreams(7)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
        else {
            mPlayer = new SoundPool(7, AudioManager.STREAM_MUSIC, 0);
        }

        mActionP1 = mPlayer.load(getContext(), R.raw.pl1sound, 1);
        mActionP2 = mPlayer.load(getContext(), R.raw.pl2sound, 1);
        mP1win = mPlayer.load(getContext(), R.raw.multi1, 1);
        mP2win = mPlayer.load(getContext(), R.raw.multi2, 1);
        mDraw = mPlayer.load(getContext(), R.raw.draw, 1);
    }
}
