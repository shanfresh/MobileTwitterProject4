package com.ict.facebook;
import java.net.UnknownHostException;
import com.ict.twitter.plantform.LogSys;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;


public class FBOperation {

	/**
	 * @param args
	 */
	class MongoDBConnectionException extends Exception{
		private static final long serialVersionUID = 4730796376733870520L;
		MongoDBConnectionException(){
			super("无法连接到MongoDB");
		}
	}
	
	private static MongoClient mongoClient;
	private static String DataBaseName="testnull";
	public static void main(String[] args) {
		//insertTimeLine("100001230554499","http://www.facebook.com","This is HTML");		
		//boolean res	= isExistTimeLine("100001230554499");
		//System.out.println(res);
		//showAllCount("FBFriend");
		//insertFriend("100001230554499",0,"http://www.facebook.com/friend","This is HTML");
		//System.out.println(isExistFriend("100001230554499",1));
		//String res=getFriend("100001230554499",0);
		String src=getTimeLine("797655017");
		System.out.println(src);
	}
	
	public static boolean insertTimeLine(String profileID,String URL,String content){
		try {
			 MongoClient mongoClient = getClient();
			 DB db = mongoClient.getDB(DataBaseName);			
			 DBCollection coll = db.getCollection("FBTimeLine");
			 BasicDBObject oneItem = new BasicDBObject("profileID", profileID);
			 oneItem.append("URL", URL).append("HTML",content);
			 coll.insert(oneItem);
			 return true;			
		}catch(com.mongodb.MongoException.DuplicateKey e){
			LogSys.nodeLogger.error("TimeLine键值重复{"+profileID+"}");
			return false;
		}catch(Exception e){
			return false;
		}
	}
	public static boolean isExistTimeLine(String profileID){		
		MongoClient mongoClient;
		try {
			mongoClient = getClient();
			DB db = mongoClient.getDB(DataBaseName);			
			DBCollection coll = db.getCollection("FBTimeLine");
			QueryBuilder myObject = new QueryBuilder();
			DBObject object= myObject.put("profileID").is(profileID).get();
			if(coll.find(object).count()>0){	
				return true;
			}else{
				 return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		 
	}
	
	
	//显示某一集合的全部数据内容
	public static void showAllCount(String CollectionName){
		MongoClient mongoClient;
		try {
			mongoClient = getClient();
			DB db = mongoClient.getDB("test");
			DBCollection coll = db.getCollection(CollectionName);
			DBCursor cursor = coll.find();
			   try {
			       while(cursor.hasNext()) {
			       	System.out.println(cursor.next());
			       }
			   } finally {
			       cursor.close();
			   }	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static MongoClient getClient() throws Exception {
		try {
			if(mongoClient==null){
				mongoClient = new MongoClient();
			}else{
				//检查连接是否有效
				try{
					mongoClient.getDB(DataBaseName).getCollectionNames();
				}catch(Exception ex){
					throw new Exception("Connect Error");
				}
			}
			return mongoClient;			 
		}catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
		
	}	
	public static boolean isExistFriend(String profileID,int index){		
		MongoClient mongoClient;
		try {
			mongoClient = getClient();
			DB db = mongoClient.getDB(DataBaseName);			
			DBCollection coll = db.getCollection("FBFriend");
			QueryBuilder myObject = new QueryBuilder();
			DBObject object= myObject.put("profileID").is(profileID).and((new QueryBuilder()).put("Index").is(index).get()).get();
			if(coll.find(object).count()>0){	
				return true;
			}else{
				 return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		 
	}
	public static boolean insertFriend(String profileID,int index,String URL,String content){
		try {
			 MongoClient mongoClient = getClient();
			 DB db = mongoClient.getDB(DataBaseName);			
			 DBCollection coll = db.getCollection("FBFriend");
			 BasicDBObject oneItem = new BasicDBObject("profileID", profileID);
			 oneItem.append("Index", index).append("URL", URL).append("HTML",content);
			 coll.insert(oneItem);
			 return true;			
		}catch(com.mongodb.MongoException.DuplicateKey e){
			LogSys.nodeLogger.error("Friends page键值重复-profileID:"+profileID+" Index:"+index);
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public static String getTimeLine(String profileID){
		
		MongoClient mongoClient;
		try {
			mongoClient = getClient();
			DB db = mongoClient.getDB(DataBaseName);	
			DBCollection coll = db.getCollection("FBTimeLine");
			QueryBuilder myObject = new QueryBuilder();
			DBObject object= myObject.put("profileID").is(profileID).get();
			if(coll.find(object).hasNext()){
				DBObject target=coll.find(object).next();
				return target.get("HTML").toString();
			}else{
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	public static String getFriend(String profileID,int index){
		MongoClient mongoClient;
		try {
			mongoClient = getClient();
			DB db = mongoClient.getDB(DataBaseName);			
			DBCollection coll = db.getCollection("FBFriend");
			QueryBuilder myObject = new QueryBuilder();
			DBObject object= myObject.put("profileID").is(profileID).and((new QueryBuilder()).put("Index").is(index).get()).get();
			if(coll.find(object).hasNext()){
				DBObject target=coll.find(object).next();
				return target.get("HTML").toString();
			}else{
				 return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

}
