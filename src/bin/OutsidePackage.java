package bin;

import javax.swing.JTextArea;

public class OutsidePackage {
	public static final int UNKNOWN = 0, ACKNOWLEDGE = 1, USER_PING = 2, TARGET_PING = 3;
	
	private Packet packet;
	
	private Node startNode;
	private int type = UNKNOWN;
	private float coordXTo, coordYTo;
	private float currentX = 0, currentY = 0;
	private long timeCreated = 0;
	
	public OutsidePackage(Node target, float coordXTo, float coordYTo, Packet pack) {
		this.startNode = target;
		this.currentX = startNode.getX();
		this.currentY = startNode.getY();
		this.coordXTo = coordXTo;
		this.coordYTo = coordYTo;
		packet = pack;
		
	}
	
	public OutsidePackage(Node target, float coordXTo, float coordYTo, int type, Packet pack) {
		this.startNode = target;
		this.currentX = startNode.getX();
		this.currentY = startNode.getY();
		this.coordXTo = coordXTo;
		this.coordYTo = coordYTo;
		this.type = type;
		packet = pack;
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

	public Packet getPacket() {
		return packet;
	}

	public void writeSending(JTextArea terminal) {
		if (type == OutsidePackage.TARGET_PING)
			terminal.append("\n>Sending ECHO REQUEST from USER to TARGET");
		else if (type == OutsidePackage.USER_PING)	
			terminal.append("\n>Sending ECHO REPLY from TARGET to USER");
		else 
			terminal.append("\n>Sending ACK package from TARGET to 98.138.253.109");
	}
	
}
