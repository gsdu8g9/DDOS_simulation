package bin;

public class OutsidePackage {
	public static final int UNKNOWN = 0, ACKNOWLEDGE = 1, USER_PING = 2, TARGET_PING = 3;
	
	private Node startNode;
	private int type = UNKNOWN;
	private float coordXTo, coordYTo;
	private long timeCreated = 0;
	
	public OutsidePackage(Node target, float coordXTo, float coordYTo) {
		this.startNode = target;
		this.coordXTo = coordXTo;
		this.coordYTo = coordYTo;
	}
	
	public OutsidePackage(Node target, float coordXTo, float coordYTo, int type) {
		this.startNode = target;
		this.coordXTo = coordXTo;
		this.coordYTo = coordYTo;
		this.type = type;
	}
	
	public int getType() { return type; }
	
	public void setTimeCreated(long timeCreated) { this.timeCreated = timeCreated; }
	public long getTimeCreated() { return this.timeCreated; }

	public float getXTo() { return coordXTo; }
	public float getYTo() { return coordYTo; }
	
}
