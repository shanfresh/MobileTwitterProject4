package com.ict.twitter.analyser.filter;

import static org.junit.Assert.*;

import java.sql.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.ict.twitter.tools.DbOperation;

public class messageFilterTest {
	DbOperation dbo=new DbOperation();
	java.sql.Connection con=dbo.GetConnection();
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
		con.close();
	}
	
	
	@Test
	public void test() {
		messageFilter mf=new messageFilter();
		String sql="select create_time from message limit 100000";
		try {
			Statement sta=con.createStatement();
			ResultSet rs=sta.executeQuery(sql);
			while(rs.next()){
				String t=rs.getString(1);
				mf.doTransForm(t);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
