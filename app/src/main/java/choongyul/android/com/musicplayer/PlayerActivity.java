package choongyul.android.com.musicplayer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    ImageButton mImgLeftBtn, mImgRightBtn, mImgPlayBtn;
    ViewPager mViewPager;
    List<Music> datas;
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
        initViewPager();

    }

    private void initViewPager(){
        // 1. 데이터 가져오기
        datas = DataLoader.getDatas(this);
        // 2. 뷰페이져 가져오기
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        // 3.뷰 페이져용 어댑터 생성
        adapter = new PlayerAdapter(this);
        // 4.뷰 페이져 어댑터 연결
        mViewPager.setAdapter(adapter);
        // 5.뷰 페이져 리스너 연결 ( 페이저 이동시 이동된 값을 seekbar와 길이 세팅)
        mViewPager.addOnPageChangeListener(viewPagerListener);
        // * 페이지 트랜스포머 연결
        mViewPager.setPageTransformer(false, pageTransformer);
        // 6. 특정페이지 호출
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

        initPlayer();
        initController();
        playMusic();
    }

    private void initPlayer() {
        Uri musicUri = datas.get(position).uri;
        player = MediaPlayer.create(this, musicUri); // 시스템파일 - context, 음원파일Uri . player를 최초 실행시키는 방법이다.
        player.setLooping(false);
        // 미디어 플레이어에 완료체크 리스너를 등록한다.
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextMusic();
            }
        });
    }

    private void initController() {
        // seekbar 최고길이 설정
        mSeekBar.setMax(player.getDuration());
        // seekbar 현재 값 0으로 설정
        mSeekBar.setProgress(0);
        // 전체 플레이 시간 설정
        mTxtDuration.setText(covertMiliToTime(player.getDuration()));
        // 현재 플레이시간 0으로 설정
        mTxtCurrent.setText("0");
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgPrevBtn:
                Log.d("여기는 왼쪽클릭" , "진입 했다.");
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
        } else {
            mViewPager.setCurrentItem(datas.size());
        }
    }

    private void nextMusic() {
        if (position < datas.size()) {
            mViewPager.setCurrentItem(position+1);
        } else {
            mViewPager.setCurrentItem(0);
        }
    }

    private void playMusic() {
        switch(playStatus) {
            case STOP:
                playStop();
                break;
            case PLAY:
                playPlay();
                break;
            case PAUSE:
                playPause();
                break;
        }
    }

    private void playStop() {
        player.start();
        playStatus = PLAY;

        mImgPlayBtn.setImageResource(android.R.drawable.ic_media_pause);

        // sub thread를 생성해서 mediaplayer의 현재 포지션 값으로 seekbar를 변경해준다
        // 매1초마다 sub thread 에서 동작할 로직 정의
        Thread thread = new timerThread();
        // 새로운 스레드 시작
        thread.start();
    }
    private void playPlay() {
        player.pause();
        mImgPlayBtn.setImageResource(android.R.drawable.ic_media_play);
        playStatus = PAUSE;
    }
    private void playPause() {
        player.seekTo(player.getCurrentPosition());
        player.start();
        playStatus = PLAY;
        mImgPlayBtn.setImageResource(android.R.drawable.ic_media_pause);
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

    class timerThread extends Thread {
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
                try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
    }

    ViewPager.PageTransformer pageTransformer = new ViewPager.PageTransformer() {
        @Override
        public void transformPage(View page, float position) {
            //현재 Page의 위치가 조금이라도 바뀔때마다 호출되는 메소드
            //첫번째 파라미터 : 현재 존재하는 View 객체들 중에서 위치가 변경되고 있는 View들
            //두번째 파라미터 : 각 View 들의 상대적 위치( 0.0 ~ 1.0 : 화면 하나의 백분율)

            //           1.현재 보여지는 Page의 위치가 0.0
            //           Page가 왼쪽으로 이동하면 값이 -됨. (완전 왼쪽으로 빠지면 -1.0)
            //           Page가 오른쪽으로 이동하면 값이 +됨. (완전 오른쪽으로 빠지면 1.0)

            //주의할 것은 현재 Page가 이동하면 동시에 옆에 있는 Page(View)도 이동함.
            //첫번째와 마지막 Page 일때는 총 2개의 View가 메모리에 만들어져 잇음.
            //나머지 Page가 보여질 때는 앞뒤로 2개의 View가 메모리에 만들어져 총 3개의 View가 instance 되어 있음.
            //ViewPager 한번에 1장의 Page를 보여준다면 최대 View는 3개까지만 만들어지며
            //나머지는 메모리에서 삭제됨.-리소스관리 차원.

            //position 값이 왼쪽, 오른쪽 이동방향에 따라 음수와 양수가 나오므로 절대값 Math.abs()으로 계산
            //position의 변동폭이 (-2.0 ~ +2.0) 사이이기에 부호 상관없이 (0.0~1.0)으로 변경폭 조절
            //주석으로 수학적 연산을 설명하기에는 한계가 있으니 코드를 보고 잘 생각해 보시기 바랍니다.
            float normalizedposition = Math.abs( 1 - Math.abs(position) );

            page.setAlpha(normalizedposition);  //View의 투명도 조절
            page.setScaleX(normalizedposition/2 + 0.5f); //View의 x축 크기조절
            page.setScaleY(normalizedposition/2 + 0.5f); //View의 y축 크기조절
            page.setRotationY(position * 80); //View의 Y축(세로축) 회전 각도
        }
    };
}

