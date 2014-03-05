package com.ict.twitter.hbase;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.conf.Configuration;
public class HbaseTool {
	static Configuration conf = HbaseFactory.conf;    
    
	public static boolean CreateTable(String tablename,String[] familiy) throws Exception{
    	HBaseAdmin admin=null;
    	try{
    		 admin = new HBaseAdmin(conf);
    		HTableDescriptor desc = new HTableDescriptor(tablename);
    		for (int i = 0; i < familiy.length; i++) {
                desc.addFamily(new HColumnDescriptor(familiy[i]));
            }
    		if(admin.tableExists(tablename)){
    			System.out.println("数据库表已经存在");
    			admin.close();
    			return false;
    		}else{
    			admin.createTable(desc);
    		}
    		
    		admin.close();
    	}catch(MasterNotRunningException ex){
    		System.err.println("MASTER链接失败");
    		ex.printStackTrace();
    	}catch(ZooKeeperConnectionException ex){
    		System.err.println("ZK链接失败");
    		ex.printStackTrace();
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    	}finally{
    		admin.close();
    	}
		return false;
    	
    }
    public static boolean InputData(String tableName,String rowKey,String familyName,String[] column,String[] value){
    	Put put = new Put(Bytes.toBytes(rowKey));// 设置rowkey
        try {
			HTable table = new HTable(conf, Bytes.toBytes(tableName));
			HColumnDescriptor[] columnFamilies = table.getTableDescriptor().getColumnFamilies();
			for(int i=0;i<column.length;i++){
				put.add(Bytes.toBytes(familyName), Bytes.toBytes(column[i]), Bytes.toBytes(value[i]));
			}
			table.put(put);
			table.close();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// HTabel负责跟记录相关的操作如增删改查等//
        
    	return true;
    }
    public static Result getResult(String tableName,String rowKey){
    	try{
    		HTable table=new HTable(conf,Bytes.toBytes(tableName));
    		Get get=new Get(Bytes.toBytes(rowKey));
    		Result result=table.get(get);
    		List<KeyValue> values=result.list();
    		for(KeyValue t:values){
    			System.out.println(Bytes.toString(t.getFamily())+"_"+Bytes.toString(t.getQualifier())+"_"+Bytes.toString(t.getValue()));
    		}
    		table.close();
    		return result;
    	}catch(Exception ex){
    		ex.printStackTrace();    		
    	}
    		
		return null;
    	
    	
    }
    
    public static void main(String[] args) throws Exception {
    	 String tableName = "message";
         String[] family = { "title", "author" };
    	
         CreateTable(tableName,family);
         for(int i=0;i<100;i++){
        	 String messageid=String.format("%3d", i);
        	 InputData(tableName,messageid,"title",new String[]{"content"},new String[]{"昆明加油有"});
        	 InputData(tableName,messageid,"author",new String[]{"id"},new String[]{"author"+i});
         }
         
         
         
    }

}
