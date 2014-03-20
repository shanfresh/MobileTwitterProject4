package com.ict.twitter.analyser.beans;
import java.util.*;
public class MessageDetail {
	String messageid;
	List<String> users;
	String weburl;
	String imgurl;
	int hash_tag_count;
	public int getHash_tag_count() {
		return hash_tag_count;
	}
	public void setHash_tag_count(int hash_tag_count) {
		this.hash_tag_count = hash_tag_count;
	}
	public String getMessageid() {
		return messageid;
	}
	public void setMessageid(String messageid) {
		this.messageid = messageid;
	}
	public List<String> getUsers() {
		return users;
	}
	public void setUsers(List<String> users) {
		this.users = users;
	}
	public String getWeburl() {
		return weburl;
	}
	public void setWeburl(String weburl) {
		this.weburl = weburl;
	}
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	
}
