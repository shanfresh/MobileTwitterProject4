package com.ict.twitter.hbase;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ict.twitter.analyser.beans.UserRelationship;

public class UserRelTwitterHbaseTest {
	HbaseTool hbasetool=new HbaseTool();
	UserRelTwitterHbase urhbase=new UserRelTwitterHbase("user_relationship");
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		urhbase.close();
	}

	@Test
	public void test() {
		ArrayList<UserRelationship> urlist=new ArrayList<UserRelationship>();
		for(int i=0;i<1000;i++){
			UserRelationship ur=new UserRelationship();
			ur.setUser_A(i+"");
			ur.setUser_B(Integer.toString(i));
			ur.setLinkType("true");
			urlist.add(ur);
		}
		try {
			urhbase.InsertIntoTable(urlist.toArray(new UserRelationship[urlist.size()]));
		} catch (IOException e) {
			fail("ERROR");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
