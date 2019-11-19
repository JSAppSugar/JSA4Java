package tech.iopi.jsa.impl;

import java.util.HashSet;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Function;
import com.eclipsesource.v8.V8Object;

import tech.iopi.jsa.JSAObject;
import tech.iopi.jsa.JSAppSugar;
import tech.iopi.jsa.JSClassLoader;

/**
 * 
 * @author Neal
 *
 */
public class JSA4Java extends Object implements JSAppSugar {

	private V8 v8;
	private JSClassLoader _jsClassLoader;
	private HashSet<String> _loadedClasses;
	private V8Function f_newClass;
	private V8Function f_classFunction;
	private JSAThread _jsaThread;

	public JSA4Java() {
		_loadedClasses = new HashSet<String>();
		_jsaThread = new JSAThread();
		_jsaThread.start();
	}

	/**
	 * Set up a JS class loader that you implement on your own.
	 * 
	 * @param loader A implementation of JSClassLoader.
	 */
	public void setJSClassLoader(JSClassLoader loader) {
		_jsClassLoader = loader;
	}

	/**
	 * stop the engine
	 */
	public void stopEngine() {
		if (v8 != null) {
			v8.release(true);
		}
	}

	/**
	 * start the engine
	 */
	public void startEngine() {
		this.startEngine(new DefaultJSClassLoader());
	}

	/**
	 * Start engine with given JSClassLoader
	 * 
	 * @param loader A implementation of JSClassLoader.
	 */
	public void startEngine(JSClassLoader loader) {
		if (v8 == null) {
			{
				JSAFeature<V8> jsRun = new JSAFeature<V8>() {
					public void run() {
						this.result = V8.createV8Runtime();
					}
				};
				_jsaThread.syncRun(jsRun);
				v8 = jsRun.result;
			}
			final String jsaScript = loader.loadJSClass("JSAppSugar");
			final String jsa4JScript = loader.loadJSClass("JSA4Java");
			if (jsaScript == null) {
				throw new RuntimeException("JSAppSugar.js not found");
			}
			if (jsa4JScript == null) {
				throw new RuntimeException("JSA4Java.js not found");
			}
			{
				final JSContext context = new JSContext(this);

				JSAFeature<V8> jsRun = new JSAFeature<V8>() {
					public void run() {
						V8Object v8Context = new V8Object(v8);
						v8Context.registerJavaMethod(context, "importJSClass", "importJSClass",
								new Class<?>[] { String.class });
						v8Context.registerJavaMethod(context, "newClass", "newClass",
								new Class<?>[] { String.class, V8Array.class });
						v8Context.registerJavaMethod(context, "invokeMethod", "invokeMethod",
								new Class<?>[] { V8Object.class, String.class, V8Array.class });
						v8Context.registerJavaMethod(context, "invokeClassMethod", "invokeClassMethod",
								new Class<?>[] { String.class, String.class, V8Array.class });
						v8.add("$context", v8Context);
						v8Context.release();

						V8Object v8Out = new V8Object(v8);
						v8Out.registerJavaMethod(System.out, "println", "println", new Class<?>[] { String.class });
						v8.add("$out", v8Out);
						v8Out.release();

						v8.executeScript(jsa4JScript, "JSA4Java", 0);
						v8.executeScript(jsaScript, "JSAppSugar", 0);

						V8Function f_newClass = (V8Function) v8.getObject("$newClass");
						V8Function f_classFunction = (V8Function) v8.getObject("$classFunction");
						this.set("f_newClass", f_newClass);
						this.set("f_classFunction", f_classFunction);
					}
				};
				_jsaThread.syncRun(jsRun);

				f_newClass = (V8Function) jsRun.get("f_newClass");
				f_classFunction = (V8Function) jsRun.get("f_classFunction");
			}
			if (_jsClassLoader == null) {
				_jsClassLoader = loader;
			}
		}
	}

	public JSAObject newClass(String className, Object... arguments) {
		this.loadJSClass(className);
		V8Object jsObj = null;
		Object jsArgs = Convertor.java2js(arguments, this);
		Object[] callArgs = { className, jsArgs };
		V8Array v8Args = new V8Array(v8);
		// TODO call args
		for (Object obj : callArgs) {
			v8Args.push(obj.toString());
		}
		jsObj = (V8Object) f_newClass.call(null, v8Args);

		if (jsObj != null) {
			return new JSAObjectJava(null, this);
		}
		return null;
	}

	public Object invokeClassMethod(String className, String method, Object... arguments) {
		this.loadJSClass(className);
		Object value = null;
//		Context cx = JSA4Java.enterContext();
//		try {
//			Object jsArgs = Convertor.java2js(arguments, this);
//			Object[] callArgs = {className,method,jsArgs};
//			value = f_classFunction.call(cx, _scope, _scope, callArgs);
//			value = Convertor.js2java(value,this);
//		}finally {
//			Context.exit();
//		}
		return value;
	}

	private void loadJSClass(String className) {
		if (!_loadedClasses.contains(className)) {
			String jsaScript = _jsClassLoader.loadJSClass(className);
			if (jsaScript == null) {
				throw new RuntimeException("Can't find JS class " + className);
			}
			{
				jsaScript = jsaScript.replaceAll("\\$super[ ]*\\(", "this.\\$super\\(\"\\$init\"\\)\\(");
				jsaScript = jsaScript.replaceAll("(\\$super)[ ]*\\.[ ]*([0-9a-zA-Z\\$_]+)[ ]*\\(",
						"this\\.$1(\"$2\")\\(");
				v8.executeScript(jsaScript, className, 0);
				_loadedClasses.add(className);
			}
		}
	}

	public static class JSContext {

		private JSA4Java _jsa;

		public JSContext(JSA4Java jsa) {
			this._jsa = jsa;
		}

		public void importJSClass(String className) {
			_jsa.loadJSClass(className);
		}

		public Object newClass(String className, V8Array arguments) {
			Object[] args = (Object[]) Convertor.js2java(arguments, _jsa);
			Class<?> cls;
			try {
				cls = Class.forName(className);
				return ObjectAccessor.constructor(cls, args);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		public Object invokeMethod(V8Object obj, String method, V8Array arguments) {
			Object[] args = (Object[]) Convertor.js2java(arguments, _jsa);
			Object value = ObjectAccessor.method(obj, method, args);
			return Convertor.java2js(value, _jsa);
		}

		public Object invokeClassMethod(String className, String method, V8Array arguments) {
			Object[] args = (Object[]) Convertor.js2java(arguments, _jsa);
			Class<?> cls = null;
			try {
				cls = Class.forName(className);
			} catch (ClassNotFoundException e) {

			}
			if (cls != null) {
				Object value = ObjectAccessor.method(cls, method, args);
				return Convertor.java2js(value, _jsa);
			}
			return null;
		}
	}

}
