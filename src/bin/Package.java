package bin;

public class Package {
	public static final int EMAIL_VIRUS = 0, CYN_PACKAGE = 1, ICMP_PACKAGE = 2;
	
	private Edge edge;
	private int size, type;
	private float cordX = 0, cordY = 0;
	private boolean received = false;
	private long timeInBuffer = 0;
	
	public Package(Edge e, int size, int type) {
		edge = e;
		this.size = size;
		this.type = type;
	}
	
	public void setX(float x) { 
		cordX = x; 
		boolean over = packageReceived();
		if (over) received = true;
	}
	public void setY(float y) { 
		cordY = y; 
		boolean over = packageReceived();
		if (over) received = true;
	}
	public float getX() { return cordX; }
	public float getY() { return cordY; }
	
	public Edge getEdge() { return edge; }
	
	public boolean packageReceived() {
		int endX = edge.getNodeTo().getX();
		int endY = edge.getNodeTo().getY();
		
		if (Math.ceil(cordX) == endX || Math.ceil(cordY) == endY)
			return true;
		if ((cordX < (endX+5) && cordX > (endX-5)) && (cordY < (endY+5) && cordX > (endX-5)))
			return true;
		else return false;
	}
	
	public int getType() { return type; }
	public int getSize() { return size; }
	
	public void setTimeReceived(long time) { timeInBuffer = time;} 
}
