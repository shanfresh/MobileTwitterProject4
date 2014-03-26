package com.ict.twitter.hbase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertTest {

	private String Convert(String id){
		SimpleDateFormat sdf=new SimpleDateFormat("YYYYMMddHHmmss");
		String date=sdf.format(new Date());
		Long longId=Long.parseLong(id);
		String t=String.format("%14s-%018d-www.twitter.com00000000000",date,longId);
		return t;
	}
	public static void main(String[] args){
		ConvertTest mt=new ConvertTest();
		mt.Convert("02220");
		
	}

}
