package com.ict.facebook.TimeLine;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.ict.facebook.TimeLine.ReadTxtFile;

public class URLFactory {

	/**
	 * @param args
	 */
	String BasicURL=null;	
	public URLFactory(){
		ReadTxtFile rtf=new ReadTxtFile("E:\\URLFile.txt");
		String baseURL=rtf.read().get(0);
		String res=baseURL;
		if(res.contains("https://www.facebook.com"))
			res=res.substring("https://www.facebook.com".length());
		try {
			res = URLDecoder.decode(res,"GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BasicURL=res;
		System.out.println("InitBasicUrl----"+BasicURL);
	}
	
	
	public static void main(String[] args) {
		URLFactory uf=new URLFactory();
		String res=null;
		res=uf.GenerateURL("shanjixi");
		System.out.println(res);

	}
	public static void findAndReplace(String target,String value){
		
		
	}
	public String  GenerateURL(String userID){
		String generateUrl=BasicURL.replaceAll("100001230554499", userID);
		return generateUrl;
	}
	

}
