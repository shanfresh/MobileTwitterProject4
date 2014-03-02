package com.ict.twitter.analyser.filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimeTransformer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public String NormTimeFormat(String create_time){
         String NormCreateTime = null;
         Date date = new Date();
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);  // 最终显示时间格式
         if (create_time.contains("上午") | create_time.contains("下午")) {
        	 String substr = create_time.substring(Math.max(create_time.length() - 2, 0)); // 提取尾部字符
        	 if (substr.contains("上午") | substr.contains("下午")) { //判断是否为"上午"或"下午"
        		 SimpleDateFormat cformatter = new SimpleDateFormat("yy年MM月dd日, hh:mm a", Locale.CHINA); // a在hh:mm后面
                 // Parse the date
            	 try { 
              		date = cformatter.parse(create_time);	
              		//System.out.println("中文正确转化数据: " + sdf.format(date));
              		NormCreateTime = sdf.format(date);
              	} catch (ParseException e) {
              		e.printStackTrace();
              	}  
        	 } else {
        		 SimpleDateFormat cformatter = new SimpleDateFormat("yy年MM月dd日, a hh:mm", Locale.CHINA); // a在hh:mm前面
                 // Parse the date
            	 try { 
              		date = cformatter.parse(create_time);	
              		//System.out.println("中文正确转化数据: " + sdf.format(date));
              		NormCreateTime = sdf.format(date);
              	} catch (ParseException e) {
              		e.printStackTrace();
              	} 
        	 }
         } else { 
         	SimpleDateFormat eformatter = new SimpleDateFormat("hh:mm aa - dd MMM yy", Locale.ENGLISH); // 时间显示地域
            // Parse the date
         	try {	 
         		date = eformatter.parse(create_time);
         		//System.out.println("英文正确转化数据: " + sdf.format(date));
         		NormCreateTime = sdf.format(date);
         	} catch (ParseException e) {
         		e.printStackTrace();
         	}
         }
         
         return NormCreateTime;
     }

	public String Convert(String timelong){
		String template="1393439473";
		if(timelong.length()==template.length()){
			long time=Long.parseLong(timelong);
			Date date=new Date(time*1000);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.format(date);
			
		}
		return null;
	}

}
