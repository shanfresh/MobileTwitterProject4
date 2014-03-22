package com.iie.twitter.classfy;

import com.iie.twitter.analyse.BaseAnalyse;

import java.sql.*;
import java.util.*;
public class LoadData extends BaseAnalyse{
	Vector<String> normaluser;
	Vector<String> spammer;
	Connection con;
	
	
	public LoadData(){
		normaluser=new Vector<String>();
		spammer=new Vector<String>();
		con=super.GetDBCon();
	}
	public void close(){
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void doLoad(){
		try {
			PreparedStatement pst=con.prepareStatement("Select user_name,is_spammer from user_profile_label_bak_3 where labeler_name='shanjixi' limit 1000");
			ResultSet rs=pst.executeQuery();
			while(rs.next()){
				if(rs.getBoolean("is_spammer")){
					spammer.add(rs.getString(1));
				}else{
					normaluser.add(rs.getString(1));
				}
				
			}
			System.out.println(normaluser.size());
			System.out.println(spammer.size());
			pst.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
	public void DoAna(){
		this.doLoad();
		System.out.println("class,username,tweet,following,follower,hasLocation,hasIntroduction,tweet_url_count,tweet_at_count,tweet_reteet_count,has_photo");
		for(int i=0;i<normaluser.size();i++){
			String name=normaluser.get(i);
			ProfileBean pb=new ProfileBean();
			pb.setIs_spammer(false);
			FindTweetByUserName(name,pb);
			FindProfileByUserName(name,pb);
			System.out.println(pb.toString());
		}
		for(int i=0;i<spammer.size();i++){
			String name=spammer.get(i);
			ProfileBean pb=new ProfileBean();
			pb.setIs_spammer(true);
			FindTweetByUserName(name,pb);
			FindProfileByUserName(name,pb);
			System.out.println(pb.toString());
		}
		
	}
	
	
	public void FindTweetByUserName(String username,ProfileBean pb){
		try {
			PreparedStatement pst=con.prepareStatement("select title,is_reteet from message where user_id=?");
			pst.setString(1, username);
			ResultSet rs=pst.executeQuery();
			int reteet_count=0;
			int url_count=0;
			int at_count=0;
			while(rs.next()){
				String title=rs.getString(1);
				boolean isreteet=rs.getBoolean(2);
				if(isreteet){
					reteet_count++;
				}
				if(title.contains("http://")){
					url_count++;
				}
				if(title.contains("@")){
					at_count =findAt(title);
				}
				
			}
			pb.setTweet_reteet_count(reteet_count);
			pb.setTweet_url_count(url_count);
			pb.setTweet_at_count(at_count);
			rs.close();
			pst.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	private int findAt(String title){
		if(title.indexOf("@")>0){
			int index=0;
			int count=0;
			while((index=title.indexOf('@',index))!=-1){
				count++;
				index++;
			}
			return count;
			
			
		}
		return 0;
		
	}
	
	
	public void FindProfileByUserName(String userName,ProfileBean pb){
		try {
			PreparedStatement pst=con.prepareStatement("select * from user_profile where user_name=?");
			pst.setString(1, userName);
			ResultSet rs=pst.executeQuery();
			if(rs.next()){
				int tweet=rs.getInt("tweet");
				int following=rs.getInt("following");
				int follower=rs.getInt("follower");
				String user_name=rs.getString("user_name");
				pb.setUser_name(user_name);
				String location=rs.getString("location");
				String intruduction=rs.getString("intruduction");
				String img_url=rs.getString("profile_url");
				
				if(location==null||location.equalsIgnoreCase("")){
					pb.hasLocation=false;
				}else{
					pb.hasLocation=true;
				}
				if(intruduction==null||intruduction.equalsIgnoreCase("")){
					pb.hasIntroduction=false;
				}else{
					pb.hasIntroduction=true;
				}
				
				if(img_url!=null&&img_url.contains("default_profile_images")){
					pb.has_photo=false;
				}else{
					pb.has_photo=true;
				}
				pb.setTweet(tweet);
				pb.setFollowing(following);
				pb.setFollower(follower);			
			}
			
			pst.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args){
		LoadData ld=new LoadData();
		ld.DoAna();
	}

}
