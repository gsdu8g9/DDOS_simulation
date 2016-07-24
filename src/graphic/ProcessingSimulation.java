package graphic;

import bin.*;
import bin.Package;
import processing.core.*;
import java.util.*;

import javax.swing.JTextArea;

public class ProcessingSimulation extends PApplet{
	
	private static final int PIXEL_RANGE_NODE = 60, APPLET_WIDTH = 1200, APPLET_HEIGHT = 700, MATRIX_RANGE_PIXEL = 100, PIXEL_START_LEFT = 100,
							 MATRIX_RANGE_6 = 75, MATRIX_RANGE_5 = 100, MATRIX_RANGE_4 = 125, MATRIX_RANGE_3 = 150, MATRIX_RANGE_2 = 175, MATRIX_RANGE_1 = 200,
							 NODES_PER_LINE = 10, PIXEL_START_TOP = 50;
	
	private static final int STAGE_INFECTING_VIRUS = 1, STAGE_INIT_NETWORK = 2, STAGE_IDLE = 3;
	private int numOfSlaves;
	private Network network;
	private DDoSSimulation GUIcontrol;
	private int stage = ProcessingSimulation.STAGE_INIT_NETWORK;
	private PImage networkBackground;
	private boolean newImageToLoad = false;
	private boolean firstImageLoad = false;
	private int infected = 0;
	
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
	}
	
	private void drawNetworkBegging() {	
		background(255);
		
		PImage imgMaster = loadImage("Laptop3.png");
		PImage imgTaget = loadImage("Laptop1.png");
		PImage imgClear = loadImage("server.png");
		PImage imgInfected = loadImage("computer-alert.png");
		PFont font = loadFont("coder-15.vlw");
		
		stroke(0);
		fill(175);
		ellipse(network.getMasterNode().getX(), network.getMasterNode().getY(), 16, 16);
			
		textFont(font);
		fill(0);
		text("ATTACKER", network.getMasterNode().getX()-35, network.getMasterNode().getY()-40);
				  
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
		
		image(imgMaster, network.getMasterNode().getX()-40, network.getMasterNode().getY()-38, 80, 60);
		image(imgTaget, network.getTargetNode().getX()-40, network.getTargetNode().getY()-40, 80, 80);
		
		textFont(font);
		fill(0);
		text("TARGET", network.getTargetNode().getX()-30, network.getTargetNode().getY()+45);
			
		//draw slaves
		Set<Node> allNodes = network.getAllNodes();
		for(Node n: allNodes) {
			if (n.getComputer().getType() == Computer.SLAVE) {
				stroke(0);
				fill(175);
				ellipse(n.getX(), n.getY(), 10, 10);
					
				if (n.getInfected() == true) 
					image(imgInfected, n.getX()-15, n.getY()-25, PIXEL_RANGE_NODE, PIXEL_RANGE_NODE);
				else 
					image(imgClear, n.getX()-15, n.getY()-25, PIXEL_RANGE_NODE, PIXEL_RANGE_NODE);
					
				textFont(font);
				fill(0);
				text(n.getID(), n.getX()-15, n.getY()-5);
			}
		}
	}

	private void update() {
		if (stage == ProcessingSimulation.STAGE_INFECTING_VIRUS) {
			//check if all nodes received packages and then change servers -> infected slaves
			Set<Edge> allEdges = network.getAllEdges();
			
			for (Edge e: allEdges) {
				Package virus = e.getVirusPackage();
				if (infected < numOfSlaves && virus != null && virus.packageReceived() == true) {
					Node n = e.getNodeTo();
					n.setInfected(true);
					
					drawNetworkBegging();
					
					e.deleteVirusPackage();
					infected++;
					
					saveFrame("data/initalNetwork.png");
					newImageToLoad = true;
				}
			}
			
			//wait for all slaves to be infected by virus
			if (infected == numOfSlaves) {
				infected = 0;
				drawNetworkBegging();
				saveFrame("data/initalNetwork.png");
				JTextArea terminal = getTerminal();
				terminal.append("\n>Infecting done... \n>");
				stage = ProcessingSimulation.STAGE_IDLE;
			}
		
		}
	}
	
	public void draw() {
		update();
		
		if (newImageToLoad == true) {
			networkBackground = loadImage("initalNetwork.png");
			newImageToLoad = false;
		}
		
		if (stage != ProcessingSimulation.STAGE_INIT_NETWORK) {
			if (firstImageLoad == false) {
				networkBackground = loadImage("initalNetwork.png");
				firstImageLoad = true;
			}
			image(networkBackground, 0, 0, APPLET_WIDTH, APPLET_HEIGHT);
		}
		
		if (mousePressed == true) checkClickedComputer(mouseX, mouseY);
		
		drawAllPackages();
		
	  }
	
	private void drawAllPackages() {
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
			
			float speedUp = 3;
			float speedX = 0;
			float speedY = 1;
			
			if (pack.getEdge().getNodeFrom().getX() > pack.getEdge().getNodeTo().getX()) {
				speedX = (float)(pack.getEdge().getNodeFrom().getX() - pack.getEdge().getNodeTo().getX()) / 
						 (float)(pack.getEdge().getNodeTo().getY() - pack.getEdge().getNodeFrom().getY());
				x = x - speedX*speedUp;
			}
			else if (pack.getEdge().getNodeFrom().getX() < pack.getEdge().getNodeTo().getX()) {
				speedX = (float)(pack.getEdge().getNodeTo().getX() - pack.getEdge().getNodeFrom().getX()) / 
						 (float)(pack.getEdge().getNodeTo().getY() - pack.getEdge().getNodeFrom().getY());
				x = x + speedX*speedUp;
			}
			
			y = y + speedY*speedUp;
			
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
	
	public void saveInitialNetwork() {
		try {
		    Thread.sleep(3000);                 
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		saveFrame("data/initalNetwork.png");
	}
	
	private void makeNetworkIn_n_lines(Node masterNode, Node targetNode, int n) {
		// calculate padding between nodes
		int padding = (APPLET_WIDTH - PIXEL_START_LEFT) / (numOfSlaves/n);
		int toFill = 0;	
		boolean lastFilling = false, end = false;
		
		if (numOfSlaves % n != 0) toFill = numOfSlaves % n;
		
		for (int j=0; j<n; j++) {
		for (int i=0; i<numOfSlaves/n + ((toFill>0) ? 1 : 0); i++) {
			Node nodeSlave = null;
			if (toFill > 0) {
				padding = (APPLET_WIDTH - PIXEL_START_LEFT) / (numOfSlaves/(n)) - 10;
				if (i == numOfSlaves/n + ((toFill>0) ? 1 : 0) - 1) {
					//add to end one more node
					toFill--;
					if (toFill == 0) lastFilling = true;
				}
			}
			if (n == 6) nodeSlave = new Node( PIXEL_START_LEFT+padding*(i), PIXEL_START_TOP+MATRIX_RANGE_6*(j+1));
			if (n == 5) nodeSlave = new Node( PIXEL_START_LEFT+padding*(i), PIXEL_START_TOP+MATRIX_RANGE_5*(j+1));
			if (n == 4) nodeSlave = new Node( PIXEL_START_LEFT+padding*(i), PIXEL_START_TOP+MATRIX_RANGE_4*(j+1));
			if (n == 3) nodeSlave = new Node( PIXEL_START_LEFT+padding*(i), PIXEL_START_TOP+MATRIX_RANGE_3*(j+1));
			if (n == 2) nodeSlave = new Node( PIXEL_START_LEFT+padding*(i), PIXEL_START_TOP+MATRIX_RANGE_2*(j+1));
			if (n == 1) nodeSlave = new Node( PIXEL_START_LEFT+padding*(i), PIXEL_START_TOP+MATRIX_RANGE_1*(j+1));
			
			if (lastFilling) padding = (APPLET_WIDTH - PIXEL_START_LEFT) / (numOfSlaves/n);
			
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
	}
	
	public void makeNetworkDefault() { 
		network = new Network(this);
		
		Computer masterComputer = new Computer("79.101.110.24", "Marko Markovic", Computer.MASTER, 2048);
		Node masterNode = new Node(masterComputer, APPLET_WIDTH/2, 50);
		network.addNode(masterNode);
		
		Computer targetComputer = new Computer("69.171.230.68", "Nikola Nikolic", Computer.TARGET, 2048);
		Node targetNode = new Node(targetComputer, APPLET_WIDTH/2, APPLET_HEIGHT-50);
		network.addNode(targetNode);
		
		if (numOfSlaves <= 60 && numOfSlaves > 50) makeNetworkIn_n_lines(masterNode, targetNode, 6);
		else if (numOfSlaves <= 50 && numOfSlaves > 40) makeNetworkIn_n_lines(masterNode, targetNode, 5);
		else if (numOfSlaves <= 40 && numOfSlaves > 30) makeNetworkIn_n_lines(masterNode, targetNode, 4);
		else if (numOfSlaves <= 30 && numOfSlaves > 20) makeNetworkIn_n_lines(masterNode, targetNode, 3);
		else if (numOfSlaves <= 20 && numOfSlaves > 10) makeNetworkIn_n_lines(masterNode, targetNode, 2);
		else if (numOfSlaves <= 10) makeNetworkIn_n_lines(masterNode, targetNode, 1);
		
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

	public JTextArea getTerminal() {
		return GUIcontrol.getTerminal();
	}
}
