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

	protected V8 v8;
	private JSClassLoader _jsClassLoader;
	private HashSet<String> _loadedClasses;
	private V8Function f_newClass;
	private V8Function f_classFunction;
	protected JSAThread _jsaThread;

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
			System.gc();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			_jsaThread.syncRun(new Runnable() {
				@Override
				public void run() {
					f_newClass.release();
					f_classFunction.release();
					v8.release(true);
				}
			});
			v8 = null;
			System.gc();
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

	public JSAObject newClass(final String className, final Object... arguments) {
		this.loadJSClass(className);
		final JSA4Java jsa = this;
		JSAFeature<JSAObject> jsRun = new JSAFeature<JSAObject>() {
			public void run() {
				V8Array v8Args = new V8Array(v8);
				V8Array jsArgs = (V8Array)Convertor.java2js(arguments, jsa);
				v8Args.push(className);
				Convertor.pushObject2js(v8Args, jsArgs);
				V8Object jso = (V8Object)f_newClass.call(null, v8Args);
				this.result = (JSAObject)Convertor.js2java(jso, jsa);
				jso.release();
				jsArgs.release();
				v8Args.release();
			}
		};
		_jsaThread.syncRun(jsRun);
		return jsRun.result;
	}

	public Object invokeClassMethod(final String className, final String method, final Object... arguments) {
		this.loadJSClass(className);
		
		final JSA4Java self = this;
		
		JSAFeature<Object> jsRun = new JSAFeature<Object>() {
			public void run() {
				V8Array v8Args = new V8Array(v8);
				V8Array jsArgs = (V8Array)Convertor.java2js((Object)arguments, self);
				v8Args.push(className);
				v8Args.push(method);
				Convertor.pushObject2js(v8Args, jsArgs);
				Object jso = f_classFunction.call(null, v8Args);
				this.result = Convertor.js2java(jso, self);
				Convertor.releaseV8Value(jso);
				jsArgs.release();
				v8Args.release();
			}
		};
		_jsaThread.syncRun(jsRun);
		return jsRun.result;
	}

	private void loadJSClass(final String className) {
		if (!_loadedClasses.contains(className)) {
			String jsaScript = _jsClassLoader.loadJSClass(className);
			if (jsaScript == null) {
				throw new RuntimeException("Can't find JS class " + className);
			}
			{
				jsaScript = jsaScript.replaceAll("\\$super[ ]*\\(", "this.\\$super\\(\"\\$init\"\\)\\(");
				jsaScript = jsaScript.replaceAll("(\\$super)[ ]*\\.[ ]*([0-9a-zA-Z\\$_]+)[ ]*\\(",
						"this\\.$1(\"$2\")\\(");
				final String runJsaScript = jsaScript;
				_jsaThread.syncRun(new Runnable() {
					@Override
					public void run() {
						v8.executeScript(runJsaScript, className, 0);
					}
				});
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
				Object o = ObjectAccessor.constructor(cls, args);
				V8Object jso = (V8Object)Convertor.java2js(o, _jsa);
				return jso;
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		public Object invokeMethod(V8Object obj, String method, V8Array arguments) {
			Object[] args = (Object[]) Convertor.js2java(arguments, _jsa);
			Object jo = _jsa._jsaThread.getJavaReference(obj.getString("$this"));
			Object value = ObjectAccessor.method(jo, method, args);
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
