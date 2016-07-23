package graphic;

import bin.*;
import bin.Package;
import processing.core.*;
import java.util.*;

public class ProcessingSimulation extends PApplet{
	
	private static final int PIXEL_RANGE_NODE = 60, APPLET_WIDTH = 1200, APPLET_HEIGHT = 700, MATRIX_RANGE_PIXEL = 100, PIXEL_START_LEFT = 100,
							 MATRIX_RANGE_6 = 75, MATRIX_RANGE_5 = 100, MATRIX_RANGE_4 = 125, MATRIX_RANGE_3 = 150, MATRIX_RANGE_2 = 175, MATRIX_RANGE_1 = 200,
							 NODES_PER_LINE = 10, PIXEL_START_TOP = 50;
	
	private static final int STAGE_INFECTING_VIRUS = 1, STAGE_INIT_NETWORK = 2;
	private int numOfSlaves;
	private Network network;
	private DDoSSimulation GUIcontrol;
	private int stage = ProcessingSimulation.STAGE_INIT_NETWORK;
	PImage networkBackground;
	boolean imageChaged = false;
	boolean imageLoaded = false;
	
	public ProcessingSimulation(DDoSSimulation DDosGui) {
		this.GUIcontrol = DDosGui;
	}
	
	public void settings() {
		size(APPLET_WIDTH,APPLET_HEIGHT);
	}
	
	public void setup() {
	  smooth();
	  background(255);
	  drawNetworkBegging();
	}
	
	public void infectSlaves() { 
		stage = ProcessingSimulation.STAGE_INFECTING_VIRUS;
		network.infectSlaves(); 
		draw(); 
	}
	
	private void drawNetworkBegging() {	
		stroke(0);
		fill(175);
		ellipse(network.getMasterNode().getX(), network.getMasterNode().getY(), 16, 16);
		
		PImage imgMaster = loadImage("master.png");
			
		PFont font;
		font = loadFont("coder-15.vlw");
		textFont(font);
		fill(0);
		text("ATTACKER", network.getMasterNode().getX()+10, network.getMasterNode().getY()-17);
				  
		stroke(0);
		fill(175);
		ellipse(network.getTargetNode().getX(), network.getTargetNode().getY(), 16, 16);
				  
		//draw edges
		Set<Edge> allEdges = network.getAllEdges();
		for(Edge e: allEdges) {
			stroke(0);
			fill(0,127);
			line(e.getNodeFrom().getX(), e.getNodeFrom().getY(), e.getNodeTo().getX(), e.getNodeTo().getY());
		}
		
		image(imgMaster, network.getMasterNode().getX()-17, network.getMasterNode().getY()-15, 35, 35);
			
		//draw slaves
		Set<Node> allNodes = network.getAllNodes();
		for(Node n: allNodes) {
			if (n.getComputer().getType() == Computer.SLAVE) {
				stroke(0);
				fill(175);
				ellipse(n.getX(), n.getY(), 10, 10);
					
				PImage img = loadImage("server.png");
				image(img, n.getX()-15, n.getY()-25, PIXEL_RANGE_NODE, PIXEL_RANGE_NODE);
					
				textFont(font);
				fill(0);
				text(n.getID(), n.getX()-15, n.getY()-5);
			}
		}
		
		saveFrame("data/initalNetwork.png");
		networkBackground = loadImage("initalNetwork.png");
		imageLoaded = true;
	}

	private void update() {
		if (stage == ProcessingSimulation.STAGE_INFECTING_VIRUS) {
			//check if all nodes received packages and then change servers -> infected slaves
			Set<Edge> allEdges = network.getAllEdges();
			int infected = 0;
			
			for (Edge e: allEdges) {
				Package virus = e.getVirusPackage();
				if (virus != null && virus.packageReceived() == true) {
					PImage img = loadImage("computer-alert.png");
					Node n = e.getNodeTo();
					image(img, n.getX()-15, n.getY()-25, PIXEL_RANGE_NODE, PIXEL_RANGE_NODE);
					infected++;
				}
			}
			
			//wait for all slaves to be infected by virus
			if (infected == numOfSlaves) {
				for (Edge e: allEdges) {
					Package virus = e.getVirusPackage();
					if (virus != null && virus.packageReceived() == true) e.deleteVirusPackage();
				}
				saveFrame("data/initalNetwork.png");
				imageChaged = true;
			}
			
		} else if (stage == ProcessingSimulation.STAGE_INIT_NETWORK)
			drawNetworkBegging();
		
	}
	
	public void draw() {
		update();
		
		if (imageChaged == true) {
			networkBackground = loadImage("initalNetwork.png");
			image(networkBackground, 0, 0, APPLET_WIDTH, APPLET_HEIGHT);
			//imageChaged = false;
		}
			
		if (imageLoaded == true)
			image(networkBackground, 0, 0, APPLET_WIDTH, APPLET_HEIGHT);
		
		if (mousePressed == true) checkClickedComputer(mouseX, mouseY);
		
		Set<Edge> allEdges = network.getAllEdges();
		for (Edge e: allEdges) {
			Set<Package> allPackages = e.getPackages();
			for (Package pack: allPackages)	
				drawPackage(pack);
		}
	  }
	
	private void drawPackage(Package pack) {
		if (!(pack.packageReceived())) {
			float x = pack.getX();
			float y = pack.getY();
			
			float speedUp = 1;
			float speedX = 0;
			float speedY = 1*speedUp;
			
			if (pack.getEdge().getNodeFrom().getX() > pack.getEdge().getNodeTo().getX()) {
				speedX = (float)(pack.getEdge().getNodeFrom().getX() - pack.getEdge().getNodeTo().getX()) / 
						 (float)(pack.getEdge().getNodeTo().getY() - pack.getEdge().getNodeFrom().getY()*speedUp);
				x = x - speedX*speedUp;
			}
			else if (pack.getEdge().getNodeFrom().getX() < pack.getEdge().getNodeTo().getX()) {
				speedX = (float)(pack.getEdge().getNodeTo().getX() - pack.getEdge().getNodeFrom().getX()) / 
						 (float)(pack.getEdge().getNodeTo().getY() - pack.getEdge().getNodeFrom().getY()*speedUp);
				x = x + speedX*speedUp;
			}
			
			y = y + speedY;
			
			pack.setX(x);
			pack.setY(y);
			
			stroke(0);
			fill(175);
			
			PImage img;
			if (pack.getType() == Package.EMAIL_VIRUS)  
				img = loadImage("email2.png");
			else
				img = loadImage("packageIcon2.png");
			
			image(img, x-15, y, PIXEL_RANGE_NODE/2, PIXEL_RANGE_NODE/2);
		} 
	}
	
	public void setNumOfSlaves(int num) { numOfSlaves = num; } 
	
	private void makeNetworkIn_6_lines(Node masterNode, Node targetNode) {
		// calculate padding between nodes
		int padding = (APPLET_WIDTH - PIXEL_START_LEFT) / (numOfSlaves/6);
						
		for (int j=0; j<6; j++) 
		for (int i=0; i<numOfSlaves/6; i++) {
			Node nodeSlave = new Node( PIXEL_START_LEFT+padding*(i), PIXEL_START_TOP+MATRIX_RANGE_6*(j+1));
			Computer newSlave = new Computer("216.58.214."+nodeSlave.getID(),"slave"+nodeSlave.getID(), Computer.SLAVE, 2048);
			nodeSlave.setComputer(newSlave);
							
			//add edges: master-slave, slave-target
			Edge edge1 = new Edge(network, masterNode, nodeSlave);
			Edge edge2 = new Edge(network, nodeSlave, targetNode);
							
			network.addEdge(edge1);
			network.addEdge(edge2);
							
			masterNode.addNeighbor(nodeSlave);
			nodeSlave.addNeighbor(masterNode);
			nodeSlave.addNeighbor(targetNode);
			targetNode.addNeighbor(nodeSlave);
							
			network.addNode(nodeSlave);
		}
	}
	
	private void makeNetworkIn_5_lines(Node masterNode, Node targetNode) {
		// calculate padding between nodes
		int padding = (APPLET_WIDTH - PIXEL_START_LEFT) / (numOfSlaves/5);
						
		for (int j=0; j<5; j++) 
		for (int i=0; i<numOfSlaves/5; i++) {
				Node nodeSlave = new Node( PIXEL_START_LEFT+padding*(i), PIXEL_START_TOP+MATRIX_RANGE_5*(j+1));
				Computer newSlave = new Computer("216.58.214."+nodeSlave.getID(),"slave"+nodeSlave.getID(), Computer.SLAVE, 2048);
				nodeSlave.setComputer(newSlave);
							
				//add edges: master-slave, slave-target
				Edge edge1 = new Edge(network, masterNode, nodeSlave);
				Edge edge2 = new Edge(network, nodeSlave, targetNode);
							
				network.addEdge(edge1);
				network.addEdge(edge2);
							
				masterNode.addNeighbor(nodeSlave);
				nodeSlave.addNeighbor(masterNode);
				nodeSlave.addNeighbor(targetNode);
				targetNode.addNeighbor(nodeSlave);
							
				network.addNode(nodeSlave);
			}
	}
	
	private void makeNetworkIn_4_lines(Node masterNode, Node targetNode) {
		// calculate padding between nodes
		int padding = (APPLET_WIDTH - PIXEL_START_LEFT) / (numOfSlaves/4);
						
		for (int j=0; j<4; j++) 
		for (int i=0; i<numOfSlaves/4; i++) {
			Node nodeSlave = new Node( PIXEL_START_LEFT+padding*(i), PIXEL_START_TOP+MATRIX_RANGE_4*(j+1));
			Computer newSlave = new Computer("216.58.214."+nodeSlave.getID(),"slave"+nodeSlave.getID(), Computer.SLAVE, 2048);
			nodeSlave.setComputer(newSlave);
							
			//add edges: master-slave, slave-target
			Edge edge1 = new Edge(network, masterNode, nodeSlave);
			Edge edge2 = new Edge(network, nodeSlave, targetNode);
							
			network.addEdge(edge1);
			network.addEdge(edge2);
							
			masterNode.addNeighbor(nodeSlave);
			nodeSlave.addNeighbor(masterNode);
			nodeSlave.addNeighbor(targetNode);
			targetNode.addNeighbor(nodeSlave);
							
			network.addNode(nodeSlave);
		}	
	}
	
	private void makeNetworkIn_3_lines(Node masterNode, Node targetNode) {
		// calculate padding between nodes
		int padding = (APPLET_WIDTH - PIXEL_START_LEFT) / (numOfSlaves/3);
						
		for (int j=0; j<3; j++) 
		for (int i=0; i<numOfSlaves/3; i++) {
			Node nodeSlave = new Node( PIXEL_START_LEFT+padding*(i), PIXEL_START_TOP+MATRIX_RANGE_3*(j+1));
			Computer newSlave = new Computer("216.58.214."+nodeSlave.getID(),"slave"+nodeSlave.getID(), Computer.SLAVE, 2048);
			nodeSlave.setComputer(newSlave);
							
			//add edges: master-slave, slave-target
			Edge edge1 = new Edge(network, masterNode, nodeSlave);
			Edge edge2 = new Edge(network, nodeSlave, targetNode);
							
			network.addEdge(edge1);
			network.addEdge(edge2);
							
			masterNode.addNeighbor(nodeSlave);
			nodeSlave.addNeighbor(masterNode);
			nodeSlave.addNeighbor(targetNode);
			targetNode.addNeighbor(nodeSlave);
							
			network.addNode(nodeSlave);
		}
	}
	
	private void makeNetworkIn_2_lines(Node masterNode, Node targetNode) {
		// calculate padding between nodes
		int padding = (APPLET_WIDTH - PIXEL_START_LEFT) / (numOfSlaves/2);
				
		for (int j=0; j<2; j++) 
		for (int i=0; i<numOfSlaves/2; i++) {
					Node nodeSlave = new Node( PIXEL_START_LEFT+padding*(i), PIXEL_START_TOP+MATRIX_RANGE_2*(j+1));
					Computer newSlave = new Computer("216.58.214."+nodeSlave.getID(),"slave"+nodeSlave.getID(), Computer.SLAVE, 2048);
					nodeSlave.setComputer(newSlave);
					
					//add edges: master-slave, slave-target
					Edge edge1 = new Edge(network, masterNode, nodeSlave);
					Edge edge2 = new Edge(network, nodeSlave, targetNode);
					
					network.addEdge(edge1);
					network.addEdge(edge2);
					
					masterNode.addNeighbor(nodeSlave);
					nodeSlave.addNeighbor(masterNode);
					nodeSlave.addNeighbor(targetNode);
					targetNode.addNeighbor(nodeSlave);
					
					network.addNode(nodeSlave);
				}
	}
	
	private void makeNetworkIn_1_lines(Node masterNode, Node targetNode) {
		// calculate padding between nodes
		int padding = (APPLET_WIDTH - PIXEL_START_LEFT) / numOfSlaves;
		
		for (int i=0; i<numOfSlaves; i++) {
			Node nodeSlave = new Node( PIXEL_START_LEFT+padding*(i), APPLET_HEIGHT/2);
			Computer newSlave = new Computer("216.58.214."+nodeSlave.getID(),"slave"+nodeSlave.getID(), Computer.SLAVE, 2048);
			nodeSlave.setComputer(newSlave);
			
			//add edges: master-slave, slave-target
			Edge edge1 = new Edge(network, masterNode, nodeSlave);
			Edge edge2 = new Edge(network, nodeSlave, targetNode);
			
			network.addEdge(edge1);
			network.addEdge(edge2);
			
			masterNode.addNeighbor(nodeSlave);
			nodeSlave.addNeighbor(masterNode);
			nodeSlave.addNeighbor(targetNode);
			targetNode.addNeighbor(nodeSlave);
			
			network.addNode(nodeSlave);
		}
	}
	
	public void makeNetworkDefault() { 
		network = new Network();
		
		Computer masterComputer = new Computer("79.101.110.24", "Marko Markovic", Computer.MASTER, 2048);
		Node masterNode = new Node(masterComputer, APPLET_WIDTH/2, 50);
		network.addNode(masterNode);
		
		Computer targetComputer = new Computer("69.171.230.68", "Nikola Nikolic", Computer.TARGET, 2048);
		Node targetNode = new Node(targetComputer, APPLET_WIDTH/2, APPLET_HEIGHT-50);
		network.addNode(targetNode);
		
		if (numOfSlaves <= 60 && numOfSlaves > 50) makeNetworkIn_6_lines(masterNode, targetNode);
		else if (numOfSlaves <= 50 && numOfSlaves > 40) makeNetworkIn_5_lines(masterNode, targetNode);
		else if (numOfSlaves <= 40 && numOfSlaves > 30) makeNetworkIn_4_lines(masterNode, targetNode);
		else if (numOfSlaves <= 30 && numOfSlaves > 20) makeNetworkIn_3_lines(masterNode, targetNode);
		else if (numOfSlaves <= 20 && numOfSlaves > 10) makeNetworkIn_2_lines(masterNode, targetNode);
		else if (numOfSlaves <= 10) makeNetworkIn_1_lines(masterNode, targetNode);
		
	}

	public void checkClickedComputer(int cordmouseX, int cordmouseY) {
		GUIcontrol.detailPanelVisible(false);
		
		//check all nodes, and for any node if mouse cords are in PIXEL_RANGE_NODE 
		Set<Node> allNodes = network.getAllNodes();
		for(Node node: allNodes) {
			if ((cordmouseX >= node.getX()) && (cordmouseX <= (node.getX() + PIXEL_RANGE_NODE))) {
				//clicked on computer -> show details
				GUIcontrol.showComputerDetails(node.getComputer(), node.getID());
				GUIcontrol.detailPanelVisible(true);
			}
		}
	}
}
