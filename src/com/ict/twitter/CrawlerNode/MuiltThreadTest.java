package com.ict.twitter.CrawlerNode;

class MuiltThreadTest implements Runnable {
	private Object lock=new Object();
    public void run() {
        for(int i=0;i<10;++i){
        	synchronized (lock) {
				mm();
			}
               
            
        }
    }
    public  void mm(){
    	 if(MuiltThreadTest.count>0){
             try{
                 Thread.sleep(1000);
             }catch(InterruptedException e){
                 e.printStackTrace();
             }
             System.out.println(MuiltThreadTest.count--);
         }
    }
 
    public static void main(String[] args) {
    	MuiltThreadTest he=new MuiltThreadTest();
        Thread h1=new Thread(he);
        Thread h2=new Thread(he);
        Thread h3=new Thread(he);
        h1.start();
        h2.start();
        h3.start();
    }
    private static int count=5;
}