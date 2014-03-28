package com.ict.twitter.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.ict.twitter.plantform.LogSys;


public class BasePath {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getBase();
	}
	
	public static String getBase(){
		String dir=System.getProperty("user.dir");
		return dir;
	}
	public static String GetWebOpLogDir(){
		try{
			FileInputStream fis=new FileInputStream(new File("config/clientproperties.ini"));
			Properties pro=new Properties();
			pro.load(fis);
			String t=pro.getProperty("webop.fileDir");
			if(t==null){
				System.err.println("注意没有配置webop.fileDir");
				return "Output/Twitter/";
			}
			return t;
		
		}catch(FileNotFoundException ex){
			System.err.println("配置文件不存在啊！");
			System.exit(-1);
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return null;
		
		
	}
	

}
