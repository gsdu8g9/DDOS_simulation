package bin;

import java.util.*;

import graphic.ProcessingSimulation;

public class Network {
	private ProcessingSimulation procSim;
	private Set<Node> allNodes = new HashSet<Node>();
	private Set<Edge> allEdges = new HashSet<Edge>();
	private Node masterNode = null, targetNode = null;
	
	public Network(ProcessingSimulation procSim) {
		this.procSim = procSim;
	}
	
	public void addNode(Node n) {
		if (n.getComputer().getType() == Computer.MASTER)
			masterNode = n;
		else if (n.getComputer().getType() == Computer.TARGET)
			targetNode = n;
		
		allNodes.add(n);
	}
	
	public void addEdge(Edge e) {
		allEdges.add(e);
	}
	
	public Node getMasterNode() { return masterNode; }
	public Node getTargetNode() { return targetNode; }
	
	public Set<Node> getAllNodes() { return allNodes; }
	public Set<Edge> getAllEdges() { return allEdges; }
	
	public Edge getEdge(Node from, Node to) {
		if (from == null || to == null) return null;
		
		for(Edge e: allEdges)
			if (e.getNodeFrom().equals(from) && e.getNodeTo().equals(to))
				return e;
		
		return null;
	}
	
	public Node getSlaveById(int id) { 
		for (Node n: allNodes)
			if (n.getID() == id)
				return n;
		
		return null;
	}

	public void infectSlaves() {
		//making all email virus packages and preparing them to be drawn
		for(Edge e: allEdges) {
			if (e.getNodeFrom().equals(getMasterNode())) {
				Package pack = new Package(e, 32, Package.EMAIL_VIRUS);
				e.startSendingPackage(pack);
				e.writeSendingStart(pack, procSim.getTerminal());
			}
		}
	}
}
