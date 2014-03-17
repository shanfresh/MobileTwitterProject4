package com.ict.twitter.hbase;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Date;
import java.util.HashMap;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.util.Bytes;

import com.ict.twitter.analyser.beans.TimeLine;
import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.analyser.beans.UserProfile;

public class UserTwitterHbase extends TwitterHbase{

	public UserTwitterHbase(String tableName) {
		super(tableName);
		
	}

	public void InsertIntoTable(TwiUser[] users) throws IOException, InterruptedIOException{
		byte[] familyName=Bytes.toBytes("user_aliasname");
		for(int i=0;i<users.length;i++){
			Put put=new Put(Bytes.toBytes(users[i].getName()));
			put.add(familyName, null, Bytes.toBytes(users[i].getAliasName()));
			
			table.put(put);
		}		
		table.flushCommits();
	}
	public boolean InsertIntoTable(UserProfile userProfile) throws IOException, InterruptedIOException{
		
		Put put=new Put(Bytes.toBytes(userProfile.getUser_name()));
		for(int j=0;j<familyNames.length;j++){
			byte[] familyName=Bytes.toBytes(familyNames[j]);
			if(!columnsmap.containsKey(familyNames[j])){//可以二次优化
				put.add(familyName, null, Exchange(userProfile,familyNames[j],null));
			}else{
				String[] allcol=columnsmap.get(familyNames[j]);
				for(int k=0;k<allcol.length;k++){
					String column=allcol[k];
					put.add(familyName,Bytes.toBytes(column),Exchange(userProfile,familyNames[j],column));
				}
			}				
		}
		table.put(put);
		table.flushCommits();
		return true;
	}


	
	protected byte[] Exchange(UserProfile userProfile, String currentFamilyName,String columnname) {
		String result="";
		switch(currentFamilyName){
			case "user_aliasname":
				result=userProfile.getUser_aliasname();
				break;
			case "user_id":
				result=userProfile.getUser_id();
				break;
			case "tweet":
				result=Integer.toString(userProfile.getTweet());
				break;
			case "following":
				result=Integer.toString(userProfile.getFollowing());
				break;
			case "follower":
				result=Integer.toString(userProfile.getFollower());
				break;
			case "detail":
				result="";
				if(columnname.equalsIgnoreCase("profile_url")){
					result=(userProfile.getPicture_url());
				}else if(columnname.equalsIgnoreCase("profile_image")){
					result=Bytes.toString(userProfile.getPicturedata());
				}else if(columnname.equalsIgnoreCase("location")){
					result=userProfile.getLocation();
				}else if(columnname.equalsIgnoreCase("intruduction")){
					result=userProfile.getSelfintroduction();
				}
				break;
			case "crawl_time":
				result=sdf.format(new Date());
				break;			
		}
		if(result==null){
			return null;
		}
		return Bytes.toBytes(result);
	}
	
	public void SetFamilyNameAndColumns(){
		familyNames=new String[]{"user_aliasname","tweet","following","follower","crawl_time","detail"};
		columnsmap=new HashMap<String,String[]>();
		String[] detailColumn=new String[]{"profile_url","profile_image","location","intruduction"};		
		columnsmap.put("detail", detailColumn);
	}

	

}
