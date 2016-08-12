package zhuoxin.eduz.medioplayer.full;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import io.vov.vitamio.widget.MediaController;
import zhuoxin.eduz.medioplayer.R;

/**
 * Created by Administrator on 2016/8/11.
 */
public class CustomMedioCotraller extends MediaController{

    
    private TextView mediacontroller_file_name;


    public CustomMedioCotraller(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View makeControllerView() {
       View v=LayoutInflater.from(getContext()).inflate(R.layout.view_custom_video_controller,this,false);


        return v;

    }

    private void init() {
    }

}
