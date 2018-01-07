package com.xmh.wcjump;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private int windowWidth;
    private int windowHeight;
    private View windowView;
    private boolean isWindowShowing = false;

    private int clickX;
    private int clickY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 计算窗口尺寸
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        windowWidth = screenWidth;
        windowHeight = screenHeight * 2 / 3;

        // 计算点击坐标
        clickX = screenWidth / 2;
        clickY = screenHeight * 5 / 6;

        initWindow();

        findViewById(R.id.v).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleWindow();
            }
        });
    }

    private void initWindow() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.format = PixelFormat.TRANSLUCENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.width = windowWidth;
        params.height = windowHeight;

        windowView = View.inflate(this, R.layout.window, null);
        windowView.setTag("max");
        windowView.setLayoutParams(params);

        windowView.findViewById(R.id.control).setOnTouchListener(new View.OnTouchListener() {
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
        windowView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleWindow();
            }
        });
        windowView.findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parent = (View) v.getParent();
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) parent.getLayoutParams();
                if (parent.getTag().equals("max")) {
                    parent.setTag("min");
                    params.gravity = Gravity.LEFT | Gravity.TOP;
                    params.width = v.getWidth();
                    params.height = v.getHeight();
                    parent.findViewById(R.id.right).setVisibility(View.GONE);
                    parent.findViewById(R.id.close).setVisibility(View.GONE);
                } else {
                    parent.setTag("max");
                    params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                    params.width = windowWidth;
                    params.height = windowHeight;
                    parent.findViewById(R.id.right).setVisibility(View.VISIBLE);
                    parent.findViewById(R.id.close).setVisibility(View.VISIBLE);
                }
                WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
                windowManager.updateViewLayout(parent, params);
            }
        });
        windowView.findViewById(R.id.right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parent = (View) v.getParent();
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) parent.getLayoutParams();
                if (parent.getTag().equals("max")) {
                    parent.setTag("min");
                    params.gravity = Gravity.RIGHT | Gravity.TOP;
                    params.width = v.getWidth();
                    params.height = v.getHeight();
                    parent.findViewById(R.id.left).setVisibility(View.GONE);
                    parent.findViewById(R.id.close).setVisibility(View.GONE);
                } else {
                    parent.setTag("max");
                    params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                    params.width = windowWidth;
                    params.height = windowHeight;
                    parent.findViewById(R.id.left).setVisibility(View.VISIBLE);
                    parent.findViewById(R.id.close).setVisibility(View.VISIBLE);
                }
                WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
                windowManager.updateViewLayout(parent, params);
            }
        });
    }

    private void toggleWindow() {
        if (isWindowShowing) {
            isWindowShowing = false;
            WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
            windowManager.removeViewImmediate(windowView);
        } else {
            isWindowShowing = true;
            WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
            windowManager.addView(windowView, windowView.getLayoutParams());
        }
    }

}
