package com.gdd.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrpty {
	
	public static void main(String[] args){
		Md5("755213779");
	}
	
	
	private static void Md5(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			System.out.println("result: " + buf.toString());// 32λ�ļ���

			System.out.println("result: " + buf.toString().substring(8, 24));// 16λ�ļ���

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
