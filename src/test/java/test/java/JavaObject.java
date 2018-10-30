package test.java;

public class JavaObject {
	private String _a = null;
	
	public JavaObject(String a) {
		this._a = a;
	}
	
	public JavaObject() {
		this._a = "-";
	}

	public String getA() {
		return this._a;
	}
}
