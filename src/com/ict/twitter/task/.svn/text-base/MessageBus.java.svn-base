package com.ict.twitter.task;

import java.util.ArrayList;
import java.util.HashSet;

public class MessageBus {
	public String source[]={"networktest1","networktest2"};
	public ArrayList<String> Arrayzhongzi;
	public HashSet<String> zhongzi;
	public HashSet<String> newZhongzi;
	public HashSet<String> oldZhongzi;
	public HashSet<String> NormalUser;
	
	
	
	public MessageBus(){
		zhongzi=new HashSet<String>();
		Arrayzhongzi=new ArrayList<String>();
		newZhongzi=new HashSet<String>();
		oldZhongzi=new HashSet<String>();
		NormalUser=new HashSet<String>();
		for(String t:source){
			zhongzi.add(t);
		}
	}
	public void showNewZhongzi(){
		for(String t:Arrayzhongzi){
			System.out.println("["+t+"]");
		}
	}

}
