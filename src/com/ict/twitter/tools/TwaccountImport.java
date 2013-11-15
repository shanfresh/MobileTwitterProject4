package com.ict.twitter.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

public class TwaccountImport {

	/**
	 * @param args
	 */
	final String ACCOUNT_FILE_PATH="config/TwitterAccounts.txt";
	private Vector<String[]> getAccounts(){
		ReadTxtFile rtf = new ReadTxtFile(ACCOUNT_FILE_PATH);
		Vector<String> counts = rtf.read();
		Vector<String[]> allAccount=new Vector<String[]>();
		for(int i=1;i<counts.size();i++){
			String item=counts.get(i);
			String fields[]=item.split(" ");
			allAccount.add(fields);
			
		}		
		return allAccount;		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TwaccountImport ti=new TwaccountImport();
		DbOperation dbo=new DbOperation();
		Connection con=dbo.GetConnection();
		Vector<String[]> res=ti.getAccounts();
		try {
			PreparedStatement pst=con.prepareStatement("insert into crawlaccount(`username`,`userpassword`,`status`,`health`) values (?,?,'free',true)");
			for(int i=0;i<res.size();i++){
				String[] t=res.get(i);
				pst.setString(1, t[0]);
				pst.setString(2, t[1]);
				pst.addBatch();
					
			}
			pst.executeBatch();
			pst.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

}
