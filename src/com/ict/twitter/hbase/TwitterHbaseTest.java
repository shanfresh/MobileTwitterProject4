package com.ict.twitter.hbase;

import static org.junit.Assert.*;
import lombok.Setter;

import org.junit.Before;
import org.junit.Test;

import com.ict.twitter.analyser.beans.TimeLine;

public class TwitterHbaseTest {
	MessageTwitterHbase th;

	
	@Before
	public void init(){
		th=new MessageTwitterHbase("message");
	}
	
	
	@Test
	public void testCreateMessageTable() {
		try {
			th.CreateTable("message");
			TimeLine[] allTime=new TimeLine[1];
			TimeLine tl=new TimeLine();
			tl.setId("001");
			tl.setAuthor("boxilai");
			tl.setContent("ÎÚ¿ËÀ¼´òÂÒ");
			tl.setDate("2012-02-10 07:48:29");
			tl.setReplyCount(1);
			tl.setReTWcount(2);
			allTime[0]=tl;
			th.InsertIntoTable(allTime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
