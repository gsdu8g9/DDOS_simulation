package bin;

public class ICMPpacket extends Packet{
	
	public static final int UNKNOWN_TYPE = -1, ECHO_REQUEST = 8, ECHO_REPLY = 0, DESTINATION_UNKNOWN = 3;
	
	// ICMP header info - 4B
	private int type = UNKNOWN_TYPE;	//1B
	private int code = 0;				//1B - always 0
	
	public ICMPpacket(int type, int sizeData) {
		packetType = Packet.ICMP;
		this.dataSize = sizeData;
		generateDataContent();
		this.type = type;
	}
	
	public int getType() { return type; }
	
	public String toString(String ipFrom, String ipTo) {
		StringBuilder temp = new StringBuilder("");
		
		String line0 = "Source ip: " + ipFrom + "\n";
		String line1 = "Destinatio ip: " + ipTo + "\n";
		String line2 = "______________________________\n";
		String line3 = "| 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 |\n";
		String line4 = "|     TYPE ( " + type + ")    |          CODE ( 0 )             |\n";
		String line5 = "|                CHECKSUM  ( " + checksum + " )            |\n";
		String line6 = "|_____________________________|\n";
		
		temp.append(line0).append(line1).append(line2).append(line3).append(line4).append(line5).append(line6);
	
		return temp.toString();
	}
}
