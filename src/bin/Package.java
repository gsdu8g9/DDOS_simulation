package bin;

public class Package {
	public static final int EMAIL_VIRUS = 0, CYN_PACKAGE = 1, ICMP_PACKAGE = 2;
	public static final int INIT = 0, WAITING = 1, TRAVELING = 2, RECEIVED = 3;
	
	private Packet packet;
	
	private Edge edge;
	private int size, type;
	private float cordX = 0, cordY = 0;
	private int status = Package.INIT;
	private long timeStartSending = 0, timeReceived = 0;
	
	public Package(Edge e, int size, int type) {
		edge = e;
		this.size = size;
		this.type = type;
	}
	
	public void setPacket(Packet pack) { packet = pack; }
	
	public Packet getPacket() { return packet; }
	
	public int getStatus() { return status; }
	
	public void setStatus(int stat) { status = stat; }
	
	public long getTimeStartSending() { return timeStartSending; }
	
	public void setTimeStartSending(long newTime) { timeStartSending = newTime; }
	
	public long getReceivedTime() { return timeReceived; }
	
	public void setReceivedTime(long newTime) { timeReceived = newTime; }
	
	public long getTimeInBuffer() {
		long currSec = System.currentTimeMillis()/1000;
		return (currSec - timeReceived);
	}
	
	public void setX(float x) { cordX = x; }
	
	public void setY(float y) { cordY = y; }
	
	public float getX() { return cordX; }
	
	public float getY() { return cordY; }
	
	public Edge getEdge() { return edge; }
	
	public boolean packageReceived() {
		if (status == Package.RECEIVED) return true;
		
		if ( cordY >= edge.getNodeTo().getY() ) {
			status = Package.RECEIVED;
			return true;
		}
		else return false;
	}
	
	public int getType() { return type; }
	
	public int getSize() { return size; }
}
