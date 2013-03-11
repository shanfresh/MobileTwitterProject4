package com.ict.facebook.TimeLine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ict.twitter.analyser.beans.TimeLine;

public class FBTimeLine extends TimeLine{
	
	Pattern p1 = Pattern.compile("[\"|'|\\\\]");
	public String getFBSql(String DateStr){
		StringBuffer sb=new StringBuffer();
		if(content==null){
			content="NULL";
		}
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
        
    	sb.append("values(7,");
		sb.append("'"+this.getId()+"',");
		sb.append("'"+RemoveInvalidStr(content)+"',");							
		sb.append("'"+this.getAuthor()+"',");			
		sb.append("'"+date+"',");	
		sb.append("'"+DateStr+"'");
	
		//sb.append(status.getRetweetCount());							
		sb.append(")");			
        String sqlStr=sb.toString();
        return sqlStr;
	}
	private String RemoveInvalidStr(String str){
		 Pattern p = Pattern.compile("'");
		 Matcher m = p.matcher(str);
		 return m.replaceAll("\\\\'");
		 
	}
}
