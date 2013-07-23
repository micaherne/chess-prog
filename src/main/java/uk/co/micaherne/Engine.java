package uk.co.micaherne;


public class Engine implements Runnable{

	IO io;
	
	public Engine() {
		super();
		io = new IO();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Engine e = new Engine();
		//new Thread("engine").start();
		e.run();
	}

	public void run() {
		io.startInput();
	}



}
