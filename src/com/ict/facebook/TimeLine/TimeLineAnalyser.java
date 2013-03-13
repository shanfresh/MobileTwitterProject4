package com.ict.facebook.TimeLine;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.util.*;
public class TimeLineAnalyser {

	/**
	 * @param args
	 */
	private JSONParser parser=new JSONParser();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ReadTxtFile rtf=new ReadTxtFile("UsefulFile/Facebook/TimeLine.html");
		String src=rtf.read().get(0);
		TimeLineAnalyser ana=new TimeLineAnalyser();
		try {
			Vector<FBTimeLine> timelines =  new Vector<FBTimeLine>();
			ana.doAnalyse("0000",src,timelines);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/*
	 * input: currentID-TargetUserID
	 * 		  Str-等待分析的链接内容
	 */	
	public String doAnalyse(String CurrentID,String str,Vector<FBTimeLine> timelines) throws ParseException{
		if(timelines==null){
			timelines=new Vector<FBTimeLine>();
		}
		if(str.startsWith("for (;;);")){
			str=str.substring("for (;;);".length());
		}
		String text=getAttributeByName(str, "payload");
		String dateTime=null;
		Document doc=Jsoup.parse(text);	
		Elements eles=doc.getElementsByAttributeValue("class", "timelineUnitContainer");
		for(int i=0;i<eles.size();i++){
			FBTimeLine timeline =  new FBTimeLine();
			Element t=eles.get(i);
			String content = GetTimeLineContent(i,t);
			dateTime=Millseconds2String(t.attr("data-time")+"000");			
			String attribute=t.attr("data-gt");
			boolean isOK=GetAttribute(attribute,timeline);
			if(isOK&&content!=null){
				timeline.setContent(content);
				timeline.setReplyCount(0);
				timeline.setReTWcount(0);
				timeline.setDate(dateTime);
				timelines.add(timeline);
			}else{
				continue;
			}
			
			
			
		}
		
		return null;
	}
	private String getAttributeByName(String src,String attrName) throws ParseException{
		if(src==null){
			throw new ParseException(-1,"输入的Src 为null");
		}		
		Map<String,Object> map=(Map<String,Object>)parser.parse(src);
		Object object;
		if((object=map.get(attrName))!=null){
			if(object instanceof JSONObject){
				JSONObject jsonObject = (JSONObject)object;
				return jsonObject.toJSONString();
			}
			else if(object instanceof String){
				String t=(String) object;
				return t;
			}else
				return null;
		}else
			return null;
	}
	
	private boolean GetAttribute(String t,FBTimeLine timeline){
		
		Object ownerid=null,contentid=null;
		Object type=null;
		try{
			@SuppressWarnings("unchecked")
			Map<String,Object> map=(Map<String,Object>)parser.parse(t);			
			ownerid=map.get("profileownerid");
			contentid=map.get("contentid");
			type=map.get("timeline_unit_type");
			if(contentid instanceof String){
				timeline.setId((String)contentid);				
			}
			if(ownerid instanceof String){
				timeline.setAuthor((String)ownerid);
				
			}
			if(type instanceof String){
				timeline.setLink((String)type);
				
			}
			
			
		}catch(ParseException ex){
			System.out.println("\t\t[HTML]"+t);
			System.out.println("\t\tcontentid:"+contentid);
			System.out.println("\t\tprofileownerid:"+ownerid);
			System.out.println("\t\ttimeline_unit_type:"+type);
			return false;
		}
		
		return true;
	}
	
	private String GetTimeLineContent(int index,Element t){
		String result=null;
		if(t.getElementsByClass("userContentWrapper").size()>0){
			String tt=t.getElementsByClass("userContentWrapper").first().text();
			result=tt;
		}
		return result;
	}
	private String Millseconds2String(String str){
		
		if(str==null)
			return null;
		try{
			String dateTime=new Date(Long.parseLong(str)).toLocaleString();
			return dateTime;
		}catch(Exception ex){
			return null;
		}
	}

}
