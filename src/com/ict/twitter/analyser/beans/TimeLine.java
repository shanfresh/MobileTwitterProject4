package com.ict.twitter.analyser.beans;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ict.twitter.plantform.LogSys;

public class TimeLine {

	/**
	 * @param args
	 */
	public String id;
	public String author;
	public String content;
	public String date;
	public String link;
	
	public boolean is_reteet=false;
	public String origin_tweet_id=null;
	public String origin_user_name=null;
	
	public int reTWcount;
	public int replyCount;
	public int favoriteCount;
	public Date strictTime;
	
	public int TaskTrackID;
	public int MainTypeID;
	
	//用于替换状态中的一些字符
	Pattern p1 = Pattern.compile("[\"|'|\\\\]");
	
	
	
	public TimeLine(String id, String author, String content, String date) {
		super();
		this.id = id;
		this.author = author;
		this.content = content;
		this.date = date;
	}
	public TimeLine() {
		// TODO Auto-generated constructor stub
	}
	public String getString()
	{
		Date nowDate=new Date();
		
		StringBuffer sb=new StringBuffer();
		Matcher m = p1.matcher(content);
		content = m.replaceAll(" ");	
		
        sb.append("insert into message(" +
        			"channel_id,"+          		
						"message_id," +
        			//"source,"+
						"title,"+ 
						"user_id," +
						"create_time,"+
						"crawl_time)"						
					//	"forward_times)"																				 
						);
        
    	sb.append("values(6,");
		sb.append("'"+this.getId()+"',");
		sb.append("'"+content+"',");							
		sb.append("'"+this.getAuthor()+"',");			
		sb.append("'"+date+"',");	
		sb.append("'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nowDate)+"'");
	
		//sb.append(status.getRetweetCount());							
		sb.append(")");						
		
        String sqlStr=sb.toString();
        return sqlStr;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getReTWcount() {
		return reTWcount;
	}

	public void setReTWcount(int reTWcount) {
		this.reTWcount = reTWcount;
	}

	public int getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}
	
	public void show(){
		LogSys.nodeLogger.debug("id     \t"+id+"\t"+TaskTrackID+"\t"+"author \t"+author+"\t"+"content \t"+content+"\t");
	}
	public int getTaskTrackID() {
		return TaskTrackID;
	}
	public void setTaskTrackID(int taskTrackID) {
		TaskTrackID = taskTrackID;
	}
	public int getMainTypeID() {
		return MainTypeID;
	}
	public void setMainTypeID(int mainTypeID) {
		MainTypeID = mainTypeID;
	}
	public boolean isIs_reteet() {
		return is_reteet;
	}
	public void setIs_reteet(boolean is_reteet) {
		this.is_reteet = is_reteet;
	}
	public String getOrigin_user_name() {
		return origin_user_name;
	}
	public void setOrigin_user_name(String origin_user_name) {
		this.origin_user_name = origin_user_name;
	}
	public String getOrigin_tweet_id() {
		return origin_tweet_id;
	}
	public void setOrigin_tweet_id(String origin_tweet_id) {
		this.origin_tweet_id = origin_tweet_id;
	}
	public int getFavoriteCount() {
		return favoriteCount;
	}
	public void setFavoriteCount(int favoriteCount) {
		this.favoriteCount = favoriteCount;
	}



	

}
