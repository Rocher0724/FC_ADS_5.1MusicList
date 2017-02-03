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
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.IOException;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        init();

    }

    private void init() {
        playStatus = STOP;

        // 액티비티 생성시 핸드폰 볼륨조절이 음악볼륨조절로 변경
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(seekBarListener);

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
        // 5.뷰 페이져 리스너 연결 ( 페이저 이동시 이동된 값을 seekbar와 길이 세팅)
        mViewPager.addOnPageChangeListener(viewPagerListener);

        // 5. 특정페이지 호출
        Intent intent = getIntent();
        if ( intent != null ) {
            Bundle bundle = intent.getExtras();
            position = bundle.getInt("position");

            // 음악 기본정보를 설정해준다. (실행시간)
            // 이유: 첫 페이지가 아닐 경우 위의 setCurrentItem에 의해서 Viwpager의 onPageSelected가 호출된다.
            // 뷰페이지는 position을 갖고가며 실행시 0번에서 position번째 페이지로 이동하여 position번째를 여는 형식이다.
            // setCurrentItem을 통해서 이동하는 경우 0번에서 position으로 이동되기 때문에 setCurrentItem - onPageSelected 를 통해
            // initPlayerSetting 이 호출되는데 0번 일경우 onPageSelected를 호출하지 않기 때문에 initPlayerSetting이 호출되지 않는다.
            // 따라서 0번일때는 initPlayerSetting를 따로 호출해주고 0번이 아닐때는 onPageSeleted를 통해서 initPlayerSetting를 호출해줘야한다.
            if (position == 0) {
                initPlayerSetting();
            } else {
                // 실제 페이지로 이동하기 위한 계산처리
                mViewPager.setCurrentItem(position);
            }
        }
    }

    private void initPlayerSetting() {
        // 뷰 페이저로 이동할 경우 플레이어에 세팅된 값을 해제한 후 로직을 실행한다.
        if (player != null) {
            // 플레이 상태를 STOP으로 변경
            playStatus = STOP;
            // 아이콘을 플레이 버튼으로 변경
            player.release();

            mImgPlayBtn.setImageResource(android.R.drawable.ic_media_play);

        }
        Uri musicUri = datas.get(position).uri;
        player = MediaPlayer.create(this, musicUri); // 시스템파일 - context, 음원파일Uri . player를 최초 실행시키는 방법이다.
        player.setLooping(false);

        // seekbar 최고길이 설정
        mSeekBar.setMax(player.getDuration());
        // seekbar 현재 값 0으로 설정
        mSeekBar.setProgress(0);
        // 전체 플레이 시간 설정
        mTxtDuration.setText(covertMiliToTime(player.getDuration()));
        // 현재 플레이시간 0으로 설정
        mTxtCurrent.setText("0");

        // 미디어 플레이어에 완료체크 리스너를 등록한다.
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextMusic();
            }
        });
        playMusic();
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
        if (position > 0) {
            mViewPager.setCurrentItem(position-1);
        }
    }

    private void nextMusic() {
        if (position < datas.size()) {
            mViewPager.setCurrentItem(position+1);
        }
    }

    private void playMusic() {
        switch(playStatus) {
            case STOP:
                player.start();
                playStatus = PLAY;

                mImgPlayBtn.setImageResource(android.R.drawable.ic_media_pause);

                // sub thread를 생성해서 mediaplayer의 현재 포지션 값으로 seekbar를 변경해준다
                // 매1초마다 sub thread 에서 동작할 로직 정의
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        while (playStatus < STOP) {
                            if (player != null) {
                                // 하단의 부분이 메인스레드에서 동작하도록 Runnable 객체를 메인스레드에 던져준다
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 플레이어가 도중에 종료되면 예외가 발생하기 때문에 예외처리를 해준다.
                                        // try로 해주면 퍼포먼스가 안좋아지기 때문에 if로 처리
                                        try {
                                            mSeekBar.setProgress(player.getCurrentPosition());
                                            mTxtCurrent.setText(covertMiliToTime(player.getCurrentPosition()));
                                        } catch (Exception e) { e.printStackTrace(); }
                                    }
                                });
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                // 새로운 스레드 시작
                thread.start();
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

    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            PlayerActivity.this.position = position;
            initPlayerSetting();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    // seekbar를 이동하면 미디어가 이동하도록 변경
    SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        boolean k;
        int progress;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            k = fromUser;
            this.progress = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (player != null && k) {
                player.seekTo(progress);
            }
        }
    };

    private String covertMiliToTime(long mili){
        long min = mili / 1000 / 60;
        long sec = mili / 1000 % 60;


        return String.format("%02d", min) + ":" + String.format("%02d", sec);
    }
}
