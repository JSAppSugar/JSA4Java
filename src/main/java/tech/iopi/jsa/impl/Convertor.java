package tech.iopi.jsa.impl;

import java.util.HashMap;
import java.util.Map;

class Convertor {
	
	protected static Object java2js(Object object,JSA4Java jsa) {
		if(object instanceof Object[]) {
			Object[] array = (Object[]) object;
			for(int i=0;i<array.length;i++) {
				array[i] = Convertor.java2js(array[i],jsa);
			}
		}else if(object instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<Object,Object> map = (Map<Object,Object>)object;
			for(Object key : map.keySet()) {
				Object value = map.get(key);
				value = Convertor.java2js(value,jsa);
			}
		}else if(object instanceof JSAObjectJava) {
			JSAObjectJava jsaObj = (JSAObjectJava)object;
			object = jsaObj._jsObj;
		}else if(object instanceof JSAFunctionJava) {
			JSAFunctionJava jsaFunc = (JSAFunctionJava)object;
			object = jsaFunc._jsFunc;
		}
		return object;
	}
	
	protected static Object js2java(Object object,JSA4Java jsa) {
//		if(object instanceof NativeJavaObject) {
//			object = ((NativeJavaObject)object).unwrap();
//		}else if(object instanceof NativeObject) {
//			NativeObject jsObj = (NativeObject)object;
//			
//			boolean isClass = false;
//			Scriptable prototype = jsObj.getPrototype();
//			Object constructor =  prototype.get("constructor", prototype);
//			if(constructor instanceof Scriptable) {
//				if(ScriptableObject.hasProperty((Scriptable)constructor, "$name")) {
//					isClass = true;
//				}
//			}
//			
//			if(isClass) {
//				if(jsObj.has("$this", jsObj)) {
//					Object wrapThis = jsObj.get("$this", jsObj);
//					if(wrapThis instanceof NativeJavaObject) {
//						object = ((NativeJavaObject)wrapThis).unwrap();
//					}else {
//						object = wrapThis;
//					}
//				}else {
//					object = new JSAObjectJava(jsObj, jsa);
//				}
//			}else {
//				HashMap<String,Object> map = new HashMap<String,Object>();
//				for(Object key : jsObj.keySet()) {
//					Object value = jsObj.get(key);
//					value = Convertor.js2java(value,jsa);
//					map.put(key.toString(), value);
//				}
//				object = map;
//			}
//		}else if(object instanceof NativeArray) {
//			NativeArray jsArray = (NativeArray)object;
//			int l = (int) jsArray.getLength();
//			Object[] array = new Object[l];
//			for(int i=0;i<l;i++) {
//				array[i] = Convertor.js2java(jsArray.get(i),jsa);
//			}
//			object = array;
//		}else if(object instanceof NativeFunction) {
//			NativeFunction jsFunc = (NativeFunction)object;
//			object = new JSAFunctionJava(jsFunc, jsa);
//		}else if(object instanceof ConsString) {
//			object = ((ConsString) object).toString();
//		}
		return object;
	}
}
