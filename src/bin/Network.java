package bin;

import java.util.*;

import graphic.ProcessingSimulation;

public class Network {
	private ProcessingSimulation procSim;
	private Set<Node> allNodes = new HashSet<Node>();
	private Vector<Node> masterSlaves = new Vector<Node>();
	private Vector<Node> slaves = new Vector<Node>();
	private Vector<Node> reflectors = new Vector<Node>();
	private Set<Edge> allEdges = new HashSet<Edge>();
	private Map<String, Node> ipAddressesMap = new HashMap<String, Node>();			// map for fast ipAddress searching
	private Node masterNode = null, targetNode = null;
	private int numPackages = 0;
	
	public Network(ProcessingSimulation procSim) {
		this.procSim = procSim;
	}
	
	public Node getMasterNode() 	{ return masterNode; }
	public Node getTargetNode() 	{ return targetNode; }
	public Set<Node> getAllNodes()	{ return allNodes; }
	public Vector<Node> getMasterSlaveNodes() { return masterSlaves; }
	public Vector<Node> getSlaveNodes() { return slaves; }
	public Vector<Node> getReflectorNodes() { return reflectors; }
	public Set<Edge> getAllEdges() 	{ return allEdges; }
	public void incrementNumPackages() { numPackages++; }
	public void decrementNumPackages() { numPackages--; }
	public int getNumPackages() 	{ return numPackages; }
	
	public Node findIPAddress(String ipAddress) { return ipAddressesMap.get(ipAddress); }
	
	public void addNode(Node n) {
		if (n.getComputer().getType() == Computer.MASTER)
			masterNode = n;
		else if (n.getComputer().getType() == Computer.TARGET)
			targetNode = n;
		
		allNodes.add(n);
		ipAddressesMap.put(n.getComputer().getIpAddress(), n);
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

	public Edge getEdge(Node from, Node to) {
		if (from == null || to == null) return null;
		
		for(Edge e: allEdges)
			if (e.getNodeFrom().equals(from) && e.getNodeTo().equals(to)) return e;
		
		return null;
	}
	
	public Node getSlaveById(int id) { 
		for (Node n: allNodes)
			if (n.getID() == id) return n;
		
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
	
	public void sendFromAllMasters(int packageType) {
		int inc = 1;
		for (Node n: masterSlaves) {
			for (Node s: n.getMySlaves()) {
				Edge e = getEdge(n, s);
				Package pack = new Package(e, 32, packageType);
				// for faster simulation -> instead of seconds use milliseconds !
				long currSec = System.currentTimeMillis()/1000;
				pack.setTimeStartSending(currSec + inc++);
				procSim.addPackageToMSQueue(pack);
				pack.setStatus(Package.WAITING);
				
				n.getComputer().addSentPackage(pack);
			}
		}
	}
	
	public float getTagetLeftPercent() {
		return (float)targetNode.getComputer().getMemBuffSizeCurrent() / (float)targetNode.getComputer().getMemBuffSize();
	}

	
	// function for ping
}