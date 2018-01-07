package com.test.cmapi;


public class CmTest01 {
	
	public String toWord() {
		
		String a = "1199";
		
		try {
			System.out.println("===="+a);
			if(a.equals("1199")) {
				throw new Exception("异常XXXXXXXX");
			}
		} catch(Exception ex){
			System.out.println("1打印a异常了"+ex);
			return a;
		}
		
		try {
			a = "2000";
			if(a.equals("2000")) {
				throw new Exception("异常XXXXXXXX");
			}
			return a;
		} catch(Exception e){
			System.out.println("2打印a异常了"+e);
			return a;
		} finally {
			System.out.println("==关闭资源==");
		}
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String str = new CmTest01().toWord();
		System.out.println("===="+str);
	}

}
