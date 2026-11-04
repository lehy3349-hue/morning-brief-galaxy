package com.morningbrief.alarm;
import android.content.*;import android.app.*;import android.os.*;import java.util.*;
public class AlarmReceiver extends BroadcastReceiver{
 public void onReceive(Context c,Intent i){
 try { if(Build.VERSION.SDK_INT>=26)c.startForegroundService(new Intent(c,SpeechService.class));else c.startService(new Intent(c,SpeechService.class)); } catch(Exception e){android.util.Log.e("MorningBrief","Unable to start speech",e);}
 if(i.getBooleanExtra("test_only",false))return;
 android.content.SharedPreferences p=c.getSharedPreferences("alarm",0);
 Calendar cal=Calendar.getInstance();cal.add(Calendar.DAY_OF_MONTH,1);cal.set(Calendar.HOUR_OF_DAY,p.getInt("hour",7));cal.set(Calendar.MINUTE,p.getInt("minute",0));cal.set(Calendar.SECOND,0);
 PendingIntent pi=PendingIntent.getBroadcast(c,1,new Intent(c,AlarmReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
 ((AlarmManager)c.getSystemService(Context.ALARM_SERVICE)).setAlarmClock(new AlarmManager.AlarmClockInfo(cal.getTimeInMillis(),PendingIntent.getActivity(c,2,new Intent(c,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE)),pi);
 }
}