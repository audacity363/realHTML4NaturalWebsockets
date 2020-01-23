package realHTML.JSONConverter.signatures;

import java.util.Arrays;

public class ArraySignature {
	public short dimensions = 1;
	public int[] length = {-1, -1, -1};
	public Types vartype = null;
	
	public ArraySignature() {
		
	}
	
	public ArraySignature(ArraySignature source) {
		this.dimensions = source.dimensions;
		System.arraycopy(source.length, 0, this.length, 0, this.length.length);
		this.vartype = source.vartype;
	}

	public Boolean equals(ArraySignature target) {
		if(this.dimensions != target.dimensions) {
			return(false);
		} else if(!Arrays.equals(this.length, target.length)) {
			return(false);
		} else if(this.vartype != target.vartype) {
			return(false);
		}
		
		return(true);
	}

	public String toString() {
		return(String.format("Type: [%s] Dimensions: [%d] Length: [%d, %d, %d]", 
				vartype, dimensions, length[0], length[1], length[2]));
	}
}
