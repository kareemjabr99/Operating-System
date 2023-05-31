import java.io.Serializable;

public class keyValuePair implements Serializable {
	
	private static final long serialVersionUID = 1L;
	Object key;
	Object val1;
	Object val2;
	
	public keyValuePair () {
		
	}
	
	public keyValuePair (Object key , Object val1 , Object val2) {
		this.key = key;
		this.val1 = val1;
		this.val2 = val2;
	}
	
	public String toString () {
		return key + " -> " + val1 + " , " + val2;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Object getVal1() {
		return val1;
	}

	public void setVal1(Object val1) {
		this.val1 = val1;
	}

	public Object getVal2() {
		return val2;
	}

	public void setVal2(Object val2) {
		this.val2 = val2;
	}
	
	
	
	

}
