package com.iie.twitter.analyse;

import com.ict.twitter.tools.ReadTxtFile;
import java.util.*;

public class GenerateSQL {

	public static void main(String[] args) {
		ReadTxtFile rxf=new ReadTxtFile("C:/Users/shanjixi/Desktop/fq.txt");
		Vector<String> result=rxf.read();
		String Base="SELECT title,create_time,type from message_wenyunchao_total where ";
		String each=" title like '%M%' or";
		for(int i=0;i<result.size();i++){
			String t=each.replaceAll("M", result.get(i));
			Base+=t;
		}
		System.out.println(Base);
	}

}
