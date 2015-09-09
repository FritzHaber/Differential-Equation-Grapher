package fxTest;

class RunnableDemo implements Runnable {
	   private Thread t;
	   private String threadName;
	   
	   RunnableDemo( String name){
	       threadName = name;
	       System.out.println("Creating " +  threadName );
	   }
	   public void run() {
	      System.out.println("Running " +  threadName );
	      for(int i = 4; i > 0; i--) {
		    System.out.println("Thread: " + threadName + ", " + i);
		    // Let the thread sleep for a while.
		    try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
	     System.out.println("Thread " +  threadName + " exiting.");
	   }
	   
	   public void start ()
	   {
	      System.out.println("Starting " +  threadName );
	      if (t == null)
	      {
	         t = new Thread (this, threadName);
	         t.start ();
	      }
	   }

	}

	public class TestThread {
	   public static void main(String args[]) {
	   
	      RunnableDemo R1 = new RunnableDemo( "Thread-1");
	      R1.start();
	      
	      RunnableDemo R2 = new RunnableDemo( "Thread-2");
	      R2.start();
	   }   
	}