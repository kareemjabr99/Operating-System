import java.io.*;

public class PCB implements Serializable{
	
	private static final long serialVersionUID = 1L;
	int processID;
	String processState;
	int programCounter;
	int lowerBound;
	int upperBound;
	public boolean serialized;
	
	public PCB( int pid , String state , int pc , int lBound , int uBound, boolean serialized ) {
		this.processID = pid;
		this.processState = state;
		this.programCounter = pc;
		this.lowerBound = lBound;
		this.upperBound = uBound;
		this.serialized = serialized;
		
	}
	
//	public void serializeProcess () throws IOException {
//			FileOutputStream fileOut = new FileOutputStream("Process " + this.processID + ".ser");
//			ObjectOutputStream out = new ObjectOutputStream (fileOut);
//			out.writeObject(this);
//			out.close();
//			fileOut.close();
//			
//	}
//	
//	public PCB deserializeProcess (String fileName) throws IOException{
//		PCB p = null;
//		FileInputStream fileIn = new FileInputStream(fileName);
//		try (ObjectInputStream out = new ObjectInputStream(fileIn)) {
//			try {
//				p = (PCB) out.readObject();
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return p;
//	}
	
	
	public int getProcessID() {
		return processID;
	}



	public void setProcessID(int processID) {
		this.processID = processID;
	}



	public String getProcessState() {
		return processState;
	}



	public void setProcessState(String processState) {
		this.processState = processState;
	}



	public int getProgramCounter() {
		return programCounter;
	}



	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter;
	}



	public int getLowerBound() {
		return lowerBound;
	}



	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}



	public int getUpperBound() {
		return upperBound;
	}



	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}



	public String toString () {
		return "Process ID: " + processID +
				" , Process State: " + processState;
	}

}
