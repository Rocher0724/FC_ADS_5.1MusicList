package choongyul.android.com.musicplayer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    ImageButton mImgLeftBtn, mImgRightBtn, mImgPlayBtn;
    ViewPager mViewPager;
    ArrayList<Music> datas;
    PlayerAdapter adapter;
    MediaPlayer player;
    SeekBar mSeekBar;
    TextView mTxtDuration, mTxtCurrent;

    private static final int PLAY = 0;
    private static final int PAUSE = 1;
    private static final int STOP = 2;

    private static int playStatus = STOP;

    int position = 0; // 현재 음악 위치

    public static final int PROGRESS_SET = 101;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case PROGRESS_SET:
                    if(player != null)
                        mSeekBar.setProgress(player.getCurrentPosition());
                        mTxtCurrent.setText(player.getCurrentPosition()/1000 + "");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);



        playStatus = STOP;

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);


        mImgLeftBtn = (ImageButton) findViewById(R.id.imgPrevBtn);
        mImgRightBtn = (ImageButton) findViewById(R.id.imgNextBtn);
        mImgPlayBtn = (ImageButton) findViewById(R.id.imgPlayBtn);
        mTxtDuration = (TextView) findViewById(R.id.txtDuration);
        mTxtCurrent = (TextView) findViewById(R.id.txtCurrent);


        mImgLeftBtn.setOnClickListener(clickListener);
        mImgRightBtn.setOnClickListener(clickListener);
        mImgPlayBtn.setOnClickListener(clickListener);




        // 1. 데이터 가져오기
        datas = DataLoader.getDatas(this);
        // 2. 뷰페이져 가져오기
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        // 3.뷰 페이져용 어댑터 생성
        adapter = new PlayerAdapter(datas, this);
        // 4.뷰 페이져 어댑터 연결
        mViewPager.setAdapter(adapter);

        // 5. 특정페이지 호출
        Intent intent = getIntent();
        if ( intent != null ) {
            Bundle bundle = intent.getExtras();
            position = bundle.getInt("position");

            // 실제 페이지로 이동하기 위한 계산처리
            //TODO

            mViewPager.setCurrentItem(position);

        }

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgPrevBtn:
                    prevMusic();
                    break;
                case R.id.imgNextBtn:
                    nextMusic();
                    break;
                case R.id.imgPlayBtn:
                    playMusic();
                    break;
            }
        }
    };


    private void prevMusic() {

    }

    private void nextMusic() {

    }

    private void playMusic() {
        switch(playStatus) {
            case STOP:
                Uri musicUri = datas.get(position).uri;
                player = MediaPlayer.create(this, musicUri); // 시스템파일 - context, 음원파일Uri
                player.setLooping(false);

                // seekbar 최고길이 설정
                mSeekBar.setMax(player.getDuration());
                mTxtDuration.setText((player.getDuration()/1000) + "Sec.");

                player.start();
                playStatus = PLAY;
                mImgPlayBtn.setImageResource(android.R.drawable.ic_media_pause);

                new Thread() {
                    @Override
                    public void run() {
                        while (playStatus < STOP) {
                            handler.sendEmptyMessage(PROGRESS_SET);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }.start();
                break;
            case PLAY:
                player.pause();
                mImgPlayBtn.setImageResource(android.R.drawable.ic_media_play);
                playStatus = PAUSE;
                break;
            case PAUSE:
                player.seekTo(player.getCurrentPosition());
                player.start();
                playStatus = PLAY;
                mImgPlayBtn.setImageResource(android.R.drawable.ic_media_pause);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (player != null) {
            player.release(); // 사용이 끝나면 해제해야만 한다.
        }
        playStatus = STOP;
    }

}
