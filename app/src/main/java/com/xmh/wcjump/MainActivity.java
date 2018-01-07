package com.xmh.wcjump;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String IP = "192.168.42.178";

    // 悬浮框
    private int windowWidth;
    private int windowHeight;
    private boolean isWindowShowing = false;
    private View windowView;
    private TextView info;

    // 点击位置
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

        // 初始化悬浮窗
        initWindow();

        // 开启/关闭悬浮窗
        findViewById(R.id.v).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleWindow();
            }
        });
    }

    /**
     * 初始化悬浮窗
     */
    private void initWindow() {
        // layout params
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.format = PixelFormat.TRANSLUCENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.width = windowWidth;
        params.height = windowHeight;

        // inflate view
        windowView = View.inflate(this, R.layout.window, null);
        info = windowView.findViewById(R.id.info);
        windowView.setTag("max");
        windowView.setLayoutParams(params);

        // 监听悬浮窗触控
        windowView.findViewById(R.id.control).setOnTouchListener(new OnControlTouchListener());

        // 开启/关闭悬浮窗
        windowView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleWindow();
            }
        });

        // 折叠到左边
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
                    info.setVisibility(View.GONE);
                } else {
                    parent.setTag("max");
                    params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                    params.width = windowWidth;
                    params.height = windowHeight;
                    parent.findViewById(R.id.right).setVisibility(View.VISIBLE);
                    parent.findViewById(R.id.close).setVisibility(View.VISIBLE);
                    info.setVisibility(View.VISIBLE);
                }
                WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
                windowManager.updateViewLayout(parent, params);
            }
        });

        // 折叠到右边
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
                    info.setVisibility(View.GONE);
                } else {
                    parent.setTag("max");
                    params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                    params.width = windowWidth;
                    params.height = windowHeight;
                    parent.findViewById(R.id.left).setVisibility(View.VISIBLE);
                    parent.findViewById(R.id.close).setVisibility(View.VISIBLE);
                    info.setVisibility(View.VISIBLE);
                }
                WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
                windowManager.updateViewLayout(parent, params);
            }
        });
    }

    /**
     * 开启/关闭悬浮窗
     */
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

    /**
     * 向server发跳跃数据
     */
    private void sendJump(final int delta) {
        info.setTextColor(Color.YELLOW);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append("http://").append(IP).append("/jump");
                    sb.append("?x=").append(clickX);
                    sb.append("&y=").append((clickY));
                    sb.append("&d=").append(delta);

                    URL url = new URL(sb.toString());
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(false);
                    conn.setDoOutput(false);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("GET");

                    Log.e("xmh", "url:" + url);

                    int code = conn.getResponseCode();
                    if (code == 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                info.setTextColor(Color.GREEN);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                info.setTextColor(Color.RED);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (conn != null) conn.disconnect();
                }
            }
        }).start();
    }

    /**
     * 向server发校正数据
     */
    private void sendAdjust(final int delta) {
        info.setTextColor(Color.YELLOW);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append("http://").append(IP).append("/adjust");
                    sb.append("?d=").append(delta);

                    URL url = new URL(sb.toString());
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(false);
                    conn.setDoOutput(false);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("GET");

                    Log.e("xmh", "url:" + url);

                    int code = conn.getResponseCode();
                    if (code == 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                info.setTextColor(Color.GREEN);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                info.setTextColor(Color.RED);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 悬浮窗触控
     */
    private class OnControlTouchListener implements View.OnTouchListener {

        private static final int MODE_JUMP = 1;
        private static final int MODE_ADJUST = -MODE_JUMP;
        private int mode = MODE_JUMP;

        // click position
        private float startX;
        private float startY;
        private float endX;
        private float endY;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getActionMasked();
            if (action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_UP) {
                return false;
            }

            // start x y
            if (action == MotionEvent.ACTION_DOWN) {
                startX = motionEvent.getX();
                startY = motionEvent.getY();
            }
            // end x y
            if (action == MotionEvent.ACTION_UP) {
                endX = motionEvent.getX();
                endY = motionEvent.getY();

                if (mode == MODE_JUMP) {
                    int delta = (int) Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
                    // send to server
                    sendJump(delta);
                    mode = -mode;
                    info.setText("需要怎么调整");
                } else if (mode == MODE_ADJUST) {
                    int delta = (int) Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
                    if (endY > startY) {
                        delta = -delta;
                    }
                    // send to server
                    sendAdjust(delta);
                    mode = -mode;
                    info.setText("需要怎么跳");
                }
            }
            return true;
        }
    }

}
