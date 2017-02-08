package choongyul.android.com.musicplayer;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;

    private final int REQ_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            init();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        if ( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            String permArr[] = {Manifest.permission.READ_EXTERNAL_STORAGE};

            requestPermissions(permArr, REQ_CODE);

        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( requestCode == REQ_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                Message.show("권한을 허용하지 않으시면 프로그램을 실행할 수 없습니다.", this);
                finish();
            }
        }
    }

    private void init() {
        Message.show("프로그램을 실행합니다.", this);

        listInit();

    }

    private void listInit () {
        // 3.2 리사이클러뷰 세팅
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        MusicAdapter adapter = new MusicAdapter(this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
