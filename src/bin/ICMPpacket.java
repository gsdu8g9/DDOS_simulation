package bin;

public class ICMPpacket extends Packet{
	
	public static final int UNKNOWN_TYPE = -1, ECHO_REQUEST = 8, ECHO_REPLY = 0, DESTINATION_UNKNOWN = 3;
	
	// ICMP header info - 4B
	private int type = UNKNOWN_TYPE;	//1B
	private int code = 0;				//1B - always 0
	private String identifier = "";		//2B
	
	public ICMPpacket(int sizeData, String identifier, int type, int size) {
		packetType = Packet.ICMP;
		this.setIdentifier(identifier);
		this.dataSize = sizeData;
		generateDataContent();
		this.type = type;
		dataSize = size;
	}
	
	public void setIdentifier(String identifier) { this.identifier = identifier; }

	public int getType() { return type; }
	
	public String toString() {
		StringBuilder temp = new StringBuilder("");
		
		String line0 = "+---------------------------------------+\n";
		String line1 = "| 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 |\n";
		String line2 = "|    TYPE ( " + type + ")  |     CODE ( 0 )       |\n";
		String line3 = "|         CHECKSUM  ( " + checksum + " )           |\n";
		String line4 = "|    IDENTIFIER ( " + identifier + " )     |\n";
		String line5 = "+---------------------------------------+\n";
		
		temp.append(line0).append(line1).append(line2).append(line3).append(line4).append(line5);
	
		return temp.toString();
	}
}
