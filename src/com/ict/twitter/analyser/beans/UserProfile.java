package com.ict.twitter.analyser.beans;

public class UserProfile {
	private String User_id;
	private String User_name;
	private String user_aliasname;
	private boolean is_alive=true;
	public String getUser_aliasname() {
		return user_aliasname;
	}

	public void setUser_aliasname(String user_aliasname) {
		this.user_aliasname = user_aliasname;
	}

	private String picture_url;
	private byte[] picturedata;
	
	private String selfintroduction;
	private String location;
	
	private int tweet,following,follower;
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("UserID:"+User_id+" \t"+"UserName:"+User_name+" \t");
		sb.append("user_screen:"+user_aliasname+" \r\n");
		sb.append("PIC:"+picture_url+" \r\n");
		sb.append("selfintroduction:"+selfintroduction+" \t");
		sb.append("Location:"+location+"\r\n");
		sb.append("tweet:  "+tweet+"Following:  "+following+"follower"+follower);
		return sb.toString();		
	}

	public String getUser_id() {
		return User_id;
	}

	public void setUser_id(String user_id) {
		User_id = user_id;
	}



	public String getUser_name() {
		return User_name;
	}

	public void setUser_name(String user_name) {
		User_name = user_name;
	}

	public String getPicture_url() {
		return picture_url;
	}

	public void setPicture_url(String picture_url) {
		this.picture_url = picture_url;
	}

	public byte[] getPicturedata() {
		return picturedata;
	}

	public void setPicturedata(byte[] picturedata) {
		this.picturedata = picturedata.clone();
	}

	public String getSelfintroduction() {
		return selfintroduction;
	}

	public void setSelfintroduction(String selfintroduction) {
		this.selfintroduction = selfintroduction;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getTweet() {
		return tweet;
	}

	public void setTweet(int tweet) {
		this.tweet = tweet;
	}

	public int getFollowing() {
		return following;
	}

	public void setFollowing(int following) {
		this.following = following;
	}

	public int getFollower() {
		return follower;
	}

	public void setFollower(int follower) {
		this.follower = follower;
	}

	public boolean isIs_alive() {
		return is_alive;
	}

	public void setIs_alive(boolean is_alive) {
		this.is_alive = is_alive;
	}
	

}
