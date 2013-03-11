package com.ict.twitter.analyser.beans;


public class Influence {
	

	public String name;

	public int following;
	public int followers;
	
	
	
	
	public String getString()
	{
		int sum=following+followers;
		StringBuffer sb=new StringBuffer();
		sb.append("<name>"+name+"</name>");
		sb.append("<sum>"+sum+"</sum>");		
		String str=sb.toString();
		
		return str;
	}
	
	
	public void show(){
		System.out.println("ÐÕÃû"+name);

		System.out.println("Following \t"+following);
		System.out.println("Follower \t"+followers);

	}
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
