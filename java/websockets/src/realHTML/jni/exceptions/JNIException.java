package realHTML.jni.exceptions;

public class JNIException extends Exception {
	String message;
	int errno;
	
	public JNIException(int errno, String message) {
		super(message);
		this.errno = errno;
		this.message = message;
	}
	
}
