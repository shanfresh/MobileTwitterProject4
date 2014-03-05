package com.ict.twitter.analyser.beans;

public class MessageReteet {
	public String getMessage_id() {
		return message_id;
	}
	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_aliasname() {
		return user_aliasname;
	}
	public void setUser_aliasname(String user_aliasname) {
		this.user_aliasname = user_aliasname;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	String message_id;
	String user_id;
	String user_name;
	String user_aliasname;
	String content;
	String date;
	
}
