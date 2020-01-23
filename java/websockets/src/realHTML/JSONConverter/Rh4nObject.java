package realHTML.JSONConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import realHTML.JSONConverter.signatures.ArraySignature;
import realHTML.JSONConverter.signatures.ObjectSignature;
import realHTML.JSONConverter.signatures.ObjectSignatureNode;
import realHTML.JSONConverter.signatures.Types;

public class Rh4nObject {
	private static final Logger logging = LogManager.getLogger(Rh4nObject.class);
	
	private HashMap<String, Object> target = null;
	
	public Rh4nObject(JSONObject target) {
		this.target = new HashMap<String, Object>(target.toMap());
	}
	
	public Rh4nObject(HashMap<String, Object> target) {
		this.target = target;
	}
	
	public Rh4nObject(Map<String, Object> target) {
		this.target = new HashMap<String, Object>(target);
	}
	
	@SuppressWarnings("unchecked")
	public ObjectSignature getSignature() throws Exception {
		ObjectSignature objsig = new ObjectSignature();
		Iterator<Entry<String, Object>> objit = this.target.entrySet().iterator();
		Entry<String, Object> objentry = null;
		
		String key = null;
		Object value = null;
		
		ObjectSignatureNode newNode = null;
		Types vartype = null;
		
		Rh4nArray arr = null;
		Rh4nObject obj = null;
		
		while(objit.hasNext()) {
			objentry = objit.next();
			key = objentry.getKey();
			value = objentry.getValue();
			
			logging.debug("God key [{}] and value of class {}", key, value.getClass());
			
			newNode = objsig.addAtEnd(key);
			newNode.originalvartype = newNode.vartype = vartype = Types.getTypefromobject(value);
			if(vartype == Types.ARRAY) {
				logging.debug("Trying to get array signature");
				arr = new Rh4nArray((ArrayList<Object>)value);
				newNode.arrsig = arr.getSignature();
				logging.debug("Successfully got array signature");
				newNode.orignalarrsig = new ArraySignature(newNode.arrsig);
				if(newNode.arrsig.vartype == Types.OBJECT) {
					logging.debug("{} is an object array", key);
					newNode.nextlvl = new Rh4nObjectArray((ArrayList<Object>)value, newNode.arrsig).getSignature().getHead();
					newNode.arrsig = null;
					newNode.vartype = Types.OBJECT;
				}
			} else if(vartype == Types.OBJECT) {
				obj = new Rh4nObject((HashMap<String, Object>)value);
				newNode.nextlvl = obj.getSignature().getHead();
			}
			logging.debug("End for handling {}", key);
		}
		
		return(objsig);
	}
}
