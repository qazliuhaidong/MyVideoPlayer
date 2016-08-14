package zhuoxin.eduz.myvideonews;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import butterknife.BindView;
import io.vov.vitamio.Vitamio;
import zhuoxin.eduz.medioplayer.full.VideoViewActivity;
import zhuoxin.eduz.medioplayer.full.part.SimpVideoView;


/**
 * Created by Administrator on 2016/8/11.
 */
public class PartPlayActivity extends AppCompatActivity{

    SimpVideoView simp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_play);
        Vitamio.isInitialized(this);
        simp= (SimpVideoView) findViewById(R.id.simpVideoPlayer);
        simp.setVideoPath(getTestVideo1());

    }

    @Override
    protected void onResume() {
        super.onResume();
        simp.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        simp.onPause();
    }
    private String getTestVideo1(){
        return "http://o9ve1mre2.bkt.clouddn.com/raw_%E6%B8%A9%E7%BD%91%E7%94%B7%E5%8D%95%E5%86%B3%E8%B5%9B.mp4";
    }
}
