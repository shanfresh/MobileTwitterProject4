package com.ict.twitter.CrawlerServer;

import java.util.ArrayList;


public class UserList<T> extends ArrayList<T>{


	private static final long serialVersionUID = -1253001268783525775L;
	@Override
	public boolean add(T e) {
		// TODO Auto-generated method stub
		if(this.contains(e)){
			return false;
		}
		return super.add(e);
	}
	
	
	
	

}
