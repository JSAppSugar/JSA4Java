package tech.iopi.jsa.impl;

import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

class Convertor {
	
	
	protected static Object java2js(Object object,Scriptable scope) {
		if(object instanceof Object[]) {
			Object[] array = (Object[]) object;
			for(int i=0;i<array.length;i++) {
				array[i] = Convertor.java2js(array[i],scope);
			}
			Context cx = Context.enter();
			try {
				object = cx.newArray(scope, array);
			}finally {
				Context.exit();
			}
		}else if(object instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<Object,Object> map = (Map<Object,Object>)object;
			Context cx = Context.enter();
			try {
				Scriptable jsObj = cx.newObject(scope);
				for(Object key : map.keySet()) {
					Object value = map.get(key);
					value = Convertor.java2js(value,scope);
					jsObj.put(key.toString(), jsObj, value);
				}
				object = jsObj;
			}finally {
				Context.exit();
			}
		}
		return object;
	}
	
	protected static Object js2java(Object object) {
		if(object instanceof NativeJavaObject) {
			object = ((NativeJavaObject)object).unwrap();
		}else if(object instanceof NativeObject) {
			NativeObject jsObj = (NativeObject)object;
			HashMap<String,Object> map = new HashMap<String,Object>();
			for(Object key : jsObj.keySet()) {
				Object value = jsObj.get(key);
				value = Convertor.js2java(value);
				map.put(key.toString(), value);
			}
			object = map;
		}else if(object instanceof NativeArray) {
			NativeArray jsArray = (NativeArray)object;
			int l = (int) jsArray.getLength();
			Object[] array = new Object[l];
			for(int i=0;i<l;i++) {
				array[i] = Convertor.js2java(jsArray.get(i));
			}
			object = array;
		}
		return object;
	}
}
