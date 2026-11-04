package com.morningbrief.alarm;
import android.app.*;import android.os.*;import android.content.*;import android.speech.tts.*;import android.media.*;import java.util.*;
public class SpeechService extends Service implements TextToSpeech.OnInitListener {
 TextToSpeech tts;
 public IBinder onBind(Intent i){return null;}
 public void onCreate(){super.onCreate();
 NotificationManager nm=getSystemService(NotificationManager.class);
 nm.createNotificationChannel(new NotificationChannel("news","뉴스 알람",NotificationManager.IMPORTANCE_HIGH));
 Notification n=new Notification.Builder(this,"news").setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle("Morning Brief").setContentText("뉴스 음성 알람").build();
 startForeground(1,n);tts=new TextToSpeech(this,this);
 }
 public int onStartCommand(Intent i,int f,int id){return START_NOT_STICKY;}
 public void onInit(int status){if(status!=TextToSpeech.SUCCESS)return;tts.setLanguage(Locale.KOREAN);
 if(tts.getVoices()!=null)for(Voice v:tts.getVoices())if(v.getLocale().getLanguage().equals("ko")&&!v.isNetworkConnectionRequired()&&(v.getName().toLowerCase().contains("female")||v.getName().contains("여성"))){tts.setVoice(v);break;}
 tts.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build());
 tts.speak("좋은 아침입니다. 모닝 브리프 뉴스 음성 알람 테스트입니다. 현재는 뉴스 연결 전 음성 동작을 확인하는 버전입니다.",TextToSpeech.QUEUE_FLUSH,null,"alarm");
 }
 public void onDestroy(){if(tts!=null){tts.stop();tts.shutdown();}super.onDestroy();}
}