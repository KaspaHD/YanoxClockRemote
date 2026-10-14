package com.uck.yanoxremote;

import android.app.Activity;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int CARRIER = 38000;
    private ConsumerIrManager ir;
    private TextView status;

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
        boolean ok = ir != null && ir.hasIrEmitter();
        status.setText(ok ? "IR 송신기 감지됨 · 38 kHz" : "표준 Android IR 송신기를 찾지 못했습니다");

        bind(R.id.power, POWER, "POWER"); bind(R.id.mode, MODE, "MODE");
        bind(R.id.alarm, ALARM, "ALARM"); bind(R.id.timer, TIMER, "TIMER");
        bind(R.id.up, UP, "UP"); bind(R.id.ss, SS, "S/S");
        bind(R.id.down, DOWN, "DOWN"); bind(R.id.clear, CLEAR, "CLEAR");
    }

    private void bind(int id, final int[] pattern, final String name) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                send(pattern, name);
            }
        });
    }

    private void send(int[] pattern, String name) {
        if (ir == null || !ir.hasIrEmitter()) {
            Toast.makeText(this, "IR 송신기를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            ir.transmit(CARRIER, pattern);
            status.setText(name + " 송신 · 38 kHz");
        } catch (Exception e) {
            status.setText("송신 오류: " + e.getClass().getSimpleName());
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
