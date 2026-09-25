package com.example.Rio;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    TextView chatView;
    EditText input;
    Button sendBtn;
    ScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Simple UI built in code so you don't need extra xml
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(20,20,20,20);
        layout.setBackgroundColor(0xFF0F172A);

        TextView title = new TextView(this);
        title.setText("RIO - Your AI");
        title.setTextSize(24);
        title.setTextColor(0xFF38BDF8);
        title.setPadding(0,0,0,20);
        layout.addView(title);

        chatView = new TextView(this);
        chatView.setText("RIO: Hey moss! I'm RIO. I work 100% offline.\nAsk me anything.\n\n");
        chatView.setTextColor(0xFFFFFFFF);
        chatView.setTextSize(16);

        scroll = new ScrollView(this);
        scroll.addView(chatView);
        android.widget.LinearLayout.LayoutParams scrollParams = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        layout.addView(scroll, scrollParams);

        input = new EditText(this);
        input.setHint("Talk to RIO...");
        input.setTextColor(0xFFFFFFFF);
        input.setHintTextColor(0xFF94A3B8);
        input.setBackgroundColor(0xFF1E293B);
        input.setPadding(20,20,20,20);
        layout.addView(input);

        sendBtn = new Button(this);
        sendBtn.setText("SEND");
        sendBtn.setBackgroundColor(0xFF38BDF8);
        layout.addView(sendBtn);

        setContentView(layout);

        sendBtn.setOnClickListener(v -> {
            String q = input.getText().toString().trim();
            if(q.isEmpty()) return;
            chatView.append("\nYOU: " + q + "\n");
            String ans = getRioAnswer(q);
            chatView.append("RIO: " + ans + "\n");
            input.setText("");
            scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
        });
    }

    String getRioAnswer(String q) {
        q = q.toLowerCase();
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        if(q.contains("hello") || q.contains("hi")) return "Yo! What's good moss? It's " + time + " now.";
        if(q.contains("who are you")) return "I am RIO - built by you. Offline, fast, no data needed.";
        if(q.contains("time")) return "Current time is " + time;
        if(q.contains("capable") || q.contains("can you")) return "I can chat offline, do math, remember, tell time, give advice. Next we add VOICE and memory.";
        if(q.contains("love") || q.contains("bored")) return "I got you moss. We are building something big together.";
        if(q.contains("math") || q.matches(".*\\d.*[+\\-*/].*")) {
            try {
                return "I can calculate that - tell me like '2+2' and I'll add it soon in V2 with full calculator brain.";
            } catch(Exception e){ }
        }
        if(q.contains("thank")) return "Anytime moss. What next feature you want?";
        
        return "Sharp question. As RIO V1, I'm offline text mode. In V2 we add voice, image, and your custom superpowers. Tell me what to learn next?";
    }
  }
