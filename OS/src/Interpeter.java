import java.io.*;
import java.util.*;

import javax.xml.crypto.dsig.keyinfo.KeyValue;

public class Interpeter {
	
	public Queue <PCB> readyQueue;
	public Queue <PCB> blockedQueueUserInput;
	public Queue <PCB> blockedQueueUserOutput;
	public Queue <PCB> blockedQueueFile;
	public Queue <PCB> generalBlockedQueue;
	
	public keyValuePair [] memory = new keyValuePair [40];
	
	public boolean isUserInputAvailable = true;
	public boolean isUserOutputAvailable = true;
	public boolean isFileAvailable = true;
	
	public int processWithInput = -1;
	public int processWithOutput = -1;
	public int processWithFile = -1;
	
	public int clockCycle = 0;
	
	
	public Interpeter() {
		
		readyQueue = new LinkedList <PCB> ();
		blockedQueueUserOutput = new LinkedList <PCB> ();
		blockedQueueUserInput = new LinkedList <PCB> ();
		blockedQueueFile = new LinkedList <PCB> ();
		generalBlockedQueue = new LinkedList <PCB> ();
		
		for (int i = 0 ; i < 40 ; i++)
			memory[i] = new keyValuePair();
		
	}
	
	@SuppressWarnings("resource")
	public static String programName () {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter Program Name: ");
		return sc.nextLine();
	}
	
	public void populateMemory (int processID) throws IOException {
		String progName = programName();
		int counter = 0;
		
		
		BufferedReader b = new BufferedReader( new FileReader(progName) );
		String l = b.readLine();
		
		while (  l != null  ) {
			counter++;
			l = b.readLine();
		}
		
		b.close();
		
		boolean existsSpace = false;
		int index = 0;
		
		for (; index < this.memory.length ; index++) {
			if (this.memory[index].getKey() == null) {
				if (this.memory.length - index > counter) {
					existsSpace = true;
					break;
				}
			}
		}
		
		PCB process = new PCB (processID, "Ready", index , index, (index + counter + 3) , false);
		
		if (existsSpace) {
			
			this.readyQueue.add(process);
			
			// put in allocated space in memory
			this.memory[process.lowerBound] = new keyValuePair("PCB" , process , null);
			BufferedReader br = new BufferedReader( new FileReader(progName) );
			
			String line = "";
			for (int i = process.lowerBound + 1 ; i <= process.upperBound ; i++) {
				this.memory[i] = new keyValuePair("variable", null, null);
				
				while ( (line = br.readLine()) != null ) {
					String [] row = line.split(" ");
					
					
					switch (row[0]) {
					
					case ("print"):
						this.memory[i] = new keyValuePair(row[0] , row[1] , null);
						break;
					
					case ("assign"):
						if(row[2].equals("readFile"))
							this.memory[i] = new keyValuePair(row[0] , row[1] , row[2] + " " + row[3]);
						else
							this.memory[i] = new keyValuePair(row[0] , row[1] , row[2]);
						break;
					
					case ("writeFile"):
						this.memory[i] = new keyValuePair(row[0] , row[1] , row[2]);
						break;
					
					case ("printFromTo"):
						this.memory[i] = new keyValuePair(row[0] , row[1] , row[2]);  
						break;
					
					case ("semWait"):
						this.memory[i] = new keyValuePair(row[0] , row[1] , null);
						break;
					
					case ("semSignal"):
						this.memory[i] = new keyValuePair(row[0] , row[1] , null);
						break;
					default: this.memory[i] = new keyValuePair();
					}
					break;
				}
				
			
			}
			br.close();
		}
		
		else {
			
			readyQueue.add(process);
			
			process.serialized = true;
			
			int size = 0;
			
			BufferedReader bb = new BufferedReader( new FileReader(progName) );
			
			String next = bb.readLine();
			
			while (  next != null  ) {
				size++;
				next = bb.readLine();
			}
			
			bb.close();
			
			keyValuePair [] data = new keyValuePair [size];
			
			// populate program in data
			
			
			
			
			BufferedReader br = new BufferedReader( new FileReader(progName) );
			
			String line = "";
			
			//	data[0] = new keyValuePair("PCB" , process , null);
				
				
				for ( int x=0 ; (line = br.readLine()) != null ; x++ ) {
					String [] row = line.split(" ");
					
					
					switch (row[0]) {
					
					case ("print"):
						data[x] = new keyValuePair(row[0] , row[1] , null);
						break;
					
					case ("assign"):
						if(row[2].equals("readFile"))
							data[x] = new keyValuePair(row[0] , row[1] , row[2] + " " + row[3]);
						else
							data[x] = new keyValuePair(row[0] , row[1] , row[2]);
						break;
					
					case ("writeFile"):
						data[x] = new keyValuePair(row[0] , row[1] , row[2]);
						break;
					
					case ("printFromTo"):
						data[x] = new keyValuePair(row[0] , row[1] , row[2]);  
						break;
					
					case ("semWait"):
						data[x] = new keyValuePair(row[0] , row[1] , null);
						break;
					
					case ("semSignal"):
						data[x] = new keyValuePair(row[0] , row[1] , null);
						break;
					default: data[x] = new keyValuePair();
					}
					//break;
				}
				
			
			
			br.close();
			

			SerializableHelper s = new SerializableHelper(process , data );
			
			//process.serializeProcess(); 
			
			 s.serializeProcess();

		}
		
	}
	
	public void processManipulation(PCB pcb, int c) throws IOException {
			
		keyValuePair []  data = new keyValuePair[40 - c];
		
		int index = 0 ;
		
		for(int i = c + 1 ; i < 40 ; i++) {
			
			if(memory[i].getKey() == null){
				break;
			}
			
			data[index] = memory[i];
			
			memory[i] = new keyValuePair();
			
			index++;
		}
		pcb.serialized = true;
		
		SerializableHelper s = new SerializableHelper(pcb,data);
		s.serializeProcess();
		
	}

	
	public static void main (String[] args) throws Exception {
		Interpeter main = new Interpeter();
			
		if (main.clockCycle == 0)
			main.populateMemory(1);
		
		main.runProgram();
			
		for (int i = 0 ; i < main.memory.length ; i++) {
			System.out.println(i + ") " + main.memory[i]);
		}
	}
	
	public void runProgram () throws Exception {
		
		while (true) {
			
			if (clockCycle == 4)
				populateMemory(3);
			
			PCB curProcess = null;
			
			if (readyQueue.peek() != null)
				curProcess = readyQueue.remove();
			
			if(curProcess == null)
				return;
			

			if (curProcess.serialized) {
				
				SerializableHelper s = new SerializableHelper();
				s = s.deserializeProcess("Process " + curProcess.processID + ".ser");
				
				curProcess.serialized = false;
				
				int index = 0;
				
				for (int i = memory.length - 1 ; i >= 0 ; i--) {
					if (memory[i].getKey()!=null &&memory[i].getKey().equals("PCB") ) {
						index = i;
						break;
					}
				}
				
				processManipulation(( (PCB) memory[index].getVal1()) , index);
				
				// 3amalna deserialize lel lazem yet3emelo deserialize and fadenalha makan fl mem
				
				keyValuePair [] dataToBeInserted = s.data;
				PCB processToBeInserted  = s.pcb;
				
				int dataIndex = 0;
				
				memory[index] = new keyValuePair("PCB" , processToBeInserted , null);
				
				processToBeInserted.setProgramCounter(processToBeInserted.programCounter - processToBeInserted.lowerBound + index);
				
				processToBeInserted.setLowerBound(index);
				
				index++;
				
				for (; (index<40) && (index < index + dataToBeInserted.length) && (dataIndex<dataToBeInserted.length) ; index++) {
					memory[index] = dataToBeInserted[dataIndex];
					dataIndex++;
				}
				
				processToBeInserted.setUpperBound(index);
				
				
				
				
				// ------------------------------------------------------------------------------------------------------------
				
//				int c = -1;
//				int curProcessSize = curProcess.upperBound - curProcess.lowerBound;
//				
//				do {
//				
//				
//				PCB toBeSerialized = serializationHandling();
//				
//				toBeSerialized.serialized = true;
//			
//				
//				c = toBeSerialized.lowerBound;
//				
//				
//				processManipulation(toBeSerialized , c);
//				
//				}while((39 - c) < (curProcessSize));
//				
//				
//				
//				
//				curProcess.serialized = false;
//				curProcess.deserializeProcess("Process " + curProcess.processID + ".ser");
				
				
				
				//deserialize the current process and serialize the last process
				
			}

				
			for (int j = 0 ; j < 2 ; j++) {
				
				System.out.println();
				System.out.println("Clock Cycle: " + clockCycle);
				
				System.out.println("Process Executing: " + curProcess);
				
				for (int i = 0 ; i < this.memory.length ; i++) {
					System.out.println(i + ") " + this.memory[i]);
				}
				
				if (clockCycle == 1)
					populateMemory(2);
				
				
				if ((curProcess.programCounter + 1<40) && (curProcess.programCounter + 1 < curProcess.upperBound)) {
					
					keyValuePair kvp = memory[curProcess.programCounter++]; // updates pc
					
					switch ((String) kvp.getKey()) {
					
					case ("semWait"):
						System.out.println("Executing: " + kvp.getKey());
						semWait((String) kvp.getVal1() , curProcess);
						break;
						
					case ("assign"):
						System.out.println("Executing: " + kvp.getKey());
						if (j == 0) 
							assign((String) kvp.val1, curProcess);
						
						j++;
					
						break;
						
					case ("writeFile"):
						System.out.println("Executing: " + kvp.getKey());
						writeFile((String) kvp.val1, (String) kvp.val2, curProcess);
						break;
						
					case ("readFile"):
						System.out.println("Executing: " + kvp.getKey());
						readFile((String) kvp.val1);
						break;
						
					case ("printFromTo"):
						System.out.println("Executing: " + kvp.getKey());
						printFromTo((String) kvp.getVal1(), (String) kvp.getVal2(), curProcess);
						break;
					
					case ("semSignal"):
						System.out.println("Executing: " + kvp.getKey());
						semSignal((String) kvp.getVal1(), curProcess);
						break;
					
					case ("print"):
						System.out.println("Executing: " + kvp.getKey());
						print((String) kvp.getVal1(), curProcess);
						break;
					}
					
				}
				
				else if ((curProcess.programCounter + 1<40) && (curProcess.programCounter + 1 < curProcess.upperBound)) {

					j++;
					
					keyValuePair kvp = memory[curProcess.programCounter++]; // updates pc
					
					switch ((String) kvp.getKey()) {
					
					case ("semWait"):
						System.out.println("Executing: " + kvp.getKey());
						semWait((String) kvp.getVal1() , curProcess);
						break;
						
					case ("assign"):
						System.out.println("Executing: " + kvp.getKey());
						if (j == 0) 
							assign((String) kvp.val1, curProcess);
						
						j++;
					
						break;
						
					case ("writeFile"):
						System.out.println("Executing: " + kvp.getKey());
						writeFile((String) kvp.val1, (String) kvp.val2, curProcess);
						break;
						
					case ("readFile"):
						System.out.println("Executing: " + kvp.getKey());
						readFile((String) kvp.val1);
						break;
						
					case ("printFromTo"):
						System.out.println("Executing: " + kvp.getKey());
						printFromTo((String) kvp.getVal1(), (String) kvp.getVal2(), curProcess);
						break;
					
					case ("semSignal"):
						System.out.println("Executing: " + kvp.getKey());
						semSignal((String) kvp.getVal1(), curProcess);
						break;
					
					case ("print"):
						System.out.println("Executing: " + kvp.getKey());
						print((String) kvp.getVal1(), curProcess);
						break;
					}
					
					curProcess.setProcessState("Finished");
					
				}
				
				else {
					curProcess.setProcessState("Finished");
				}
				
				System.out.println("Ready Queue --> " + this.readyQueue);
				System.out.println("Blocked Queue --> " + this.generalBlockedQueue);
				
				clockCycle++;
				
			}
			
			if (! (curProcess.getProcessState().equals("Finished") || curProcess.getProcessState().equals("Blocked")))
				readyQueue.add(curProcess);
			
		}
		
		
		
	}
	
//	public PCB serializationHandling() {
//		PCB toBeSerialized = null;
//		
//		for(int i = memory.length - 1 ; i >= 0 ; i--) {
//			
//			if(memory[i].key.equals("PCB")) {
//				
//				int process_id = Integer.parseInt(((String) memory[i].val1).substring(12));
//				
//				for( int j = 0 ; j < readyQueue.size() ; j++) {
//					if(readyQueue.peek().processID == process_id) {
//						toBeSerialized = readyQueue.remove();
//						readyQueue.add(toBeSerialized);
//						
//					}
//					else {
//						readyQueue.add(readyQueue.remove());
//					}
//				}
//				break;
//			}
//		}
//		
//		return toBeSerialized;
//	}
	
	public String readFile (String filePath) throws Exception {
		
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = "";
		String result = "";
		
		while ( (line = br.readLine()) != null) {
			result += line;
		}
		
		
		br.close();
		
		return result;
	}
	
	public void print (String argument, PCB curProc) throws Exception {
        String data = "";

        for(int i = curProc.upperBound - 3; i < curProc.upperBound; i++) {
            if(this.memory[i].getVal1().equals(argument) && this.memory[i].getKey().equals("variable")) {
                data = (String) this.memory[i].getVal2();
                break;
            }
        }
        System.out.println();
        System.out.println("Print Reslut: " + data);
        System.out.println();
    }
	
	@SuppressWarnings("resource")
	public void assign (String var1, PCB curProc) throws Exception {
		Scanner sc = new Scanner(System.in);
		Object var2 = new Object();
		
		if (this.memory[curProc.programCounter - 1].getVal2().equals("input")) {
			System.out.println("Please input a value for '" + var1 + "' for Program " + curProc.getProcessID() + ": ");
			var2 = sc.nextLine();
		} 
		
		else if (((String) this.memory[curProc.programCounter - 1].getVal2()).contains("readFile")) {
			String var = ((String) this.memory[curProc.programCounter - 1].getVal2()).substring(9);
			
			for(int i = curProc.upperBound - 3; i < curProc.upperBound; i++) {
				if(this.memory[i].getVal1() != null && this.memory[i].getVal1().equals(var)) {
					
					Object g = null;
					for (int j = curProc.upperBound - 3 ; j < curProc.upperBound ; j++) {
						if (this.memory[i].getKey().equals("variable") && this.memory[i].getVal1().equals(var)) {
							g = this.memory[i].getVal2();
						}
					}
					
					var2 = readFile((String) g);
				}
			}
		}
		
		for(int i = curProc.upperBound - 3; i < curProc.upperBound; i++) {
			if(this.memory[i].getVal1() == null || this.memory[i].getVal1().equals(var1)) {
				this.memory[i].setVal1(var1);
				this.memory[i].setVal2(var2);
				break;
			}
		}
	}
	
	public void writeFile (String var1, String var2, PCB curProc) throws Exception {
		String v1 = "";
		Object v2 = new Object();
		for(int i = curProc.upperBound - 3; i < curProc.upperBound; i++) {
			if(this.memory[i].getVal1().equals(var1)) {
				v1 = (String) this.memory[i].getVal2() + ".txt";
			}
			if(this.memory[i].getVal1().equals(var2)) {
				v2 = this.memory[i].getVal2();
			}
		}
		
		FileWriter writer = new FileWriter(v1);
		writer.write(v2.toString());
		writer.flush();
		writer.close();
	}
	
	public void printFromTo (String var1 , String var2, PCB curProc) {
		int v1 = 0;
		int v2 = 0;
		for(int i = curProc.upperBound - 3; i < curProc.upperBound; i++) {
			if(this.memory[i].getVal1().equals(var1)) {
				v1 = Integer.parseInt((String) this.memory[i].getVal2());
			}
			if(this.memory[i].getVal1().equals(var2)) {
				v2 = Integer.parseInt((String) this.memory[i].getVal2());
			}
		}
		
		System.out.println();
		System.out.print("Values From " + v1 + " to " + v2 + ": ");
		for(int i = v1; i <= v2; i++) {
			System.out.print(i + " ");
		}
		System.out.println();
		System.out.println();
	}
	
	public void semSignal (String argument , PCB process) {
		
		switch (argument) {
		
		case ("userInput"):
			if (processWithInput == process.processID) {
				processWithInput = -1;
				isUserInputAvailable = true;
				
				if (! this.blockedQueueUserInput.isEmpty() ) {
					PCB tmp = this.blockedQueueUserInput.remove();
					processWithInput = tmp.processID;
					tmp.setProcessState("Ready");
					this.readyQueue.add(tmp);
					this.generalBlockedQueue.remove(tmp);
				}
				
			}
			break;
		
		case ("userOutput"):
			if (processWithOutput == process.processID) {
				processWithOutput = -1;
				isUserOutputAvailable = true;
				
				if (! this.blockedQueueUserOutput.isEmpty()) {
					PCB tmp = this.blockedQueueUserOutput.remove();
					processWithOutput = tmp.processID;
					tmp.setProcessState("Ready");
					this.readyQueue.add(tmp);
					this.generalBlockedQueue.remove(tmp);
				}
			}
			break;
		
		case ("file"):
			if (processWithFile == process.processID) {
				processWithFile = -1;
				isFileAvailable = true;
				
				if (! this.blockedQueueFile.isEmpty()) {
					PCB tmp = this.blockedQueueFile.remove();
					processWithFile = tmp.processID;
					tmp.setProcessState("Ready");
					this.readyQueue.add(tmp);
					this.generalBlockedQueue.remove(tmp);
				}
			}
			break;
		
		}
		
	}
	
	public void semWait (String argument , PCB process) {
		
		switch (argument) {
		
		case ("userInput"):
			
			if (isUserInputAvailable && processWithInput == -1) {
				processWithInput = process.processID;
				isUserInputAvailable = false;
			}
		
			else {
				this.readyQueue.remove(process);
				this.blockedQueueUserInput.add(process);
				process.setProcessState("Blocked");
				this.generalBlockedQueue.add(process);
			}
			break;
		
		case ("userOutput"):
			
			if (isUserOutputAvailable && processWithOutput == -1) {
				processWithOutput = process.processID;
				isUserOutputAvailable = false;
			}
		
			else {
				this.readyQueue.remove(process);
				this.blockedQueueUserOutput.add(process);
				process.setProcessState("Blocked");
				this.generalBlockedQueue.add(process);
			}
			break;
		
		case ("file"):
			
			if (isFileAvailable && processWithFile == -1) {
				processWithFile = process.processID;
				isFileAvailable = false;
			}
		
			else {
				this.readyQueue.remove(process);
				this.blockedQueueFile.add(process);
				process.setProcessState("Blocked");
				this.generalBlockedQueue.add(process);
			}
			break;
		
		}
		
	}

}
