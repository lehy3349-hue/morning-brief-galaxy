package com.morningbrief.alarm;
import android.app.*;
import android.os.*;
import android.content.*;
import android.provider.Settings;
import android.webkit.*;
import android.widget.*;
import android.graphics.Color;
import android.net.Uri;
import java.net.*;
import java.io.*;
import java.util.*;
import org.json.JSONObject;
public class MainActivity extends Activity {
 private WebView web;
 private final String PAGE="https://raw.githubusercontent.com/lehy3349-hue/morning-brief-galaxy/main/docs/index.html";
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(Color.rgb(3,12,37));getWindow().setNavigationBarColor(Color.rgb(3,12,37));
  LinearLayout layout=new LinearLayout(this);layout.setOrientation(1);layout.setBackgroundColor(Color.rgb(3,12,37));
  web=new WebView(this);web.setBackgroundColor(Color.rgb(3,12,37));web.getSettings().setJavaScriptEnabled(true);web.getSettings().setDomStorageEnabled(true);web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
  web.addJavascriptInterface(new Bridge(),"Android");
  web.setWebViewClient(new WebViewClient(){
   @Override public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest request){if(!request.getUrl().toString().startsWith("https://appassets.androidplatform.net/")){startActivity(new Intent(Intent.ACTION_VIEW,request.getUrl()));return true;}return false;}
   @Override public void onPageFinished(WebView view,String url){refreshNews();refreshWeather(getSharedPreferences("weather",0).getString("city","수원"));}
  });
  layout.addView(web,new LinearLayout.LayoutParams(-1,0,1));setContentView(layout);
  loadBundledDesign();loadLiveDesign();
 }
 private void loadBundledDesign(){try(InputStream in=getAssets().open("index.html")){ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[4096];int n;while((n=in.read(b))!=-1)out.write(b,0,n);render(new String(out.toByteArray(),"UTF-8"));}catch(Exception e){android.util.Log.e("MorningBrief","Bundled UI error",e);}}
 private void render(String html){web.loadDataWithBaseURL("https://appassets.androidplatform.net/",html,"text/html","UTF-8",null);}
 private void loadLiveDesign(){new Thread(()->{try{HttpURLConnection con=(HttpURLConnection)new URL(PAGE+"?v="+System.currentTimeMillis()).openConnection();con.setConnectTimeout(5000);con.setReadTimeout(5000);try(InputStream in=con.getInputStream()){ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[4096];int n;while((n=in.read(b))!=-1)out.write(b,0,n);String html=new String(out.toByteArray(),"UTF-8");if(html.contains("MORNING BRIEF")&&html.contains("renderNews"))runOnUiThread(()->render(html));}finally{con.disconnect();}}catch(Exception e){android.util.Log.w("MorningBrief","Offline UI fallback",e);}}).start();}
 private void refreshWeather(String city){
  getSharedPreferences("weather",0).edit().putString("city",city).apply();
  new Thread(()->{String result=WeatherRepository.fetch(city);runOnUiThread(()->{if(web!=null)web.evaluateJavascript("renderWeather("+JSONObject.quote(result)+")",null);});}).start();
 }
 private void refreshNews(){new Thread(()->{String items=NewsRepository.fetch(this);runOnUiThread(()->{if(web!=null)web.evaluateJavascript("renderNews("+org.json.JSONObject.quote(items)+")",null);});}).start();}
 private void schedule(long millis,int code,boolean test){AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
  if(Build.VERSION.SDK_INT>=31&&!am.canScheduleExactAlarms()){startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));status("정확한 알람 권한을 허용한 후 다시 설정해 주세요.");return;}
  Intent alarm=new Intent(this,AlarmReceiver.class);alarm.putExtra("test_only",test);
  PendingIntent pi=PendingIntent.getBroadcast(this,code,alarm,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  PendingIntent show=PendingIntent.getActivity(this,code+10,new Intent(this,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  am.setAlarmClock(new AlarmManager.AlarmClockInfo(millis,show),pi);
 }
 private void status(String s){runOnUiThread(()->{if(web!=null)web.evaluateJavascript("setStatus("+org.json.JSONObject.quote(s)+")",null);});}
 public class Bridge{
  @JavascriptInterface public void setDailyAlarm(String time){runOnUiThread(()->{try{String[] a=time.split(":");int h=Integer.parseInt(a[0]),m=Integer.parseInt(a[1]);Calendar c=Calendar.getInstance();c.set(Calendar.HOUR_OF_DAY,h);c.set(Calendar.MINUTE,m);c.set(Calendar.SECOND,0);if(c.getTimeInMillis()<=System.currentTimeMillis())c.add(Calendar.DAY_OF_MONTH,1);getSharedPreferences("alarm",0).edit().putInt("hour",h).putInt("minute",m).putBoolean("enabled",true).apply();schedule(c.getTimeInMillis(),1,false);status("매일 "+time+" 뉴스 알람 설정 완료");}catch(Exception e){status("알람 시간 형식을 확인해 주세요.");}});}
  @JavascriptInterface public void testAlarm(){runOnUiThread(()->{schedule(System.currentTimeMillis()+60000,3,true);status("1분 뒤 테스트 알람 예약. 화면을 꺼서 확인하세요.");});}
  @JavascriptInterface public void refreshNews(){MainActivity.this.refreshNews();}
  @JavascriptInterface public String getAlarmTime(){android.content.SharedPreferences p=getSharedPreferences("alarm",0);return String.format(java.util.Locale.ROOT,"%02d:%02d",p.getInt("hour",7),p.getInt("minute",0));}
  @JavascriptInterface public void refreshWeather(String city){MainActivity.this.refreshWeather(city);}
  @JavascriptInterface public String getWeatherCity(){return getSharedPreferences("weather",0).getString("city","수원");}
  @JavascriptInterface public void speakNews(){runOnUiThread(()->{Intent i=new Intent(MainActivity.this,SpeechService.class);if(Build.VERSION.SDK_INT>=26)startForegroundService(i);else startService(i);status("뉴스 음성 브리핑을 준비 중입니다.");});}
  @JavascriptInterface public void stopSpeech(){runOnUiThread(()->stopService(new Intent(MainActivity.this,SpeechService.class)));}
  @JavascriptInterface public void openArticle(String url){runOnUiThread(()->{try{Uri u=Uri.parse(url);if("https".equals(u.getScheme()))startActivity(new Intent(Intent.ACTION_VIEW,u));}catch(Exception e){status("기사를 열 수 없습니다.");}});}
 }
}
