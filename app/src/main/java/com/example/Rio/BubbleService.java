package com.example.Rio;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class BubbleService extends Service {
    private WindowManager windowManager;
    private View bubbleView;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        ImageView image = new ImageView(this);
        image.setImageResource(android.R.drawable.presence_online);
        image.setBackgroundColor(0xFFFF6A00); // RIO orange
        image.setPadding(30,30,30,30);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            150, 150,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0; params.y = 100;

        image.setOnTouchListener(new View.OnTouchListener() {
            float x, y, tx, ty;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN: x = params.x; y = params.y; tx = event.getRawX(); ty = event.getRawY(); return true;
                    case MotionEvent.ACTION_MOVE: params.x = (int)(x + event.getRawX() - tx); params.y = (int)(y + event.getRawY() - ty); windowManager.updateViewLayout(bubbleView, params); return true;
                    case MotionEvent.ACTION_UP:
                        if(Math.abs(event.getRawX() - tx) < 10) {
                            Intent i = new Intent(BubbleService.this, MainActivity.class); i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(i);
                        }
                        return true;
                }
                return false;
            }
        });

        bubbleView = image;
        windowManager.addView(bubbleView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(bubbleView != null) windowManager.removeView(bubbleView);
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
                  }
