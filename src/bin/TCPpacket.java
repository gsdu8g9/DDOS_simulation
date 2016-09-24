package bin;

import java.util.Random;

public class TCPpacket extends Packet{
	public static final int UNKNOWN_TYPE = 0, ACK = 1, SYN = 2;
	
	//TCP header info
	private String source = "";				//2B
	private String destination = "";		//2B
	private String sequenceNumber = "";		//4B - max 4.294.967.295
	private String ackNumber = "";			//4B
	private int type = UNKNOWN_TYPE; 		//1bit
	
	public TCPpacket(String source, String destination, int type, int size) {
		this.source = source;
		this.destination = destination;
		this.sequenceNumber = makeRandom4BNumber();
		this.ackNumber = makeRandom4BNumber();
		this.type = type;
		this.dataSize = size;
		this.generateDataContent();
	}
	
	public TCPpacket(String source, String destination, int type, int size, int checksum) {
		this.source = source;
		this.destination = destination;
		this.sequenceNumber = makeRandom4BNumber();
		this.ackNumber = makeRandom4BNumber();
		this.type = type;
		this.dataSize = size;
		this.checksum = checksum;
		this.generateDataContent();
	}
	
	private String makeRandom4BNumber() {
		Random rand = new Random();
		StringBuilder temp = new StringBuilder("");
	
		int low = 48 /* '0' */ ,high = 57; /* '9' */
		int currSize = 0;
		
		while ( currSize < 32) {
			int charInt = rand.nextInt(high - low) + low;
			temp.append((char)charInt);
			currSize++;
		}
		return temp.toString();
	}
	
	public String toString() {
		StringBuilder temp = new StringBuilder("");
		
		source = "98.138.253.109";	//spoffed ip address
		
		String line0 = "__________________________________\n"; 			temp.append(line0);
		String line1 = "| BIT | 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 |\n"; 			temp.append(line1);
		String line2 = "|------|---------------------------------------------------|\n"; 			temp.append(line2);
		String line3 = "|   0   |          SOURCE ( " + source + " )             \n";				temp.append(line3);  // PORT?
		String line4 = "|------|---------------------------------------------------|\n"; 			temp.append(line4);
		String line5 = "|  16 |    DESTINATION ( " + destination + " )      \n";			temp.append(line5);  // PORT?
		String line6 = "|----------------------------------------------------------|\n"; 			temp.append(line6);
		String line7 = "|  32 |              SEQUENCE NUMBER                 |\n"; 			temp.append(line7);
		String line8 = "|  " + sequenceNumber + " \n"; 	temp.append(line8);
		String line9 = "|----------------------------------------------------------|\n";    			temp.append(line9);
		String line10= "|  64 |                   ACK NUMBER                        |\n";				temp.append(line10);
		if (type == ACK)
		{
			String line11= "| " + ackNumber + " |\n";		temp.append(line11);
		}
		else if (type == SYN) 
		{
			String line11= "|                            xxxxxxxx                              |\n";		temp.append(line11);
		}
		String line12= "|------|---------------------------------------------------|\n";				temp.append(line12);
		String line13= "| BIT | 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 |\n";				temp.append(line13);
		String line14= "|------|---------------------------------------------------|\n";				temp.append(line14);
		if (type == ACK) 
		{
			String line15= "| 96  |  DATA   |0 0 0|N|C|E|U|A-1|P| R|S-0|F  |\n";				temp.append(line15);
			String line16= "|       | OFFSET |        |S|W|C|R|C-1|S| S|Y-0|I  |\n";				temp.append(line16);
			String line17= "|       |                |        |    |R|E|G|K-1|H| T|N-0|N  |\n";				temp.append(line17);
		}
		else if (type == SYN) 
		{
			String line15= "| 96  |  DATA   |0 0 0|N|C|E|U|A-0|P| R|S-1|F  |\n";				temp.append(line15);
			String line16= "|       | OFFSET |        |S|W|C|R|C-0|S| S|Y-1|I  |\n";				temp.append(line16);
			String line17= "|       |                |        |    |R|E|G|K-0|H| T|N-1|N  |\n";				temp.append(line17);
		}
		String line18= "|------|---------------------------------------------------|\n";				temp.append(line18);
		String line19= "|112 |                    WINDOW SIZE                     |\n";				temp.append(line19);
		String line20= "|------|---------------------------------------------------|\n";				temp.append(line20);
		String line21= "|128 |              CHECKSUM ( " + checksum +" )               |\n";	temp.append(line21);
		String line22= "|------|---------------------------------------------------|\n";				temp.append(line22);
		String line23= "|144 |                   URGENT POINTER                |\n";				temp.append(line23);
		String line24= "|----------------------------------------------------------|\n";				temp.append(line24);
			
		return temp.toString();
	}

	
	public String getSource() {
		return source;
	}

	public String getDestination() {
		return destination;
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public String getAckNumber() {
		return ackNumber;
	}

	public int getType() {
		return type;
	}

	
	
}
