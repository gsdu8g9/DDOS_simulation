package bin;

public class Computer {
	
	public static final int MASTER = 0, MASTER_SLAVE = 1, SLAVE = 2, TARGET = 3;
	
	private String ipAddress;
	private String domain;
	private int type;
	private int[] memBuffer;
	
	public Computer(String ipAddress, String domain, int type, int memSize) {
		this.ipAddress = ipAddress;
		this.domain = domain;
		this.type = type;
		
		//check for max memory
		if (memSize > 0) memBuffer = new int[memSize];
	}
	
	public int getType() { return type; }
	
	public String getIpAddress() { return ipAddress; }
}
