package com.morningbrief.alarm;
import android.app.*;
import android.content.*;
import android.os.Build;
import java.util.Calendar;
public class BootReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context context,Intent intent){
  if(!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))return;
  android.content.SharedPreferences prefs=context.getSharedPreferences("alarm",0);
  if(!prefs.getBoolean("enabled",false))return;
  AlarmManager manager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
  if(Build.VERSION.SDK_INT>=31&&!manager.canScheduleExactAlarms())return;
  Calendar c=Calendar.getInstance();
  c.set(Calendar.HOUR_OF_DAY,prefs.getInt("hour",7));c.set(Calendar.MINUTE,prefs.getInt("minute",0));c.set(Calendar.SECOND,0);c.set(Calendar.MILLISECOND,0);
  if(c.getTimeInMillis()<=System.currentTimeMillis())c.add(Calendar.DAY_OF_MONTH,1);
  PendingIntent pi=PendingIntent.getBroadcast(context,1,new Intent(context,AlarmReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  PendingIntent show=PendingIntent.getActivity(context,2,new Intent(context,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  manager.setAlarmClock(new AlarmManager.AlarmClockInfo(c.getTimeInMillis(),show),pi);
 }
}