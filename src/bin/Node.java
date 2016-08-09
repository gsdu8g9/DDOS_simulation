package bin;

import java.util.*;

public class Node {
	private static int generatorID = 1;
	private Network network;
	private int cordX, cordY, id;
	private Set<Node> neighbors = new HashSet<Node> ();
	private Computer computer;
	private boolean infected = false;
	
	public Node(Network net, Computer comp, int x, int y){
		network = net;
		computer = comp;
		cordX = x;
		cordY = y;
		id = generatorID++;
	}
	
	public Node(Network net, int x, int y) {
		network = net;
		id = generatorID++;
		cordX = x;
		cordY = y;
	}
	
	public void setInfected(boolean inf) { infected = inf; }
	
	public boolean getInfected() { return infected; }
	
	public Computer getComputer() { return computer; }
	
	public int getID() { return id; }
	
	public void setComputer(Computer comp) { computer = comp; }
	
	public void addNeighbor(Node n) { neighbors.add(n); }
	
	public int getX() { return cordX; }
	
	public int getY() { return cordY; }
	
	public Node processPackage(Package pack) {
		//check if there is return IP in network, and return ACK to it
		Node retAckNode = network.findIPAddress(pack.getEdge().getReturnIPAddress());
		
		long currSec = System.currentTimeMillis()/1000;
		pack.setReceivedTime(currSec);
		computer.increaseMemory(pack.getSize());
		return retAckNode;
	}
}
