package graphic;

import bin.*;
import bin.Package;
import processing.core.*;

import java.awt.Color;
import java.util.*;

import javax.swing.JTextArea;

public class ProcessingSimulation extends PApplet{
	
	private static final int PIXEL_RANGE_NODE = 60, APPLET_WIDTH = 1200, APPLET_HEIGHT = 700, MATRIX_RANGE_PIXEL = 100, PIXEL_START_LEFT = 100,
							 MATRIX_RANGE_6 = 75, MATRIX_RANGE_5 = 100, MATRIX_RANGE_4 = 125, MATRIX_RANGE_3 = 150, MATRIX_RANGE_2 = 175, MATRIX_RANGE_1 = 200,
							 NODES_PER_LINE = 10, PIXEL_START_TOP = 50;
	
	public static final int STAGE_INFECTING_VIRUS = 1, STAGE_INIT_NETWORK = 2, STAGE_IDLE = 3, STAGE_ATTACKING = 4;
	private int numOfSlaves, ddosType, infected = 0, currentNumPackages = 0;
	private long lastPackageWave = 0;
	private Network network;
	private DDoSSimulation GUIcontrol;
	private int stage = ProcessingSimulation.STAGE_INIT_NETWORK;
	private PImage networkBackground;
	private boolean newImageToLoad = false, firstImageLoad = false;
	private float angleTargetMemory = 0;
	private Node mostLeftDown, mostRightDown;
	private int numLines;
	private Set<LineCoords> ackLinesToDelete = new HashSet<LineCoords>();
	
	private class LineCoords {
		public float xFrom, yFrom, xTo, yTo;
		
		public LineCoords(float x1, float y1, float x2, float y2) {
			xFrom = x1;
			yFrom = y1;
			xTo = x2;
			yTo = y2;
		}
	}

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
		GUIcontrol.updateLastInputTerminal();
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
		currentNumPackages = network.getNumPackages();
		angleTargetMemory = (network.getTagetLeftPercent())*360;
		
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
				GUIcontrol.updateLastInputTerminal();
				
				stage = ProcessingSimulation.STAGE_IDLE;
			}
		} else if (stage == ProcessingSimulation.STAGE_ATTACKING) {
			generateCYNpackages(ddosType);
		}
	}
	
	private void generateCYNpackages(int packageType) {
		long currSec = System.currentTimeMillis()/1000;
		
		if (lastPackageWave == 0) {
			lastPackageWave = currSec;
			network.sendFromAllSlaves(packageType);
		} else 
			if ((currSec - lastPackageWave) >= 5) {  
				lastPackageWave = currSec;
				network.sendFromAllSlaves(packageType);
			}
			else if ((currSec - lastPackageWave) >= 4) {
				deletePreviousAckLines();
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
		drawMemoryInfoCircle(angleTargetMemory);
	}
	
	private void drawMemoryInfoCircle(float angleTargetMemory) {
		Color color = null;
		if ((angleTargetMemory/36)*10 < 30) color = Color.GREEN;
		else if ((angleTargetMemory/36)*10 > 30 && (angleTargetMemory/36)*10 < 60) color = Color.ORANGE;
		else color = Color.RED;
		
		if (angleTargetMemory > 0) {
			noStroke();
			fill(color.getRGB(), 51);
			arc(APPLET_WIDTH - 200, APPLET_HEIGHT - 150, 200, 200, 0, radians(angleTargetMemory));
			
			noStroke();
			fill(255);
			arc(APPLET_WIDTH - 200, APPLET_HEIGHT - 150, 100, 100, 0, radians(360));
			
			PFont font = loadFont("coder-15.vlw");
			textFont(font);
			fill(0);
			text((angleTargetMemory/36)*10+"%", APPLET_WIDTH - 230, APPLET_HEIGHT - 150);
		}
	}
	
	private void drawAllPackages() {
		Set<Edge> allEdges = network.getAllEdges();
		for (Edge e: allEdges) {
			Set<Package> allPackages = e.getPackages();
			for (Package pack: allPackages)	
				drawPackage(pack);
		}
	}
	
	private void deleteAckLines() {
		
	}
	
	private void deletePreviousAckLines() {
		for (LineCoords coord : ackLinesToDelete) {
			fill(255);
			line(coord.xFrom, coord.yFrom, coord.xTo, coord.yTo);
		}
		ackLinesToDelete.clear();
	}
	
	private void drawACK_toUnknown(int ID) {
		
		Node nodeTo = mostLeftDown;
		int id = ID%(numOfSlaves/numLines);
		if (id > numOfSlaves/(numLines*2)) nodeTo = mostRightDown;
		
		float xFromArrow = network.getTargetNode().getX() + 15;
		float yFromArrow = network.getTargetNode().getY();
		float xToArrow = nodeTo.getX();
		float yToArrow = nodeTo.getY() + ( network.getTargetNode().getY() - nodeTo.getY() ) / 4 ;
		
		Random random = new Random();
		float  randomY = random.nextInt((int)yFromArrow - (int)yToArrow + 1) + (int)yToArrow;
		
		// style line, draw arrows
		stroke(3);
		fill(Color.GREEN.getRGB());
		line(xFromArrow, yFromArrow, xToArrow, randomY);
		LineCoords coords = new LineCoords(xFromArrow, yFromArrow, xToArrow, randomY);
		ackLinesToDelete.add(coords);
	}
	
	private void drawPackage(Package pack) {
		if (!(pack.packageReceived())) {
			float x = pack.getX();
			float y = pack.getY();
			
			float speedUp = 7;
			
			if (currentNumPackages/10 > 0) speedUp = speedUp * currentNumPackages/10;
			
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
			else if (pack.getType() == Package.CYN_PACKAGE)
				img = loadImage("spoofedPackage.png");
			else 
				img = loadImage("spoofedPackage.png");
			
			image(img, x-15, y, PIXEL_RANGE_NODE/2, PIXEL_RANGE_NODE/2);
		}
		else if (pack.getType() == Package.CYN_PACKAGE) {
			// increase target memory
			boolean targetExists = network.getTargetNode().processPackage(pack);
			drawACK_toUnknown(pack.getEdge().getNodeFrom().getID());
			}
	}
	
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
			
			if (j == n-1 && i == numOfSlaves/n - 1)
				mostRightDown = nodeSlave;
			
			if (i == n-1 && i == 0)
				mostLeftDown = nodeSlave;
			
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
		
		if (numOfSlaves <= 60 && numOfSlaves > 50) 		{ makeNetworkIn_n_lines(masterNode, targetNode, 6); numLines = 6; }
		else if (numOfSlaves <= 50 && numOfSlaves > 40) { makeNetworkIn_n_lines(masterNode, targetNode, 5); numLines = 5; }
		else if (numOfSlaves <= 40 && numOfSlaves > 30) { makeNetworkIn_n_lines(masterNode, targetNode, 4); numLines = 4; }
		else if (numOfSlaves <= 30 && numOfSlaves > 20) { makeNetworkIn_n_lines(masterNode, targetNode, 3); numLines = 3; }
		else if (numOfSlaves <= 20 && numOfSlaves > 10) { makeNetworkIn_n_lines(masterNode, targetNode, 2); numLines = 2; }
		else if (numOfSlaves <= 10) 					{ makeNetworkIn_n_lines(masterNode, targetNode, 1); numLines = 1; }
		
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

	public void startDDos(int ddosType) {
		stage = ProcessingSimulation.STAGE_ATTACKING;
		this.ddosType = ddosType;
	}
	
	public JTextArea getTerminal() { return GUIcontrol.getTerminal(); }
	
	public void setNumOfSlaves(int num) { numOfSlaves = num; } 
	
	public int getStage() { return stage; }
}