package zhuoxin.eduz.myvideonews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.ButterKnife;
import zhuoxin.eduz.medioplayer.full.VideoViewActivity;

public class MainActivity extends AppCompatActivity {
    private Button bt;
    private Button btn_like;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt= (Button) findViewById(R.id.btnLocal);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              VideoViewActivity.open(MainActivity.this,getTestVideo1());
            }
        });
        btn_like=(Button)findViewById(R.id.btnLikes);
        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PartPlayActivity.class);
                startActivity(intent);
            }
        });
    }


    private String getTestVideo1(){
        return "http://o9ve1mre2.bkt.clouddn.com/raw_%E6%B8%A9%E7%BD%91%E7%94%B7%E5%8D%95%E5%86%B3%E8%B5%9B.mp4";
    }
}
