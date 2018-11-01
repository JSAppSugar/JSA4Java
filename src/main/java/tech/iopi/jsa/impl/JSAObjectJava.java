package tech.iopi.jsa.impl;

import tech.iopi.jsa.JSAObject;

import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.NativeJavaObject;

class JSAObjectJava implements JSAObject {
	
	private NativeObject _jsObj;
	
	public JSAObjectJava(NativeObject jsObj) {
		_jsObj = jsObj;
	}

	public Object invokeMethod(String method, Object... arguments) {
		Object value = ScriptableObject.callMethod(_jsObj, method, arguments);
		if(value instanceof NativeJavaObject) {
			value = ((NativeJavaObject)value).unwrap();
		}
		return value;
	}
}
