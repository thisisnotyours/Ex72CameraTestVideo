package com.suek.ex72cameratestvideo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    VideoView vv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vv= findViewById(R.id.vv);

        //동적퍼미션
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){   //줄여쓰기
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
            }
        }
    }//onCreate()

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 200:
                if(grantResults[0]==PackageManager.PERMISSION_DENIED){    //퍼미션을 거부할때= 비디오뷰인데 저장이 안될때
                    Toast.makeText(this, "앱 사용불가", Toast.LENGTH_SHORT).show();
                    finish();     //퍼미션을 거부하면 꺼버림
                }
                break;
        }
    }







    public void clickBtn(View view) {
        //버튼을 누르면 비디오촬영 화면 Camera 앱 실행
        Intent intent= new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, 50);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //비디오는 용량때문에 무조건 파일저장 방식임.
        //즉, 무조건 Uri 로 캡쳐된 동영상의 경로가 옴
        switch (requestCode){
            case 50:
                if(resultCode == RESULT_OK){  //비디오 촬영이 잘 됐으면 ok 면
                    Uri uri= data.getData();
                    vv.setVideoURI(uri);

                    //비디오뷰를 클릭했을때 아래쪽에 컨트롤바 올라오도록 지정해야함(play, pause 바)
                    MediaController mediaController= new MediaController(this);
                    mediaController.setAnchorView(vv);    //vv에 닻을내리다(놓여지는 '위치'를 vv에 놓음)
                    vv.setMediaController(mediaController);

                    //동영상은 로딩의 시간이 소요되므로
                    //로딩이 완료되는 것을 듣고 시작하는 것을 권장
                    vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            vv.start();      //준비가 됐으면 start
                        }
                    });
                }else {
                    Toast.makeText(this, "canceled", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
