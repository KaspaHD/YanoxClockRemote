package com.uck.yanoxremote;

import android.app.Activity;
import android.hardware.ConsumerIrManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int CARRIER = 38000;
    private ConsumerIrManager ir;
    private TextView status;
    private IControl quickSet;
    private boolean quickSetBound = false;
    private final Handler repeatHandler = new Handler();
    private static final long LONG_PRESS_DELAY_MS = 350;
    // The learned Broadlink command already shows the real protocol timing:
    // full frame -> ~42.8 ms gap -> repeat frame (about 9.8/2.4/0.65 ms).
    // For a true hold we send ONE continuous waveform through QuickSet, rather
    // than separate Binder transmit() calls which insert an unwanted break.
    private static final int LONG_REPEAT_COUNT = 18;
    private static final int FULL_FRAME_LENGTH = 67;
    private static final int FIRST_REPEAT_GAP_US = 42790;
    private static final int REPEAT_MARK_US = 9780;
    private static final int REPEAT_SPACE_US = 2400;
    private static final int REPEAT_END_MARK_US = 656;
    private static final int REPEAT_GAP_US = 97000;

    private static final int[] POWER = {9687,4827,656,558,656,558,656,558,656,591,623,591,623,591,623,591,656,558,656,1773,656,1806,623,1806,656,1773,656,1806,623,1806,623,1806,656,1806,656,558,623,591,656,1773,656,1773,689,1773,656,558,623,591,656,558,656,1773,689,1773,656,558,656,558,656,558,656,1773,689,1773,656,1773,656,42790,9753,2397,656,109455};
    private static final int[] MODE = {9720,4827,656,558,656,558,656,558,656,558,689,558,656,558,656,558,656,558,656,1806,623,1806,656,1773,656,1773,656,1806,656,1773,656,1773,656,1773,689,558,656,558,656,558,656,558,656,558,656,591,656,1773,656,558,656,1773,656,1806,656,1773,656,1773,656,1773,656,1806,656,558,656,1773,656,42790,9786,2397,656,109455};
    private static final int[] ALARM = {9720,4827,656,558,656,558,656,558,689,525,689,558,656,558,656,558,656,558,656,1773,689,1773,656,1773,656,1773,689,1773,656,1773,656,1773,689,1773,656,558,656,558,656,558,656,1773,689,558,656,558,656,558,656,558,656,1773,689,1773,656,1773,656,558,656,1773,689,1773,656,1773,656,1773,689,42790,9753,2397,656,109455};
    private static final int[] TIMER = {9720,4827,623,591,656,558,656,558,656,558,656,591,623,591,623,591,623,591,623,1806,656,1806,623,1806,623,1806,656,1773,656,1806,623,1806,656,1773,656,558,689,558,623,1806,623,591,623,591,656,558,656,1806,623,591,656,1773,656,1773,656,591,623,1806,656,1773,656,1806,623,591,623,1806,656,42757,9753,2430,656,109455};
    private static final int[] UP = {9720,4794,656,591,656,558,656,558,656,558,656,558,656,558,689,558,656,558,656,1773,656,1773,689,1773,656,1773,656,1773,689,1773,656,1773,656,1773,689,558,656,558,656,558,656,1773,656,1806,656,558,656,558,656,558,656,1806,656,1773,656,1773,656,558,656,591,656,1773,656,1773,656,1773,689,42790,9786,2397,656,109455};
    private static final int[] DOWN = {9687,4827,656,558,656,558,656,591,656,558,656,558,656,558,656,558,656,558,689,1773,656,1773,656,1773,656,1806,656,1773,656,1773,656,1806,656,1773,656,558,656,1773,689,1773,656,1773,656,1773,656,558,689,1773,656,558,656,1773,656,558,689,558,656,558,656,558,656,1773,689,525,689,1773,656,42790,9753,2430,623,109455};
    private static final int[] SS = {9753,4794,656,558,656,558,656,558,656,558,689,558,656,558,656,558,656,558,656,1773,689,1773,656,1773,656,1773,689,1773,656,1773,656,1773,689,1773,656,558,656,1773,656,558,689,558,656,1773,656,558,656,1773,689,558,656,1773,656,558,656,1773,689,1773,656,558,656,1773,656,558,656,1806,656,42790,9786,2397,656,109455};
    private static final int[] CLEAR = {9753,4794,656,558,656,558,656,558,656,591,656,558,656,558,656,558,656,558,656,1773,689,1773,656,1773,656,1773,689,1773,656,1773,656,1773,656,1806,656,558,656,1773,656,558,656,1806,656,558,656,558,656,1773,656,558,689,1773,656,558,656,1773,656,558,656,1806,656,1773,656,558,656,1773,689,42790,9753,2397,656,109455};

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = (TextView)findViewById(R.id.status);
        ir = (ConsumerIrManager)getSystemService(CONSUMER_IR_SERVICE);
        status.setText("LeEco QuickSet 연결 중...");
        bindQuickSet();

        bind(R.id.power, POWER, "POWER"); bind(R.id.mode, MODE, "MODE");
        bind(R.id.alarm, ALARM, "ALARM"); bind(R.id.timer, TIMER, "TIMER");
        bind(R.id.up, UP, "UP"); bind(R.id.ss, SS, "S/S");
        bind(R.id.down, DOWN, "DOWN"); bind(R.id.clear, CLEAR, "CLEAR");
    }

    private void bind(int id, final int[] pattern, final String name) {
        final View button = findViewById(id);
        button.setOnTouchListener(new View.OnTouchListener() {
            private boolean pressed = false;
            private boolean longSent = false;

            private final Runnable longPressSender = new Runnable() {
                @Override public void run() {
                    if (!pressed) return;
                    longSent = true;
                    send(buildLongPattern(pattern), name + " LONG");
                }
            };

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        pressed = true;
                        longSent = false;
                        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                        repeatHandler.removeCallbacks(longPressSender);
                        repeatHandler.postDelayed(longPressSender, LONG_PRESS_DELAY_MS);
                        return true;

                    case MotionEvent.ACTION_UP:
                        pressed = false;
                        repeatHandler.removeCallbacks(longPressSender);
                        if (!longSent) send(pattern, name);
                        v.performClick();
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE:
                        pressed = false;
                        repeatHandler.removeCallbacks(longPressSender);
                        return true;
                }
                return true;
            }
        });
    }

    private int[] buildLongPattern(int[] learnedPattern) {
        // First 67 timings are the complete command frame including its final mark.
        // Then reproduce the repeat frame continuously inside the SAME transmit call.
        int firstLen = Math.min(FULL_FRAME_LENGTH, learnedPattern.length);
        int[] out = new int[firstLen + 1 + LONG_REPEAT_COUNT * 4];
        int pos = 0;
        for (int i = 0; i < firstLen; i++) out[pos++] = learnedPattern[i];
        out[pos++] = FIRST_REPEAT_GAP_US;
        for (int i = 0; i < LONG_REPEAT_COUNT; i++) {
            out[pos++] = REPEAT_MARK_US;
            out[pos++] = REPEAT_SPACE_US;
            out[pos++] = REPEAT_END_MARK_US;
            out[pos++] = REPEAT_GAP_US;
        }
        return out;
    }

    private void send(int[] pattern, String name) {
        // LeEco EUI: use UEI QuickSet first. This bypasses the ROM's restricted ConsumerIrManager.
        if (quickSetBound && quickSet != null) {
            try {
                int result = quickSet.transmit(CARRIER, pattern);
                int last = quickSet.getLastResultcode();
                status.setText(name + " 송신 · QuickSet (" + result + "/" + last + ")");
                return;
            } catch (Exception e) {
                status.setText("QuickSet 오류: " + e.getClass().getSimpleName());
            }
        }

        // Fallback for normal Android IR devices.
        try {
            if (ir == null || !ir.hasIrEmitter()) throw new IllegalStateException("IR 송신기 없음");
            ir.transmit(CARRIER, pattern);
            status.setText(name + " 송신 · Android IR");
        } catch (Exception e) {
            status.setText("송신 오류: " + e.getClass().getSimpleName());
            Toast.makeText(this, "QuickSet 미연결 / " + e.toString(), Toast.LENGTH_LONG).show();
            bindQuickSet();
        }
    }

    private final ServiceConnection quickSetConnection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            quickSet = new IControl(service);
            quickSetBound = true;
            status.setText("LeEco QuickSet 연결됨 · 38 kHz");
        }
        @Override public void onServiceDisconnected(ComponentName name) {
            quickSetBound = false;
            quickSet = null;
            status.setText("LeEco QuickSet 연결 끊김");
        }
    };

    private void bindQuickSet() {
        try {
            Intent i = new Intent(IControl.ACTION);
            i.setClassName(IControl.QUICKSET_UEI_PACKAGE_NAME, IControl.QUICKSET_UEI_SERVICE_CLASS);
            boolean ok = bindService(i, quickSetConnection, Context.BIND_AUTO_CREATE);
            if (!ok) status.setText("QuickSet 서비스를 찾지 못했습니다");
        } catch (Throwable t) {
            status.setText("QuickSet 연결 오류: " + t.getClass().getSimpleName());
        }
    }

    @Override protected void onDestroy() {
        repeatHandler.removeCallbacksAndMessages(null);
        if (quickSetBound) {
            try { unbindService(quickSetConnection); } catch (Exception ignored) {}
        }
        super.onDestroy();
    }
}
