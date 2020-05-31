package com.example.tictactoe.game_layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.tictactoe.R;
import com.example.tictactoe.option_page;

public class Single_TTT extends View {
    Paint paint_line;
    private int mTurns;
    private ImageView[][] mImages;
    private Animation rotate, bounce;
    private char[][] mBoard;
    private final char nil = ' ', ai = 'X', human = 'O';
    private SoundPool mSoundPool;
    private ImageView change_pic;
    private MediaPlayer player;
    private int sound_win, sound_touch_human, sound_touch_computer, sound_draw;

    public Single_TTT(Context context) {
        super(context);
        init(null, true);
    }

    public Single_TTT(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, true);
    }

    public Single_TTT(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, true);
    }

    public Single_TTT(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, true);
    }

    public void init(@Nullable AttributeSet set, boolean start_from) {
        paint_line = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_line.setColor(Color.WHITE);

        init_sounds();
        player = MediaPlayer.create(getContext(), R.raw.lost);
        rotate = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        bounce = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
        mTurns = 0;
        change_pic = ((Activity)getContext()).findViewById(R.id.imageView_change_pic);
        mBoard = new char[3][3];
        mImages = new ImageView[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String imageID = "simage_" + i + j;
                int resID = getResources().getIdentifier(imageID, "id", getContext().getPackageName());
                mImages[i][j] = ((Activity) getContext()).findViewById(resID);
                mBoard[i][j] = nil;
            }
        }

        if (!start_from)
            computer_move();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLines(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value = super.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int[] a = get_i_j(event.getX(), event.getY());
            if (a != null) {
                played(a[0], a[1]);
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

    private void played(int i, int j ) {
        mTurns++;
        mSoundPool.play(sound_touch_human, 1,1,0,0,1);
        mBoard[i][j] = human;
        mImages[i][j].setVisibility(View.VISIBLE);
        mImages[i][j].setImageResource(R.drawable.o);
        mImages[i][j].startAnimation(rotate);
        change_pic.setImageResource(R.drawable.computer);
        change_pic.startAnimation(bounce);

        if (mTurns<9)
            computer_move();

        int checker = check_winner(mTurns);

        if (checker != 2) {
            if (checker == 1) {
                Toast.makeText(getContext(), "The computer won :(", Toast.LENGTH_SHORT).show();
                play_loser();
                getContext().startActivity(new Intent(getContext(), option_page.class));
                ((Activity) getContext()).finish();
            }
            else if (checker == -1) {
                Toast.makeText(getContext(), "You won :)", Toast.LENGTH_SHORT).show();
                mSoundPool.play(sound_win, 1,1,0,0,1);
                getContext().startActivity(new Intent(getContext(), option_page.class));
                ((Activity)getContext()).finish();
            }
            else {
                Toast.makeText(getContext(), "Its a draw :(", Toast.LENGTH_SHORT).show();
                mSoundPool.play(sound_draw,1,1,0,0,1);
                getContext().startActivity(new Intent(getContext(), option_page.class));
                ((Activity) getContext()).finish();
            }
        }
    }

    private void computer_move() {
        mSoundPool.play(sound_touch_computer, 1,1,0,0,1);
        mTurns++;
        int[] move = bestMove();
        int i = move[0], j = move[1];
        mImages[i][j].setVisibility(View.VISIBLE);
        mImages[i][j].setImageResource(R.drawable.x);
        mImages[i][j].startAnimation(rotate);
        mBoard[i][j] = ai;
        change_pic.setImageResource(R.drawable.sunglasses);
        change_pic.startAnimation(bounce);
    }

    private int[] bestMove() {
        int[] move = new int[2];
        int best_score = Integer.MIN_VALUE;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (mBoard[i][j] == nil) {
                    mBoard[i][j] = ai;
                    int score = minimax(0, false, mTurns);
                    mBoard[i][j] = nil;
                    if (best_score<score) {
                        best_score = score;
                        move[0] = i;
                        move[1] = j;
                    }
                }
            }
        }
        System.out.println(best_score);
        return move;
    }

    private int minimax(int depth, boolean isMaximising, int turns) {
        int result = check_winner(turns);
        if(result != 2) {
            return result;
        }
        if (isMaximising) {
            int best_score = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (mBoard[i][j] == nil) {
                        mBoard[i][j] = ai;
                        int score = minimax(depth+1, false, turns + 1);
                        mBoard[i][j] = nil;
                        best_score = Math.max(best_score, score);
                    }
                }
            }
            return best_score;
        }
        else {
            int best_score = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (mBoard[i][j] == nil) {
                        mBoard[i][j] = human;
                        int score = minimax(depth+1, true, turns + 1);
                        mBoard[i][j] = nil;
                        best_score = Math.min(best_score, score);
                    }
                }
            }
            return best_score;
        }
    }

    private int check_winner(int turns) {
        for (int i = 0; i < 3; i++) {
            if(mBoard[0][i] == mBoard[1][i] && mBoard[1][i] == mBoard[2][i] && mBoard[0][i]!=nil) {
                return get_score(mBoard[0][i]);
            }
        }
        for (int i = 0; i < 3; i++) {
            if(mBoard[i][0] == mBoard[i][1] && mBoard[i][1] == mBoard[i][2] && mBoard[i][0]!=nil) {
                return get_score(mBoard[i][0]);
            }
        }
        if(mBoard[0][0] == mBoard[1][1] && mBoard[1][1] == mBoard[2][2] && mBoard[1][1]!=nil) {
            return get_score(mBoard[1][1]);
        }
        if (mBoard[0][2] == mBoard[1][1] && mBoard[1][1] == mBoard[2][0] && mBoard[1][1]!=nil) {
            return get_score(mBoard[1][1]);
        }
        if (turns == 9)
            return 0;
        return 2;
    }

    private int get_score(char winner) {
        if (winner == ai)
            return 1;
        else
            return -1;
    }

    private void play_loser() {
        player.start();
        new CountDownTimer(10000, 10000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                player.stop();
            }
        }.start();
    }

    private void init_sounds() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build();

            mSoundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(7)
                    .build();
        }

        sound_win = mSoundPool.load(getContext(), R.raw.single, 0);
        sound_draw = mSoundPool.load(getContext(), R.raw.draw, 0);
        sound_touch_human = mSoundPool.load(getContext(), R.raw.pl1sound, 0);
        sound_touch_computer = mSoundPool.load(getContext(), R.raw.pl2sound, 0);
    }
}
