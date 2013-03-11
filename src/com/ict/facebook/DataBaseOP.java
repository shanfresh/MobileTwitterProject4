package com.ict.facebook;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.Iterator;
import com.ict.facebook.FriendList.User;
import com.ict.facebook.TimeLine.FBTimeLine;
import com.ict.twitter.tools.DbOperation;

public class DataBaseOP {
	
	DbOperation dbo=null;
	public DataBaseOP(DbOperation dbo){
		if(dbo!=null)
			this.dbo=dbo;
		else{
			this.dbo=new DbOperation();
		}
	}
	public boolean saveUser(String ownerID,Vector<User> users){
		Date nowDate= new Date();
		String nowDateStr=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nowDate);
		Iterator<User> it=users.iterator();
		while(it.hasNext()){
			User user=it.next();
			dbo.insertWithoutBatch(user.toUserSQL(nowDateStr));			
		}
		return true;
	}
	
	public boolean saveUserRel(String ownerID,Vector<User> users){
		Date nowDate= new Date();
		String nowDateStr=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nowDate);
		Iterator<User> it=users.iterator();
		while(it.hasNext()){
			User user=it.next();
			dbo.insertWithoutBatch(user.toUserRelSql(ownerID,user,nowDateStr));			
		}
		return true;
	}
	public boolean saveTimeLine(String ownerID,Vector<FBTimeLine> timelines){
		Date nowDate= new Date();
		String nowDateStr=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nowDate);
		Iterator<FBTimeLine> it=timelines.iterator();
		while(it.hasNext()){
			FBTimeLine timeLine =  it.next();		
			dbo.insertWithoutBatch(timeLine.getFBSql(nowDateStr));		
		}
		return true;
	}
	
}
