package tech.iopi.jsa;

import tech.iopi.jsa.android.MainThread;

public class TestMainThread implements MainThread {

	@Override
	public void asyncRun(Runnable r) {
		r.run();
	}

}
