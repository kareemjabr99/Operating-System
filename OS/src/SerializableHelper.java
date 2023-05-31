import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializableHelper implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public PCB pcb;
	public keyValuePair [] data;
	
	public SerializableHelper (PCB p , keyValuePair [] d) {
		this.pcb = p;
		this.data = d;
	}
	
	public SerializableHelper () {
		
	}
	
	public void serializeProcess () throws IOException {
		FileOutputStream fileOut = new FileOutputStream("Process " + this.pcb.processID + ".ser");
		ObjectOutputStream out = new ObjectOutputStream (fileOut);
		out.writeObject(this);
		out.close();
		fileOut.close();
	}
	
	public SerializableHelper deserializeProcess (String fileName) throws IOException{
		SerializableHelper sh = null;
		FileInputStream fileIn = new FileInputStream(fileName);
		try (ObjectInputStream out = new ObjectInputStream(fileIn)) {
			try {
				sh = (SerializableHelper) out.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sh;
	}

	public PCB getPcb() {
		return pcb;
	}

	public void setPcb(PCB pcb) {
		this.pcb = pcb;
	}

	public keyValuePair[] getData() {
		return data;
	}

	public void setData(keyValuePair[] data) {
		this.data = data;
	}
		
	
	
	
	
	
	
	
	
}

	
	
	
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * ProcessID = 2;
	 * 
	 * int index = 0;
	 * 
	 * keyValuePair [] data = new keyValuePair[processID.upperbound - processID.lowerbound]; 
	 * 
	 * 
	 * for (int i= processID.lowerbound + 1 ; i < processId.upperboubd ; i++){
	 * 			data[index] = memory[i];
	 * 			index++;
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * SerializeHelper s = new SerializeHelper (PCB , data);
	 * 
	 * s.serialize();
	 * 
	 * SerializeHelper h = s.deserialize()
	 * 
	 * h.pcb();
	 * h.data();
	 * 
	 * 
	 * 
	 * 
	 * */
	

