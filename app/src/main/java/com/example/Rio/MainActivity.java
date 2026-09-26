package com.example.rio;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView outputText;
    private EditText inputText;
    private static final int OVERLAY_PERMISSION_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outputText = findViewById(R.id.outputText);
        inputText = findViewById(R.id.inputText);
        Button sendButton = findViewById(R.id.sendButton);
        Button micButton = findViewById(R.id.micButton);
        Button bubbleButton = findViewById(R.id.bubbleButton);

        sendButton.setOnClickListener(v -> {
            String cmd = inputText.getText().toString().trim().toLowerCase();
            if(cmd.isEmpty()) return;
            handleCommand(cmd);
        });

        micButton.setOnClickListener(v -> {
            Toast.makeText(this, "Mic V3 coming!", Toast.LENGTH_SHORT).show();
        });

        bubbleButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_CODE);
            } else {
                startService(new Intent(MainActivity.this, BubbleService.class));
                Toast.makeText(this, "Bubble started!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleCommand(String cmd) {
        outputText.append("\n> " + cmd);
        
        if (cmd.startsWith("open ")) {
            String appName = cmd.replace("open ", "").trim();
            openApp(appName);
        } else if (cmd.contains("whatsapp")) {
            openApp("whatsapp");
        } else if (cmd.contains("tiktok")) {
            openApp("tiktok");
        } else {
            outputText.append("\nRIO: Say 'open WhatsApp'");
        }
    }

    private void openApp(String appName) {
        PackageManager pm = getPackageManager();
        List apps = pm.getInstalledApplications(0);
        for (Object app : apps) {
            String pkg = ((android.content.pm.ApplicationInfo) app).packageName.toLowerCase();
            String name = pm.getApplicationLabel((android.content.pm.ApplicationInfo) app).toString().toLowerCase();
            if (name.contains(appName) || pkg.contains(appName)) {
                Intent launch = pm.getLaunchIntentForPackage(((android.content.pm.ApplicationInfo) app).packageName);
                if (launch != null) {
                    startActivity(launch);
                    outputText.append("\nRIO: Opening " + name);
                    return;
                }
            }
        }
        outputText.append("\nRIO: App not found: " + appName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                startService(new Intent(this, BubbleService.class));
                Toast.makeText(this, "Bubble permission granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }
                                        }
