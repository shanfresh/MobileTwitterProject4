package com.ict.facebook.FriendList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class User {
	public String ProfileID,AliasName,ProfileURL;
	
	public String toString(){
		return String.format("ProfileID:%s Name:%1s ProfileURL:%1s",ProfileID, AliasName,ProfileURL);
	}
		
	public String toUserSQL(String SaveDate){
		StringBuffer sb=new StringBuffer();
		int tagIndex=-1;

		sb.append("insert into user(" +		
				"channel_id,"+					
				//"origin_id,"+					
				"user_id," +
				"real_name," +
				//"user_create_time," +
				"crawl_time,"+
				"fans_num,"+
				"friends_num,"+				
			//	"weibo_num,"+
			//	"last_update_time,"+
			//	"auth_flag,"+
				"location,"+					
				"description," +				
				"profile_image_url,"+
				"url)");
				//"protect_info)");	
		sb.append("values(");
		sb.append("7,");
		sb.append("'"+this.ProfileID+"'"+",");
		sb.append("'"+RemoveInvalidStr(AliasName)+"',");
		sb.append("'"+SaveDate+"',");
		sb.append(0+",");
		sb.append(0+",");
		sb.append("'"+"',");
		sb.append("'"+"',");
		sb.append("'"+"',");
		sb.append("'"+this.ProfileURL+"'");
		sb.append(")");
        String sqlStr=sb.toString();
        return sqlStr;
        	
	}
	public String toUserRelSql(String ownerID,User users,String nowDateStr){
		StringBuffer sb =  new StringBuffer();
		sb.append("insert into user_relationship(" +
				"channel_id,"+
				"user_id_A," +
				"user_id_B,"+
				"link_type,"+
				"crawl_time)"
				 );			
		sb.append("values(7,");					
		sb.append("'"+ownerID+"'"+",");							
		sb.append("'"+users.ProfileID+"'"+",");
		sb.append("'"+0+"'"+",");
		sb.append("'"+nowDateStr+"'");
		sb.append(")");		
		String sqlStr=sb.toString();		
		return sqlStr;
	
	}
	public static void main(String[] args){
		User user =  new User();
		user.AliasName="shanjixi'";
		user.ProfileID="333333333";
		user.ProfileURL="!!!!!!!";
		System.out.println(user.toUserSQL("~~~~~~~~"));

	}
	private String RemoveInvalidStr(String str){
		 Pattern p = Pattern.compile("'");
		 Matcher m = p.matcher(str);
		 return m.replaceAll("\\\\'");
		 
	}
	
}
