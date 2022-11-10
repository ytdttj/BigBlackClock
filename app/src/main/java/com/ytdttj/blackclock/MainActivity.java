package com.ytdttj.blackclock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private Handler mHandler;
    private static final int MSG_UPDATE_CURRENT_TIME = 1;
    private TextView tv_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_time = (TextView) this.findViewById(R.id.textViewTime);
        mHandler = new MyHandler(this);
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_CURRENT_TIME, 500);

    }
    //
    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            switch (msg.what) {
                case MSG_UPDATE_CURRENT_TIME:
                    activity.updateCurrentTime();
                    sendEmptyMessageDelayed(MSG_UPDATE_CURRENT_TIME, 100);//设置文本框内时间的更新速度
                    break;
                default:
                    break;
            }
        }
    }
    private void updateCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");//设置输出格式
        Date curDate = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(curDate);
        tv_time.setText(time);
    }

}