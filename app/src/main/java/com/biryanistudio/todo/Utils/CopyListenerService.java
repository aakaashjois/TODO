package com.biryanistudio.todo.utils;

import android.animation.Animator;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.ui.MainActivity;

public class CopyListenerService extends Service implements
        ClipboardManager.OnPrimaryClipChangedListener {
    private final String TAG = CopyListenerService.class.getSimpleName();
    private ClipboardManager clipboardManager;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private TextView notifier;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        setupWindow();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(this);
        return START_STICKY;
    }

    @Override
    public void onPrimaryClipChanged() {
        ClipData clipData = clipboardManager.getPrimaryClip();
        clipboardManager.removePrimaryClipChangedListener(this);
        if (clipData.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
            String clipText = String.valueOf(clipData.getItemAt(0).getText());
            if (clipText.contains("//TODO") || clipText.contains("// TODO")
                    || clipText.contains("// todo") || clipText.contains("//todo")) {
                clipText = clipText.contains("//TODO") ? clipText.replace("//TODO", "") :
                        clipText.contains("// TODO") ? clipText.replace("// TODO", "") :
                                clipText.contains("//todo") ? clipText.replace("//todo", "") :
                                        clipText.replace("// todo", "");
                clipText = clipText.trim();
                if (!(clipText.trim().equals(""))) {
                    Log.i(TAG, "onPrimaryClipChanged: " + clipText);
                    saveTextToDatabase(clipText);
                }
            }
        }
    }

    private void setupWindow() {
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER;
        params.dimAmount = 0.3f;
        params.x = 0;
        params.y = 0;
    }

    private void saveTextToDatabase(String text) {
        long newRowId = DbTransactions.writeTask(this, text);
        if (newRowId != -1) {
            addNotifier();
            new CountDownTimer(3500, 3500) {

                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    removeNotifier();
                }
            }.start();

        }
        clipboardManager.addPrimaryClipChangedListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeNotifier();
    }

    private void addNotifier() {
        setupNotifier();
        windowManager.addView(notifier, params);
        notifier.animate()
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(1000).start();

    }

    private void setupNotifier() {
        notifier = new TextView(this);
        notifier.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_launcher),
                null,
                null);
        notifier.setCompoundDrawablePadding((int) getResources()
                .getDimension(R.dimen.list_item_horizontal_margin));
        notifier.setTextColor(ContextCompat
                .getColor(getApplicationContext(), R.color.colorPrimaryDark));
        notifier.setBackground(ContextCompat
                .getDrawable(getApplicationContext(), R.drawable.notifier_background));
        notifier.setPaddingRelative(
                (int) getResources().getDimension(R.dimen.activity_horizontal_margin),
                (int) getResources().getDimension(R.dimen.activity_vertical_margin),
                (int) getResources().getDimension(R.dimen.activity_horizontal_margin),
                (int) getResources().getDimension(R.dimen.activity_vertical_margin));
        notifier.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            }
        });
        notifier.setText(getResources().getString(R.string.todo_added));
        notifier.setAlpha(0f);
    }

    private void removeNotifier() {
        if (notifier != null && ViewCompat.isAttachedToWindow(notifier)) {
            notifier.animate()
                    .alpha(0f)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(1000)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            windowManager.removeView(notifier);
                            notifier = null;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    })
                    .start();
        }
    }
}
