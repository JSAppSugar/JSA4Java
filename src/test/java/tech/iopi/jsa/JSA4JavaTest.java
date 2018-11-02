package tech.iopi.jsa;

import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import java.util.HashMap;

import tech.iopi.jsa.impl.JSA4Java;
import tech.iopi.jsa.JSAppSugar;
import tech.iopi.jsa.JSAObject;

/**
 * Unit test for simple App.
 */
public class JSA4JavaTest{
	
	private static JSAppSugar jsa;
	
	@BeforeClass
	public static void beforeClass() {
		JSA4Java jsa4Java = new JSA4Java();
		jsa4Java.startEngine();
		jsa = jsa4Java;
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
			int a = (int)testObject.invokeMethod("getA");
			assertEquals(1, a);
		}
		{
			JSAObject testObject = jsa.newClass("test.jsa.TestObject",true);
			boolean a = (boolean)testObject.invokeMethod("getA");
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
			JSAObject testObject = jsa.newClass("test.jsa.TestObject",obj);
			Object a = testObject.invokeMethod("getA");
			assertEquals(obj, a);
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
			boolean r = (boolean)test.invokeMethod("testBool",true);
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
			assertEquals(1, a.intValue());
			assertEquals("1", b);
		}
	}

}
