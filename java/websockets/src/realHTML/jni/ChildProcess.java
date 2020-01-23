package realHTML.jni;

public class ChildProcess {
	public int pid = 0;
	public boolean exited = false;
	public int exitCode = 0;
	public String reason = null;
	
	public ChildProcess(int pid, boolean exited, int exitCode, String reason) {
		this.pid = pid;
		this.exited = exited;
		this.exitCode = exitCode;
		this.reason = reason;
	}
	
	public String toString() {
		return(String.format("PID: %d; exited: %b; code: %d; Reason: %s", this.pid, this.exited, this.exitCode, this.reason));
	}
}
