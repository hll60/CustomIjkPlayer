package com.linlin.videodemo4;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.linlin.videodemo4.media.AndroidMediaController;
import com.linlin.videodemo4.media.IRenderView;
import com.linlin.videodemo4.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by lin on 2016/4/29.
 */
public class Test1Activity extends AppCompatActivity {

    IjkVideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);

        videoView = (IjkVideoView) findViewById(R.id.videoView);

        AndroidMediaController mediaController = new AndroidMediaController(this);
        videoView.setMediaController(mediaController);
        videoView.toggleAspectRatio();
        videoView.setVideoPath("http://7xq1s0.com2.z0.glb.qiniucdn.com/1459773584962_764.mp4");
        videoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                videoView.start();
            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();

        if (videoView!=null || !videoView.isBackgroundPlayEnabled()) {
            videoView.stopPlayback();
            videoView.release(true);
            videoView.stopBackgroundPlay();
        } else {
            videoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }
}
