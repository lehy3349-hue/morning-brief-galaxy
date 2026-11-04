package com.morningbrief.alarm;
import android.app.*;import android.os.*;import android.content.*;import android.provider.Settings;import android.widget.*;import java.util.*;
public class MainActivity extends Activity {
 public void onCreate(Bundle b){super.onCreate(b);getWindow().addFlags(128); LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(28,48,28,20);
 TextView title=new TextView(this);title.setText("MORNING BRIEF v0.3\n여성 음성 뉴스 알람 테스트");title.setTextSize(23);root.addView(title);
 TimePicker picker=new TimePicker(this);picker.setIs24HourView(true);root.addView(picker);
 TextView status=new TextView(this);status.setText("화면을 끈 상태에서도 테스트하세요.");root.addView(status);
 Button set=new Button(this);set.setText("매일 뉴스 알람 설정");root.addView(set);
 set.setOnClickListener(v->{AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);if(Build.VERSION.SDK_INT>=31&&!am.canScheduleExactAlarms()){startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));return;}
 Calendar c=Calendar.getInstance();c.set(Calendar.HOUR_OF_DAY,picker.getHour());c.set(Calendar.MINUTE,picker.getMinute());c.set(Calendar.SECOND,0);if(c.getTimeInMillis()<=System.currentTimeMillis())c.add(Calendar.DAY_OF_MONTH,1);
 getSharedPreferences("alarm",0).edit().putInt("hour",picker.getHour()).putInt("minute",picker.getMinute()).apply();
 PendingIntent pi=PendingIntent.getBroadcast(this,1,new Intent(this,AlarmReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
 am.setAlarmClock(new AlarmManager.AlarmClockInfo(c.getTimeInMillis(),PendingIntent.getActivity(this,2,new Intent(this,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE)),pi);status.setText("설정 완료: "+picker.getHour()+":"+picker.getMinute());});
 Button test=new Button(this);test.setText("지금 여성 음성 테스트");root.addView(test);test.setOnClickListener(v->startService(new Intent(this,SpeechService.class)));
 Button minute=new Button(this);minute.setText("1분 뒤 화면 꺼짐 알람 테스트");root.addView(minute);
 minute.setOnClickListener(v->{AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);if(Build.VERSION.SDK_INT>=31&&!am.canScheduleExactAlarms()){startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));return;}
 PendingIntent pi=PendingIntent.getBroadcast(this,3,new Intent(this,AlarmReceiver.class).putExtra("test_only",true),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
 am.setAlarmClock(new AlarmManager.AlarmClockInfo(System.currentTimeMillis()+60000,PendingIntent.getActivity(this,4,new Intent(this,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE)),pi);
 status.setText("1분 후 테스트 알람 예정. 화면을 꺼서 확인하세요.");});
 Button preview=new Button(this);preview.setText("웹 디자인 미리보기 열기");root.addView(preview);
 preview.setOnClickListener(v->startActivity(new Intent(Intent.ACTION_VIEW,android.net.Uri.parse("https://lehy3349-hue.github.io/morning-brief-galaxy/"))));
 Button stop=new Button(this);stop.setText("음성 중지");root.addView(stop);stop.setOnClickListener(v->stopService(new Intent(this,SpeechService.class)));
 ScrollView scroll=new ScrollView(this);scroll.addView(root);setContentView(scroll);
 }
}