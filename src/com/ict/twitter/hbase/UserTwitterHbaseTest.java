package com.ict.twitter.hbase;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InterruptedIOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.analyser.beans.UserProfile;

public class UserTwitterHbaseTest {
	HbaseTool hbasetool=new HbaseTool();
	UserTwitterHbase ut=new UserTwitterHbase("user");
	@Before
	public void setUp() throws Exception {
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Ignore
	public void test() {
		for(int i=0;i<1000;i++){
			TwiUser tw=new TwiUser();
			tw.setName(i+"_twi");
			tw.setAliasName("Alias"+"_"+i);
			TwiUser[] target=new TwiUser[1];
			target[0]=tw;
			try {
				ut.InsertIntoTable(target);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail("出错了");
			}
		}
	}
	@Test
	public void test2() {
		for(int i=0;i<1000;i++){
			UserProfile userpro=new UserProfile();
			userpro.setTweet(1);
			userpro.setFollower(1);
			userpro.setFollowing(1);
			userpro.setUser_name(i+"_twi");
			userpro.setLocation("Beijing");
			userpro.setPicture_url("www.baidu.com");
			userpro.setSelfintroduction("I am OK");
			userpro.setUser_aliasname("I AM OK");
			
			
			
			try {
				ut.InsertIntoTable(userpro);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail("出错了");
			}
		}
	}

}
