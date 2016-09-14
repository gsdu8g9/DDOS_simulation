package bin;

import java.awt.Color;
import java.util.*;

import graphic.DDoSSimulation;
import graphic.ProcessingSimulation;

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
	
	public Package attackTarget(int packageType) {
		Edge e = null;
		Packet packet = null;
		Package pack = null;
		
		if (DDoSSimulation.globalPackageTypeTCP) {
			e = network.getEdge(this, network.getTargetNode());
			packet = new TCPpacket(e.getNodeFrom().getComputer().getIpAddress(), e.getNodeTo().getComputer().getIpAddress(), TCPpacket.SYN, DDoSSimulation.globalPackageSizeConf);
			pack = new Package(e, Package.TCP_PACKAGE, packet);
		} else {
			e = network.getEdge(this, network.getRouterNode());
			packet = new ICMPpacket(e.getNodeTo().getComputer().getIpAddress(), ICMPpacket.ECHO_REPLY, DDoSSimulation.globalPackageSizeConf);
			pack = new Package(e, Package.ICMP_PACKAGE, packet);
		}
		
		
		long currSec = System.currentTimeMillis()/1000;
		pack.setTimeStartSending(currSec);
		pack.setStatus(Package.WAITING);
		
		return pack;
	}
	
	public Set<Package> attackReflector(int packageType) {
		Set<Edge> allReflected = network.getAllReflectorEdges(this);
		Set<Package> retSet = new HashSet<Package>();
		for(Edge e: allReflected) {
			Packet packet = new ICMPpacket(e.getNodeTo().getComputer().getIpAddress(), ICMPpacket.ECHO_REQUEST, DDoSSimulation.globalPackageSizeConf);
			Package pack = new Package(e, Package.ICMP_PACKAGE, packet);
			long currSec = System.currentTimeMillis()/1000;
			pack.setTimeStartSending(currSec);
			pack.setStatus(Package.WAITING);
			retSet.add(pack);
		}
		return retSet;
	}
	
	public Node processPackage(Package pack) {
		//check if there is return IP in network, and return ACK to it
		long currSec = System.currentTimeMillis()/1000;
		pack.setReceivedTime(currSec);
		
		computer.addReceivedPackage(pack);
		pack.getEdge().getNodeFrom().getComputer().addSentPackage(pack);
		pack.setStatus(Package.RECEIVED);
		
		switch (pack.getType()) {
			case Package.EMAIL_VIRUS :  processVirus(pack); return null;
			case Package.COMMAND :  processCommand(pack); return null;
			case Package.TCP_PACKAGE: return processSYNpackage(pack);
			case Package.ICMP_PACKAGE: return processICMPpackage(pack);
			default: return null;
		}
	}
	
	public OutsidePackage processPing(OutsidePackage ping) {
		// input in received - AKO ODABERES DA SE PAMTE SVI PAKETI - s tim da ovde mora da ide novi red, zbog tipa paketa
		// if not - just process and return ack to user
		
		OutsidePackage retAck = null;
		if (this.getComputer().isMemoryFull() == false) {
			// odraditi neki momenat da sto je target optereceniji sa memorijom, da to sporije odgovara na ping usera
			// tako generalno funckionise, kapiram da se moze srediti standardno sa nekim onim vremenima
			
			Packet packet = new ICMPpacket(network.getUserNode().getComputer().getIpAddress(), ICMPpacket.ECHO_REPLY, DDoSSimulation.globalPackageSizeConf);
			retAck = new OutsidePackage(this, network.getUserNode().getX(), network.getUserNode().cordY, OutsidePackage.TARGET_PING, packet);
			long currSec = System.currentTimeMillis()/1000;
			retAck.setTimeCreated(currSec);
		}
		return retAck;
	}
	
	private void processVirus(Package virus) {
		infected = true;
		network.getProcSim().infected++;
	}
	
	private void processCommand(Package command) {
		CommandPacket cmp = (CommandPacket)command.getPacket();
		if (cmp.getType() == CommandPacket.INFECT)
			infected = true;
		else if (cmp.getType() == CommandPacket.GEN_SYN)
			processSYNpackage(null);
		else if (cmp.getType() == CommandPacket.GEN_ECHO_REQ)
			processICMPpackage(null);
	}

	private Node processSYNpackage(Package pack) {
		if (infected || this.getComputer().getType() == Computer.TARGET) {
			if (computer.getType() == Computer.SLAVE) {
				//slave sends new package to target direct 
				Package newPack = attackTarget(Package.TCP_PACKAGE);
				network.getProcSim().addPackageToQueue(newPack);
			}
			else if (computer.getType() == Computer.TARGET) {
				Node retAckNode = network.findIPAddress(pack.getEdge().getReturnIPAddress());
				computer.increaseMemory(DDoSSimulation.globalPackageSizeConf);
				return retAckNode;
			}
		}
		return null;
	}

	private Node processICMPpackage(Package pack) {
		if (infected || this.getComputer().getType() == Computer.REFLECTING || this.getComputer().getType() == Computer.TARGET) {
			if (computer.getType() == Computer.SLAVE) {
				//slave sends to reflecting nodes
				Set<Package> newPackages = attackReflector(Package.ICMP_PACKAGE);
				for(Package p: newPackages)
					network.getProcSim().addPackageToQueue(p);
			}
			else if (computer.getType() == Computer.TARGET) {
				Node retAckNode = network.findIPAddress(pack.getEdge().getReturnIPAddress());
				computer.increaseMemory(DDoSSimulation.globalPackageSizeConf);
				return retAckNode;
			} 
			else if (computer.getType() == Computer.REFLECTING) {
				Package newPack = attackTarget(Package.ICMP_PACKAGE);
				network.getProcSim().addPackageToQueue(newPack);
			}
		}
		return null;
	}

}