package com.ict.twitter.CrawlerSchedul;
import java.util.*;


import org.junit.Test;
public class KeyUserSchedulerTest {
	
	@Test
	public void doTest(){
		KeyUserScheduler kt=new KeyUserScheduler(null);
		Vector rs=kt.getAllKeyUsers();
		System.out.println(rs.size());
	}
}
