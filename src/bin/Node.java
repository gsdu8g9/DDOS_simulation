package bin;

import java.awt.Color;
import java.util.*;

public class Node {
	private static int generatorID = -1;
	private Network network;
	private int cordX, cordY, id;
	private Set<Node> neighbors = new HashSet<Node> ();
	private Set<Node> mySlaves = new HashSet<>();
	private Computer computer;
	private boolean infected = false;
	private Color color = new Color(0,0,0);
	
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
	
	public void setID (int id) { if (computer.getType() == Computer.MASTER || computer.getType() == Computer.TARGET) this.id = id;}
	
	public void setComputer(Computer comp) { computer = comp; }
	
	public void addNeighbor(Node n) { neighbors.add(n); }
	
	public void addSlave(Node n) {mySlaves.add(n); }
	
	public Set<Node> getMySlaves() {return mySlaves; }
	
	public int getX() { return cordX; }
	
	public int getY() { return cordY; }
	
	public void setColor (Color color) {this.color=color;}
	
	public Color getColor () {return color;}
	
	public Package sendFromSlaveDirect(int packageType) {
		Edge e = network.getEdge(this, network.getTargetNode());
		Package pack = new Package(e, 32, packageType);
		// for faster simulation -> instead of seconds use milliseconds !
		long currSec = System.currentTimeMillis()/1000;
		pack.setTimeStartSending(currSec);
		pack.setStatus(Package.WAITING);
		computer.addSentPackage(pack);
		
		return pack;
	}
	
	public Node processPackage(Package pack) {
		//check if there is return IP in network, and return ACK to it
		long currSec = System.currentTimeMillis()/1000;
		pack.setReceivedTime(currSec);
		computer.addReceivedPackage(pack);
		
		if (computer.getType() == Computer.SLAVE) {
			
		}
		else if (computer.getType() == Computer.TARGET) {
			Node retAckNode = network.findIPAddress(pack.getEdge().getReturnIPAddress());
			computer.increaseMemory(pack.getSize());
			return retAckNode;
		}
		return null;
	}
}