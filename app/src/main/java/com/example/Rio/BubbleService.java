package com.rio.v4;
import android.app.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.*;
import android.speech.*;
import android.speech.tts.TextToSpeech;
import android.view.*;
import android.widget.TextView;
import java.util.Locale;
public class BubbleService extends Service implements TextToSpeech.OnInitListener {
    WindowManager wm; View bubble; SpeechRecognizer sr; TextToSpeech tts;
    Handler h = new Handler(Looper.getMainLooper());
    long startTime;
    @Override public void onCreate(){
        super.onCreate();
        startTime = System.currentTimeMillis();
        wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        TextView tv = new TextView(this);
        tv.setText("R"); tv.setTextSize(32); tv.setTextColor(0xFF000000);
        tv.setBackgroundColor(0xFFFF8800); tv.setPadding(30,20,30,20);
        WindowManager.LayoutParams p = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            Build.VERSION.SDK_INT>=26? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY: WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        p.gravity = android.view.Gravity.TOP|android.view.Gravity.LEFT; p.x=100; p.y=300;
        try{ wm.addView(tv,p); bubble=tv; }catch(Exception e){}
        Notification n = new Notification.Builder(this, createChannel())
           .setContentTitle("Rio listening...").setContentText("Say Rio to command. Auto stop in 4h")
           .setSmallIcon(android.R.drawable.ic_btn_speak_now).build();
        startForeground(1,n);
        tts = new TextToSpeech(this,this);
        startListeningLoop();
        h.postDelayed(()->stopSelf(), 4*60*60*1000); // 4h max
    }
    String createChannel(){
        String id="rio"; if(Build.VERSION.SDK_INT>=26){
            NotificationChannel c=new NotificationChannel(id,"Rio",NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(c);
        } return id;
    }
    void startListeningLoop(){
        try{
            sr = SpeechRecognizer.createSpeechRecognizer(this);
            sr.setRecognitionListener(new RecognitionListener(){
                public void onResults(Bundle b){
                    String txt = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0).toLowerCase();
                    if(txt.contains("rio")){ // KEY RULE
                        speak("At your service");
                        handleCommand(txt);
                    }
                    restartListen();
                }
                public void onError(int e){ restartListen(); }
                public void onReadyForSpeech(Bundle p){} public void onBeginningOfSpeech(){}
                public void onRmsChanged(float r){} public void onBufferReceived(byte[] b){}
                public void onEndOfSpeech(){} public void onEvent(int e, Bundle b){}
                public void onPartialResults(Bundle b){}
            });
            Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            sr.startListening(i);
        }catch(Exception e){ h.postDelayed(()->startListeningLoop(),2000); }
    }
    void restartListen(){ h.postDelayed(()->startListeningLoop(),1000); }
    void handleCommand(String cmd){
        try{
            if(cmd.contains("quick settings")){
                Intent i = new Intent(android.provider.Settings.ACTION_SETTINGS);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(i);
            } else if(cmd.contains("open")){
                String appName = cmd.substring(cmd.indexOf("open")+4).replace("rio","").trim();
                PackageManager pm=getPackageManager();
                Intent launch=null;
                for(android.content.pm.ApplicationInfo ai: pm.getInstalledApplications(0)){
                    String label = pm.getApplicationLabel(ai).toString().toLowerCase();
                    if(label.contains(appName) || appName.contains(label)){
                        launch=pm.getLaunchIntentForPackage(ai.packageName); if(launch!=null) break;
                    }
                }
                if(launch!=null){ launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(launch); }
                else { speak("App not found"); }
            }
        }catch(Exception e){ speak("I could not do that"); }
    }
    void speak(String s){ if(tts!=null) tts.speak(s, TextToSpeech.QUEUE_FLUSH,null,null); }
    @Override public void onInit(int i){}
    @Override public IBinder onBind(Intent intent){ return null; }
    @Override public void onDestroy(){ try{ if(bubble!=null) wm.removeView(bubble); if(sr!=null) sr.destroy(); }catch(Exception e){} super.onDestroy(); }
        }
