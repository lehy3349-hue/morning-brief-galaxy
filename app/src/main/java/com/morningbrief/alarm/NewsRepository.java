package com.morningbrief.alarm;
import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;
import java.net.*;
import java.io.*;
import java.util.*;
public final class NewsRepository {
 private static final String[] CATEGORIES={"라이온즈","주요","경제","세계"};
 private static final String[] QUERIES={"삼성 라이온즈 야구","대한민국 주요 뉴스","한국 경제 증시","국제 세계 뉴스"};
 private NewsRepository(){}
 public static String fetch(Context context){
  JSONArray all=new JSONArray();
  for(int i=0;i<QUERIES.length;i++){
   try{
    String url="https://news.google.com/rss/search?q="+URLEncoder.encode(QUERIES[i],"UTF-8")+"&hl=ko&gl=KR&ceid=KR:ko";
    HttpURLConnection con=(HttpURLConnection)new URL(url).openConnection();
    con.setConnectTimeout(5000);con.setReadTimeout(5000);con.setRequestProperty("User-Agent","MorningBrief/1.0");
    try(InputStream stream=con.getInputStream()){
     XmlPullParser parser=Xml.newPullParser();parser.setInput(stream,"UTF-8");
     int event;boolean inItem=false;String title=null,link=null,pub=null;int count=0;
     while((event=parser.next())!=XmlPullParser.END_DOCUMENT && count<3){
      if(event==XmlPullParser.START_TAG){
       String tag=parser.getName();
       if("item".equals(tag)){inItem=true;title=null;link=null;pub=null;}
       else if(inItem && ("title".equals(tag)||"link".equals(tag)||"pubDate".equals(tag))){
        String value=parser.nextText();
        if("title".equals(tag))title=value;
        else if("link".equals(tag))link=value;
        else pub=value;
       }
      }else if(event==XmlPullParser.END_TAG && "item".equals(parser.getName())){
       if(title!=null && link!=null){JSONObject item=new JSONObject();item.put("category",CATEGORIES[i]);item.put("title",title);item.put("url",link);item.put("date",pub==null?"":pub);all.put(item);count++;}
       inItem=false;
      }
     }
    }finally{con.disconnect();}
   }catch(Exception e){android.util.Log.w("MorningBrief","News fetch failed: "+CATEGORIES[i],e);}
  }
  if(all.length()>0){context.getSharedPreferences("news",0).edit().putString("items",all.toString()).putLong("updated",System.currentTimeMillis()).apply();return all.toString();}
  return context.getSharedPreferences("news",0).getString("items","[]");
 }
 public static String briefing(String json){
  try{
   JSONArray a=new JSONArray(json);StringBuilder s=new StringBuilder("좋은 아침입니다. 라이온즈 모닝 브리프입니다. 오늘의 주요 뉴스입니다. ");
   for(int i=0;i<Math.min(a.length(),7);i++){JSONObject o=a.getJSONObject(i);s.append(o.optString("category")).append(" 소식. ").append(o.optString("title").replaceAll(" - [^-]+$","")).append(". ");}
   if(a.length()==0)return "좋은 아침입니다. 라이온즈 모닝 브리프입니다. 현재 뉴스를 불러올 수 없어 기본 알람을 재생합니다.";
   return s.toString();
  }catch(Exception e){return "좋은 아침입니다. 라이온즈 모닝 브리프입니다. 뉴스 음성 알람입니다.";}
 }
}
