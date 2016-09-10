package bin;

import java.util.Random;

public abstract class Packet {
	public static final int UNKNOWN_PACKET = 0, TCP = 1, ICMP = 2;
	public static int globalChecksum = 22222; 				//max 65535 - 1111 1111 1111 1111
	
	protected int packetType = UNKNOWN_PACKET;
	protected int checksum = globalChecksum++;				//2B
	protected int dataSize = 0;								// from configuration
	protected String dataContent = "";
	
	public int getChecksum() { return checksum; }
	public void stepChecksum() { checksum++; }
	
	public void setDataSize(int dataSize) { this.dataSize = dataSize;}
	public int getDataSize () { return dataSize;}
	
	public int getPacketType () { return packetType; }
	
	public String getDataContent() { return dataContent; }
	
	public void generateDataContent() {
		Random rand = new Random();
		StringBuilder temp = new StringBuilder("");
	
		int low = 97; // 'a'
		int high = 122; // 'z'
		int currSize = 0;
		
		while ( currSize < dataSize) {
			int charInt = rand.nextInt(high - low) + low;
			temp.append((char)charInt);
			currSize++;
		}
		dataContent = temp.toString();
	}
}
