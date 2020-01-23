package realHTML.tomcat.routing;

public class Route {

	public int id;
	public String natLibrary;
	public String natProgram;
	public Boolean login;
	public String loglevel;
	public Boolean active;
	
	public Route(String library, String program, Boolean login, String loglevel, Boolean active) {
		this.natLibrary = library;
		this.natProgram = program;
		this.login = login;
		this.loglevel = loglevel;
		this.active = active;
	}
	
	public String toString() {
		return "Route: active: " + this.active;
	}
}
