package realHTML.jni;

import org.apache.tomcat.jni.Library;

import realHTML.jni.exceptions.NoClientException;
import realHTML.jni.natural.Message;

public class JNI {

	static {
		System.out.println("Loading so with Library");
		Library.load("/home/tom/Documents/Java/websockets/c/librealHTMLconnector.so");
	}
	
	public native int jni_createUDS_Server(String path, Boolean blocking);
	public native int jni_waitForClient(String path, int serverFD) throws NoClientException;
	public native int jni_startNaturalWS(String naturalbinpath, String socketPath);
	public native ChildProcess jni_getChildProcessStatus(int pid);
	public native void jni_killChildProcess(int pid, int signal);
	public native void jni_sendMessageToNatural(int clientFD, Message msg);
	
	public int createNonBlockingUDS(String path) {
		return(this.jni_createUDS_Server(path, false));
	}
	
	public int waitForClient(String path, int serverFD) throws NoClientException {
		return(this.jni_waitForClient(path, serverFD));
	}
	
	public int startNaturalWS(String naturalbinpath, String socketPath) {
		return(this.jni_startNaturalWS(naturalbinpath, socketPath));
	}
	
	public ChildProcess getChildProcessStatus(int pid) {
		return(this.jni_getChildProcessStatus(pid));
	}
	
	public void killChildProcess(int pid, int signal) {
		this.jni_killChildProcess(pid, signal);
	}
	
	public void sendMessageToNatural(int clientFD, Message msg) {
		this.jni_sendMessageToNatural(clientFD, msg);
	}
}
