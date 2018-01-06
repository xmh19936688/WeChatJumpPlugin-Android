package com.xmh.wcjump;

import android.annotation.TargetApi;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAndGetPermission();


        findViewById(R.id.v).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("xmh", "touch");
                return false;
            }
        });

        // 获取屏幕尺寸
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        // 计算点击坐标
        int x = screenWidth / 2;
        int y = screenHeight * 5 / 6;

        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
//        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        params.format = PixelFormat.TRANSLUCENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.width = screenWidth;
        params.height = screenHeight * 2 / 3;

        View view = View.inflate(this, R.layout.window, null);
        view.findViewById(R.id.control).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getActionMasked();
                if (action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_UP) {
                    return false;
                }

                float x = motionEvent.getX();
                float y = motionEvent.getY();
                Log.e("xmh", "touch2||" + "action:" + MotionEvent.actionToString(action) + x + "*" + y);

                return true;
            }
        });
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
                windowManager.removeViewImmediate(view);
            }
        });
        windowManager.addView(view, params);

    }

    @TargetApi(23)
    private void checkAndGetPermission() {
        if (!Settings.canDrawOverlays(this)) {
        }
    }

}
