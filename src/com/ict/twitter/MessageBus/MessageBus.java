package com.ict.twitter.MessageBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.ict.twitter.tools.FileTool;


//本类主要是实现对于MessageBus的启动和相关任务的分配
public class MessageBus {
	public  static final String ErrorString = null;
	public static String WINDOWS_DIR = "C:\\WINDOWS\\system32\\";
	public static String batSourceFile="c:\\BackUp.bat";
	public static String basePath="ActiveMQ\\bin\\win32\\";
	public static String successStr="Started SelectChannelConnelConnectoror@0.0.0.0";
	
	
	
	public static void main(String[] args){
		
	}
	
	
	
	
	
	
	
	
	public  boolean doStart() {//实际执行启动activeMq 的地方		
		if (FileTool.isExistFile(basePath+"activemq.bat")) {
			File f = new File(basePath+"activemq.bat");
			f=f.getAbsoluteFile();
			String path=f.getAbsolutePath();
			BufferedReader bReader=null;
	        InputStreamReader sReader=null;
			try {				
				Process p = Runtime.getRuntime().exec("cmd.exe /c start "+path);				
				sReader = new InputStreamReader(p.getInputStream(),"GBK");
				bReader=new BufferedReader(sReader);				
				String str=null;
				while( (str=bReader.readLine()) !=null){
					System.out.println("--------");
					if(str.indexOf(successStr)!=-1){
						System.out.println("启动成功");
						return true;
					}
				}				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}		
		}
		return false;
	}
	
	//判断服务器是否已经启动
	public boolean isStart(){
		
		
		
		return false;
	}
	
	
}

