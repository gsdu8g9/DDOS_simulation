package bin;

public class UnknownPackage {
	private Node targetNode;
	private float coordXTo, coordYTo;
	private long timeCreated = 0;
	
	public UnknownPackage(Node target, float coordXTo, float coordYTo) {
		this.targetNode = target;
		this.coordXTo = coordXTo;
		this.coordYTo = coordYTo;
	}
	
	public void setTimeCreated(long timeCreated) { this.timeCreated = timeCreated; }
	public long getTimeCreated() { return this.timeCreated; }

	public float getXTo() { return coordXTo; }
	public float getYTo() { return coordYTo; }
	
}
