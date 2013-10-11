package com.gdd.utils;

import java.io.File;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 获取配置资源文件 [公共参数] 信息
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

	
	// test
	public static void main(String arg[]) {

		File f = new File("");

		String absolutePath = f.getAbsolutePath();

		System.out.println(absolutePath);
		System.out.println(ComParam.getParam("Port"));
	}
}
