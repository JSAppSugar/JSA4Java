package tech.iopi.jsa.impl;

import java.util.HashMap;

public abstract class JSAFeature<T> implements Runnable {
	public T result;
	private HashMap<String,Object> results = new HashMap<String,Object>();
	
	public void set(String key,Object value) {
		this.results.put(key, value);
	}
	
	public Object get(String key) {
		return this.results.get(key);
	}
}
