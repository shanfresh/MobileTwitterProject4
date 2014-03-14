package com.ict.twitter.hbase;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Date;
import java.util.HashMap;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.util.Bytes;

import com.ict.twitter.analyser.beans.UserRelationship;

public class UserRelTwitterHbase extends TwitterHbase{

	public UserRelTwitterHbase(String tableName) {
		super(tableName);
	}
	
	public void InsertIntoTable(UserRelationship[] userrel) throws IOException{
		
		for(int i=0;i<userrel.length;i++){
			Put put=new Put(Bytes.toBytes(this.GenerateCompositeKey(userrel[i])));
			for(int j=0;j<familyNames.length;j++){
				if(columnsmap.get(familyNames[j])!=null){
					;
				}else{
					put.add(Bytes.toBytes(familyNames[j]), null,Bytes.toBytes(Exchange(familyNames[j],null,userrel[i]) ));
				}
				
			}
			table.put(put);
		}
		table.flushCommits();
	}
	private String GenerateCompositeKey(UserRelationship rel){
		return rel.getUser_A()+"#"+rel.getUser_B();		
	}
	
	private String Exchange(String familyName,String columnname,UserRelationship rel){
		String result=null;
		if(familyName.equals("type")){
			result=rel.getLinkType();
		}else if(familyName.equals("crawltime")){
			result=sdf.format(new Date());
			
		}
		
		return result;
	}
	

	@Override
	protected void SetFamilyNameAndColumns() {
		familyNames=new String[]{"type","crawltime"};
		columnsmap=new HashMap<String,String[]>();		
	}

}
