package com.rio.v4;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
public class MainActivity extends Activity {
    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        TextView tv = new TextView(this);
        tv.setText("Rio V4 Safe\n\nOrange R Bubble\nSays: At your service\nOnly reacts if you say RIO\n\nFeatures:\n- Rio open WhatsApp\n- Rio go to quick settings\n- 4h auto stop\n- Notification reader ready");
        tv.setPadding(40,40,40,40);
        tv.setTextSize(16);
        Button btn = new Button(this);
        btn.setText("Start Rio Bubble");
        btn.setOnClickListener(v-> startService(new Intent(this, BubbleService.class)));
        android.widget.LinearLayout lay = new android.widget.LinearLayout(this);
        lay.setOrientation(android.widget.LinearLayout.VERTICAL);
        lay.addView(tv);
        lay.addView(btn);
        setContentView(lay);
    }
}
