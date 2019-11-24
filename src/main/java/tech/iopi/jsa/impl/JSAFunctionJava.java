package tech.iopi.jsa.impl;

import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Function;
import com.eclipsesource.v8.V8Object;

import tech.iopi.jsa.JSAFunction;
import tech.iopi.jsa.JSAObject;

class JSAFunctionJava implements JSAFunction {
	
	protected V8Function _jsFunc;
	private JSA4Java _jsa;
	
	public JSAFunctionJava(V8Function jsFunc,JSA4Java jsa) {
		_jsFunc = jsFunc.twin();
		_jsa = jsa;
		jsa._jsaThread.addJsReference(this, _jsFunc);
	}

	public Object call(Object... arguments) {
		return this.apply(null, arguments);
	}

	public Object apply(final JSAObject thisObject, final Object... arguments) {
		JSAFeature<Object> jsRun = new JSAFeature<Object>() {
			public void run() {
				V8Array jsArgs = (V8Array)Convertor.java2js((Object)arguments, _jsa);
				V8Object jsThis = null;
				if(thisObject instanceof JSAObjectJava) {
					jsThis = ((JSAObjectJava)thisObject)._jsObj;
				}
				Object jso = _jsFunc.call(jsThis, jsArgs);
				this.result = Convertor.js2java(jso, _jsa);
				Convertor.releaseV8Value(jso);
				jsArgs.release();
			}
		};
		_jsa._jsaThread.syncRun(jsRun);
		return jsRun.result;
	}

}
