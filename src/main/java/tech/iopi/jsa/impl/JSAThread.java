package tech.iopi.jsa.impl;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import com.eclipsesource.v8.V8Object;

/**
 * 
 * @author Neal
 *
 */
public class JSAThread implements Runnable {
	private Thread _thread = null;
	private Thread _javaReferenceThread = null;
	private boolean _isRun = false;
	private Runnable _worker = null;
	final private ReferenceQueue<Object> _javaReferenceQueue = new ReferenceQueue<Object>();
	final HashMap<String, JavaPhantomReference<Object>> _javaReference = new HashMap<String, JavaPhantomReference<Object>>();
	
	

	public JSAThread() {
		_thread = new Thread(this);
		_isRun = false;
	}

	public void start() {
		this._isRun = true;
		_thread.start();
		
		_javaReferenceThread = new Thread() {
            @SuppressWarnings("unchecked")
			public void run() {
                while (true) {
                	JavaPhantomReference<Object> obj = null;
					try {
						obj = (JavaPhantomReference<Object>) _javaReferenceQueue.remove();
					} catch (InterruptedException e) {
						//e.printStackTrace();
					}
                    if (obj != null) {
                    	if(obj.js == null) System.out.println("release:"+obj.i);
                    	_javaReference.remove(obj.i);
                    	if(obj.js!=null) {
                    		final V8Object v8obj = obj.js;
                    		syncRun(new Runnable() {
								@Override
								public void run() {
									v8obj.release();
								}
							});
                    	}
                    }
                }
            }
        };
        _javaReferenceThread.start();
	}

	public void stop() {
		if(_thread !=null) {
			this._isRun = false;
			synchronized (_thread) {
				_thread.notifyAll();
			}
			_javaReferenceThread.interrupt();
		}
	}
	
	public void syncRun(Runnable worker) {
		Thread runThread = Thread.currentThread();
		if(runThread == _thread) {
			worker.run();
		}else {
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
	
	protected void addJavaReference(Object o,V8Object jso) {
		String i = o.toString();
		if(!_javaReference.containsKey(i)) {
			JavaPhantomReference<Object> r = new JavaPhantomReference<Object>(o,_javaReferenceQueue);
			r.i = i;
			r.j = new WeakReference<Object>(o);
			_javaReference.put(i, r);
		}
		jso.add("$this", i);
		jso.registerJavaMethod(o, "toString", "$_", new Class<?>[] {  });
	}
	
	protected void addJsReference(Object o,V8Object jso) {
		String i = o.toString();
		if(!_javaReference.containsKey(i)) {
			JavaPhantomReference<Object> r = new JavaPhantomReference<Object>(o,_javaReferenceQueue);
			r.i = i;
			r.js = jso;
			_javaReference.put(i, r);
		}
	}
	
	protected Object getJavaReference(String i) {
		JavaPhantomReference<Object> r = _javaReference.get(i);
		if(r!=null) {
			return r.j.get();
		}
		return null;
	}
	
	protected static class JavaPhantomReference<T>  extends PhantomReference<T>{
		protected JavaPhantomReference(T referent, ReferenceQueue<? super T> q) {
			super(referent, q);
		}
		protected String i;
		protected WeakReference<T> j;
		protected V8Object js;
	}
}
