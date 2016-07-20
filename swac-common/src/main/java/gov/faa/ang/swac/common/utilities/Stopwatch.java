package gov.faa.ang.swac.common.utilities;

/**
  * Simulates a stopwatch using the System time (with millisecond resolution).
  */
public class Stopwatch
{
	private long startTime = -1;
	private long stopTime = -1;
	private boolean running = false;

	/**
	 * Starts the {@link Stopwatch}
	 * @return this {@link Stopwatch}
	 */
	public Stopwatch start()
	{
		this.startTime = System.currentTimeMillis();
		this.running = true;
		return this;
   }
	
	/**
	 * Stops the {@link Stopwatch}
	 * @return this {@link Stopwatch}
	 */
   public Stopwatch stop()
   {
	   this.stopTime = System.currentTimeMillis();
	   this.running = false;
	   return this;
   }

   /**
    * Returns elapsed time since start. Calling {@link #getElapsedMilliseconds()} not change the running state of the {@link Stopwatch}
    * (i.e. if it was running before it will be running afterwards, and vice versa).
    * @return Time since last call to {@link #start()} (in milliseconds). If {@link Stopwatch} has not been started, returns 0.
    */
   public long getElapsedMilliseconds()
   {
      if (this.startTime == -1)
      {
    	  return 0;
      }
      
      if (this.running)
      {
    	  return System.currentTimeMillis() - this.startTime;
      }

      return this.stopTime - this.startTime; 
   }
   
   /**
    * Returns elapsed time in seconds. (Convenience method that calls {@link #getElapsedMilliseconds()}.
    */
   public double getElapsedSeconds()
   {
	   return this.getElapsedMilliseconds() / 1000.0;
   }
  
   /**
    * Returns elapsed time in minutes. (Convenience method that calls {@link #getElapsedMilliseconds()}.
    */
   public double getElapsedMinutes()
   {
	   return this.getElapsedMilliseconds() / 60000.0;
   }
  
   /**
    * Resets {@link Stopwatch}.
    * @return this {@link Stopwatch}
    */
   public Stopwatch reset()
   {
      this.startTime = -1;
      this.stopTime = -1;
      this.running = false;
      return this;
   }
}
