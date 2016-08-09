package bin;

import java.util.*;

import graphic.ProcessingSimulation;

public class Network {
	private ProcessingSimulation procSim;
	private Set<Node> allNodes = new HashSet<Node>();
	private Set<Node> masterSlaves = new HashSet<Node>();
	private Set<Node> slaves = new HashSet<Node>();
	private Set<Node> reflectors = new HashSet<Node>();
	private Set<Edge> allEdges = new HashSet<Edge>();
	private Node masterNode = null, targetNode = null;
	private int numPackages = 0;
	
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
	
	public void addMasterSlaveNode(Node n) {
		masterSlaves.add(n);
	}
	public void addSlaveNode(Node n) {
		slaves.add(n);
	}
	public void addReflectorNode(Node n) {
		reflectors.add(n);
	}
	
	public void addEdge(Edge e) {
		allEdges.add(e);
	}
	
	public Node getMasterNode() { return masterNode; }
	public Node getTargetNode() { return targetNode; }
	
	public Set<Node> getAllNodes() { return allNodes; }
	public Set<Node> getMasterSlaveNodes() { return masterSlaves; }
	public Set<Node> getSlaveNodes() { return slaves; }
	public Set<Node> getReflectorNodes() { return reflectors; }
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
		int inc = 1;
		for(Edge e: allEdges) {
			if (e.getNodeFrom().equals(getMasterNode())) {
				Package pack = new Package(e, 32, Package.EMAIL_VIRUS);
				// for faster simulation -> instead of seconds use milliseconds !
				long currSec = System.currentTimeMillis()/1000;
				pack.setTimeStartSending(currSec + inc++);
				procSim.addPackageToQueue(pack);
				pack.setStatus(Package.WAITING);
				
				e.getNodeFrom().getComputer().addSentPackage(pack);
			}
		}
	}

	public void sendFromAllSlaves(int packageType) {
		int inc = 1;
		for (Node n: allNodes) {
			if (n.getComputer().getType() == Computer.SLAVE) {
				Edge e = getEdge(n, targetNode);
				Package pack = new Package(e, 32, packageType);
				// for faster simulation -> instead of seconds use milliseconds !
				long currSec = System.currentTimeMillis()/1000;
				pack.setTimeStartSending(currSec + inc++);
				procSim.addPackageToQueue(pack);
				pack.setStatus(Package.WAITING);
				
				n.getComputer().addSentPackage(pack);
			}
		}
	}
	
	public void incrementNumPackages() { numPackages++; }
	public void decrementNumPackages() { numPackages--; }
	
	public int getNumPackages() { return numPackages; }
	
	public float getTagetLeftPercent() {
		return (float)targetNode.getComputer().getMemBuffSizeCurrent() / (float)targetNode.getComputer().getMemBuffSize();
	}

	// function for ping
}