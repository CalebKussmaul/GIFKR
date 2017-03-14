package kussmaulUtils;

public class Stopwatch {

	private final String taskName;
	private final long startTime;
	
	public Stopwatch() {
		taskName 		= "[no-name]";
		startTime 		= System.currentTimeMillis();
	}
	
	public Stopwatch(String taskName) {
		this.taskName 	= "["+taskName+"]";
		startTime 		= System.currentTimeMillis();
	}
	
	public long getElapsedTime() {
		return System.currentTimeMillis() - startTime;
	}
	
	public void print() {
		System.out.printf("%,8d millis %s %n", getElapsedTime(), taskName);
	}
}
