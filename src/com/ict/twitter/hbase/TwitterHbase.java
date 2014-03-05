package com.ict.twitter.hbase;

import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;

public abstract class TwitterHbase {
	Configuration conf=HbaseFactory.conf;
	HTable table=null;
	String tableName=null;
	String[] familyNames;
	Map<String,String[]> columnsmap;
	public TwitterHbase(String tableName){
		try {
			this.tableName=tableName;
			SetFamilyNameAndColumns();
			if(CheckTableExists(tableName)){
				
			}else{
				System.out.println("数据库表不存在准备创建");
				CreateTable(tableName);
			}
			table=new HTable (conf,tableName);
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}catch(Exception e){
				e.printStackTrace();
		}
	}
	protected boolean CheckTableExists(String tablename) throws Exception{
		HBaseAdmin admin = new HBaseAdmin(conf);
		HTableDescriptor desc = new HTableDescriptor(tablename);
		boolean flag=false;
		if(admin.tableExists(tablename)){
			System.out.println("数据库表已经存在");
			flag=true;
		}else{
			flag=false;
		}
		admin.close();
		return flag;
		
	}
	
	public boolean CreateTable(String tablename) throws Exception{
    	HBaseAdmin admin=null;
    	try{
    		admin = new HBaseAdmin(conf);
    		HTableDescriptor desc = new HTableDescriptor(tablename);
    		for (int i = 0; i < familyNames.length; i++) {
    			if(columnsmap.get(familyNames[i])==null){
    				 desc.addFamily(new HColumnDescriptor(familyNames[i]));
    			}else{
    				String[] columns=columnsmap.get(familyNames[i]);
    				HColumnDescriptor hcd=new HColumnDescriptor(familyNames[i]);
    				desc.addFamily(hcd);
    			}
               
            }
    		if(CheckTableExists(tablename)){
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
	protected abstract void SetFamilyNameAndColumns();
	
}
