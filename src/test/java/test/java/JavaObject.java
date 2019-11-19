package test.java;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Field;
import java.util.Map;

import tech.iopi.jsa.JSAFunction;
import tech.iopi.jsa.JSAObject;

public class JavaObject {
	private String _s = "-";
	private int _i = 0;
	
	public static String staticA() {
		return "a";
	}
	
	public JavaObject() {
		
	}
	
	public JavaObject(String s,int i) {
		this._s = s;
		this._i = i;
	}
	
	public JavaObject(Map<String,Object> m) {
		_s = (String)m.get("s");
		_i = (Integer)m.get("i");
	}

	public String getS() {
		return this._s;
	}
	
	public int getI() {
		return this._i;
	}
	
	public Object testNull(Object undefined) {
		if(undefined == null) {
			return null;
		}
		return "null";
	}
	
	public String testString(String s) {
		if(s != null && s instanceof String) {
			return s;
		}
		return null;
	}
	
	public int testInt(int i) {
		if( i!=0 ) {
			return i;
		}
		return 0;
	}
	
	public boolean testBool(boolean b) {
		if(b) {
			return b;
		}
		return false;
	}
	
	public Map<String,Object> testMap(Map<String,Object> m){
		if(m.size() >0 ) {
			Number a = (Number)m.get("a");
			String b = (String)m.get("b");
			Object o = m.get("o");
			Object f = m.get("f");
			Object s = m.get("s");
			if(a.intValue() == 1 
					&&b.equals("1")
					&&o.getClass().getName().equals("test.java.JavaObject")
					&& (f instanceof JSAFunction)
					&& (s instanceof JSAObject)
			) {
				return m;
			}
		}
		return null;
	}
	
	public Object[] testArray(Object[] array) {
		if(array.length>0) {
			Number a = (Number)array[0];
			String b = (String)array[1];
			if(a.intValue() == 1 &&b.equals("1")) {
				return array;
			}
		}
		return null;
	}
	
	public Object testObject(Object o) {
		if(o.getClass() == Object.class) {
			return o;
		}
		return null;
	}
	
	public JSAObject testJSAObject(JSAObject jsaObject) {
		if(jsaObject instanceof JSAObject) {
			String a = (String)jsaObject.invokeMethod("getA");
			if(a.equals("a")) {
				return jsaObject;
			}
		}
		return null;
	}
	
	public JSAFunction testJSAFunction(JSAFunction jsaFunc) {
		String r = (String)jsaFunc.call("f");
		if(r.equals("f")) {
			return jsaFunc;
		}
		return null;
	}
	
	public static class TPR<T>  extends PhantomReference<T>{

		public TPR(T referent, ReferenceQueue<? super T> q) {
			super(referent, q);
		}
		
		public String a;
		
	}
	
	public static boolean isRun = true;
	public static void main(String[] args) throws Exception {
		System.out.println("begin");
		final ReferenceQueue<JavaObject> referenceQueue = new ReferenceQueue<JavaObject>();
		new Thread() {
            @SuppressWarnings("unchecked")
			public void run() {
                while (isRun) {
                	System.out.println("1");
					TPR<JavaObject> obj = null;
					try {
						obj = (TPR<JavaObject>) referenceQueue.remove();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                	System.out.println("2");
                    if (obj != null) {
                        try {
                            //Object result = rereferent.get(obj);
                        	TPR<JavaObject>  result = obj;
                            System.out.println("gc will collect："
                                    + result.getClass() + "@"
                                    + result.a);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
        JavaObject jo = new JavaObject();
        jo._s = "ok";
        TPR<JavaObject> joWeakRef = new TPR<JavaObject>(jo, referenceQueue);
        joWeakRef.a = "aa";
        System.out.println(joWeakRef);
        jo = null;
        System.out.println("null");
        Thread.sleep(3000);
        System.out.println("before gc");
        System.gc();
        System.out.println("end gc");
        Thread.sleep(3000);
        System.out.println("null over");
        isRun = false;
		System.out.println("end");
	}
	
}
