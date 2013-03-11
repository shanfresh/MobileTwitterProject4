package com.ict.twitter.analyser.beans;

import java.text.SimpleDateFormat;
import java.util.Date;



public class MessageRelationship {
	/**
	 * @param args
	 */
	public String id_A;

	public String id_B;
	
	public String relation;
	
	
	
	public String getString()
	{
		Date nowDate=new Date();
		StringBuffer sb=new StringBuffer();
       													
		sb.append("insert into message_relationship(" +
							"channel_id,"+
							"message_id_A," +
							"message_id_B,"+
							"link_type,"+
							"crawl_time)"
							 );			
		
		sb.append("values(6,");					
		sb.append("'"+id_A+"',");							
		sb.append("'"+id_B+"',");
		sb.append("'"+relation+"',");
		sb.append("'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nowDate)+"'");
		sb.append(")");	
		
        String sqlStr=sb.toString();
        return sqlStr;
	}
	
	public String getId_B() {
		return id_B;
	}

	public void setId_B(String id_B) {
		this.id_B = id_B;
	}

	
	public String getId_A() {
		return id_A;
	}

	public void setId_A(String id_A) {
		this.id_A = id_A;
	}

	
	public String getrelation() {
		return relation;
	}

	public void setrelation(String relation) {
		this.relation = relation;
	}
	
	public void show(){
		System.out.println("id_A     \t"+id_A);
		System.out.println("id_B     \t"+id_B);
		System.out.println("relation    \t"+relation);
		
	}

}
