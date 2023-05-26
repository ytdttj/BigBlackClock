package com.ytdttj.bigblackclock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{

    private TextView timeTextView;
    private TextView appRunningTimeTextView;
    private TextView batteryTextView;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateTimeRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            updateTime();
            updateAppRunningTime();
            handler.postDelayed(this, 1000); // 每隔一秒更新时间
        }
    };
    private long appStartTime;
    private float initialEnergy = -1;
    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                if (scale != -1) {
                    float batteryPercentage = (level / (float) scale) * 100;

                    if (initialEnergy == -1) {
                        // 第一次获取电量信息时记录初始电量百分比
                        initialEnergy = batteryPercentage;
                    } else {
                        // 计算已消耗的电量百分比
                        float consumedPercentage = initialEnergy - batteryPercentage;
                        batteryTextView.setText("Consumed Battery: " + consumedPercentage + "%");
                    }
                }
            }
        }
    };

    private void getInitialBatteryLevel() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, filter);
        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            if (scale != -1) {
                initialEnergy = (level / (float) scale) * 100;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //沉浸状态栏
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        //沉浸导航条
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window1 = getWindow();
            View decorView = window1.getDecorView();
            int flags = decorView.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(flags);
            window1.setStatusBarColor(Color.TRANSPARENT);
            window1.setNavigationBarColor(Color.TRANSPARENT);
        }

        timeTextView = findViewById(R.id.timeTextView);
        appRunningTimeTextView = findViewById(R.id.textView2);
        batteryTextView = findViewById(R.id.batteryTextView);
        handler.post(updateTimeRunnable);
        appStartTime = SystemClock.elapsedRealtime(); // 记录应用程序启动时间

        //getBatteryLevel(); // 获取初始电量并显示
        getInitialBatteryLevel(); // 获取初始电量并保存

        // 注册电池电量变化的广播接收器
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // 停止更新时间的Runnable
        handler.removeCallbacks(updateTimeRunnable);
        unregisterReceiver(batteryReceiver);
    }


    private void updateTime()
    {
        // 获取当前时间
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String time = sdf.format(new Date(currentTimeMillis));

        // 更新TextView显示的时间
        timeTextView.setText(time);
    }
    private void updateAppRunningTime() {
        long currentTimeMillis = SystemClock.elapsedRealtime(); // 获取当前时间
        long runningTimeMillis = currentTimeMillis - appStartTime; // 计算应用程序已运行的时间（毫秒）

        // 将毫秒转换为小时、分钟和秒
        int hours = (int) (runningTimeMillis / (1000 * 60 * 60));
        int minutes = (int) ((runningTimeMillis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((runningTimeMillis % (1000 * 60)) / 1000);

        // 格式化显示的时间
        String runningTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        // 更新TextView显示的应用程序已运行的时间
        appRunningTimeTextView.setText("Run Time: "+runningTime);
    }

    /*private void getBatteryLevel() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, filter);
        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            if (scale != -1) {
                int batteryLevel = (int) ((level / (float) scale) * 100);
                batteryTextView.setText(batteryLevel + "%");
            }
        }
    }*/

}