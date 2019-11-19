package tech.iopi.jsa.impl;

/**
 * 
 * @author Neal
 *
 */
public class JSAThread implements Runnable {
	private Thread _thread = null;
	private boolean _isRun = false;
	private Runnable _worker = null;

	public JSAThread() {
		_thread = new Thread(this);
		_isRun = false;
	}

	public void start() {
		this._isRun = true;
		_thread.start();
	}

	public void stop() {
		this._isRun = false;
		synchronized (_thread) {
			_thread.notifyAll();
		}
	}
	
	public void syncRun(Runnable worker) {
		synchronized (this) {
			_worker = worker;
			synchronized (_thread) {
				_thread.notifyAll();
				try {
					_thread.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			_worker = null;
		}
	}

	@Override
	public void run() {
		while (_isRun) {
			if (_worker != null) {
				try {
					_worker.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			synchronized (_thread) {
				try {
					_thread.notifyAll();
					_thread.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
