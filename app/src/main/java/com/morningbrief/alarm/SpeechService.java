package com.morningbrief.alarm;
import android.app.*;
import android.os.*;
import android.content.*;
import android.speech.tts.*;
import android.media.*;
import java.util.*;
public class SpeechService extends Service implements TextToSpeech.OnInitListener {
 private TextToSpeech tts;private boolean ready=false;private String pending=null;private boolean started=false;
 public IBinder onBind(Intent i){return null;}
 public void onCreate(){super.onCreate();
  NotificationManager nm=getSystemService(NotificationManager.class);
  nm.createNotificationChannel(new NotificationChannel("news","뉴스 알람",NotificationManager.IMPORTANCE_HIGH));
  Notification n=new Notification.Builder(this,"news").setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle("Morning Brief").setContentText("최신 뉴스 음성 브리핑 준비 중").build();
  startForeground(1,n);
  tts=new TextToSpeech(this,this);
 }
 public int onStartCommand(Intent i,int flags,int id){
  if(!started){started=true;
   new Thread(()->{String items=NewsRepository.fetch(this);String script=NewsRepository.briefing(items);
    new Handler(Looper.getMainLooper()).post(()->{pending=script;speakIfReady();});
   }).start();
  }
  return START_NOT_STICKY;
 }
 public void onInit(int status){
  if(status!=TextToSpeech.SUCCESS){stopSelf();return;}
  tts.setLanguage(Locale.KOREAN);
  Set<Voice> voices=tts.getVoices();
  if(voices!=null)for(Voice v:voices)if(v.getLocale().getLanguage().equals("ko")&&!v.isNetworkConnectionRequired()&&(v.getName().toLowerCase(Locale.ROOT).contains("male")&&!v.getName().toLowerCase(Locale.ROOT).contains("female")||v.getName().contains("남성"))){tts.setVoice(v);break;}
  tts.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build());
  tts.setSpeechRate(1.05f);\n  tts.setPitch(0.87f);
  tts.setOnUtteranceProgressListener(new UtteranceProgressListener(){
   public void onStart(String id){}
   public void onDone(String id){stopSelf();}
   public void onError(String id){stopSelf();}
  });
  ready=true;speakIfReady();
 }
 private void speakIfReady(){if(ready && pending!=null){String script=pending;pending=null;tts.speak(script,TextToSpeech.QUEUE_FLUSH,null,"morning-brief");}}
 public void onDestroy(){if(tts!=null){tts.stop();tts.shutdown();}super.onDestroy();}
}
