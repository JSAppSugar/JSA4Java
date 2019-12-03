package tech.iopi.jsa;

import tech.iopi.jsa.android.Mainthread;

public class TestMainThread implements Mainthread {

	@Override
	public void asyncRun(Object o, Runnable r) {
		r.run();
	}

}
