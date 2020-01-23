package realHTML.JSONConverter.signatures;

import java.util.ArrayList;

public class ObjectSignatureNode {
	public String name;
	public Types vartype = null;
	public Types originalvartype = null;
	
	//When the variable is an array this is set and "arrvartype" contains 
	//the actual variable type
	public ArraySignature arrsig = null;
	
	//When the entry in the json string is an array this is set. 
	//For example: {'key1': [{'key2:' 'foo'}, {'key2': 'bar'}]}
	//On Entry for key1 orignalarrsig is set but on key2 is the normalised value for rh4n
	public ArraySignature orignalarrsig = null;
	
	public Object value = null;
	 
	public ObjectSignatureNode next;
	public ObjectSignatureNode nextlvl;
	public ObjectSignatureNode prev;
	
	public Boolean equals(ObjectSignatureNode target) {
		if(!this.name.equals(target.name)) {
			return(false);
		} else if(this.vartype != target.vartype) {
			return(false);
		} else if(this.arrsig != null && target.arrsig == null) {
			return(false);
		} else if(this.arrsig == null && target.arrsig != null) {
			return(false);
		} else if(this.arrsig != null && this.arrsig != null) {
			if(!this.arrsig.equals(target.arrsig)) {
				return(false);
			}
		}
		
		return(true);
	}
	
	
	public void initValue() {
		this.value = this._initValue(this.vartype, this.arrsig);
	}
	
	private Object _initValue(Types vartype, ArraySignature arrsig) {
		Object var = null;
		
		switch(vartype) {
		case STRING:
			var = new String("");
			break;
		case BOOLEAN:
			var = new Boolean(false);
			break;
		case FLOAT:
			var = new Double(0);
			break;
		case ARRAY:
			var = this.initArray(1, arrsig);
			break;
		case OBJECT:
			var = null;
		}
		
		return var;
	}
	
	@SuppressWarnings("unchecked")
	private Object initArray(int dim, ArraySignature arrsig) {
		Object hptr = null;
		
		hptr = new ArrayList<Object>();
		
		for(int i = 0; i < arrsig.length[dim-1]; i++) {
			if(dim == arrsig.dimensions) {
				((ArrayList<Object>)hptr).add(this._initValue(arrsig.vartype, null));
			} else {
				((ArrayList<Object>)hptr).add(initArray(dim+1, arrsig));
			}
		}
		return(hptr);
	}
	
	public String toString() {
		String retstr = "";
		retstr = String.format("Name: %s type: %s", this.name, this.vartype);
		if(this.arrsig != null) {
			retstr += " Array: " + this.arrsig.toString();
		}
		
		if(this.value != null) {
			retstr += " Value: " + this.value.toString();
		}
		
		retstr += " (Original: vartype:" + this.originalvartype + ", " + this.orignalarrsig + ")";
		return(retstr);
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	@SuppressWarnings("unchecked")
	public void setValue(Object value, int index[]) {
		this.setValue(value, (ArrayList<Object>)this.value, index, 1);
	}
	
	@SuppressWarnings("unchecked")
	private void setValue(Object value, ArrayList<Object> target, int index[], int curdim) {
		Object hptr = null;
	
		if(curdim != this.arrsig.dimensions) {
			hptr = target.get(index[curdim-1]);
			this.setValue(value, (ArrayList<Object>)hptr, index, curdim+1);
			return;
		}
		target.set(index[curdim-1], value);
	}
}