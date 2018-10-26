package tech.iopi.jsa;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

import tech.iopi.jsa.impl.JSA4Java;
import tech.iopi.jsa.JSAppSugar;

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

}
