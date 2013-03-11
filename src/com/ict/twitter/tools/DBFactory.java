package com.ict.twitter.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

import com.ict.twitter.plantform.PlatFormMain;
import java.sql.Connection;;

public class DBFactory {
	//初始化方法
	Properties pro;
	public DBFactory(){
		this(null);
	}
	public DBFactory(String fileName){
		String path=fileName;
		if(fileName==null||fileName.equals("")){
			path="config/clientproperties.ini";
		}
		pro=new Properties();
		try {
			pro.load(new FileInputStream(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			PlatFormMain.log.error("配置文件打开错误:"+"/config/clientProperties.ini");
			e.printStackTrace();
		}
	}
	public Connection getConnection(){
		Connection con = null;
		String address=pro.getProperty("report.databaseaddress");
		String databasename=pro.getProperty("report.databasename");
		String user=pro.getProperty("report.databaseusername");
		String password=pro.getProperty("report.databaseuserpassword");
		
		if(address==null || databasename ==null || user ==null || password == null){
			System.err.println("配置文件错误");
			System.exit(1);
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://" + address+ ":3306/" + databasename+ "?useUnicode=true&characterEncoding=utf-8" , user,password);
		} catch (SQLException e) {
			System.err.println("数据库链接失败");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
	}
	
	
	
	
	
	
	public static void main(String[] args){
		DBFactory db=new DBFactory();
		Iterator<Entry<Object, Object>> it=db.pro.entrySet().iterator();
		while(it.hasNext()){
			Entry ent=it.next();
			String t=ent.getKey().toString()+"  "+ent.getValue().toString();
			System.out.println(t);
		}
		db.getConnection();
	}
	
}
