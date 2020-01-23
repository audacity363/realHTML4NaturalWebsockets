package realHTML.JSONConverter.signatures;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONPointer;

import realHTML.JSONConverter.utils.NameStack;

public class ObjectSignature {
	private static final Logger logging = LogManager.getLogger(ObjectSignature.class);
	
	ObjectSignatureNode head = null;
	
	int index[] = {0, 0, 0};
	int dim = 0;
	
	public ObjectSignatureNode addAtEnd(String name) {
		ObjectSignatureNode hptr = this.getLast();
		ObjectSignatureNode newNode = new ObjectSignatureNode();
		
		if(hptr == null) {
			this.head = newNode; 
		} else {
			hptr.next = newNode;
		}
		
		newNode.name = name;
		return(newNode);
	}
	
	public ObjectSignatureNode getHead() {
		return(this.head);
	}
	
	public void printList() {
		this.printEntry(this.head, 0);
	}
	
	public Boolean equals(ObjectSignature target) {
		return(this.compareNode(this.head, new NameStack(), target));
	}
	
	public void convertToArray(ArraySignature arrsig) throws Exception {
		this.convertToArray(this.head, arrsig);
	}
	
	public void convertToArray(ObjectSignatureNode head, ArraySignature arrsig) throws Exception{
		ObjectSignatureNode hptr = head;
		
		for(; hptr != null; hptr = hptr.next) {
			if(hptr.vartype == Types.OBJECT) {
				this.convertToArray(hptr.nextlvl, arrsig);
			} else if(hptr.vartype == Types.ARRAY) {
				if(hptr.arrsig.dimensions + arrsig.dimensions > 3) {
					throw new Exception("More then three dimensions are not supported by natural");
				} 
				System.arraycopy(hptr.arrsig.length, 0, hptr.arrsig.length, arrsig.dimensions, hptr.arrsig.dimensions);
				System.arraycopy(arrsig.length, 0, hptr.arrsig.length, 0, arrsig.dimensions);
				hptr.arrsig.dimensions += arrsig.dimensions;
			} else {
				hptr.arrsig = new ArraySignature(arrsig);
				hptr.arrsig.vartype = hptr.vartype;
				hptr.vartype = Types.ARRAY;
			}
			
		}
	}
	
	public void initValues() {
		this.initNodeValue(this.head);
	}
	
	private void initNodeValue(ObjectSignatureNode head) {
		ObjectSignatureNode hptr = head;
		
		for(; hptr != null; hptr = hptr.next) {
			if(hptr.nextlvl != null) {
				initNodeValue(hptr.nextlvl);
			} else {
				hptr.initValue();
			}
		}
	}	
	
	private Boolean compareNode(ObjectSignatureNode target, NameStack parents, ObjectSignature comparesig) {
		ObjectSignatureNode hptr = target;
		ObjectSignatureNode compareNode = null;
		
		for(; hptr != null; hptr = hptr.next) {
			parents.pushName(hptr.name);
			
			logging.debug("Getting Node {}", parents.toString());
			compareNode = comparesig.getNode(parents);
			if(compareNode == null) {
				logging.debug("Didn't find {} in compare target", parents.toString());
				return(false);
			}
			
			if(!hptr.equals(compareNode)) {
				logging.debug("Node [{}] is not equals to [{}]", hptr.toString(), compareNode.toString());
				return(false);
			}
			
			if(hptr.nextlvl != null) {
				if(!compareNode(hptr.nextlvl, parents, comparesig)) {
					return(false);
				}
			}
			parents.popName();
		}
		
		return(true);
	}
	
	public ObjectSignatureNode getNode(NameStack names) {
		ObjectSignatureNode hptr = this.head;
		
		for(int i = 0; i < names.size(); i++) {
			hptr = this.getNode(hptr, names.get(i));
			if(hptr == null) {
				return(null);
			}
			if(i+1 < names.size()) {
				hptr = hptr.nextlvl;
			}
		}
		
		return(hptr);
	}
	
	private void printEntry(ObjectSignatureNode target, int level) {
		ObjectSignatureNode hptr = target;
		
		for(; hptr != null; hptr = hptr.next) {
			logging.debug("{}{}", this.getTabs(level), hptr.toString());
			/*logging.debug("{}Name: {}", this.getTabs(level), hptr.name);
			logging.debug("{}Vartype: {}", this.getTabs(level), hptr.vartype);
			if(hptr.arrsig != null) {
				logging.debug("{}Array: {}", this.getTabs(level), hptr.arrsig.toString());
			}*/
			if(hptr.nextlvl != null) {
				this.printEntry(hptr.nextlvl, level+1);
			}
		}
	}
	
	private String getTabs(int level) {
		String tabs = "";
		for(int i = 0; i < level; i++) {
			tabs += "\t";
		}
		return(tabs);
	}
	
	private ObjectSignatureNode getLast() {
		ObjectSignatureNode hptr = this.head;
		if(hptr == null) { return(null); }
		
		for(; hptr.next != null; hptr = hptr.next);
		return(hptr);
	}
	
	private ObjectSignatureNode getNode(ObjectSignatureNode head, String name) {
		ObjectSignatureNode hptr = head;
		
		for(; hptr != null; hptr = hptr.next) {
			if(hptr.name.equals(name)) {
				return(hptr);
			}
		}
		return(null);
	}
	
	public void fillValues(JSONObject root) {
		List<String> pointer = new ArrayList<String>();
		
		this.fillValues(this.head, pointer, root);
	}
	
	private void fillValues(ObjectSignatureNode hptr, List<String> pointer, JSONObject root) {
		Integer arrindex[] = {0, 0, 0};
		Object value = null;
		
		for(; hptr != null; hptr = hptr.next) {
			pointer.add(hptr.name);
			if(hptr.originalvartype == Types.OBJECT) {
				this.fillValues(hptr.nextlvl, pointer, root);
			} else if(hptr.originalvartype == Types.ARRAY && hptr.orignalarrsig.vartype == Types.OBJECT) {
				this.fillObjectArray(hptr, pointer, 1, arrindex, root);				
			} else if(hptr.originalvartype == Types.ARRAY) {
				this.fillArray(hptr, pointer, 1, arrindex, root);
			} else {
				value = root.query(new JSONPointer(pointer));
				logging.debug("Name: {}, Value: [{}]", pointer.toString(), value);
				logging.debug("Rh4n index: {}", this.index);
				hptr.setValue(value);
			}
			pointer.remove(pointer.lastIndexOf(hptr.name));
		}
	}
	
	private void fillArray(ObjectSignatureNode target, List<String> pointer, int dim, Integer index[], JSONObject root) {
		Object value = null;
		
		this.dim++;
		for(index[dim-1] = 0 ; index[dim-1] < target.orignalarrsig.length[dim-1]; index[dim-1]++) {
			
			this.index[this.dim-1] = index[dim-1];
			pointer.add(index[dim-1].toString());
			
			if(dim < target.orignalarrsig.dimensions) {
				this.fillArray(target, pointer, dim+1, index, root);
			} else {
				value = root.query(new JSONPointer(pointer));
				logging.debug("Name: {}; Value: [{}]", pointer.toString(), value);
				logging.debug("Rh4n index: {}", this.index);
				target.setValue(value, this.index);
			}
			pointer.remove(pointer.lastIndexOf(index[dim-1].toString()));
		}
		
		this.index[this.dim-1] = 0;
		this.dim--;
	}
	
	private void fillObjectArray(ObjectSignatureNode target, List<String> pointer, int dim, Integer index[], JSONObject root) {
		this.dim++;
		for(index[dim-1] = 0; index[dim-1] < target.orignalarrsig.length[dim-1]; index[dim-1]++) {
			
			this.index[this.dim-1] = index[dim-1];
			pointer.add(index[dim-1].toString());
			
			if(dim < target.orignalarrsig.dimensions) {
				this.fillObjectArray(target, pointer, dim+1, index, root);
			} else {
				fillValues(target.nextlvl, pointer, root);
			}
			
			pointer.remove(pointer.lastIndexOf(index[dim-1].toString()));
		}
		this.index[this.dim-1] = 0;
		this.dim--;
	}
}
