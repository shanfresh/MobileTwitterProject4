package com.ict.twitter.hbase;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import com.ict.twitter.analyser.beans.TimeLine;

public class MessageTwitterHbase extends TwitterHbase{
	public MessageTwitterHbase(String tableName) {
		super(tableName);
		SetFamilyNameAndColumns();
	}
	public void close(){
		try {
			table.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean InsertIntoTable(TimeLine[] allTimeLine) throws IOException{
		for(int i=0;i<allTimeLine.length;i++){
			Put put=new Put(Bytes.toBytes(allTimeLine[i].id));
			for(int j=0;j<familyNames.length;j++){
				if(j==5){
					System.out.println(familyNames.length);
					System.out.println(j);
					
				}
				byte[] familyName=Bytes.toBytes(familyNames[j]);
				if(!columnsmap.containsKey(familyNames[j])){//可以二次优化
					put.add(familyName, null, Exchange(allTimeLine[i],familyNames[j],null));
				}else{
					String[] allcol=columnsmap.get(familyNames[j]);
					for(int k=0;k<allcol.length;k++){
						String column=allcol[k];
						put.add(familyName,Bytes.toBytes(column),Exchange(allTimeLine[i],familyNames[j],column));
					}
				}				
			}
			table.put(put);
		}
		table.flushCommits();
		return true;
	}
	
	public byte[] Exchange(TimeLine timeline,String currentFamilyName,String columnname){
		String result="";
		switch(currentFamilyName){
			case "id":
				result=timeline.getId();
				break;
			case "user_id":
				result=timeline.getAuthor();
				break;
			case "title":
				result=timeline.getContent();
				break;
			case "date":
				result=timeline.getDate();
				if(columnname.equalsIgnoreCase("create_time")){
					result=timeline.getDate();
				}else if(columnname.equalsIgnoreCase("crawl_time")){
					result=sdf.format(new Date());
				}
				break;
			case "link":
				result=timeline.getLink();
				break;
			case "detail":{
				if(columnname.equalsIgnoreCase("retw_count")){
					result=Integer.toBinaryString(timeline.getReTWcount());
				}else if(columnname.equalsIgnoreCase("reply_count")){
					result=Integer.toBinaryString(timeline.getReplyCount());
				}
				break;
			}
				
		}
		if(result==null){
			return null;
		}
		return Bytes.toBytes(result);
	}
	@Override
	protected void SetFamilyNameAndColumns() {
		familyNames=new String[]{"user_id","title","date","link","detail"};
		columnsmap=new HashMap<String,String[]>();
		String[] dateColumn=new String[]{"create_time","crawl_time"};
		String[] detailColumn=new String[]{"retw_count","reply_count"};
		columnsmap.put("date", dateColumn);
		columnsmap.put("detail", detailColumn);
	}

}
