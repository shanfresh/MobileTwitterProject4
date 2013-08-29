package com.ict.twitter;

import com.ict.twitter.analyser.beans.UserProfile;

import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ict.twitter.task.beans.Task;
import com.ict.twitter.tools.MulityInsertDataBase;

public class AjaxProfileAnalyser extends AjaxAnalyser {
	
	
	
	

	public AjaxProfileAnalyser(MulityInsertDataBase batchdb, Task task) {
		super(batchdb, task);
		// TODO Auto-generated constructor stub
	}
	public void doAnylyze(String content,UserProfile userprofile){
		String picture_url="";
		int tweet=0,following=0,follower=0;
		String location=null,selfIntroductionstr=null;
		Document doc=Jsoup.parse(content, "/");
		Elements picture=doc.getElementsByAttributeValue("class", "profile-picture media-thumbnail js-nav");
		if(picture.size()>0){
			picture_url=picture.get(0).child(0).attr("src");
		}else{
			picture_url="null";
		}
		Elements locationElements=doc.getElementsByAttributeValue("class", "location profile-field");
		if(locationElements!=null&&locationElements.size()>0){
			location=locationElements.first().ownText();
		}else{
			location="null";
		}
		Elements selfIntroduction=doc.getElementsByAttributeValue("class", "bio profile-field");
		if(selfIntroduction!=null&&selfIntroduction.size()>0){
			selfIntroductionstr=selfIntroduction.first().ownText();
		}else{
			selfIntroductionstr="null";
		}
		
		Elements CountElement=doc.getElementsByAttributeValue("class", "default-footer");
		if(CountElement!=null&&CountElement.size()>0){
			Element target=CountElement.first();
			tweet=this.getCount(target, "tweet_stats");
			following=this.getCount(target, "following_stats");
			follower=this.getCount(target, "follower_stats");
		}else{
			tweet=-1;following=-1;follower=-1;
		}
		userprofile.setTweet(tweet);
		userprofile.setFollower(follower);
		userprofile.setFollowing(following);
		userprofile.setPicture_url(picture_url);
		userprofile.setLocation(location);
		userprofile.setSelfintroduction(selfIntroductionstr);
		
	}
	private int getCount(Element ele,String dataElementTerm) throws NumberFormatException{
		Elements allElements = ele.getElementsByAttributeValue("data-element-term",dataElementTerm);
		if(allElements!=null&&allElements.size()>0){
			Element target=allElements.first();
			String count=target.child(0).ownText();
			count=count.replaceAll(",", "");
			int res=Integer.parseInt(count);
			return res;
		}
		return -1;
	}
	
	
	public static void main(String[] args) {

	}

}
