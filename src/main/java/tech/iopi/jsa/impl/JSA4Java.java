package tech.iopi.jsa.impl;

import java.util.HashSet;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import tech.iopi.jsa.JSAObject;
import tech.iopi.jsa.JSAppSugar;
import tech.iopi.jsa.JSClassLoader;


/**
 * 
 * @author Neal
 *
 */
public class JSA4Java implements JSAppSugar {
	
	protected Scriptable _scope;
	private JSClassLoader _jsClassLoader;
	private HashSet<String> _loadedClasses;
	
	private Function f_newClass;
	private Function f_classFunction;
	
	public JSA4Java() {
		_loadedClasses = new HashSet<>();
	}
	
	/**
	 * Set up a JS class loader that you implement on your own.
	 * @param loader A implementation of JSClassLoader.
	 */
	public void setJSClassLoader(JSClassLoader loader) {
		_jsClassLoader = loader;
	}
	
	/**
	 * start engine
	 */
	public void startEngine() {
		this.startEngine(new DefaultJSClassLoader());
	}
	
	
	/**
	 * Start engine with given JSClassLoader
	 * @param loader A implementation of JSClassLoader.
	 */
	public void startEngine(JSClassLoader loader) {
		if(_scope == null) {
			Context cx = Context.enter();
			String jsaScript = loader.loadJSClass("JSAppSugar");
			String jsa4JScript = loader.loadJSClass("JSA4Java");
			if(jsaScript == null) {
				throw new RuntimeException("JSAppSugar.js not found");
			}
			if(jsa4JScript == null) {
				throw new RuntimeException("JSA4Java.js not found");
			}
			try {
				_scope = cx.initStandardObjects();
				
				JSContext context = new JSContext(this);
				Object wrappedContext = Context.javaToJS(context, _scope);
				ScriptableObject.putProperty(_scope, "$context", wrappedContext);
				
				Object wrappedOut = Context.javaToJS(System.out, _scope);
				ScriptableObject.putProperty(_scope, "$out", wrappedOut);
				
				cx.evaluateString(_scope, jsa4JScript, "JSA4Java", 1, null);
				cx.evaluateString(_scope, jsaScript, "JSAppSugar", 1, null);
				f_newClass = (Function)_scope.get("$newClass", _scope);
				f_classFunction = (Function)_scope.get("$classFunction", _scope);
			}finally {
				Context.exit();
			}
			if(_jsClassLoader == null) {
				_jsClassLoader = loader;
			}
		}
	}

	public JSAObject newClass(String className, Object... arguments) {
		this.loadJSClass(className);
		NativeObject jsObj = null;
		Context cx = Context.enter();
		try {
			Object jsArgs = Convertor.java2js(arguments, this);
			Object[] callArgs = {className , jsArgs};
			jsObj = (NativeObject)f_newClass.call(cx, _scope, _scope, callArgs);
		}finally {
			Context.exit();
		}
		if(jsObj != null) {
			return new JSAObjectJava(jsObj,this);
		}
		return null;
	}

	public Object invokeClassMethod(String className, String method, Object... arguments) {
		this.loadJSClass(className);
		Object value = null;
		Context cx = Context.enter();
		try {
			Object jsArgs = Convertor.java2js(arguments, this);
			Object[] callArgs = {className,method,jsArgs};
			value = f_classFunction.call(cx, _scope, _scope, callArgs);
			value = Convertor.js2java(value,this);
		}finally {
			Context.exit();
		}
		return value;
	}
	
	private void loadJSClass(String className) {
		if(!_loadedClasses.contains(className)) {
			String jsaScript = _jsClassLoader.loadJSClass(className);
			if(jsaScript == null) {
				throw new RuntimeException("Can't find JS class "+className);
			}
			Context cx = Context.enter();
			try {
				jsaScript = jsaScript.replaceAll("\\$super[ ]*\\(", "this.\\$super\\(\"\\$init\"\\)\\(");
				jsaScript = jsaScript.replaceAll("(\\$super)[ ]*\\.[ ]*([0-9a-zA-Z\\$_]+)[ ]*\\(", "this\\.$1(\"$2\")\\(");
				cx.evaluateString(_scope, jsaScript, className, 1, null);
				_loadedClasses.add(className);
			}finally {
				Context.exit();
			}
		}
	}
	
	public static class JSContext{
		
		private JSA4Java _jsa;
		
		public JSContext(JSA4Java jsa) {
			this._jsa = jsa;
		}
		
		public void importJSClass(String className) {
			_jsa.loadJSClass(className);
		}
		
		public Object newClass(String className,NativeArray arguments) {
			Object[] args = (Object[])Convertor.js2java(arguments, _jsa);
			Class<?> cls;
			try {
				cls = Class.forName(className);
				return ObjectAccessor.constructor(cls,args);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		
		public Object invokeMethod(Object obj,String method,NativeArray arguments) {
			Object[] args = (Object[])Convertor.js2java(arguments, _jsa);
			Object value = ObjectAccessor.method(obj, method, args);
			return Convertor.java2js(value, _jsa);
		}
		
		public Object invokeClassMethod(String className,String method,NativeArray arguments) {
			Object[] args = (Object[])Convertor.js2java(arguments, _jsa);
			Class<?> cls = null;
			try {
				cls = Class.forName(className);
			} catch (ClassNotFoundException e) {
				
			}
			if(cls!=null) {
				Object value = ObjectAccessor.method(cls, method, args);
				return Convertor.java2js(value, _jsa);
			}
			return null;
		}
	}

}
