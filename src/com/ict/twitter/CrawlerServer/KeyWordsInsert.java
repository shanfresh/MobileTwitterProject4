package com.ict.twitter.CrawlerServer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.TaskType;
import com.ict.twitter.tools.BasePath;
import com.ict.twitter.tools.DbOperation;

public class KeyWordsInsert {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		DbOperation dboperation=new DbOperation();
		Connection con=dboperation.GetConnection();
		BufferedReader br = null;
		String file=BasePath.getBase()+"/UsefulFile/minganci.txt";
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			String t;
			int i=0;
			Vector<String> res=new Vector<String>();
			while((t=br.readLine())!=null){
				res.add(t);
			}
			Insert(res,con);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void Insert(Vector<String> src,Connection con){
		
		try {
			Statement sta=con.createStatement();
			sta.executeUpdate("TRUNCATE Table keyword");
			PreparedStatement pst=con.prepareStatement("insert into keyword (WordStr,`CrawlCount`,`CrawlTime`,`ResultCount`,`Weight`) values(?,?,now(),2000,100)");
			for(int i=0;i<src.size();i++){
				pst.setString(1, src.get(i));
				int size=(int) (Math.random()*1000);
				pst.setInt(2, size);
				try{
					pst.execute();	
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
			pst.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
