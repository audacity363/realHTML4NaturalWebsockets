package realHTML.JSONConverter.utils;

import java.util.LinkedList;

public class NameStack {

	LinkedList<String> stack = null;
	
	public NameStack() {
		this.stack = new LinkedList<String>();
	}
	
	public void pushName(String name) {
		this.stack.addLast(name);
	}
	
	public String popName() {
		if(this.stack == null) {
			return(null);
		}
		return(this.stack.removeLast());
	}
	
	public String get(int index) {
		return(this.stack.get(index));
	}
	
	public int size() {
		return(this.stack.size());
	}
	
	public String[] getStack() {
		return(this.stack.toArray(new String[this.stack.size()]));
	}
	
	public String toString() {
		String returnstr = "";
		
		for(int i = 0; i < this.stack.size(); i++) {
			returnstr += this.stack.get(i) + ".";
		}
		return(returnstr);
		
	}

}
