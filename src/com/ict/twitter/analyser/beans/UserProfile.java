package com.ict.twitter.analyser.beans;

public class UserProfile {
	private String User_id;
	private String user_screen_name;
	private String picture_url;
	private byte[] picturedata;
	
	private String selfintroduction;
	private String location;
	
	private int tweet,following,follower;
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("UserID:"+User_id+" \t");
		sb.append("user_screen:"+user_screen_name+" \r\n");
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

	public String getUser_screen_name() {
		return user_screen_name;
	}

	public void setUser_screen_name(String user_screen_name) {
		this.user_screen_name = user_screen_name;
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
	

}
