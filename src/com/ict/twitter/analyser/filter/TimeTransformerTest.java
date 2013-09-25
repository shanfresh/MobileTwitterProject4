package com.ict.twitter.analyser.filter;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.ict.twitter.tools.DbOperation;

public class TimeTransformerTest {
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
	}

	@Test
	public void test() {
		TimeTransformer mf=new TimeTransformer();
		String sql="select id,create_time from message";
		try {
			Statement sta=con.createStatement();
			PreparedStatement upsta=con.prepareStatement("update message set create_time=? where id=?");
			int count=0;
			ResultSet rs=sta.executeQuery(sql);
			while(rs.next()){
				int id=rs.getInt(1);
				String t=rs.getString(2);
				String res=mf.NormTimeFormat(t);
				upsta.setString(1, res);
				upsta.setInt(2, id);
				upsta.executeUpdate();
				if(count%1000==0){
					System.out.print(count/1000+" ");
				}
				if(count%10000==0){
					System.out.println();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
