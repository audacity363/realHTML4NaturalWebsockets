package realHTML.JSONConverter.signatures;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public enum Types {
	OBJECT(0), 
	STRING(1), 
	BOOLEAN(2),
	INT(3),
	FLOAT(4),
	ARRAY(5),
	NULL(6),
	UNKNOWN(17);
	
	Integer number_rep;
	
	private Types(Integer number) {
		this.number_rep = number;
	}
	
	public int getNumberRep() {
		return (this.number_rep.intValue());
	}
	
	public static Types getTypefromobject(Object target) {
		if(target == null) {
			return(NULL);
		} else if(target instanceof Integer) {
			return(INT);
		} else if(target instanceof String) {
			return(STRING);
		} else if(target instanceof Boolean) {
			return(BOOLEAN);
		} else if (target instanceof JSONObject || target instanceof HashMap){
			return(OBJECT);
		} else if (target instanceof ArrayList) {
			return(ARRAY);
		} else if(target instanceof JSONArray) {
			return(ARRAY);
		}
		
		return(UNKNOWN);
	}
	
}