package tech.iopi.jsa.impl;

import org.mozilla.javascript.Context;
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
	
	public JSA4Java() {
		
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
			try {
				_scope = cx.initStandardObjects();
				loadJSClass("JSAppSugar");
			}finally {
				Context.exit();
			}
		}
	}
	
	private void loadJSClass(String className) {
		DefaultJSClassLoader defaultLoader = new DefaultJSClassLoader();
		if(_jsClassLoader == null) {
			_jsClassLoader = defaultLoader;
		}
		String jsaScript = defaultLoader.loadJSClass(className);
		if(jsaScript == null) {
			throw new RuntimeException("Can't find JS class "+className);
		}
		Context cx = Context.enter();
		try {
			cx.evaluateString(_scope, jsaScript, className, 1, null);
		}finally {
			Context.exit();
		}
	}

	public JSAObject newClass(String className, Object... arguments) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object invokeClassMethod(String className, String method, Object... arguments) {
		// TODO Auto-generated method stub
		return null;
	}

}
