package tech.iopi.jsa.impl;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

class Convertor {
	
	protected static Object java2js(Object object,Scriptable scope) {
		if(object instanceof Object[]) {
			Object[] array = (Object[]) object;
			Context cx = Context.enter();
			try {
				object = cx.newArray(scope, array);
			}finally {
				Context.exit();
			}
		}else {
			
		}
		return object;
	}
	
	protected static Object js2java(Object object) {
		if(object instanceof NativeJavaObject) {
			object = ((NativeJavaObject)object).unwrap();
		}
		return object;
	}
}
