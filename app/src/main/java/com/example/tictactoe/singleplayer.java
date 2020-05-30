package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class singleplayer extends AppCompatActivity {

    private int mTurns;
    private Button[][] mButtons;
    private ImageView[][] mImages;
    private Animation rotate, bounce;
    private char[][] mBoard;
    private final char nil = ' ', ai = 'X', human = 'O';
    private int[] mResources;
    private SoundPool mSoundPool;
    private ImageView change_pic;
    private MediaPlayer player;
    private int sound_win, sound_touch_human, sound_touch_computer, sound_draw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);

        player = MediaPlayer.create(this, R.raw.lost);
        change_pic = findViewById(R.id.imageView_change_pic);
        boolean start = true;
        mButtons = new Button[3][3];
        mResources = new int[9];
        mBoard = new char[3][3];
        mImages = new ImageView[3][3];
        mTurns = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String button_id = "sbutton_" + i + j;
                String image_id = "simage_" + i + j;
                int resID = getResources().getIdentifier(button_id,"id", getPackageName());
                int resID_string = getResources().getIdentifier(image_id, "id", getPackageName());
                mButtons[i][j] = findViewById(resID);
                mImages[i][j] = findViewById(resID_string);
                mButtons[i][j].setText(String.valueOf(nil));
                mResources[linear(i,j)] = resID;
                mBoard[i][j] = nil;
            }
        }

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

        sound_win = mSoundPool.load(this, R.raw.single, 0);
        sound_draw = mSoundPool.load(this, R.raw.draw, 0);
        sound_touch_human = mSoundPool.load(this, R.raw.pl1sound, 0);
        sound_touch_computer = mSoundPool.load(this, R.raw.pl2sound, 0);

        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

        if (getIntent().getExtras() != null)
            start = getIntent().getExtras().getBoolean("start");

        if (!start)
            computer_move();
    }

    public void tictactoe(View v) {
        String text = ((Button) v).getText().toString();

        if (!text.equals(" ")) {
            Toast.makeText(this, "Already pressed!", Toast.LENGTH_SHORT).show();
        }
        else {
            mTurns++;
            ((Button) v).setText(String.valueOf(human));
            int[] a = de_linear(binary_search(v.getId()));
            int i = a[0], j = a[1];
            mSoundPool.play(sound_touch_human, 1,1,0,0,1);
            mBoard[i][j] = human;
            mImages[i][j].setImageResource(R.drawable.o);
            mImages[i][j].startAnimation(rotate);
            mButtons[i][j].setVisibility(View.INVISIBLE);
            change_pic.setImageResource(R.drawable.computer);
            change_pic.startAnimation(bounce);


            if (mTurns<9)
                computer_move();

            int checker = check_winner(mTurns);

            if (checker != 2) {
                if (checker == 1) {
                    Toast.makeText(this, "The computer won :(", Toast.LENGTH_SHORT).show();
                    play_loser();
                    startActivity(new Intent(this, option_page.class));
                    finish();
                }
                else if (checker == -1) {
                    Toast.makeText(this, "You won :)", Toast.LENGTH_SHORT).show();
                    mSoundPool.play(sound_win, 1,1,0,0,1);
                    startActivity(new Intent(this, option_page.class));
                    finish();
                }
                else {
                    Toast.makeText(this, "Its a draw :(", Toast.LENGTH_SHORT).show();
                    mSoundPool.play(sound_draw,1,1,0,0,1);
                    startActivity(new Intent(this, option_page.class));
                    finish();
                }
            }
        }
    }

    private void computer_move() {
        mSoundPool.play(sound_touch_computer, 1,1,0,0,1);
        mTurns++;
        int[] move = bestMove();
        int i = move[0], j = move[1];
        mButtons[i][j].setVisibility(View.INVISIBLE);
        mImages[i][j].setImageResource(R.drawable.x);
        mImages[i][j].startAnimation(rotate);
        mBoard[i][j] = ai;
        change_pic.setImageResource(R.drawable.sunglasses);
        change_pic.startAnimation(bounce);
        mButtons[i][j].setText(String.valueOf(ai));
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

    private int linear(int a, int b) {
        return a*3 + b;
    }

    private int get_score(char winner) {
        if (winner == ai)
            return 1;
        else
            return -1;
    }

    private int[] de_linear(int s) {
        int b = s%3;
        int a = (s-b)/3;
        int[] c = new int[2];
        c[0] = a;
        c[1] = b;
        return c;
    }

    private int binary_search(int x) {
        int l = 0, r = 8;
        while (l <= r) {
            int m = l + (r - l) / 2;
            if (mResources[m] == x)
                return m;
            if (mResources[m] < x)
                l = m + 1;
            else
                r = m - 1;
        }
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
}