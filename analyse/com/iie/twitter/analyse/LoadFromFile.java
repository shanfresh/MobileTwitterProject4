package com.iie.twitter.analyse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;

import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.ReadTxtFile;

public class LoadFromFile {
	Vector<String> total;
	public void doLoad(String fileName){
		
		ReadTxtFile rxf=new ReadTxtFile(fileName);
		total=rxf.read();
		System.out.println("总的行数"+total.size());
	}
	
	public Vector<MessageBean> Analyse(){
		Vector<MessageBean> mblist=new Vector<MessageBean>();
		for(int i=0;i<total.size();i++){
			String currentLine=total.get(i);
			StringTokenizer stz=new StringTokenizer(currentLine, "\t");
			MessageBean msg=new MessageBean();
			msg.setUser_name(stz.nextToken());
			msg.setUser_id(stz.nextToken());
			msg.setCreat_time(stz.nextToken());
			msg.setType(stz.nextToken());
			msg.setTitle(stz.nextToken());
			if(stz.hasMoreTokens()){
				msg.setURL(stz.nextToken());
				String URL=msg.getURL();
				if(URL.lastIndexOf("/")>0){
					String message_id=URL.substring(URL.lastIndexOf('/'));
					msg.setMessage_id(message_id);
				}

			}

			mblist.add(msg);			
		}
		return mblist;
		
	}
	public void SaveToDatabase(Vector<MessageBean> allmsg){
		Connection con=this.GetDBCon();
		try {
			PreparedStatement pst=con.prepareStatement("insert into message_wenyunchao_total(message_id,user_name,user_id,title,create_time,type,URL,crawl_time) values(?,?,?,?,?,?,?,?)");
			pst.clearBatch();
			for(MessageBean msg:allmsg){
				pst.setString(1, msg.getMessage_id());
				pst.setString(2, msg.getUser_name());
				pst.setString(3, msg.getUser_id());
				pst.setString(4, msg.getTitle());
				pst.setString(5, msg.getCreat_time());
				pst.setString(6, msg.getType());
				pst.setString(7, msg.getURL());
				pst.setDate(8, msg.getCrawl_time());
				pst.addBatch();
			}
			pst.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	DbOperation dbo;
	Connection con;
	public Connection GetDBCon(){
		try {
			if(con==null||con.isClosed()){
				dbo=new DbOperation();
				con=dbo.GetConnection();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dbo=new DbOperation();
			con=dbo.GetConnection();
		}
		return con;

	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LoadFromFile lff=new LoadFromFile();
		lff.doLoad("C:/Users/shanjixi/Desktop/"+"tweets-all.txt");
		Vector<MessageBean> allmsg=lff.Analyse();
		lff.SaveToDatabase(allmsg);
	}

}
