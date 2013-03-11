package com.ict.facebook;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		doTest();
	}
	public static int doTest(){
		int i=0;
		try{
			i++;
			System.out.println("before"+i);
		}catch(Exception ex){
			i++;
			System.out.println(i);
			return i++;			
		}finally{
			i++;
			System.out.println("end"+i);
		}
		System.out.println("ss");
		return i;
	}

}
