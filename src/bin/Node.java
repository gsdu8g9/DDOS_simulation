package bin;

import java.util.*;

public class Node {
	private static int generatorID = 1;
	
	private int cordX, cordY;
	private int id;
	private Set<Node> neighbors = new HashSet<Node> ();
	private Computer computer;
	private boolean infected = false;
	
	public Node(Computer comp, int x, int y){
		computer = comp;
		cordX = x;
		cordY = y;
		id = generatorID++;
	}
	
	public Node(int x, int y) {
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
}
