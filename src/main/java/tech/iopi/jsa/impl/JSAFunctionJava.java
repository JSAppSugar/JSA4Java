package tech.iopi.jsa.impl;

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

	public Object apply(JSAObject thisObject, Object... arguments) {
		if(arguments != null) {
			for(int i=0;i<arguments.length;i++) {
				arguments[i] = Convertor.java2js(arguments[i], _jsa);
			}
		}
		V8Object jsThis = null;
		if(thisObject instanceof JSAObjectJava) {
			jsThis = ((JSAObjectJava)thisObject)._jsObj;
		}
		Object value = _jsFunc.call(jsThis, null);
		return Convertor.js2java(value,_jsa);
	}

}
