package com.ict.twitter.plantform;

import java.net.UnknownHostException;

import com.mongodb.MongoClient;

public class MongoDBTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static boolean doTest(){
		MongoClient mongoClient=null;
		try {
			mongoClient = new MongoClient();
		}catch(UnknownHostException e){
			System.err.print("Can't connect to MongoDB");
			return false;
		}catch(Exception e){
			return false;
		}finally{
			if(mongoClient!=null)
				mongoClient.close();
		}
		return true;
	}

}
