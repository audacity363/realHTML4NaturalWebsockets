package realHTML.JSONConverter;

import org.json.JSONObject;

import realHTML.JSONConverter.signatures.ObjectSignature;

public class JSONConverter {
	
	JSONObject root = null;
	
	public JSONConverter(String jsonstr) {
		this.root = new JSONObject(jsonstr);
	}
	
	public ObjectSignature parse() throws Exception {
		Rh4nObject rootobj = new Rh4nObject(this.root);
		
		ObjectSignature signature =  rootobj.getSignature();
		signature.initValues();
		signature.fillValues(this.root);

		signature.printList();
		return signature;
	}
}
