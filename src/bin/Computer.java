package bin;

public class Computer {
	
	public static final int MASTER = 0, MASTER_SLAVE = 1, SLAVE = 2, TARGET = 3;
	
	private String ipAddress;
	private String domain;
	private int type, currSizeMemBuff, TTL;
	private int[] memBuffer;
	
	public Computer(String ipAddress, String domain, int type, int memSize) {
		this.ipAddress = ipAddress;
		this.domain = domain;
		this.type = type;
		currSizeMemBuff = 0;
		TTL = 4;				// default TTL = 4 sec;
		
		//check for max memory
		if (memSize > 0) memBuffer = new int[memSize];
	}
	
	public Computer(String ipAddress, String domain, int type, int memSize, int TTL) {
		this.ipAddress = ipAddress;
		this.domain = domain;
		this.type = type;
		this.TTL = TTL;
		currSizeMemBuff = 0;
			
		//check for max memory
		if (memSize > 0) memBuffer = new int[memSize];
	}
	
	public int getTTL() { return TTL; }
	public int getMemBuffSizeCurrent() { return currSizeMemBuff;}
	public int getMemBuffSize() {return memBuffer.length; }
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

}
