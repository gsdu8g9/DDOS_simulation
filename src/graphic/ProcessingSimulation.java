package graphic;

import bin.*;
import java.util.Random;
import bin.Package;
import processing.core.*;
import java.awt.Color;
import java.util.*;
import javax.swing.JTextArea;

public class ProcessingSimulation extends PApplet{
	
	private static final int PIXEL_RANGE_NODE = 60, APPLET_WIDTH = 1200, APPLET_HEIGHT = 700, MATRIX_RANGE_PIXEL = 100, PIXEL_START_LEFT = 100,
							 MATRIX_RANGE_6 = 75, MATRIX_RANGE_5 = 100, MATRIX_RANGE_4 = 125, MATRIX_RANGE_3 = 150, MATRIX_RANGE_2 = 175, MATRIX_RANGE_1 = 200,
							 NODES_PER_LINE = 10, PIXEL_START_TOP = 50;
	
	public static final int STAGE_INFECTING_VIRUS = 1, STAGE_INIT_NETWORK = 2, STAGE_IDLE = 3, STAGE_ATTACKING = 4,
							STAGE_GEN = 5, STAGE_FINISHED = 6, STAGE_PAUSE = 7;
	
	public static final int TIMER_ACK_PROCESSING = 2;
	
	public static int speedUp = 1;
	
	public static int stageBeforePause = 2;
	
	private int infected = 0;
	private long lastPackageWave = 0;
	private Network network;
	private DDoSSimulation GUIcontrol;
	private int stage = ProcessingSimulation.STAGE_INIT_NETWORK;
	private PImage networkBackground;
	private boolean newImageToLoad = false, firstImageLoad = false;
	private float angleTargetMemory = 0;
	private Node mostLeftDown, mostRightDown;
	private int numLines;
	private List<Package> packageQueue = new LinkedList<Package>();
	private Set<Package> travellingPackages = new HashSet<Package>();
	private Set<UnknownPackage> ackPackages = new HashSet<UnknownPackage>();
	
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
		
		/*
		---------------------------------------HARDCODED
		------------should appear only when DDoSSimulation.globalNumSlaves > 60
		textFont(font);
		fill(0,0,100);
		text("MASTER ZOMBIES", 950+PIXEL_START_LEFT, 120+PIXEL_START_TOP);
		
		textFont(font);
		fill(0,0,100);
		text("SLAVE ZOMBIES", 950+PIXEL_START_LEFT, 120+5*PIXEL_START_TOP);
		--------------REFLECTING
		
		textFont(font);
		fill(0,0,100);
		text("MASTER ZOMBIES", 950+PIXEL_START_LEFT, 90+PIXEL_START_TOP);
		
		textFont(font);
		fill(0,0,100);
		text("SLAVE ZOMBIES", 950+PIXEL_START_LEFT, 90+4*PIXEL_START_TOP);
		
		textFont(font);
		fill(0,0,100);
		text("REFLECTORS", 950+PIXEL_START_LEFT, 90+8*PIXEL_START_TOP);
		
		--------------------------------------	
		*/
			  
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
			int ntype = n.getComputer().getType();
			if ((ntype == Computer.SLAVE)||(ntype == Computer.MASTER_SLAVE)||(ntype == Computer.REFLECTING)) {
				
				if (n.getInfected() == true) {
					stroke(0);
					fill(ntype == Computer.MASTER_SLAVE? 100: 255,0 ,0);
					ellipse(n.getX(), n.getY(), ntype==Computer.MASTER_SLAVE? 13 : 10, ntype==Computer.MASTER_SLAVE? 13 : 10);
					if (DDoSSimulation.globalNumSlaves<=60) image(imgInfected, n.getX()-15, n.getY()-25, PIXEL_RANGE_NODE, PIXEL_RANGE_NODE);
				}
				else {
					stroke(0);
					fill(0, ntype == Computer.MASTER_SLAVE? 100: 255 ,0);
					ellipse(n.getX(), n.getY(), ntype==Computer.MASTER_SLAVE? 13 : 10, ntype==Computer.MASTER_SLAVE? 13 : 10);
					if (DDoSSimulation.globalNumSlaves<=60) image(imgClear, n.getX()-15, n.getY()-25, PIXEL_RANGE_NODE, PIXEL_RANGE_NODE);
				}
					
				textFont(font);
				fill(0);
				text(n.getID(), n.getX()-15, n.getY()-5);
			}
		}
	}

	private void update() {
		angleTargetMemory = (network.getTagetLeftPercent())*360;
		
		refreshPackageQueue();
		
		if (stage == ProcessingSimulation.STAGE_INFECTING_VIRUS) {
			//check if all nodes received packages and then change servers -> infected slaves
			Set<Edge> allEdges = network.getAllEdges();
			
			for (Edge e: allEdges) {
				Package virus = e.getVirusPackage();
				if (infected < DDoSSimulation.globalNumSlaves && virus != null && virus.packageReceived() == true) {
					Node n = e.getNodeTo();
					n.setInfected(true);
					n.processPackage(virus);
					drawNetworkBegging();
					
					e.deleteVirusPackage();
					infected++;
					travellingPackages.remove(virus);
					
					saveFrame("data/initalNetwork.png");
					newImageToLoad = true;
				}
			}
			
			//wait for all slaves to be infected by virus
			if (infected == DDoSSimulation.globalNumSlaves) {
				infected = 0;
				drawNetworkBegging();
				saveFrame("data/initalNetwork.png");
				JTextArea terminal = getTerminal();
				terminal.append("\n>Infecting done... \n>");
				GUIcontrol.updateLastInputTerminal();
				
				stage = ProcessingSimulation.STAGE_IDLE;
			}
		} else if (stage == ProcessingSimulation.STAGE_GEN) {
			generateCYNpackages();
			
			stage = ProcessingSimulation.STAGE_ATTACKING;
		}
	}
	
	private void generateCYNpackages() {
		long currSec = System.currentTimeMillis()/1000;
		
		if (lastPackageWave == 0) {
			lastPackageWave = currSec;
			network.sendFromAllSlaves(Package.CYN_PACKAGE);
		} else if ((currSec - lastPackageWave) >= 4) {  
			lastPackageWave = currSec;
			network.sendFromAllSlaves(Package.CYN_PACKAGE);
		}
	}
	
	public void draw() {
		if (!isPaused()) {
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
			
			// insert part for pause stage
			if (mousePressed == true) checkClickedComputer(mouseX, mouseY);
			
			if (stage == ProcessingSimulation.STAGE_ATTACKING || stage == ProcessingSimulation.STAGE_INFECTING_VIRUS) {
				drawAllTravellingPackages();
				drawAllAckPackages();
			}
			drawMemoryInfoCircle(angleTargetMemory);
			
			if (network.getTargetNode().getComputer().isMemoryFull())
				stage = ProcessingSimulation.STAGE_FINISHED;
		}
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
	
	private void refreshPackageQueue() {
		long currSec = System.currentTimeMillis()/1000;
		
		Package head = null;
		if (packageQueue.size() > 0)
			head = packageQueue.get(0);
		
		while (head != null && head.getTimeStartSending() <= currSec && head.getStatus() == Package.WAITING) {
			head.setStatus(Package.TRAVELING);
			travellingPackages.add(head);
			Edge edge = head.getEdge();
			edge.startSendingPackage(head);
			edge.writeSendingStart(head, getTerminal());
			GUIcontrol.updateLastInputTerminal();
			packageQueue.remove(0);
			if (packageQueue.size() > 0)
				head = packageQueue.get(0);
			else
				head = null;
		}
	}
	
	private void drawAllAckPackages() {
		long currSec = System.currentTimeMillis()/1000;
		Set<UnknownPackage> newAckPackages = new HashSet<UnknownPackage>();
		
		for (UnknownPackage ack: ackPackages)
			if (currSec - ack.getTimeCreated() < TIMER_ACK_PROCESSING) {
				newAckPackages.add(ack);
				drawAckPackage_Helper(ack);
			}
		
		ackPackages = newAckPackages;
	}
	
	private void drawAckPackage_Helper(UnknownPackage ack) {
		// style line, draw arrows
        stroke(3);
        fill(Color.GREEN.getRGB());
        line(network.getTargetNode().getX(), network.getTargetNode().getY(), ack.getXTo(), ack.getYTo());
	}
	
	private void drawAllTravellingPackages() {
		Set<Package> newTravellingPackages = new HashSet<Package>();
		
		for(Package pack: travellingPackages) {
			if (pack.getStatus() == Package.TRAVELING) {
				drawPackage(pack);
				newTravellingPackages.add(pack);
			}
		}
		//removing received from list
		travellingPackages = newTravellingPackages;
		if (travellingPackages.size() == 0 && stage == ProcessingSimulation.STAGE_ATTACKING)
			stage = ProcessingSimulation.STAGE_GEN;
	}
	
	private void drawPackage_Helper(Package pack) {
		float x = pack.getX();
		float y = pack.getY();
		
		
		
		//if (travellingPackages.size()/10 > 0) speedUp = speedUp * travellingPackages.size()/10;
		
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
	
	private void drawPackage(Package pack) {
		
		if (!(pack.packageReceived())) {
			drawPackage_Helper(pack);
		}
		// package is received - increase target memory, make ACK package, prepare ACK package for drawing
		else if (pack.getType() == Package.CYN_PACKAGE) {	
			pack.setStatus(Package.RECEIVED);
			Node retNode = network.getTargetNode().processPackage(pack);
			
			// spoofed ip address
			if (retNode == null) {
				int borderXTop = mostRightDown.getX();
				int borderYTop = mostRightDown.getY();
				float yFromArrow = network.getTargetNode().getY();
				
				float yToArrow = mostRightDown.getY() + ( network.getTargetNode().getY() - mostRightDown.getY() ) / 4 ;
				
				Random random = new Random();
				float randomY = random.nextInt((int)yFromArrow - (int)yToArrow + 1) + (int)yToArrow;
				
				UnknownPackage unknown = new UnknownPackage(pack.getEdge().getNodeTo(), borderXTop, randomY);
				
				long currSec = System.currentTimeMillis()/1000;
				unknown.setTimeCreated(currSec);
				
				ackPackages.add(unknown);
			}
			
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
	
	public void addPackageToQueue(Package pack) { packageQueue.add(pack); }
	
	private void makeNetworkIn_n_lines(Node masterNode, Node targetNode, int n) {
		// calculate padding between nodes
		int padding = (APPLET_WIDTH - PIXEL_START_LEFT) / (DDoSSimulation.globalNumSlaves/n);
		int toFill = 0;	
		boolean lastFilling = false, end = false;
		
		if (DDoSSimulation.globalNumSlaves % n != 0) toFill = DDoSSimulation.globalNumSlaves % n;
		
		for (int j=0; j<n; j++) {
		for (int i=0; i<DDoSSimulation.globalNumSlaves/n + ((toFill>0) ? 1 : 0); i++) {
			Node nodeSlave = null;
			if (toFill > 0) {
				padding = (APPLET_WIDTH - PIXEL_START_LEFT) / (DDoSSimulation.globalNumSlaves/(n)) - 10;
				if (i == DDoSSimulation.globalNumSlaves/n + ((toFill>0) ? 1 : 0) - 1) {
					//add to end one more node
					toFill--;
					if (toFill == 0) lastFilling = true;
				}
			}
			if (n == 6) nodeSlave = new Node(network, PIXEL_START_LEFT+padding*(i), PIXEL_START_TOP+MATRIX_RANGE_6*(j+1));
			if (n == 5) nodeSlave = new Node(network, PIXEL_START_LEFT+padding*(i), PIXEL_START_TOP+MATRIX_RANGE_5*(j+1));
			if (n == 4) nodeSlave = new Node(network, PIXEL_START_LEFT+padding*(i), PIXEL_START_TOP+MATRIX_RANGE_4*(j+1));
			if (n == 3) nodeSlave = new Node(network, PIXEL_START_LEFT+padding*(i), PIXEL_START_TOP+MATRIX_RANGE_3*(j+1));
			if (n == 2) nodeSlave = new Node(network, PIXEL_START_LEFT+padding*(i), PIXEL_START_TOP+MATRIX_RANGE_2*(j+1));
			if (n == 1) nodeSlave = new Node(network, PIXEL_START_LEFT+padding*(i), PIXEL_START_TOP+MATRIX_RANGE_1*(j+1));
			
			if (lastFilling) padding = (APPLET_WIDTH - PIXEL_START_LEFT) / (DDoSSimulation.globalNumSlaves/n);
			
			Computer newSlave = new Computer("216.58.214."+nodeSlave.getID(),"slave"+nodeSlave.getID(), Computer.SLAVE, 2048);
			nodeSlave.setComputer(newSlave);
			
			if (j == n-1 && i == DDoSSimulation.globalNumSlaves/n - 1)
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
		Node masterNode = new Node(network, masterComputer, APPLET_WIDTH/2, 50);
		network.addNode(masterNode);
		
		Computer targetComputer = new Computer("69.171.230.68", "Nikola Nikolic", Computer.TARGET, 2048);
		Node targetNode = new Node(network, targetComputer, APPLET_WIDTH/2, APPLET_HEIGHT-50);
		network.addNode(targetNode);
		
		if (DDoSSimulation.globalResourceTypeInternal) {
			if (DDoSSimulation.globalDDOSTypeDirect) {
				if (DDoSSimulation.globalGraphTypeU60) { //internal,direct, u60
					
				} else { // internal, direct, a60
					
				}
			} else { 
				if (DDoSSimulation.globalGraphTypeU60) { //internal,reflected, u60
					
				} else { // internal, reflected, a60
					
				}
			}
			
		} else {  // network
			if (DDoSSimulation.globalDDOSTypeDirect) {
				if (DDoSSimulation.globalGraphTypeU60) { //network,direct, u60
					
				} else { // network, direct, a60
					
				}
			} else { 
				if (DDoSSimulation.globalGraphTypeU60) { //network,reflected, u60
					
				} else { // network, reflected, a60
					
				}
			}
		}
		
		
		if ( DDoSSimulation.globalNumSlaves > 40) 							makeNetworkForManyReflected(masterNode, targetNode);
		else if (DDoSSimulation.globalNumSlaves <= 60 && DDoSSimulation.globalNumSlaves > 50) { makeNetworkIn_n_lines(masterNode, targetNode, 6); numLines = 6; }
		else if (DDoSSimulation.globalNumSlaves <= 50 && DDoSSimulation.globalNumSlaves > 40) { makeNetworkIn_n_lines(masterNode, targetNode, 5); numLines = 5; }
		else if (DDoSSimulation.globalNumSlaves <= 40 && DDoSSimulation.globalNumSlaves > 30) { makeNetworkIn_n_lines(masterNode, targetNode, 4); numLines = 4; }
		else if (DDoSSimulation.globalNumSlaves <= 30 && DDoSSimulation.globalNumSlaves > 20) { makeNetworkIn_n_lines(masterNode, targetNode, 3); numLines = 3; }
		else if (DDoSSimulation.globalNumSlaves <= 20 && DDoSSimulation.globalNumSlaves > 10) { makeNetworkIn_n_lines(masterNode, targetNode, 2); numLines = 2; }
		else if (DDoSSimulation.globalNumSlaves <= 10) 					{ makeNetworkIn_n_lines(masterNode, targetNode, 1); numLines = 1; }
		
		
	}
		
	public void makeNetworkForManyReflected(Node masterNode, Node targetNode) {
		int Y = 40;
		int X = 100;
		
		for (int i=0; i<DDoSSimulation.globalNumSlaves/5; i++) {
			
			//Random rand = new Random(i);
			int randomX = i%10 * X;
			int randomY = i/10 * Y;
			Node nodeMasterSlave = new Node(network, PIXEL_START_LEFT+randomX, 2*PIXEL_START_TOP+randomY);
					
			Computer newMasterSlave = new Computer("216.58.214."+nodeMasterSlave.getID(),"slave"+nodeMasterSlave.getID(), Computer.MASTER_SLAVE, 2048);
			nodeMasterSlave.setComputer(newMasterSlave);
					
			//add edges: master-masterSlave
			Edge edge1 = new Edge(network, masterNode, nodeMasterSlave);
			network.addEdge(edge1);
			masterNode.addNeighbor(nodeMasterSlave);
			nodeMasterSlave.addNeighbor(masterNode);
			network.addNode(nodeMasterSlave);
			network.addMasterSlaveNode(nodeMasterSlave);
			
			Node nodeSlave = new Node(network, PIXEL_START_LEFT+randomX-30, 3*PIXEL_START_TOP+randomY+120);
			Computer newSlave = new Computer("216.58.214."+nodeSlave.getID(),"slave"+nodeSlave.getID(), Computer.SLAVE, 2048);
			nodeSlave.setComputer(newSlave);
			
			Node nodeSlave2 = new Node(network, PIXEL_START_LEFT+randomX+30, 3*PIXEL_START_TOP+randomY+120);
			Computer newSlave2 = new Computer("216.58.214."+nodeSlave2.getID(),"slave"+nodeSlave2.getID(), Computer.SLAVE, 2048);
			nodeSlave2.setComputer(newSlave2);
			
			Edge edgeMSS1 = new Edge(network, nodeMasterSlave, nodeSlave);
			network.addEdge(edgeMSS1);
			nodeSlave.addNeighbor(nodeMasterSlave);
			nodeMasterSlave.addNeighbor(nodeSlave);
			network.addNode(nodeSlave);
			network.addSlaveNode(nodeSlave);
			
			Edge edgeMSS2 = new Edge(network, nodeMasterSlave, nodeSlave2);
			network.addEdge(edgeMSS2);
			nodeSlave2.addNeighbor(nodeMasterSlave);
			nodeMasterSlave.addNeighbor(nodeSlave2);
			network.addNode(nodeSlave2);				
			network.addSlaveNode(nodeSlave2);
			
			Node nodeReflector1 = new Node(network, PIXEL_START_LEFT+randomX-30, 3*PIXEL_START_TOP+randomY+300);
			Computer newReflector1 = new Computer("216.58.214."+nodeReflector1.getID(),"slave"+nodeReflector1.getID(), Computer.REFLECTING, 2048);
			nodeReflector1.setComputer(newReflector1);
			network.addNode(nodeReflector1);
			network.addReflectorNode(nodeReflector1);
			
			Node nodeReflector2 = new Node(network, PIXEL_START_LEFT+randomX+30, 3*PIXEL_START_TOP+randomY+300);
			Computer newReflector2 = new Computer("216.58.214."+nodeReflector2.getID(),"slave"+nodeReflector2.getID(), Computer.REFLECTING, 2048);
			nodeReflector2.setComputer(newReflector2);
			network.addNode(nodeReflector2);
			network.addReflectorNode(nodeReflector2);
						
		}	
		Set<Node> reflectors = network.getReflectorNodes();
		Set<Node> slaves = network.getSlaveNodes();
		
		for(Node nodeReflector1: reflectors) {
			
			for(Node nodeSlave: slaves) {
				
				Edge edgeSR1 = new Edge(network, nodeReflector1, nodeSlave);
				network.addEdge(edgeSR1);
				nodeSlave.addNeighbor(nodeReflector1);
				nodeReflector1.addNeighbor(nodeSlave);
				
			}
			
			Edge edgeRT1 = new Edge(network, nodeReflector1, targetNode);
			network.addEdge(edgeRT1);
			targetNode.addNeighbor(nodeReflector1);
			nodeReflector1.addNeighbor(targetNode);
		}
		
		
	}
	
	public void makeNetworkForMany(Node masterNode, Node targetNode) {
		int Y = 60;
		int X = 100;
		
		for (int i=0; i<DDoSSimulation.globalNumSlaves/3; i++) {
			
			//Random rand = new Random(i);
			int randomX = i%10 * X;
			int randomY = i/10 * Y;
			Node nodeMasterSlave = new Node(network, PIXEL_START_LEFT+randomX, 2*PIXEL_START_TOP+randomY);
					
			Computer newMasterSlave = new Computer("216.58.214."+nodeMasterSlave.getID(),"slave"+nodeMasterSlave.getID(), Computer.MASTER_SLAVE, 2048);
			nodeMasterSlave.setComputer(newMasterSlave);
					
			//add edges: master-masterSlave
			Edge edge1 = new Edge(network, masterNode, nodeMasterSlave);
			network.addEdge(edge1);
			masterNode.addNeighbor(nodeMasterSlave);
			nodeMasterSlave.addNeighbor(masterNode);
			network.addNode(nodeMasterSlave);
			
			Node nodeSlave = new Node(network, PIXEL_START_LEFT+randomX-30, 3*PIXEL_START_TOP+randomY+200);
			Computer newSlave = new Computer("216.58.214."+nodeSlave.getID(),"slave"+nodeSlave.getID(), Computer.SLAVE, 2048);
			nodeSlave.setComputer(newSlave);
			
			Node nodeSlave2 = new Node(network, PIXEL_START_LEFT+randomX+30, 3*PIXEL_START_TOP+randomY+200);
			Computer newSlave2 = new Computer("216.58.214."+nodeSlave2.getID(),"slave"+nodeSlave2.getID(), Computer.SLAVE, 2048);
			nodeSlave2.setComputer(newSlave2);
			
			Edge edge2 = new Edge(network, nodeMasterSlave, nodeSlave);
			Edge edge22 = new Edge(network, nodeSlave, targetNode);
			network.addEdge(edge2);
			network.addEdge(edge22);
			nodeSlave.addNeighbor(targetNode);
			targetNode.addNeighbor(nodeSlave);
			nodeSlave.addNeighbor(nodeMasterSlave);
			nodeMasterSlave.addNeighbor(nodeSlave);
			network.addNode(nodeSlave);
			
			Edge edge3 = new Edge(network, nodeMasterSlave, nodeSlave2);
			Edge edge33 = new Edge(network, nodeSlave2, targetNode);
			network.addEdge(edge3);
			network.addEdge(edge33);
			nodeSlave2.addNeighbor(targetNode);
			targetNode.addNeighbor(nodeSlave2);
			nodeSlave2.addNeighbor(nodeMasterSlave);
			nodeMasterSlave.addNeighbor(nodeSlave2);
			network.addNode(nodeSlave2);						
			
		}		
	}
	
	public void checkClickedComputer(int cordmouseX, int cordmouseY) {
		GUIcontrol.detailPanelVisible(false);
		GUIcontrol.historyPanelVisible(false);
		//check all nodes, and for any node if mouse cords are in PIXEL_RANGE_NODE 
		Set<Node> allNodes = network.getAllNodes();
		for(Node node: allNodes) {
			if ((cordmouseX >= node.getX() - PIXEL_RANGE_NODE) && (cordmouseX <= (node.getX() + PIXEL_RANGE_NODE)) &&
				(cordmouseY >= node.getY() - PIXEL_RANGE_NODE) && (cordmouseY <= (node.getY() + PIXEL_RANGE_NODE))) {
				//clicked on computer -> show details
				GUIcontrol.showComputerDetails(node.getComputer(), node.getID());
				GUIcontrol.detailPanelVisible(true);
				GUIcontrol.historyPanelVisible(true);
				
				if (node.getID() == 7) speedUp = 7;
				if (node.getID() == 3) speedUp = 3; 
				if (node.getID() == 10) speedUp = 20; 
			}
		}
	}

	public void startDDos() { stage = ProcessingSimulation.STAGE_GEN; }
	
	public JTextArea getTerminal() { return GUIcontrol.getTerminal(); }
	
	public int getStage() { return stage; }
	
	public void continueSimulation() {
		stage = stageBeforePause;
	}
	
	public void pauseSimulation () {
		stageBeforePause = stage;
		stage = STAGE_PAUSE;
	}
	
	public boolean isPaused() { return stage == STAGE_PAUSE; }
}