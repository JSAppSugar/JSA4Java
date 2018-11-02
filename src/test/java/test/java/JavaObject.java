package test.java;

import java.util.Map;

import tech.iopi.jsa.JSAFunction;
import tech.iopi.jsa.JSAObject;

public class JavaObject {
	private String _s = "-";
	private int _i = 0;
	
	public JavaObject() {
		
	}
	
	public JavaObject(String s,int i) {
		this._s = s;
		this._i = i;
	}
	
	public JavaObject(Map<String,Object> m) {
		_s = (String)m.get("s");
		_i = (int)m.get("i");
	}

	public String getS() {
		return this._s;
	}
	
	public int getI() {
		return this._i;
	}
	
	public Object testNull(Object undefined) {
		if(undefined == null) {
			return null;
		}
		return "null";
	}
	
	public String testString(String s) {
		if(s != null && s instanceof String) {
			return s;
		}
		return null;
	}
	
	public int testInt(int i) {
		if( i!=0 ) {
			return i;
		}
		return 0;
	}
	
	public boolean testBool(boolean b) {
		if(b) {
			return b;
		}
		return false;
	}
	
	public Map<String,Object> testMap(Map<String,Object> m){
		if(m.size() >0 ) {
			Number a = (Number)m.get("a");
			String b = (String)m.get("b");
			if(a.intValue() == 1 &&b.equals("1")) {
				return m;
			}
		}
		return null;
	}
	
	public Object[] testArray(Object[] array) {
		if(array.length>0) {
			return array;
		}
		return null;
	}
	
	public Object testObject(Object o) {
		return o;
	}
	
	public JSAFunction testFunction(JSAFunction func) {
		if(func instanceof JSAFunction) {
			return func;
		}
		return null;
	}
	
	public JSAObject testJSAObject(JSAObject jsaObject) {
		if(jsaObject instanceof JSAObject) {
			return jsaObject;
		}
		return null;
	}
	
	
}
