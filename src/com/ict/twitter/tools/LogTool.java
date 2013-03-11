package com.ict.twitter.tools;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.log4j.PropertyConfigurator;

public class LogTool {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	static Logger logger = Logger.getLogger(LogTool.class.getName());
	
	public LogTool(){
		 PropertyConfigurator.configure ("config/log4j.properties" ) ;
		 // add for log4j: 定义日志等级
		    //logger.setLevel ( ( Level ) Level.DEBUG ) ;   
    
	    logger.info("start !!!!");
	    String name = "hello";
	    logger.info("log4j   layout:"+name);
	    System.out.println("end of instance");
	    logger.info("dddddddddddddddddddd");
	    logger.info("program exists");
	    logger.log(Level.FINE,"start");
		    
	}

}
