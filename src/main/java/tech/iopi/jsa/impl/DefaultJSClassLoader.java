package tech.iopi.jsa.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import tech.iopi.jsa.JSClassLoader;

/**
 * Load JSA class file from default class path
 * 
 * @author Neal
 *
 */
public class DefaultJSClassLoader implements JSClassLoader {
	
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
	
	public String loadJSClass(String className) {
		String classFilePath = className.replaceAll("\\.", "/") +".js";
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(classFilePath);
		if(in!=null) {
			try { 
				byte[] filecontent = readAllBytes(in);
	            return new String(filecontent, "UTF-8");
			}catch(Exception e) {
				throw new RuntimeException(e);
			}finally {
				try {
					in.close();
				} catch (IOException e) {
					
				}
			}
		}
		return null;
	}
	
	private static byte[] readAllBytes(InputStream in) throws IOException {
		byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
		int capacity = buf.length;
		int nread = 0;
		int n;
		for (;;) {
			while ((n = in.read(buf, nread, capacity - nread)) > 0)
				nread += n;
			if (n < 0)
				break;
			if (capacity <= MAX_BUFFER_SIZE - capacity) {
				capacity = capacity << 1;
			} else {
				if (capacity == MAX_BUFFER_SIZE)
					throw new OutOfMemoryError("Required array size too large");
				capacity = MAX_BUFFER_SIZE;
			}
			buf = Arrays.copyOf(buf, capacity);
		}
		return (capacity == nread) ? buf : Arrays.copyOf(buf, nread);
	}
}
