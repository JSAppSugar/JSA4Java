package tech.iopi.jsa.impl;

import tech.iopi.jsa.JSAObject;

import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

class JSAObjectJava implements JSAObject {
	
	protected V8Object _jsObj;
	private JSA4Java _jsa;
	
	public JSAObjectJava(V8Object jsObj,JSA4Java jsa) {
		_jsObj = jsObj.twin();
		_jsa = jsa;
		jsa._jsaThread.addJsReference(this, _jsObj);
	}

	public Object invokeMethod(final String method, final Object... arguments) {
		JSAFeature<Object> jsRun = new JSAFeature<Object>() {
			public void run() {
				V8Array jsArgs = (V8Array)Convertor.java2js((Object)arguments, _jsa);
				Object jso = _jsObj.executeFunction(method, jsArgs);
				this.result = Convertor.js2java(jso, _jsa);
				Convertor.releaseV8Value(jso);
				jsArgs.release();
			}
		};
		_jsa._jsaThread.syncRun(jsRun);
		return jsRun.result;
	}
}
