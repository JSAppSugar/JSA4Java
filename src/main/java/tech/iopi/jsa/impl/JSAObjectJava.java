package tech.iopi.jsa.impl;

import tech.iopi.jsa.JSAObject;

import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;

public class JSAObjectJava implements JSAObject {
	
	private NativeObject _jsObj;
	
	public JSAObjectJava(NativeObject jsObj) {
		_jsObj = jsObj;
	}

	@Override
	public Object invokeMethod(String method, Object... arguments) {
		return ScriptableObject.callMethod(_jsObj, method, arguments);
	}

}
