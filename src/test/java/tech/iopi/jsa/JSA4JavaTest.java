package tech.iopi.jsa;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import java.util.HashMap;
import tech.iopi.jsa.impl.JSA4Java;
import tech.iopi.jsa.JSAObject;
import test.java.JavaObject;

/**
 * Unit test for simple App.
 */
public class JSA4JavaTest{
	
	private static JSA4Java jsa;
	
	@BeforeClass
	public static void beforeClass() {
		JSA4Java jsa4Java = new JSA4Java();
		jsa4Java.setMainthread(new TestMainThread());
		jsa4Java.startEngine();
		jsa = jsa4Java;
	}
	
	@AfterClass
	public static void afterClass() {
		jsa.stopEngine();
	}
	
	@Test
	public void testLoadJSModule() {
		assertNotNull(jsa);
	}
	
	@Test
	public void jsSuperTest() {
		JSAObject testObject = jsa.newClass("test.jsa.TestObjectB","a","b");
		String b = (String)testObject.invokeMethod("getB");
		assertEquals("ab", b);
	}
	
	@Test
	public void newJSClassTest() {
		{
			Object[] args = {null};
			JSAObject testObject = jsa.newClass("test.jsa.TestObject",args);
			Object a = testObject.invokeMethod("getA");
			assertEquals("-", a);
		}
		{
			JSAObject testObject = jsa.newClass("test.jsa.TestObject","a");
			String a = (String)testObject.invokeMethod("getA");
			assertEquals("a", a);
		}
		{
			JSAObject testObject = jsa.newClass("test.jsa.TestObject",1);
			int a = (Integer)testObject.invokeMethod("getA");
			assertEquals(1, a);
		}
		{
			JSAObject testObject = jsa.newClass("test.jsa.TestObject",true);
			boolean a = (Boolean)testObject.invokeMethod("getA");
			assertEquals(true, a);
		}
		{
			HashMap<String,Object> m = new HashMap<String,Object>();
			m.put("a",1);
			m.put("b","b");
			JSAObject testObject = jsa.newClass("test.jsa.TestObject",m);
			@SuppressWarnings("unchecked")
			HashMap<String,Object> a = (HashMap<String,Object>)testObject.invokeMethod("getA");
			assertEquals(1, a.get("a"));
			assertEquals("b", a.get("b"));
		}
		{
			Object[] array = {1,"a"};
			Object[] args = {array};
			JSAObject testObject = jsa.newClass("test.jsa.TestObject",args);
			Object[] a = (Object[])testObject.invokeMethod("getA");
			assertEquals(1, a[0]);
			assertEquals("a", a[1]);
		}
		{
			Object obj = new Object();
			int objHash = obj.hashCode();
			JSAObject testObject = jsa.newClass("test.jsa.TestObject",obj);
			obj = null;
			System.gc();
			Object a = testObject.invokeMethod("getA");
			assertEquals(objHash, a.hashCode());
		}
		{
			Object obj = jsa.newClass("test.jsa.TestObject","a");
			JSAObject testObject = jsa.newClass("test.jsa.TestObject",obj);
			JSAObject a = (JSAObject)testObject.invokeMethod("getA");
			assertEquals("a", a.invokeMethod("getA"));
		}
	}
	
	@Test
	public void typesTest() {
		JSAObject test = jsa.newClass("test.jsa.TestObject");
		{
	        Object testJavaObject = test.invokeMethod("getNativeObj");
	        assertEquals("test.java.JavaObject", testJavaObject.getClass().getName());
	    }
		{
			String r = (String)test.invokeMethod("testNativeInit","a",1);
			assertEquals("a1", r);
		}
		{
			Object o = null;
			Object r = test.invokeMethod("testNull",o);
			assertEquals(null, r);
		}
		{
			String r = (String)test.invokeMethod("testString","s");
			assertEquals("s", r);
		}
		{
			Number r = (Number)test.invokeMethod("testInt",1);
			assertEquals(1, r.intValue());
		}
		{
			boolean r = (Boolean)test.invokeMethod("testBool",true);
			assertEquals(true, r);
		}
		{
			HashMap<String,Object> m = new HashMap<String,Object>();
			m.put("a", 1);
			m.put("b", "1");
			@SuppressWarnings("unchecked")
			HashMap<String,Object> r = (HashMap<String,Object>)test.invokeMethod("testMap",m);
			Number a = (Number)r.get("a");
			String b = (String)r.get("b");
			Object o = r.get("o");
			Object f = r.get("f");
			assertEquals(1, a.intValue());
			assertEquals("1", b);
			assertEquals("test.java.JavaObject",o.getClass().getName());
			assertTrue(f instanceof JSAFunction);
		}
		{
			Object[] m = new Object[] {1,"1"};
			Object[] r = (Object[])test.invokeMethod("testArray",new Object[] {m});
			Number a = (Number)r[0];
			String b = (String)r[1];
			assertEquals(1, a.intValue());
			assertEquals("1", b);
		}
		{
			Object o = new Object();
			Object r = test.invokeMethod("testObject",o);
			assertEquals(o, r);
		}
		{
			JSAObject o = jsa.newClass("test.jsa.TestObject","a");
			JSAObject r = (JSAObject)test.invokeMethod("testJSAObject",o);
			assertEquals("a", r.invokeMethod("getA"));
		}
		{
			JSAObject o = jsa.newClass("test.jsa.TestObject","a");
			JSAFunction f = (JSAFunction)test.invokeMethod("getTestFunc");
			String r = (String)f.call("f");
			assertEquals("f", r);
			String a = (String)f.apply(o, "f");
			assertEquals("a", a);
		}
		{
			JSAFunction f = (JSAFunction)test.invokeMethod("getTestFunc");
			JSAFunction r = (JSAFunction)test.invokeMethod("testJSAFunction",f);
			String t = (String)r.call("f");
			assertEquals("f", t);
		}
	}
	
	@Test
	public void staticTest() {
		{
			String a = (String)jsa.invokeClassMethod("test.jsa.TestObject", "staticGetA", "a");
			assertEquals("aa", a);
		}
		{
			JSAObject test = jsa.newClass("test.jsa.TestObject");
			String r = (String)test.invokeMethod("testNativeStatic");
			assertEquals("a", r);
		}
	}
	
	@Test
	public void weakTest() {
		{
			JavaObject testJavaObj = new JavaObject();
	        JSAObject testObject = jsa.newClass("test.jsa.TestObject");
	        testObject.invokeMethod("testWeakNativeA", testJavaObj);
	        testJavaObj = (JavaObject)testObject.invokeMethod("testWeakNativeB");
	        assertNotNull(testJavaObj);
		}
	}
	
	@Test
	public void testJSNativeObject() {
		{
			JSAObject testObject = jsa.newClass("test.jsa.TestObject");
		    Object v = testObject.invokeMethod("testNativeObject");
		    assertEquals("test.java.JavaObject", v.getClass().getName());
		}
	}
	
	@Test
	public void testStaticInit() {
		JSAObject testObject = jsa.newClass("test.jsa.TestObject");
		HashMap<String,Object> m = new HashMap<String,Object>();
		m.put("s", "s");
		Object o = testObject.invokeMethod("testStaticInit",m);
		System.out.println(o.getClass());
		JavaObject oo = (JavaObject) o;
		assertEquals("s", oo.getS());
	}
	
	@Test
	public void testWorkInMain() {
		JSAObject testObject = jsa.newClass("test.jsa.TestObject");
		testObject.invokeMethod("testWorkInMain");
		assertTrue(true);
	}
	
	@Test
	public void testPerformance() {
		JSAObject testObject = jsa.newClass("test.jsa.TestObject");
		int max = 100000000;
		int c = ((Number) testObject.invokeMethod("testPerformance", max)).intValue();
		assertEquals(c,max);
	}
}
