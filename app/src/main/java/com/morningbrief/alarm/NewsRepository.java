package com.morningbrief.alarm;
import android.content.Context;
import android.util.Xml;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
public final class NewsRepository {
 private static final String[] CATEGORIES={"주요","사회","경제","세계","IT·과학"};
 private static final String[] PATHS={
  "https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko",
  "https://news.google.com/rss/headlines/section/topic/NATION?hl=ko&gl=KR&ceid=KR:ko",
  "https://news.google.com/rss/headlines/section/topic/BUSINESS?hl=ko&gl=KR&ceid=KR:ko",
  "https://news.google.com/rss/headlines/section/topic/WORLD?hl=ko&gl=KR&ceid=KR:ko",
  "https://news.google.com/rss/headlines/section/topic/TECHNOLOGY?hl=ko&gl=KR&ceid=KR:ko"
 };
 private NewsRepository(){}
 public static String fetch(Context context){
  JSONArray result=new JSONArray();Set<String> seen=new HashSet<>();
  for(int i=0;i<PATHS.length;i++){
   HttpURLConnection con=null;
   try{
    con=(HttpURLConnection)new URL(PATHS[i]).openConnection();
    con.setConnectTimeout(4000);con.setReadTimeout(4000);
    con.setRequestProperty("User-Agent","MorningBrief/1.0");
    try(InputStream stream=con.getInputStream()){
     XmlPullParser parser=Xml.newPullParser();parser.setInput(stream,"UTF-8");
     int event, count=0;boolean item=false;String title=null,link=null,pub=null;
     while((event=parser.next())!=XmlPullParser.END_DOCUMENT && count<3){
      if(event==XmlPullParser.START_TAG){
       String tag=parser.getName();
       if("item".equals(tag)){item=true;title=null;link=null;pub=null;}
       else if(item && ("title".equals(tag)||"link".equals(tag)||"pubDate".equals(tag))){
        String value=parser.nextText();
        if("title".equals(tag))title=value;
        else if("link".equals(tag))link=value;
        else pub=value;
       }
      }else if(event==XmlPullParser.END_TAG && "item".equals(parser.getName())){
       if(title!=null&&link!=null){
        String clean=title.replaceAll("\\s+-\\s+[^-]+$","").trim();
        String key=clean.replaceAll("[^가-힣a-zA-Z0-9]","").toLowerCase(Locale.ROOT);
        if(clean.length()>8 && seen.add(key)){
         JSONObject o=new JSONObject();o.put("category",CATEGORIES[i]);o.put("title",clean);o.put("url",link);o.put("date",pub==null?"":pub);result.put(o);count++;
        }
       }
       item=false;
      }
     }
    }
   }catch(Exception e){android.util.Log.w("MorningBrief","RSS unavailable: "+CATEGORIES[i],e);}
   finally{if(con!=null)con.disconnect();}
  }
  if(result.length()>0){
   context.getSharedPreferences("news",0).edit().putString("items",result.toString()).putLong("updated",System.currentTimeMillis()).apply();
   return result.toString();
  }
  return context.getSharedPreferences("news",0).getString("items","[]");
 }
 public static String briefing(String json){
  try{
   JSONArray a=new JSONArray(json);
   if(a.length()==0)return "좋은 아침입니다. 오늘의 뉴스 브리핑입니다. 현재 인터넷에서 최신 기사를 가져올 수 없습니다. 알람 시간입니다. 힘찬 하루 시작하세요.";
   StringBuilder s=new StringBuilder("좋은 아침입니다. 오늘의 뉴스 하이라이트, 지금부터 짚어보겠습니다. ");
   int n=0;String prev="";
   for(int i=0;i<a.length()&&n<8;i++){
    JSONObject o=a.getJSONObject(i);String category=o.optString("category","주요");
    if(!category.equals(prev)){
     if(n==0)s.append("첫 소식입니다. ");
     else s.append("자, 이번에는 ").append(category).append(" 분야로 넘어가 보겠습니다. ");
     prev=category;
    }else s.append("다음 소식도 살펴보죠. ");
    String headline=o.optString("title","").replaceAll("[\\[\\]<>]"," ").trim();
    if(headline.length()>0){s.append(headline).append(". ");n++;}
   }
   s.append("여기까지 오늘의 뉴스 하이라이트였습니다. 핵심을 확인하고, 오늘도 좋은 하루 보내시기 바랍니다.");
   return s.toString();
  }catch(Exception e){return "좋은 아침입니다. 오늘의 뉴스 브리핑입니다. 힘찬 하루 시작하세요.";}
 }
}
