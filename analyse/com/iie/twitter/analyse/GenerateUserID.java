package com.iie.twitter.analyse;

import com.ict.twitter.tools.SaveTxtFile;

public class GenerateUserID {

	public final int BaseInt=1531794450;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GenerateUserID gu=new GenerateUserID();
		SaveTxtFile sxf=new SaveTxtFile("RandomUser_2.txt", true);
		for(int i=0;i<500000;i++){
			int t=gu.generate();
			sxf.Append(t+"\r\n");
		}
		sxf.close();

	}
	public int generate(){
		double eachDouble=Math.random();
		int t=(int) (eachDouble*BaseInt);
		return t;
	}

}
