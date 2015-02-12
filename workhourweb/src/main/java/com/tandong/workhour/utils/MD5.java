/**  
 * $Id: MD5.java 7677 2012-01-13 09:53:38Z tandong $
 * Copyright @Expoint.com.cn All Right Reserved
 */
package com.tandong.workhour.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * 
 * @author tandong
 * @date 2011-12-29 上午10:38:56
 * @version V1.0
 */
public class MD5 {
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static String encode(String origin) {
		String resultString = null;
		try {
			resultString = origin;
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString
					.getBytes()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultString;
	}

	public static void main(String[] args) {
		int a = 5;
		if(a<6){
			System.out.println("1");}
		if(a<10){
			System.out.println("2");
		}
		System.out.println("tandong:" + MD5.encode("tandong"));
//		String url = "C:\\Users\\faywang\\Desktop\\资源包\\fota-interface.war";
//		File file = new File(url);
//		try {
//			FileInputStream fileInputStream = new FileInputStream(file);
//			System.out.println(MD5.getMD5(fileInputStream));
//			fileInputStream.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		System.out.println(MD5.encode("DLXUHTC0"));
	}

	public static String getMD5(InputStream stream) {
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[2048];
			int length = -1;
			long startDate = System.currentTimeMillis();
			while ((length = stream.read(buffer)) != -1) {
				md.update(buffer, 0, length);
			}
			byte[] b = md.digest();
			long endDate = System.currentTimeMillis();
			System.out.println("本次文件HASH共花费" + (endDate - startDate) / 1000
					+ "秒");
			return byteToHexString(b);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			try {
				stream.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/****
	 * 对文件全文生成MD5摘要
	 * 
	 * @param file
	 *            要加密的文件
	 * @return MD5摘要码
	 */
	static char hexdigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static String byteToHexString(byte[] tmp) {
		String s;
		char str[] = new char[16 * 2];
		int k = 0;
		for (int i = 0; i < 16; i++) {
			byte byte0 = tmp[i];
			str[k++] = hexdigits[byte0 >>> 4 & 0xf];
			str[k++] = hexdigits[byte0 & 0xf];
		}
		s = new String(str).toUpperCase();
		return s;
	}

}
