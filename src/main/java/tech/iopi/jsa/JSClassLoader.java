package tech.iopi.jsa;

/**
 * The interface for loading JSA class file
 * 
 * You can implement your own JSA class file loader.
 * 
 * @author Neal
 *
 */
public interface JSClassLoader {
	
	/**
	 * Loads JS class file with the specified class name
	 * 
	 * @param className
	 * @return The file content
	 */
	public String loadJSClass(String className);
}
