package com.ict.twitter.tools;

import com.ict.twitter.plantform.LogSys;

public class BasePath {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getBase();
	}
	
	public static String getBase(){
		String dir=System.getProperty("user.dir");
		return dir;
	}

}
