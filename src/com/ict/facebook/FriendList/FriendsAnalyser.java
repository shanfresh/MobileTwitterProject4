package com.ict.facebook.FriendList;

import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.nodes.*;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;;

public class FriendsAnalyser {


	
	public String webContent;
	JSONParser parser=new JSONParser();
	Parser htmlparser=new Parser();
	
	private final String TAG="fsl fwb fcb";
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public Object getAttributeByName(String Src,String attrName) throws ParseException{
		String src=Src.substring("for (;;);".length());
		Map<String,Object> map=(Map<String,Object>)parser.parse(src);
		Object t=map.get(attrName);
		return t;
	}
	public String getHtml(String Src) throws ParseException,NullPointerException{
		try{
			JSONArray content = (JSONArray)this.getAttributeByName(Src, "domops");
			Map<String,String> htmlMap=(Map<String,String>)((JSONArray)content.get(0) ).get(3);
			return htmlMap.get("__html");
		}catch(NullPointerException ne){
			throw ne;
		}
		
	}
	public Vector<User> AnalyserHtml(String html){
		Document doc=htmlparser.parse(html, "/");
		Elements elements=doc.getElementsByAttributeValue("class",TAG );
		Vector<User> users = new Vector<User>(elements.size());
		for(int i=0;i<elements.size();i++){
			Element ele=elements.get(i);
			User user = new User();
			user.AliasName=ele.text();
			user.ProfileURL=ele.child(0).attr("href");
			String ajaxString=ele.child(0).attr("data-hovercard");
			if(ajaxString.lastIndexOf('=')>0)
				user.ProfileID=ajaxString.substring(ajaxString.lastIndexOf('=')+1);
			if(user.ProfileID!=null){
				users.add(user);
			}			
		}
		users.trimToSize();
		return users;
	}

}
