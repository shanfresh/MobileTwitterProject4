package com.iie.twitter.analyse;
import java.util.*;
import java.sql.*;

import com.ict.twitter.tools.ReadTxtFile;
public class ChinesDailyStatistic extends BaseAnalyse{
	TreeMap<String,Integer> total;
	ChineseRecognizer  chineseRec;
	public Vector<String> minganci;
	public ChinesDailyStatistic(){
		total=new TreeMap<String,Integer>();
		chineseRec=new ChineseRecognizer();
		ReadTxtFile rxf=new ReadTxtFile("UsefulFile/mingan_0316.txt");
		minganci=rxf.read();	
	}
	public static void main(String[] args) {
		ChinesDailyStatistic cd=new ChinesDailyStatistic();
		try {
			cd.doAnalyse();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cd.show();
	}
	public void doAnalyse() throws Exception{
		Connection con=super.GetDBCon();
		PreparedStatement pst=con.prepareStatement("Select title,user_id,create_time from profile_message_sample1 where create_time >'2014-01-09 00:00:00'");
		ResultSet rs=pst.executeQuery();
		int count=0;
		while(rs.next()){
			String date=rs.getString(3).substring(0,10);
			if(this.IsXi(rs.getString(1))){
				if(total.containsKey(date)){
					total.put(date, total.get(date)+1);
				}else{
					total.put(date, new Integer(1));
				}				
			}
		}		
	}
	public void show(){
		Iterator<String> it=total.keySet().iterator();
		while(it.hasNext()){
			String value=it.next();
			System.out.println(value+"\t"+total.get(value));
		}
		
	}
	public boolean isCHinese(String content){
		if(chineseRec.isContainChinese(content)){
			if(chineseRec.isContainJapanese(content)){
				return false;
			}else{
				return true;
			}
		}else{
			return false;
		}
	}
	public boolean OK(String content){
		if(IsMinGan(content)){
			return true;
		}
		
		return false;
		
	}
	private boolean IsXi(String content){
		if(content.contains("¡ı‘∆…Ω")||content.contains("¡ı")){
			System.out.println(content);
			return true;
		}else{
			return false;
		}
	}
	
	private boolean IsMinGan(String content){
		if(content==null){
			return false;
		}
		boolean flag=false;
		String mingan="";
		int mingancount=0;
		for(int i=0;i<minganci.size();i++){
			if(content.contains(minganci.get(i))){
				mingan+=minganci.get(i)+"  ";
				mingancount++;
				flag=true;				
			}
		}
		if(flag&&mingancount>=2){
			System.out.println("Conent"+content);
			System.out.println("KeyWords:"+mingan);			
			return true;
		}else{
			return false;
		}
	}

}
