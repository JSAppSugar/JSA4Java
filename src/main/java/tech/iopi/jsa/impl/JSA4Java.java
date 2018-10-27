package tech.iopi.jsa.impl;

import java.util.HashSet;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import tech.iopi.jsa.JSAObject;
import tech.iopi.jsa.JSAppSugar;
import tech.iopi.jsa.JSClassLoader;


/**
 * 
 * @author Neal
 *
 */
public class JSA4Java implements JSAppSugar {
	
	private Scriptable _scope;
	private JSClassLoader _jsClassLoader;
	private HashSet<String> _loadedClasses;
	
	private Function f_newClass;
	
	public JSA4Java() {
		_loadedClasses = new HashSet<>();
	}
	
	public void setJSClassLoader(JSClassLoader loader) {
		_jsClassLoader = loader;
	}
	
	/**
	 * start the engine
	 */
	public void startEngine() {
		if(_scope == null) {
			Context cx = Context.enter();
			DefaultJSClassLoader defaultLoader = new DefaultJSClassLoader();
			String jsaScript = defaultLoader.loadJSClass("JSAppSugar");
			try {
				_scope = cx.initStandardObjects();
				cx.evaluateString(_scope, jsaScript, "JSAppSugar", 1, null);
				f_newClass = (Function)_scope.get("$newClass", _scope);
			}finally {
				Context.exit();
			}
			if(_jsClassLoader == null) {
				_jsClassLoader = defaultLoader;
			}
		}
	}
	
	private void loadJSClass(String className) {
		if(!_loadedClasses.contains(className)) {
			String jsaScript = _jsClassLoader.loadJSClass(className);
			if(jsaScript == null) {
				throw new RuntimeException("Can't find JS class "+className);
			}
			Context cx = Context.enter();
			try {
				cx.evaluateString(_scope, jsaScript, className, 1, null);
				_loadedClasses.add(className);
			}finally {
				Context.exit();
			}
		}
	}

	public JSAObject newClass(String className, Object... arguments) {
		this.loadJSClass(className);
		NativeObject jsObj = null;
		Context cx = Context.enter();
		try {
			Object jsArgs = cx.newArray(_scope, arguments);
			Object[] callArgs = {className , jsArgs};
			jsObj = (NativeObject)f_newClass.call(cx, _scope, _scope, callArgs);
		}finally {
			Context.exit();
		}
		if(jsObj != null) {
			return new JSAObjectJava(jsObj);
		}
		return null;
	}

	public Object invokeClassMethod(String className, String method, Object... arguments) {
		// TODO Auto-generated method stub
		return null;
	}

}
