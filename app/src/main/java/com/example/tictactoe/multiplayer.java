package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class multiplayer extends AppCompatActivity {

    private boolean mPlayer1Turn;
    private ImageView mEmojiPicture;
    private Button[][] mButtons;
    private int mTurns;
    private ImageView[][] mEmojis;
    private int[] mResources;
    private String mPl1, mPl2;
    private SoundPool mPlayer;
    private int mDraw, mP1win, mP2win, mActionP1, mActionP2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        mEmojis = new ImageView[3][3];
        mPlayer1Turn = true;
        mResources = new int[9];
        mButtons = new Button[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                String imageID = "image_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                int resID2 = getResources().getIdentifier(imageID, "id", getPackageName());
                mButtons[i][j] = findViewById(resID);
                mEmojis[i][j] = findViewById(resID2);
                mResources[linear(i,j)] = resID;
            }
        }
        mEmojiPicture = findViewById(R.id.imageView_emoji);
        mTurns = 0;

        if (getIntent().getExtras() != null) {
            mPl1 = getIntent().getExtras().getString("player1");
            mPl2 = getIntent().getExtras().getString("player2");
        }

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

        mActionP1 = mPlayer.load(this, R.raw.pl1sound, 1);
        mActionP2 = mPlayer.load(this, R.raw.pl2sound, 1);
        mP1win = mPlayer.load(this, R.raw.multi1, 1);
        mP2win = mPlayer.load(this, R.raw.multi2, 1);
        mDraw = mPlayer.load(this, R.raw.draw, 1);
    }

    public void tictactoe(View v) {
        String text = ((Button) v).getText().toString();
        if(!text.equals("")) {
            Toast.makeText(this, "Already entered!", Toast.LENGTH_SHORT).show();
        }
        else {
            Animation image_animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
            int ID = binary_search(v.getId());
            int[] a = de_linear(ID);
            int i = a[0];
            int j = a[1];
            mTurns++;
            if (mPlayer1Turn) {
                ((Button) v).setText("X");
                mEmojis[i][j].setImageResource(R.drawable.x);
                mButtons[i][j].setVisibility(View.INVISIBLE);
                mEmojis[i][j].startAnimation(image_animation);
                mPlayer.play(mActionP1, 1, 1, 0, 0, 1);
            }
            else {
                ((Button) v).setText("O");
                mEmojis[i][j].setImageResource(R.drawable.o);
                mButtons[i][j].setVisibility(View.INVISIBLE);
                mEmojis[i][j].startAnimation(image_animation);
                mPlayer.play(mActionP2, 1, 1, 0, 0, 1);
            }
            if(check_winner()) {
                return;
            }
            change_image();
            if (mTurns == 9) {
                Toast.makeText(this, "Its a draw! :(", Toast.LENGTH_SHORT).show();
                mPlayer.play(mDraw, 1, 1, 0, 0, 1);
                startActivity(new Intent(this, option_page.class));
                finish();
            }
            mPlayer1Turn = !mPlayer1Turn;
        }
    }

    private boolean check_winner() {
        int[][] a = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String text = mButtons[i][j].getText().toString();
                if(text.equals("X"))
                    a[i][j] = 1;
                else if ((text.equals("O")))
                    a[i][j] = 2;
                else
                    a[i][j] = 0;
            }
        }
        for (int i = 0; i < 3; i++) {
            if(a[0][i] == a[1][i] && a[1][i] == a[2][i] && a[0][i]!=0) {
                declare_winner();
                return true;
            }
        }
        for (int i = 0; i < 3; i++) {
            if(a[i][0] == a[i][1] && a[i][1] == a[i][2] && a[i][0]!=0) {
                declare_winner();
                return true;
            }
        }
        if(a[0][0] == a[1][1] && a[1][1] == a[2][2] && a[1][1]!=0) {
            declare_winner();
            return true;
        }
        if (a[0][2] == a[1][1] && a[1][1] == a[2][0] && a[1][1]!=0) {
            declare_winner();
            return true;
        }
        return false;
    }

    public void declare_winner(){

        String declare = " won! :)";
        String winner;
        Intent i = new Intent(this, result_page.class);
        if(mPlayer1Turn) {
            Toast.makeText(this, mPl1 + declare, Toast.LENGTH_SHORT).show();
            mPlayer.play(mP1win, 1, 1, 0, 0, 1);
            winner = mPl1;
        }
        else {
            Toast.makeText(this, mPl2 + declare, Toast.LENGTH_SHORT).show();
            mPlayer.play(mP2win, 1, 1, 0, 0, 1);
            winner = mPl2;
        }
        i.putExtra("winner", winner);
        startActivity(i);
        finish();
    }

    private void change_image() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        if(mPlayer1Turn) {
            mEmojiPicture.startAnimation(animation);
            mEmojiPicture.setImageResource(R.drawable.scissor);
        }
        else {
            mEmojiPicture.startAnimation(animation);
            mEmojiPicture.setImageResource(R.drawable.sunglasses);
        }
    }

    private int linear(int a, int b) {
        return 3*a + b;
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
}