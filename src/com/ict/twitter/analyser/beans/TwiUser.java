package com.ict.twitter.analyser.beans;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwiUser {
	
	public String webpageLink;
	public String name;
	public String AliasName;
	public int following;
	public int followers;
	
	public String location;
	public String summarized;
	public String website;
	public String profileImageUrl;
	
	public TwiUser(String name, String aliasName, int following, int followers) {
		super();
		this.name = name;
		AliasName = aliasName;
		this.following = following;
		this.followers = followers;
	}


	public TwiUser() {
		// TODO Auto-generated constructor stub
	}


	//�����滻״̬�е�һЩ�ַ�
	Pattern p1 = Pattern.compile("[\"|'|\\\\]");	
	public String getString()
	{
		Matcher m = p1.matcher(summarized);
		summarized = m.replaceAll(" ");		
		Date nowDate=new Date();		
		StringBuffer sb=new StringBuffer();       			
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
		sb.append("6,");
		sb.append("'"+this.getName()+"'"+",");
		sb.append("'"+this.getAliasName()+"',");
		sb.append("'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nowDate)+"',");
		sb.append(this.getFollowing()+",");
		sb.append(this.getFollowers()+",");
		sb.append("'"+this.getLocation()+"',");
		sb.append("'"+summarized+"',");
		sb.append("'"+this.getProfileImageUrl()+"',");
		sb.append("'"+this.getWebpageLink()+"'");
		sb.append(")");
        String sqlStr=sb.toString();
        return sqlStr;
	}
	
	
	public void show(){
		System.out.println("����"+name);
		System.out.println("�ǳ�"+AliasName);
		System.out.println("����"+webpageLink);
		System.out.println("Following \t"+following);
		System.out.println("Follower \t"+followers);
		System.out.println("location"+location);
		System.out.println("summarized"+summarized);
		System.out.println("website"+website);
	}
	
	
	public String getWebpageLink() {
		return webpageLink;
	}
	public void setWebpageLink(String webpageLink) {
		this.webpageLink = webpageLink;
	}
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAliasName() {
		return AliasName;
	}
	public void setAliasName(String aliasName) {
		AliasName = aliasName;
	}

	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public String getSummarized() {
		return summarized;
	}


	public void setSummarized(String summarized) {
		this.summarized = summarized;
	}


	public String getWebsite() {
		return website;
	}


	public void setWebsite(String website) {
		this.website = website;
	}


	public int getFollowing() {
		return following;
	}


	public void setFollowing(int following) {
		this.following = following;
	}


	public int getFollowers() {
		return followers;
	}


	public void setFollowers(int followers) {
		this.followers = followers;
	}

	
}

