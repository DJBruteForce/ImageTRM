package com.sunyard.cm.test;


public class KO {
	
	
	public boolean getBoo() {
		try {
			System.out.println("111111111");
			try {
				System.out.println("2222222222");
				throw new Exception("364826343");
			} catch (Exception e) {
				System.out.println("333333333");
				return false;
			}
		} catch (Exception e) {
			return false;
		} finally {
			System.out.println("44444444444");
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean b = new KO().getBoo();
		System.out.println("=========="+b);
	}

}
