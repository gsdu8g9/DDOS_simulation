package graphic;

import bin.*;
import processing.core.*;
import java.util.*;

public class ProcessingSimulation extends PApplet{
	
	private static final int PIXEL_RANGE_NODE = 25, APPLET_WIDTH = 1200, APPLET_HEIGHT = 700, MATRIX_RANGE_PIXEL = 100, PIXEL_START_LEFT = 100,
							 MATRIX_RANGE_6 = 75, MATRIX_RANGE_5 = 100, MATRIX_RANGE_4 = 125, MATRIX_RANGE_3 = 150, MATRIX_RANGE_2 = 175, MATRIX_RANGE_1 = 200,
							 NODES_PER_LINE = 10, PIXEL_START_TOP = 50;
	
	private int numOfSlaves;
	private Network network;
	private DDoSSimulation GUIcontrol;
	PImage networkBackground;
	
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
	
	private void drawNetworkBegging() {
		
		//draw master and target	
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
					
				PImage img = loadImage("photo.png");
				image(img, n.getX(), n.getY(), PIXEL_RANGE_NODE, PIXEL_RANGE_NODE);
					
				textFont(font);
				fill(0);
				text(n.getID(), n.getX()+7, n.getY()-2);
			}
		}
		
		saveFrame("data/initalNetwork.png");
		networkBackground = loadImage("initalNetwork.png");
	}

	public void draw() {
		image(networkBackground, 0, 0, APPLET_WIDTH, APPLET_HEIGHT);
		
		
		Edge e = network.getEdge(network.getMasterNode(), network.getSlaveById(7));
		
		drawPackage(e);
		
		if (mousePressed == true) checkClickedComputer(mouseX, mouseY);
		
	  }
	
	private void drawPackage(Edge e) {
		if (e.packageNotReachedEnd()) {
			float x = e.getPackageCordX();
			float y = e.getPackageCordY();
			
			float speedUp = 1;
			float speedX = 0;
			float speedY = 1*speedUp;
			
			if (e.getNodeFrom().getX() > e.getNodeTo().getX()) {
				speedX = (float)(e.getNodeFrom().getX() - e.getNodeTo().getX()) / (float)(e.getNodeTo().getY() - e.getNodeFrom().getY()*speedUp);
				x = x - speedX*speedUp;
			}
			else if (e.getNodeFrom().getX() < e.getNodeTo().getX()) {
				speedX = (float)(e.getNodeTo().getX() - e.getNodeFrom().getX()) / (float)(e.getNodeTo().getY() - e.getNodeFrom().getY()*speedUp);
				x = x + speedX*speedUp;
			}
			
			y = y + speedY;
			
			e.updatePackageCordX(x);
			e.updatePackageCordY(y);
			
			stroke(0);
			fill(175);
			
			PImage img = loadImage("packageIcon2.png");
			image(img, x-12, y, PIXEL_RANGE_NODE, PIXEL_RANGE_NODE);
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
		
		// testing
		Edge e = network.getEdge(network.getMasterNode(), network.getSlaveById(7));
		e.startSendingPackage();
		
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
