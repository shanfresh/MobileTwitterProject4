package com.ict.twitter;

import com.ict.twitter.analyser.beans.TimeLine;
import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.analyser.beans.UserRelationship;
import com.ict.twitter.analyser.filter.TimeTransformer;
import com.ict.twitter.hbase.TwitterHbase;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MulityInsertDataBase;

public class AjaxAnalyser {
	static boolean isdebug=false;
	static{
		try{
			isdebug=System.getProperties().getProperty("isdebug").equals("TRUE");
		}catch(Exception ex){
			isdebug=false;
		}
	}
	DbOperation dbo;
	Task task;
	AjaxCrawl ajaxcrawl;

	protected TimeTransformer timeTrans;
	public MulityInsertDataBase batchdb;
	/////////////////////////////HBASE///////////////////////////////////////////
	protected boolean HbaseEnable=false;
	protected TwitterHbase hbase=null; 
	//////////////////////////////////////////////////////////////////////////////
	public void SetHabae(TwitterHbase hbase,boolean HbaseEnable){
		this.HbaseEnable=HbaseEnable;
		this.hbase=hbase;
	}

	public AjaxAnalyser(MulityInsertDataBase batchdb,Task task){//创建分析器的同时将Task传给分析器，方便存放数据库
		
		if(batchdb!=null){
			this.batchdb=batchdb;
		}else{
			this.batchdb=new MulityInsertDataBase();
		}
		this.task=task;
		timeTrans=new TimeTransformer();
	}

	
	
	//采集的游动标记位置
	public class AnalyserCursor{
		String lastID;
		int size;
		public AnalyserCursor(String lastID, int size) {
			super();
			this.lastID = lastID;
			this.size = size;
		}
		public AnalyserCursor() {
			
		}
	
	}
	//采集的结果进行反馈时使用
	public class SearchResult{
		
	}
	
	
	
	
	
	
	public boolean _insertIntoMessage(String tweetID,String userName,String content,String date){
		TimeLine timeline=new TimeLine();
		timeline.setId(tweetID);				
		timeline.setAuthor(userName);
		timeline.setContent(content);
		timeline.setDate(date);
		timeline.show();
		String sqlStr=timeline.getString();
		return dbo.insertWithoutBatch(sqlStr);		
	}
	public boolean _insertIntoUser(String name){
		TwiUser user=new TwiUser();
		user.setName(name);
		user.setSummarized("");
		user.setAliasName(name);
		user.setFollowers(0);
		user.setFollowing(0);
		String sql=user.getString();
		return dbo.insertWithoutBatch(sql);	
	}
	public boolean _insertIntoUserRelationship(String auth,String otherName,boolean IsFollowing){
		UserRelationship userRel=new UserRelationship();
		userRel.setUser_A(auth);
		userRel.setUser_B(otherName);
		if(IsFollowing){
			userRel.setLinkType("following");
		}else{
			userRel.setLinkType("follower");
		}						
		String sqlStr=userRel.getString();
		return dbo.insertWithoutBatch(sqlStr);	
		
	}


	
}
