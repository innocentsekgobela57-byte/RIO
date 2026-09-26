package com.example.Rio;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText input;
    Button sendBtn, micBtn, bubbleBtn;
    TextView output;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = findViewById(R.id.inputText);
        sendBtn = findViewById(R.id.sendButton);
        micBtn = findViewById(R.id.micButton);
        bubbleBtn = findViewById(R.id.bubbleButton);
        output = findViewById(R.id.outputText);

        tts = new TextToSpeech(this, status -> {
            if(status == TextToSpeech.SUCCESS) tts.setLanguage(Locale.ENGLISH);
        });

        sendBtn.setOnClickListener(v -> handle(input.getText().toString()));
        
        micBtn.setOnClickListener(v -> {
            Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            startActivityForResult(i, 100);
        });

        bubbleBtn.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivity(i);
            } else {
                startService(new Intent(this, BubbleService.class));
                Toast.makeText(this, "RIO bubble activated!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handle(String cmd) {
        if(cmd.isEmpty()) return;
        String lower = cmd.toLowerCase();
        output.setText("You: " + cmd + "\nRIO thinking...");
        
        try {
            if(lower.startsWith("open ")) {
                String appName = lower.replace("open ", "").trim();
                if(openAnyApp(appName)) {
                    speak("Opening " + appName); return;
                } else {
                    speak("App " + appName + " not found"); return;
                }
            }
            if(lower.startsWith("call ")) {
                String name = cmd.substring(5).trim();
                callContact(name); return;
            }
            if(lower.contains("bluetooth")) {
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS)); speak("Opening bluetooth"); return;
            }
            if(lower.contains("wifi") || lower.contains("wi-fi")) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); speak("Opening wifi"); return;
            }
            if(lower.contains("flashlight") || lower.contains("torch")) {
                startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS)); speak("Flashlight control opened"); return;
            }
            if(lower.contains("data") || lower.contains("hotspot") || lower.contains("airplane")) {
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)); speak("Opening network settings"); return;
            }
            
            String reply = "I got you. You said: " + cmd + ". I can open any app, call any contact, and control settings. Try 'open TikTok' or 'call mom'";
            output.setText(reply);
            speak(reply);

        } catch (Exception e) {
            output.setText("Error: " + e.getMessage());
        }
    }

    private boolean openAnyApp(String name) {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        for(ApplicationInfo app : apps) {
            String appLabel = pm.getApplicationLabel(app).toString().toLowerCase();
            if(appLabel.contains(name)) {
                Intent launch = pm.getLaunchIntentForPackage(app.packageName);
                if(launch != null) { startActivity(launch); return true; }
            }
        }
        return false;
    }

    private void callContact(String name) {
        Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?", new String[]{"%" + name + "%"}, null);
        if(c != null && c.moveToFirst()) {
            String number = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            c.close();
            Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            startActivity(call);
            speak("Calling " + name);
        } else {
            speak("Contact " + name + " not found");
            if(c != null) c.close();
        }
    }

    private void speak(String text) {
        output.setText(text);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && data != null) {
            ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(res != null && !res.isEmpty()) {
                input.setText(res.get(0));
                handle(res.get(0));
            }
        }
    }
                             }
