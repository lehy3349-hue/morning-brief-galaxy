package com.morningbrief.alarm;
import org.json.JSONObject;
import java.net.*;
import java.io.*;
import java.util.*;
public final class WeatherRepository {
 private static final String[] CITIES={"수원","서울","대구","부산","인천","대전","광주","울산","세종","제주","성남","용인","고양","화성","안양","천안","청주","전주","포항","창원","강릉","춘천"};
 private static final double[][] COORDS={{37.2636,127.0286},{37.5665,126.9780},{35.8714,128.6014},{35.1796,129.0756},{37.4563,126.7052},{36.3504,127.3845},{35.1595,126.8526},{35.5384,129.3114},{36.4800,127.2890},{33.4996,126.5312},{37.4200,127.1265},{37.2411,127.1776},{37.6584,126.8320},{37.1995,126.8312},{37.3943,126.9568},{36.8151,127.1139},{36.6424,127.4890},{35.8242,127.1480},{36.0190,129.3435},{35.2279,128.6811},{37.7519,128.8761},{37.8813,127.7298}};
 public static String fetch(String city){
  int index=Arrays.asList(CITIES).indexOf(city);if(index<0)index=0;
  HttpURLConnection con=null;
  try{
   String url="https://api.open-meteo.com/v1/forecast?latitude="+COORDS[index][0]+"&longitude="+COORDS[index][1]+"&current=temperature_2m,weather_code&daily=temperature_2m_max,temperature_2m_min&timezone=Asia%2FSeoul&forecast_days=1";
   con=(HttpURLConnection)new URL(url).openConnection();con.setConnectTimeout(6000);con.setReadTimeout(6000);
   ByteArrayOutputStream out=new ByteArrayOutputStream();try(InputStream in=con.getInputStream()){byte[] buf=new byte[2048];int n;while((n=in.read(buf))!=-1)out.write(buf,0,n);}
   JSONObject data=new JSONObject(new String(out.toByteArray(),"UTF-8"));
   JSONObject current=data.getJSONObject("current"),daily=data.getJSONObject("daily");
   JSONObject result=new JSONObject();result.put("city",CITIES[index]);result.put("temperature",current.getDouble("temperature_2m"));result.put("code",current.getInt("weather_code"));result.put("high",daily.getJSONArray("temperature_2m_max").getDouble(0));result.put("low",daily.getJSONArray("temperature_2m_min").getDouble(0));result.put("updated",current.optString("time"));return result.toString();
  }catch(Exception e){return "{\"error\":\"날씨 정보를 가져올 수 없습니다. 인터넷 연결을 확인해 주세요.\"}";}
  finally{if(con!=null)con.disconnect();}
 }
}
