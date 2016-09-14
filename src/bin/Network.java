package bin;

import java.util.*;

import graphic.DDoSSimulation;
import graphic.ProcessingSimulation;

public class Network {
	private ProcessingSimulation procSim;
	private Set<Node> allNodes = new HashSet<Node>();
	private Vector<Node> masterSlaves = new Vector<Node>();
	private Vector<Node> slaves = new Vector<Node>();
	private Vector<Node> reflectors = new Vector<Node>();
	private Set<Edge> allEdges = new HashSet<Edge>();
	private Map<String, Node> ipAddressesMap = new HashMap<String, Node>();			// map for fast ipAddress searching
	private Node masterNode = null, targetNode = null, userNode = null, routerNode = null;
	private int numPackages = 0;
	private boolean firstTimeSending = true;
	private Edge connectionToUser = null, connectionToTarget = null; 
	
	public static long TIME_WAIT_PROCESSING = 2000;		// backdoor for this - to faster or slower
	
	public Network(ProcessingSimulation procSim) {
		this.procSim = procSim;
	}
	
	public Node getMasterNode() 	{ return masterNode; }
	public Node getTargetNode() 	{ return targetNode; }
	public Node getUserNode() 		{ return userNode; }
	public Set<Node> getAllNodes()	{ return allNodes; }
	public Vector<Node> getMasterSlaveNodes() { return masterSlaves; }
	public Vector<Node> getSlaveNodes() { return slaves; }
	public Vector<Node> getReflectorNodes() { return reflectors; }
	public Set<Edge> getAllEdges() 	{ return allEdges; }
	public void incrementNumPackages() { numPackages++; }
	public void decrementNumPackages() { numPackages--; }
	public int getNumPackages() 	{ return numPackages; }
	public ProcessingSimulation getProcSim() { return procSim; }
	
	public Node findIPAddress(String ipAddress) { return ipAddressesMap.get(ipAddress); }
	
	public void addNode(Node n) {
		Node node = ipAddressesMap.get(n.getComputer().getIpAddress());
		
		if ( targetNode != null && node != null) 
		{	// node already exist, the second addition is router Node
			routerNode = n;
		}
		else 
		{
			if (n.getComputer().getType() == Computer.MASTER)
				masterNode = n;
			else if (n.getComputer().getType() == Computer.TARGET)
				targetNode = n;
		}
		if (node == null)
		{
			allNodes.add(n);
			ipAddressesMap.put(n.getComputer().getIpAddress(), n);
		}
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
	
	public Node getRouterNode() { return routerNode; }

	public Edge getEdge(Node from, Node to) {
		if (from == null || to == null) return null;
		
		for(Edge e: allEdges)
			if (e.getNodeFrom().equals(from) && e.getNodeTo().equals(to)) return e;
		
		return null;
	}
	
	public void createConnectionWithUser() {
		Computer userComp = new Computer("79.255.255.255", "RANDOM USER", Computer.USER, 2048, 4);
		userNode = new Node(this, userComp, ProcessingSimulation.APPLET_WIDTH - 100, targetNode.getY());
		connectionToUser = new Edge(targetNode, userNode);
		connectionToTarget = new Edge(userNode, targetNode);
	}
	
	public Set<Edge> getAllReflectorEdges(Node from) {
		
		Set<Edge> allReflectedEdges = new HashSet<Edge>();
		
		for(Edge e: allEdges)
			if (e.getNodeFrom().equals(from))
				allReflectedEdges.add(e);
		
		return allReflectedEdges;
	}
	
	public void refreshComputerMemories() {
		for (Node n: allNodes) {
			int type = n.getComputer().getType();
			if (type == Computer.SLAVE || type == Computer.REFLECTING || type == Computer.TARGET) {
				Package firstPack = n.getComputer().getFirstReceivedPackage();
				if (firstPack != null && n.getComputer().getFirstReceivedPackage().getTimeInBuffer() >= DDoSSimulation.globalInMemTimeConf)
					n.getComputer().refreshMemory();
			}	
		}
	}
	
	public void refreshComputerMemory(Node n, int numToProcess) {
		long time = n.getComputer().refreshMemory(numToProcess, TIME_WAIT_PROCESSING);
		n.getComputer().setLastWave(time);
	}
		
	public void infectMasterZombies() {
		//making all email virus packages and preparing them to be drawn
		int inc = 1;
		for(Edge e: allEdges) {
			if (e.getNodeFrom().equals(getMasterNode())) {
				Packet packet = new CommandPacket(e.getNodeFrom().getComputer().getIpAddress(), e.getNodeTo().getComputer().getIpAddress(), CommandPacket.INFECT);
				Package pack = new Package(e, Package.EMAIL_VIRUS, packet);
				
				// for faster simulation -> instead of seconds use milliseconds !
				long currSec = System.currentTimeMillis()/1000;
				pack.setTimeStartSending(currSec + inc++);
				pack.setStatus(Package.WAITING);
				procSim.addPackageToQueue(pack);
				
			}
		}
	}

	/*public void sendFromAllSlaves(int packageType) {
		int inc = 1;
		for (Node n: allNodes) {
			if (n.getComputer().getType() == Computer.SLAVE) {
				Edge e = null;
				if (DDoSSimulation.globalPackageTypeTCP == true)
					e = getEdge(n, targetNode);
				else 
					e = getEdge(n, routerNode);
				Package pack = new Package(e, DDoSSimulation.globalPackageSizeConf, packageType);
				// for faster simulation -> instead of seconds use milliseconds !
				long currSec = System.currentTimeMillis()/1000;
				pack.setTimeStartSending(currSec + inc++);
				procSim.addPackageToQueue(pack);
				pack.setStatus(Package.WAITING);
			}
		}
	} */
	
	public void sendFromAllMasters() {
		int inc = 1;
		for (Node n: masterSlaves) {
			for (Node s: n.getMySlaves()) {
				Edge e = getEdge(n, s);
				Packet packet = null;
				Package pack = null;
				
				if (firstTimeSending) {
					packet = new CommandPacket(n.getComputer().getIpAddress(), s.getComputer().getIpAddress(), CommandPacket.INFECT);
					pack = new Package(e, Package.COMMAND, packet);
				}
				else {
					if (DDoSSimulation.globalPackageTypeTCP) {
						packet = new CommandPacket(n.getComputer().getIpAddress(), s.getComputer().getIpAddress(), CommandPacket.GEN_SYN);
						pack = new Package(e, Package.COMMAND, packet);
					}
					else {
						packet = new CommandPacket(n.getComputer().getIpAddress(), s.getComputer().getIpAddress(), CommandPacket.GEN_ECHO_REQ);
						pack = new Package(e, Package.COMMAND, packet);
					}
				}
				
				// for faster simulation -> instead of seconds use milliseconds !
				long currSec = System.currentTimeMillis()/1000;
				pack.setTimeStartSending(currSec + inc++);
				procSim.addPackageToQueue(pack);
				pack.setStatus(Package.WAITING);
				
			}
		}
		firstTimeSending = false;
		procSim.shuffleMS();
		
	}
	
	public float getTagetLeftPercent() {
		return (float)targetNode.getComputer().getMemBuffSizeCurrent() / (float)targetNode.getComputer().getMemBuffSize();
	}

	public Node getNodeByID(int id) {
		for (Node n: allNodes)
			if (n.getID() == id) return n;
		
		return null;
	}
}