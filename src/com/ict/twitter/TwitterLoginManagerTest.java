package com.ict.twitter;

import static org.junit.Assert.*;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class TwitterLoginManagerTest {

	AdvanceLoginManager tlo;
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient httpclient = cm.getClientByIpAndPort("192.168.120.219", 8087);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		tlo=new AdvanceLoginManager(httpclient);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		try{
			boolean flag=tlo.trylogin();
			if(flag){
				System.out.println("µ«¬Ω≥…π¶");
			}else{
				fail("≤‚ ‘ ß∞‹");
			}
		}catch(Exception ex){
			ex.printStackTrace();
			fail("≤‚ ‘ ß∞‹");
		}
	}

}
