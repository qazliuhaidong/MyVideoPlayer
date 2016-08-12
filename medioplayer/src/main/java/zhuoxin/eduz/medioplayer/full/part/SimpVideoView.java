package zhuoxin.eduz.medioplayer.full.part;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewGroupCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import zhuoxin.eduz.medioplayer.R;
import zhuoxin.eduz.medioplayer.full.VideoViewActivity;

/**
 * Created by Administrator on 2016/8/11.
 */
public class SimpVideoView extends FrameLayout{
//    public SimpVideoView(Context context) {
//        this(context, null);
//    }

    public SimpVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public SimpVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();

    }

    private String videoPath;

    private ImageView ivPreview;
    private ImageButton btnToggle;
    private ProgressBar progressBar;
    private TextView tvLoading;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private MediaPlayer mediaPlayer;

    private boolean isPrepared; // 当前视频是否已准备好(防止在还未prepared就进行play)
    private boolean isPlaying; // 当前视频是否正在播放(主要将在更新播放进度时用到)

    private static final int PROGRESS_MAX = 1000;

    private void initView() {
        Vitamio.isInitialized(getContext());
        LayoutInflater.from(getContext()).inflate(R.layout.view_simple_video_player, this, true);
        // surfaceview的初始化
        initSurfaceView();
        // 控制视图的初始化
        initControllerViews();

    }

    // 用来更新播放进度的handler
    private final Handler handler = new Handler(){
        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(isPlaying){
                long current = mediaPlayer.getCurrentPosition();
                long duration = mediaPlayer.getDuration();
                int progress = (int) (current * PROGRESS_MAX / duration);
                // 更新当前播放进度的进度条
                progressBar.setProgress(progress);
                // 每200毫秒，再更新一次
                handler.sendEmptyMessageDelayed(0, 200);
            }
        }
    };
    /**
     * 设置播放谁(一定要在onResume方法调用前来调用):
     */
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    /**
     * 初始化MediaPlayer,准备MediaPlayer(和activity的onResume同步执行):
     */
    public void onResume() {
        // 初始化mediaplayer及监听处理
        initMediaPlayer();
        // 设置资源进行准备
        prepareMediaPlayer();
    }

    /**
     * 暂停mediaPlayer,释放MediaPlayer(和activity的onPasuse同步执行):
     */
    public void onPause() {
        pauseMediaPlayer();
        releaseMediaPlayer();
    }

    // 初始化SurfaceView
    private void initSurfaceView() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        // 注意：Vitamio在使用SurfaceView播放时,要fotmat
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
    }

    // 初始化自定义的简单的视频播放控制视图
    private void initControllerViews() {
        tvLoading = (TextView) findViewById(R.id.tvLoading);
        // 预览图片
        ivPreview = (ImageView) findViewById(R.id.ivPreview);
        // 播放/暂停 按钮
        btnToggle = (ImageButton) findViewById(R.id.btnToggle);
        btnToggle.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    pauseMediaPlayer();
                } else if (isPrepared) {
                    startMediaPlayer();
                } else {
                    Toast.makeText(getContext(), "Can't play now !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 进度条
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(PROGRESS_MAX);
        // 全屏按钮
        ImageButton btnFullScreen = (ImageButton) findViewById(R.id.btnFullScreen);
        btnFullScreen.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                VideoViewActivity.open(getContext(), videoPath);
            }
        });
    }

    // 初始化MediaPlayer, 设置一系列的监听
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer(getContext());
        mediaPlayer.setDisplay(surfaceHolder);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                // 准备好后，自动开始播放
                startMediaPlayer();
            }
        });
        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                int videoWidth=surfaceView.getWidth();
                int videoHeight=width*height/width;
                ViewGroup.LayoutParams layoutParams=surfaceView.getLayoutParams();
                layoutParams.width=videoWidth;
                layoutParams.height=videoHeight;
                surfaceView.setLayoutParams(layoutParams);
            }
        });
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                    return true;
                }
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END){
                    return true;
                }
                if (what == MediaPlayer.MEDIA_INFO_FILE_OPEN_OK) {
                    // 注意：Vitamio5.0 要对音频进行设置才能播放
                    // 否则，不能播放在线视频
                    long bufferSize = mediaPlayer.audioTrackInit();
                    mediaPlayer.audioInitedOk(bufferSize);
                    return true;
                }
                return false;
            }
        });
    }

    // 准备MediaPlayer, 同时更新UI状态
    private void prepareMediaPlayer() {
        try {
            mediaPlayer.reset();
            // 设置资源
            mediaPlayer.setDataSource(videoPath);
            mediaPlayer.setLooping(true);
            // 异步准备
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 开始MediaPlayer, 同时更新UI状态
    private void startMediaPlayer() {
        // 是否已准备好，防止在还未prepared,用户就按下play进行播放
        if (isPrepared) {
            mediaPlayer.start();
        }
        isPlaying = true;
        // 更新UI（进度条）
        handler.sendEmptyMessage(0);
        // 播放和暂停按钮图像的更新
        btnToggle.setImageResource(R.drawable.ic_pause);
    }

    // 暂停mediaPlayer, 同时更新UI状态
    private void pauseMediaPlayer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        isPlaying = false;
        handler.removeMessages(0);
        // 播放和暂停按钮图像的更新
        btnToggle.setImageResource(R.drawable.ic_play_arrow);
    }

    // 释放mediaPlayer, 同时更新UI状态
    private void releaseMediaPlayer() {
        mediaPlayer.release();
        mediaPlayer = null;
        isPlaying = false;
        isPrepared = false;
    }

}
