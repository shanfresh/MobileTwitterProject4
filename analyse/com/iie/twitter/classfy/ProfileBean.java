package com.iie.twitter.classfy;

public class ProfileBean {

	public boolean is_spammer;
	public String user_name;
	public int tweet=0;
	public int following=0;
	public int follower=0;
	
	public boolean hasLocation=false;
	public boolean hasIntroduction=false;
	
	
	public int tweet_url_count;
	public int tweet_at_count;
	public int tweet_reteet_count;
	public boolean has_photo;
	
	public String toString(){
		StringBuffer sb=new StringBuffer();
		sb.append(is_spammer+",");
		sb.append(user_name+",");
		sb.append(tweet+",");
		sb.append(following+",");
		sb.append(follower+",");
		sb.append(hasLocation+",");
		sb.append(hasIntroduction+",");
		sb.append(tweet_url_count+",");
		sb.append(tweet_at_count+",");
		sb.append(tweet_reteet_count+",");
		sb.append(has_photo+"");
		return sb.toString();
	}
	public boolean isIs_spammer() {
		return is_spammer;
	}


	public void setIs_spammer(boolean is_spammer) {
		this.is_spammer = is_spammer;
	}


	public String getUser_name() {
		return user_name;
	}


	public void setUser_name(String user_name) {
		this.user_name = user_name;
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


	public boolean isHasLocation() {
		return hasLocation;
	}


	public void setHasLocation(boolean hasLocation) {
		this.hasLocation = hasLocation;
	}


	public boolean isHasIntroduction() {
		return hasIntroduction;
	}


	public void setHasIntroduction(boolean hasIntroduction) {
		this.hasIntroduction = hasIntroduction;
	}


	public int getTweet_url_count() {
		return tweet_url_count;
	}


	public void setTweet_url_count(int tweet_url_count) {
		this.tweet_url_count = tweet_url_count;
	}


	public int getTweet_at_count() {
		return tweet_at_count;
	}


	public void setTweet_at_count(int tweet_at_count) {
		this.tweet_at_count = tweet_at_count;
	}


	public int getTweet_reteet_count() {
		return tweet_reteet_count;
	}


	public void setTweet_reteet_count(int tweet_reteet_count) {
		this.tweet_reteet_count = tweet_reteet_count;
	}


	public boolean isHas_photo() {
		return has_photo;
	}


	public void setHas_photo(boolean has_photo) {
		this.has_photo = has_photo;
	}


	
	

}
