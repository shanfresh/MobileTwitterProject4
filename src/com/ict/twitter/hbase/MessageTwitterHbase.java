package com.ict.twitter.hbase;

import java.io.IOException;
import java.util.HashMap;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import com.ict.twitter.analyser.beans.TimeLine;

public class MessageTwitterHbase extends TwitterHbase{
	public MessageTwitterHbase(String tableName) {
		super(tableName);
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
			table.flushCommits();
		}
		return true;
	}
	
	public byte[] Exchange(TimeLine timeline,String currentFamilyName,String columnname){
		String result="";
		switch(currentFamilyName){
			case "id":
				result=timeline.getId();
				break;
			case "author":
				result=timeline.getAuthor();
				break;
			case "content":
				result=timeline.getContent();
				break;
			case "date":
				result=timeline.getDate();
				break;
			case "link":
				result=timeline.getLink();
				break;
			case "detail":{
				if(columnname.equalsIgnoreCase("reTWcount")){
					result=Integer.toBinaryString(timeline.getReTWcount());
				}else if(columnname.equalsIgnoreCase("replycount")){
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
		familyNames=new String[]{"author","content","date","link","detail"};
		columnsmap=new HashMap<String,String[]>();
		String[] detailColumn=new String[]{"reTWcount","replycount"};
		columnsmap.put("detail", detailColumn);
	}

}
