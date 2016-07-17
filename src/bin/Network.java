package bin;

import java.util.*;

public class Network {
	private Set<Node> allNodes = new HashSet<Node>();
	private Set<Edge> allEdges = new HashSet<Edge>();
	private Node masterNode = null, targetNode = null;
	
	public void addNode(Node n) {
		if (n.getComputer().getType() == Computer.MASTER)
			masterNode = n;
		else if (n.getComputer().getType() == Computer.TARGET)
			masterNode = n;
		
		allNodes.add(n);
	}
	
	public void addEdge(Edge e) {
		allEdges.add(e);
	}
}
