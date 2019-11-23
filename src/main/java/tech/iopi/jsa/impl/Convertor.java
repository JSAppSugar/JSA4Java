package tech.iopi.jsa.impl;

import java.util.Map;

import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8Value;

class Convertor {

	protected static void releaseV8Value(Object value) {
		if (value instanceof V8Value) {
			((V8Value) value).release();
		}
	}

	protected static void pushObject2js(V8Array array, Object value) {
		if (value instanceof V8Value) {
			array.push((V8Value) value);
		} else if (value instanceof String) {
			array.push((String) value);
		} else if (value instanceof Integer) {
			array.push((Integer) value);
		} else if (value instanceof Number) {
			array.push(((Number) value).doubleValue());
		} else if (value instanceof Boolean) {
			array.push((Boolean) value);
		} else {
			array.pushUndefined();
		}
	}

	protected static void setObject2js(V8Object jsObj, String key, Object value) {
		if (value instanceof V8Value) {
			jsObj.add(key, (V8Value) value);
		} else if (value instanceof String) {
			jsObj.add(key, (String) value);
		} else if (value instanceof Integer) {
			jsObj.add(key, (Integer) value);
		} else if (value instanceof Number) {
			jsObj.add(key, ((Number) value).doubleValue());
		} else if (value instanceof Boolean) {
			jsObj.add(key, (Boolean) value);
		} else {
			jsObj.addUndefined(key);
		}
	}

	protected static Object java2js(Object object, JSA4Java jsa) {
		Object jsValue = null;
		if (object instanceof Object[]) {
			V8Array jsArray = new V8Array(jsa.v8);
			jsValue = jsArray;
			Object[] array = (Object[]) object;
			for (int i = 0; i < array.length; i++) {
				Object a = Convertor.java2js(array[i], jsa);
				Convertor.pushObject2js(jsArray, a);
				Convertor.releaseV8Value(a);
			}
		} else if (object instanceof Map) {
			V8Object jsObject = new V8Object(jsa.v8);
			jsValue = jsObject;
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) object;
			for (String key : map.keySet()) {
				Object value = map.get(key);
				value = Convertor.java2js(value, jsa);
				Convertor.setObject2js(jsObject, key, value);
				Convertor.releaseV8Value(value);
			}
		} else if (object instanceof JSAObjectJava) {
			JSAObjectJava jsaObj = (JSAObjectJava) object;
			jsValue = jsaObj._jsObj;
		} else if (object instanceof JSAFunctionJava) {
			JSAFunctionJava jsaFunc = (JSAFunctionJava) object;
			jsValue = jsaFunc._jsFunc;
		} else if (object instanceof String) {
			jsValue = object;
		} else if (object instanceof Integer) {
			jsValue = object;
		} else if (object instanceof Number) {
			jsValue = object;
		} else if (object instanceof Boolean) {
			jsValue = object;
		} else if (object != null) {
			V8Object javaJsRef = new V8Object(jsa.v8);
			String javaRef = jsa._jsaThread.addJavaReference(object);
			javaJsRef.add("$this", javaRef);
			jsValue = javaJsRef;
		}
		return jsValue;
	}

	protected static Object js2java(Object object,JSA4Java jsa) {
		if(object instanceof String ||
				object instanceof Number ||
				object instanceof Boolean ||
				object == null
				) {
			return object;
		}
		else if(object instanceof V8Array) {
			V8Array jsArray = (V8Array)object;
			int l = jsArray.length();
			Object[] array = new Object[l];
			for(int i=0;i<l;i++) {
				Object jso = jsArray.get(i);
				array[i] = Convertor.js2java(jso,jsa);
				Convertor.releaseV8Value(jso);
			}
			object = array;
		}
		else if(object instanceof V8Object) {
			V8Object jsObj = (V8Object)object;
			if(jsObj.contains("constructor")) {
				object = new JSAObjectJava(jsObj, jsa);
				jsa._jsaThread.addJsReference(object, jsObj);
			}
		}
		
//	   else if(object instanceof V8Object) {
//		   V8Object jsObj = (V8Object)object;
//			boolean isClass = false;
//			if(jsObj.contains("prototype")) {
//				
//			}
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
//			
//		}else if(object instanceof NativeFunction) {
//			NativeFunction jsFunc = (NativeFunction)object;
//			object = new JSAFunctionJava(jsFunc, jsa);
//		}else if(object instanceof ConsString) {
//			object = ((ConsString) object).toString();
//		}
		return object;
	}
}
