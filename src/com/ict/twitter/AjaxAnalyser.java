package com.ict.twitter;

import com.ict.twitter.analyser.beans.TimeLine;
import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.analyser.beans.UserRelationship;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MulityInsertDataBase;

public class AjaxAnalyser {
	boolean isdebug=System.getProperties().getProperty("isdebug").equals("TRUE");
	DbOperation dbo;
	public MulityInsertDataBase batchdb;
	public AjaxAnalyser(DbOperation dbo){
		if(dbo!=null)
			this.dbo=dbo;
		else
			this.dbo=new DbOperation();
	}
	public AjaxAnalyser(MulityInsertDataBase batchdb){
		if(batchdb!=null){
			this.batchdb=batchdb;
		}else{
			this.batchdb=new MulityInsertDataBase();
		}
	}
	
	
	
	
	//�ɼ����ζ����λ��
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
	//�ɼ��Ľ�����з���ʱʹ��
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
