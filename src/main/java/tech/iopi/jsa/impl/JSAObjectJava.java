package tech.iopi.jsa.impl;

import tech.iopi.jsa.JSAObject;

import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;

class JSAObjectJava implements JSAObject {
	
	private NativeObject _jsObj;
	private JSA4Java _jsa;
	
	public JSAObjectJava(NativeObject jsObj,JSA4Java jsa) {
		_jsObj = jsObj;
		_jsa = jsa;
	}

	public Object invokeMethod(String method, Object... arguments) {
		if(arguments != null) {
			for(int i=0;i<arguments.length;i++) {
				arguments[i] = Convertor.java2js(arguments[i], _jsa._scope);
			}
		}
		Object value = ScriptableObject.callMethod(_jsObj, method, arguments);
		return Convertor.js2java(value);
	}
}
