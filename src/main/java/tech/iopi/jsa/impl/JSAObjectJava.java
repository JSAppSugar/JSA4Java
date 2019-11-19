package tech.iopi.jsa.impl;

import tech.iopi.jsa.JSAObject;

import com.eclipsesource.v8.V8Object;

class JSAObjectJava implements JSAObject {
	
	protected V8Object _jsObj;
	private JSA4Java _jsa;
	
	public JSAObjectJava(V8Object jsObj,JSA4Java jsa) {
		_jsObj = jsObj;
		_jsa = jsa;
	}

	public Object invokeMethod(String method, Object... arguments) {
		if(arguments != null) {
			for(int i=0;i<arguments.length;i++) {
				arguments[i] = Convertor.java2js(arguments[i], _jsa);
			}
		}
		Object value = _jsObj.executeFunction(method, null);
		return Convertor.js2java(value,_jsa);
	}
}
