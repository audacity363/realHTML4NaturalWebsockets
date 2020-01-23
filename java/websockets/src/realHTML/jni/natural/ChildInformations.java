package realHTML.jni.natural;

public class ChildInformations {
	public String library;
	public String program;
	public String parms;
	public String srcPath;
	public String loglevel;
	public String logPath;
	
	public ChildInformations() {
		
	}
	
	public ChildInformations(String library, String program, String parms,
			String srcPath, String logLevel, String logPath) {
		this.library = library;
		this.program = program;
		this.parms = parms;
		this.srcPath = srcPath;
		this.loglevel = logLevel;
		this.logPath = logPath;
	}
}
