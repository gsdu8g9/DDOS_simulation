package bin;

public class Computer {
	
	public static final int MASTER = 0, MASTER_SLAVE = 1, SLAVE = 2, TARGET = 3;
	
	private String ipAddress;
	private String domain;
	private int type, TTL;
	private int[] memBuffer;
	private int maxSize = 0, currSize = 0;
	
	public Computer(String ipAddress, String domain, int type, int memSize) {
		this.ipAddress = ipAddress;
		this.domain = domain;
		this.type = type;
		TTL = 4;
		maxSize = memSize;
	}
	
	public Computer(String ipAddress, String domain, int type, int memSize, int TTL) {
		this.ipAddress = ipAddress;
		this.domain = domain;
		this.type = type;
		this.TTL = TTL;
		maxSize = memSize;
	}
	
	public int getTTL() { return TTL; }
	public int getMemBuffSizeCurrent() { return currSize;}
	public int getMemBuffSize() {return maxSize; }
	public int getType() { return type; }
	public String getIpAddress() { return ipAddress; }
	public String getDomain() {return domain; }
	
	public String getTypeString() {
		if (type == MASTER) return "MASTER";
		if (type == MASTER_SLAVE) return "MASTER SLAVE";
		if (type == SLAVE) return "SLAVE";
		if (type == TARGET) return "TARGET";
		
		return "missing info";
	}
	
	public void increaseMemory(int size) { 
		if ((currSize + size) < maxSize) currSize += size;
		else if ( currSize + size >= maxSize) currSize = maxSize;
	} 
	
	public void decreaseMemory(int size) {
		if ((currSize - size) > 0) currSize -= size;
		else if (currSize > 0 && (currSize - size <= 0) ) currSize = 0;
	}

}