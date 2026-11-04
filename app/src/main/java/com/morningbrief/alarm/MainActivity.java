package com.morningbrief.alarm;
import android.app.*;import android.os.*;import android.content.*;import android.provider.Settings;import android.webkit.*;import android.graphics.Color;import android.widget.*;import java.util.*;
public class MainActivity extends Activity {
 public void onCreate(Bundle b){super.onCreate(b);getWindow().addFlags(128); LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(28,48,28,20);
 TextView title=new TextView(this);title.setText("MORNING BRIEF v0.4\n여성 음성 뉴스 알람 테스트");title.setTextSize(23);root.addView(title);
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
 Button preview=new Button(this);preview.setText("실시간 화면 열기 / 새로고침");root.addView(preview);
 preview.setOnClickListener(v->showLivePreview());
 Button stop=new Button(this);stop.setText("음성 중지");root.addView(stop);stop.setOnClickListener(v->stopService(new Intent(this,SpeechService.class)));
 ScrollView scroll=new ScrollView(this);scroll.addView(root);setContentView(scroll);
 }
 private void showLivePreview(){
  LinearLayout layout=new LinearLayout(this);layout.setOrientation(1);layout.setBackgroundColor(Color.rgb(8,13,24));
  LinearLayout toolbar=new LinearLayout(this);toolbar.setOrientation(0);
  Button back=new Button(this);back.setText("← 알람 설정");toolbar.addView(back,new LinearLayout.LayoutParams(0,-2,1));
  Button refresh=new Button(this);refresh.setText("↻ 새로고침");toolbar.addView(refresh,new LinearLayout.LayoutParams(0,-2,1));
  layout.addView(toolbar);
  WebView web=new WebView(this);web.getSettings().setJavaScriptEnabled(true);web.getSettings().setDomStorageEnabled(true);
  web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
  web.setWebViewClient(new WebViewClient(){public void onReceivedError(WebView v,WebResourceRequest req,WebResourceError err){if(req.isForMainFrame())v.loadDataWithBaseURL(null,"<html><body style='background:#101a2d;color:white;font-family:sans-serif;padding:24px'><h2>미리보기 배포 준비 중</h2><p>GitHub Pages가 활성화되면 이 화면에서 바로 확인할 수 있습니다.</p></body></html>","text/html","UTF-8",null);}});
  layout.addView(web,new LinearLayout.LayoutParams(-1,0,1));
  back.setOnClickListener(v->recreate());
  refresh.setOnClickListener(v->web.loadUrl("https://lehy3349-hue.github.io/morning-brief-galaxy/?v="+System.currentTimeMillis()));
  setContentView(layout);refresh.performClick();
 }
}