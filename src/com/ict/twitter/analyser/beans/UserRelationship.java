package com.ict.twitter.analyser.beans;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class UserRelationship {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public String user_A;
	public String user_B;
	public String linkType;
	
	public UserRelationship(String user_A, String user_B, String linkType) {
		super();
		this.user_A = user_A;
		this.user_B = user_B;
		this.linkType = linkType;
	}

	public UserRelationship() {
		// TODO Auto-generated constructor stub
	}

	//用于替换状态中的一些字符
	Pattern p1 = Pattern.compile("[\"|'|\\\\]");
	
	public String getString()
	{
		StringBuffer sb=new StringBuffer();
       	
		Date nowDate=new Date();
			
							
		sb.append("insert into user_relationship(" +
							"channel_id,"+
							"user_id_A," +
							"user_id_B,"+
							"link_type,"+
							"crawl_time)"
							 );			
		sb.append("values(6,");					
		sb.append("'"+user_A+"'"+",");							
		sb.append("'"+user_B+"'"+",");
		sb.append("'"+linkType+"'"+",");
		sb.append("'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nowDate)+"'");
		sb.append(")");		
        String sqlStr=sb.toString();
        return sqlStr;
	}
	
	public String getUser_B() {
		return user_B;
	}

	public void setUser_B(String user_B) {
		this.user_B = user_B;
	}

	
	public String getUser_A() {
		return user_A;
	}

	public void setUser_A(String user_A) {
		this.user_A = user_A;
	}

	
	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}
	
	public void show(){
		System.out.println("user_A     \t"+user_A);
		System.out.println("user_B     \t"+user_B);
		System.out.println("linkType    \t"+linkType);
		
	}
}
