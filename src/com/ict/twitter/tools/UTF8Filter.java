package com.ict.twitter.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UTF8Filter {
	static String  queryStr="[^\u4e00-\u9fa5\ufe00-\uff5a\u0040\u0000-\u00ff\u0800-\u4e00]";
	/*
	 * if all UTF8 return true;
	 * OR return false;
	 */
	public static boolean IsAllUTF8(String Str){
		Pattern p1 = Pattern.compile(queryStr);
		Matcher m = p1.matcher(Str);
		if(m.find()){
			return true;
		}else{
			return false;
		}
	}
	public static String CleanLeaveUTF8(String Str){
		Pattern p1 = Pattern.compile(queryStr);
		Matcher m = p1.matcher(Str);
		while(m.find()){
			Str=m.replaceAll("");
		}
		

//		
//		try {
//			System.out.println("OLD:"+bytesToHexString(OLD.getBytes("unicode")));
//			System.out.println("NEW:"+bytesToHexString(Str.getBytes("unicode")));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return Str;
	}
	
	public static void main(String[] args) throws Exception{
		String t="﹣insert into table(va1,va2,va3) valuses(中国人@\uff41,va2,va3)";
		String t2=t;
		System.out.println("T2是："+t2);
		System.out.println(bytesToHexString(t2.getBytes("unicode")));
		
		
		if(UTF8Filter.IsAllUTF8(t2)){
			System.out.println("存在非法字符");
		}else{
			System.out.println("全部字符均为有效字符");
		}
		System.out.println("尝试进行清除");
		
		t2=CleanLeaveUTF8(t2);
		
		if(UTF8Filter.IsAllUTF8(t2)){
			System.out.println("存在非法字符");
		}else{
			System.out.println("全部字符均为有效字符");
		}
		
		//1:尝试正常的存放数据、并记录当期的字符串数值：
		//2:如果存放异常，并且捕获则进行处理
		//3:再次尝试存放。
		
		
	}
    public static final String bytesToHexString(byte[] bytes) {  
        StringBuilder sb = new StringBuilder(bytes.length * 2);  
        for (int i = 0; i < bytes.length; i++) {  
            String hex = Integer.toHexString(bytes[i] & 0xff);// &0xff是byte小于0时会高位补1，要改回0  
            if (hex.length() == 1)  
                sb.append('0');  
            sb.append(hex);  
            sb.append(" ");  
        }  
        return sb.toString().toUpperCase();  
    }  

}
