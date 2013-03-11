package com.ict.twitter.analyser.beans;

public class DetailTwiUser extends TwiUser {
	public int following;
	public int followers;
	
	public String location;
	public String summarized;
	public String website;
	
	
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


	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		System.out.println("Following \t"+following);
		System.out.println("Follower \t"+followers);
		System.out.println("location"+location);
		System.out.println("summarized"+summarized);
		System.out.println("website"+website);
	}
	

}
