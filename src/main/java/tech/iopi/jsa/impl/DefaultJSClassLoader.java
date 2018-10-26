package tech.iopi.jsa.impl;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import tech.iopi.jsa.JSClassLoader;

/**
 * Load JSA class file from default class path
 * 
 * @author Neal
 *
 */
public class DefaultJSClassLoader implements JSClassLoader {
	
	public String loadJSClass(String className) {
		String classFilePath = className.replaceAll("\\.", "/") +".js";
		URL url = Thread.currentThread().getContextClassLoader().getResource(classFilePath);
		if(url!=null) {
			File file = new File(url.getFile());
			Long filelength = file.length();
			byte[] filecontent = new byte[filelength.intValue()];
			try {
				FileInputStream in = new FileInputStream(file);  
	            in.read(filecontent);  
	            in.close();
	            return new String(filecontent, "UTF-8");
			}catch(Exception e) {
				
			}
		}
		return null;
	}
}
