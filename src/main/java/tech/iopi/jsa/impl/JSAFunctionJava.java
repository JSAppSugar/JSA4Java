package tech.iopi.jsa.impl;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Scriptable;

import tech.iopi.jsa.JSAFunction;
import tech.iopi.jsa.JSAObject;

class JSAFunctionJava implements JSAFunction {
	
	protected NativeFunction _jsFunc;
	private JSA4Java _jsa;
	
	public JSAFunctionJava(NativeFunction jsFunc,JSA4Java jsa) {
		_jsFunc = jsFunc;
		_jsa = jsa;
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
		Scriptable jsThis = null;
		if(thisObject instanceof JSAObjectJava) {
			jsThis = ((JSAObjectJava)thisObject)._jsObj;
		}
		Context cx = JSA4Java.enterContext();
		try {
			Object value = _jsFunc.call(cx, _jsa._scope, jsThis, arguments);
			return Convertor.js2java(value,_jsa);
		}finally {
			Context.exit();
		}
	}

}
