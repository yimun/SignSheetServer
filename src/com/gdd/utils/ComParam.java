package com.gdd.utils;


import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * ��ȡ������Դ�ļ� [��������] ��Ϣ
 * 
 * @author
 */
public class ComParam {
	private static String propertyFileName;
	private static ResourceBundle resourceBundle;


	public static String getParam(String key) {
		propertyFileName = "com/gdd/utils/SysConfig";
		resourceBundle = ResourceBundle.getBundle(propertyFileName);
		if (key == null || key.equals("") || key.equals("null")) {
			return "";
		}
		String result = "";
		try {
			result = resourceBundle.getString(key);
		} catch (MissingResourceException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String arg[]){

		System.out.println(ComParam.getParam("Port"));
	}
}
