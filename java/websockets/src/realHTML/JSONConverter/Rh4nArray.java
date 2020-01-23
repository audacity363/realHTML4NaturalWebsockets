package realHTML.JSONConverter;

import java.util.ArrayList;

import realHTML.JSONConverter.signatures.ArraySignature;
import realHTML.JSONConverter.signatures.Types;

public class Rh4nArray {
	
	private ArrayList<Object> target = null;
	
	public Rh4nArray(ArrayList<Object> target) {
		this.target = target;
	}
	
	public ArraySignature getSignature() throws Exception {
		ArraySignature arrsig = new ArraySignature();
		
		arrsig = this._getSignature(this.target, arrsig, 1);
		
		return(arrsig);
	}
	
	@SuppressWarnings("unchecked")
	private ArraySignature _getSignature(ArrayList<Object> value, ArraySignature sig, int dim) throws Exception {
		Object target = null;
		Types vartype = null;
		
		if(dim > 3) {
			throw new Exception("More then three dimensions are not supported by natural");
		}
		
		sig.dimensions = (short)dim;
		
		if(sig.length[sig.dimensions-1] < value.size()) {
			sig.length[sig.dimensions-1] = value.size();
		}
		
		for(int i = 0; i < value.size(); i++) {
			target = value.get(i);
			if(target instanceof ArrayList) {
				sig = _getSignature((ArrayList<Object>)target, sig, dim+1);
			} else {
				vartype = Types.getTypefromobject(target);
				if(sig.vartype == null) {
					sig.vartype = vartype;
				} else if(sig.vartype != vartype) {
					throw new Exception("Found multiple vartypes in array");
				}
			}
		}
		
		return(sig);
		
	}
}
