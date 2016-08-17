package bin;

public class OutsidePackage {
	public static final int UNKNOWN = 0, ACKNOWLEDGE = 1, USER_PING = 2, TARGET_PING = 3;
	
	private Node startNode;
	private int type = UNKNOWN;
	private float coordXTo, coordYTo;
	private float currentX = 0, currentY = 0;
	private long timeCreated = 0;
	
	public OutsidePackage(Node target, float coordXTo, float coordYTo) {
		this.startNode = target;
		this.currentX = startNode.getX();
		this.currentY = startNode.getY();
		this.coordXTo = coordXTo;
		this.coordYTo = coordYTo;
	}
	
	public OutsidePackage(Node target, float coordXTo, float coordYTo, int type) {
		this.startNode = target;
		this.currentX = startNode.getX();
		this.currentY = startNode.getY();
		this.coordXTo = coordXTo;
		this.coordYTo = coordYTo;
		this.type = type;
	}
	
	public int getType() { return type; }
	
	public Node getStartNode() { return startNode; }
	
	public void setTimeCreated(long timeCreated) { this.timeCreated = timeCreated; }
	public long getTimeCreated() { return this.timeCreated; }

	public float getCurrentX() { return currentX; } 
	public float getCurrentY() { return currentY; }
	
	public void setCurrentX(float x) { this.currentX = x; } 
	public void setCurrentY(float y) { this.currentY = y; }
	
	public float getXTo() { return coordXTo; }
	public float getYTo() { return coordYTo; }
	
	public boolean isReceived() {
		if (type == OutsidePackage.USER_PING) {
			if (currentX > coordXTo ) return false;
			else return true;
			
		} 
		else if (type == OutsidePackage.TARGET_PING) {
			if (currentX < coordXTo ) return false;
			else return true;
		}
		
		return false;	// UNKNOWN, ACK
	}
	
}
