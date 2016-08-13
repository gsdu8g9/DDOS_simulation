package graphic;

import bin.*;
import java.util.Random;
import bin.Package;
import processing.core.*;
import java.awt.Color;
import java.util.*;
import javax.swing.JTextArea;

public class ProcessingSimulation extends PApplet{
	
	public static final int PIXEL_RANGE_NODE = 60, APPLET_WIDTH = 1200, APPLET_HEIGHT = 700, PIXEL_START_LEFT = 100, PIXEL_START_TOP = 50,
							PIXEL_START_LEFT_U30 = 200, MAX_MASTERS_U30 = 5;
	public static final int STAGE_INFECTING_VIRUS = 1, STAGE_INIT_NETWORK = 2, STAGE_IDLE = 3, STAGE_ATTACKING = 4,
							 STAGE_GEN = 5, STAGE_FINISHED = 6, STAGE_PAUSE = 7;
	public static final int X_STEP_U30 = 200, Y_STEP_U30 = 70;
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
	private Node mostRightDown;
	private List<Package> packageQueue = new LinkedList<Package>();
	private List<Package> MSPackageQueue = new LinkedList<Package>();
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
	  drawNetworkBegining();
	}
	
	public void infectSlaves() { 
		stage = ProcessingSimulation.STAGE_INFECTING_VIRUS;
		network.infectSlaves();
		GUIcontrol.updateLastInputTerminal();
	}
	
	private void drawNetworkBegining() {	
		background(255);
		
		PImage imgMaster = loadImage("Laptop3.png");
		PImage imgTaget = loadImage("Laptop1.png");
		PImage imgClear = loadImage("server.png");
		PImage imgMasterSlave = loadImage("computer-success.png");
		PImage imgInfected = loadImage("computer-alert.png");
		PFont font = loadFont("coder-15.vlw");
		
		stroke(0);
		fill(175);
		ellipse(network.getMasterNode().getX(), network.getMasterNode().getY(), 16, 16);
			
		textFont(font);
		fill(0);
		text("ATTACKER", network.getMasterNode().getX()-35, network.getMasterNode().getY()-40);
		
		//------------should appear only when DDoSSimulation.globalNumSlaves > 60
		if (!DDoSSimulation.globalGraphTypeU60) {
			if (DDoSSimulation.globalDDOSTypeDirect) {
				textFont(font);
				fill(0,0,100);
				text("MASTER ZOMBIES", 965+PIXEL_START_LEFT, 100+PIXEL_START_TOP);
				
				textFont(font);
				fill(0,0,100);
				text("SLAVE ZOMBIES", 965+PIXEL_START_LEFT, 100+4*PIXEL_START_TOP);
			}
			else {
			
				textFont(font);
				fill(0,0,100);
				text("MASTER ZOMBIES", 965+PIXEL_START_LEFT, 90+PIXEL_START_TOP);
				
				textFont(font);
				fill(0,0,100);
				text("SLAVE ZOMBIES", 965+PIXEL_START_LEFT, 90+4*PIXEL_START_TOP);
				
				textFont(font);
				fill(0,0,100);
				text("REFLECTORS", 965+PIXEL_START_LEFT, 90+9*PIXEL_START_TOP);
			}
		}
			  
		stroke(0);
		fill(175);
		ellipse(network.getTargetNode().getX(), network.getTargetNode().getY(), 16, 16);
			  
		//draw edges
		Set<Edge> allEdges = network.getAllEdges();
		for(Edge e: allEdges) {
			if (!DDoSSimulation.globalGraphTypeU60) {
				stroke(e.getNodeFrom().getColor().getRed(), e.getNodeFrom().getColor().getGreen(), e.getNodeFrom().getColor().getBlue());
			} else {
				if (e.getNodeTo().getComputer().getType() == Computer.TARGET || e.getNodeTo().getComputer().getType() == Computer.MASTER_SLAVE) {
					stroke(0);
					strokeWeight(1);
				}
				else {
					stroke(e.getNodeFrom().getColor().getRed(), e.getNodeFrom().getColor().getGreen(), e.getNodeFrom().getColor().getBlue());
					strokeWeight(3);
				}
			}
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
					if (DDoSSimulation.globalNumSlaves<=50) image(imgInfected, n.getX()-15, n.getY()-25, PIXEL_RANGE_NODE, PIXEL_RANGE_NODE);
				}
				else {
					stroke(0);
					strokeWeight(1);
					//fill(0, ntype == Computer.MASTER_SLAVE? 100: 255 ,0);
					fill(n.getColor().getRed(), n.getColor().getGreen(), n.getColor().getBlue());
					ellipse(n.getX(), n.getY(), ntype==Computer.MASTER_SLAVE? 13 : 10, ntype==Computer.MASTER_SLAVE? 13 : 10);

					if (DDoSSimulation.globalNumSlaves<=50) 
						if (ntype == Computer.MASTER_SLAVE)
							image(imgMasterSlave, n.getX()-15, n.getY()-25, PIXEL_RANGE_NODE, PIXEL_RANGE_NODE);
						else
							image(imgClear, n.getX()-15, n.getY()-25, PIXEL_RANGE_NODE, PIXEL_RANGE_NODE);

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
		refreshMSPackageQueue();
		
		if (stage == ProcessingSimulation.STAGE_INFECTING_VIRUS) {
			//check if all nodes received packages and then change servers -> infected slaves
			Set<Edge> allEdges = network.getAllEdges();
			
			for (Edge e: allEdges) {
				Package virus = e.getVirusPackage();
				if (infected < DDoSSimulation.globalNumSlaves && virus != null && virus.packageReceived() == true) {
					virus.setStatus(Package.RECEIVED);
					Node n = e.getNodeTo();
					n.setInfected(true);
					n.processPackage(virus);
					drawNetworkBegining();
					e.deleteVirusPackage();
					infected++;
					travellingPackages.remove(virus);
					
					saveFrame("data/initalNetwork.png");
					newImageToLoad = true;
					
				}
			}
			
			//wait for all slaves to be infected by virus
			if (infected == DDoSSimulation.globalNumMasterSlaves) {
				infected = 0;
				drawNetworkBegining();
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
		
		if (!DDoSSimulation.globalGraphTypeU60) {
			network.sendFromAllMasters(Package.CYN_PACKAGE);
		}
		else {
			if (lastPackageWave == 0) {
				lastPackageWave = currSec;
				network.sendFromAllSlaves(Package.CYN_PACKAGE);
			} else if ((currSec - lastPackageWave) >= 4) {  
				lastPackageWave = currSec;
				network.sendFromAllSlaves(Package.CYN_PACKAGE);
			}
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
	
	private void refreshMSPackageQueue() {
		long currSec = System.currentTimeMillis()/1000;
		
		Package head = null;
		if (MSPackageQueue.size() > 0)
			head = MSPackageQueue.get(0);
		
		while (head != null && head.getTimeStartSending() <= currSec && head.getStatus() == Package.WAITING) {
			head.setStatus(Package.TRAVELING);
			travellingPackages.add(head);
			Edge edge = head.getEdge();
			edge.startSendingPackage(head);
			edge.writeSendingStart(head, getTerminal());
			GUIcontrol.updateLastInputTerminal();
			MSPackageQueue.remove(0);
			if (MSPackageQueue.size() > 0)
				head = MSPackageQueue.get(0);
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
		
		if (DDoSSimulation.globalGraphTypeU60) {
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
		else blinkEdges(pack);
	}
	
	private void blinkEdges(Package pack) {
		Edge e = pack.getEdge();
		
		stroke(255,0,0);
		strokeWeight(2);
		line(e.getNodeFrom().getX(), e.getNodeFrom().getY(), e.getNodeTo().getX(), e.getNodeTo().getY());
		
		stroke(e.getNodeFrom().getColor().getRed(), e.getNodeFrom().getColor().getGreen(), e.getNodeFrom().getColor().getBlue());
		strokeWeight(1);
		line(e.getNodeFrom().getX(), e.getNodeFrom().getY(), e.getNodeTo().getX(), e.getNodeTo().getY());
	}
	
	private void drawPackage(Package pack) {
		
		if (!(pack.packageReceived())) {
			drawPackage_Helper(pack);
		}
		// package is received - increase target memory, make ACK package, prepare ACK package for drawing
		else if (pack.getType() == Package.CYN_PACKAGE) {	
			pack.setStatus(Package.RECEIVED);
			
			if (!DDoSSimulation.globalGraphTypeU60) {
				pack.getEdge().getNodeTo().processPackage(pack);
				if (pack.getEdge().getNodeTo().getComputer().getType() == Computer.SLAVE && pack.getType() == Package.CYN_PACKAGE) {
					Package newPack = pack.getEdge().getNodeTo().sendFromSlaveDirect(pack.getType());
					addPackageToQueue(newPack);
				}
			}
			
			// spoofed ip address
			if (DDoSSimulation.globalGraphTypeU60) {
				Node retNode = network.getTargetNode().processPackage(pack);
				
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
	
	public void addPackageToMSQueue(Package pack) { MSPackageQueue.add(pack); }
	
	public void makeNetworkDefault() { 
		network = new Network(this);
		
		Computer masterComputer = new Computer("79.101.110.24", "Marko Markovic", Computer.MASTER, 2048);
		Node masterNode = new Node(network, masterComputer, APPLET_WIDTH/2, 50);
		masterNode.setID(500);
		network.addNode(masterNode);
		
		Computer targetComputer = new Computer("69.171.230.68", "Nikola Nikolic", Computer.TARGET, 2048);
		Node targetNode = new Node(network, targetComputer, APPLET_WIDTH/2, APPLET_HEIGHT-50);
		targetNode.setID(505);
		network.addNode(targetNode);
		
		if (DDoSSimulation.globalDDOSTypeDirect) {
			if (DDoSSimulation.globalGraphTypeU60) { 	
				makeInternalDirectUnder30();	
			} else { 									
				makeInternalDirectAbove50();
			}
		} else { 
			if (DDoSSimulation.globalGraphTypeU60) { 
				makeInternalReflectedUnder30();
			} else {
				makeInternalReflectedAbove50();
			}
		}
	}
		
	public void makeInternalReflectedAbove50() {
		int Ystep = 40;
		int Xstep = 100;
		
		int masters = DDoSSimulation.globalNumMasterSlaves = DDoSSimulation.globalNumSlaves/7;
		int toFill = masters % 10;
		int linePixel = masters / 10 * Ystep;
		
		for (int i=0; i<masters; i++) {
			
			int X = i%10 * Xstep;
			int Y = i/10 * Ystep;
			
			int leftStart = PIXEL_START_LEFT - ((i/10)%2)*17 + ((i/10)%3)*7;
			if (Y==linePixel && toFill!=0) leftStart = PIXEL_START_LEFT+400-(toFill-1)*50;
			
			Node nodeMasterSlave = makeMasterSlaveAndAddToNetwork(leftStart, X, Y);
			
			makeSlaveAndAddToNetwork(nodeMasterSlave, leftStart, X-35, Y, 100);
			makeSlaveAndAddToNetwork(nodeMasterSlave, leftStart, X, Y, 100);
			makeSlaveAndAddToNetwork(nodeMasterSlave, leftStart, X+35, Y, 100);
			
			makeReflectorAndAddToNetwork(leftStart, X-35, Y, 350);
			makeReflectorAndAddToNetwork(leftStart, X, Y, 350);
			makeReflectorAndAddToNetwork(leftStart, X+35, Y, 350);
			
			if (i==(masters-1) && (DDoSSimulation.globalNumSlaves%7 > 0)) {
				for (int j=0; j<DDoSSimulation.globalNumSlaves%7; j++) {
					if (j%2 == 0) makeSlaveAndAddToNetwork(nodeMasterSlave, leftStart, X+35+35*((j+2)/2), Y, 100);
					else makeReflectorAndAddToNetwork(leftStart, X+35+35*((j+2)/2), Y, 350);
				}
			}
			
		}	
		
		Vector<Node> reflectors = network.getReflectorNodes();
		Vector<Node> slaves = network.getSlaveNodes();
		
		for(Node reflector: reflectors) {
			for(int i =0; i<7; i++) {
				
				Random rand = new Random();
				int slaveIndex = rand.nextInt(slaves.size());
				Node nodeSlave = slaves.get(slaveIndex);
				makeNeighbours(nodeSlave, reflector);
				nodeSlave.addSlave(reflector); //not sure if this should stay
			}
			makeNeighbours(reflector, network.getTargetNode());
		}
		
	}
	
	public void makeInternalDirectAbove50() {
		int Ystep = 40;
		int Xstep = 100;
		int masters = DDoSSimulation.globalNumMasterSlaves = DDoSSimulation.globalNumSlaves/4;
		int toFill = masters % 10;
		int linePixel = masters / 10 * Ystep;
		
		for (int i=0; i<masters; i++) {
			
			int X = i%10 * Xstep;
			int Y = i/10 * Ystep;
			
			int leftStart = PIXEL_START_LEFT - ((i/10)%2)*17 + ((i/10)%3)*7;
			if (Y==linePixel && toFill!=0) leftStart = PIXEL_START_LEFT+400-(toFill-1)*50;
			
			Node nodeMasterSlave = makeMasterSlaveAndAddToNetwork(leftStart, X, Y);
			
			makeSlaveAndAddToNetwork(nodeMasterSlave, leftStart, X-35, Y, 250);
			makeSlaveAndAddToNetwork(nodeMasterSlave, leftStart, X, Y, 250);
			makeSlaveAndAddToNetwork(nodeMasterSlave, leftStart, X+35, Y, 250);
			
			if (i==(masters-1) && (DDoSSimulation.globalNumSlaves%4 > 0)) {
				for (int j=0; j<DDoSSimulation.globalNumSlaves%4; j++) {
					makeSlaveAndAddToNetwork(nodeMasterSlave, leftStart, X+35+35*(j+1), Y, 250);
				}
			}
		}		
	}
	
	private void makeNeighbours(Node from, Node to) {
		Edge edge = new Edge(network, from, to);
		network.addEdge(edge);
		from.addNeighbor(to);
		to.addNeighbor(from);
	}
	
	private Node makeMasterSlaveAndAddToNetwork(int leftStart, int x, int y) {
		
		Node nodeMasterSlave = new Node(network, leftStart+x, 2*PIXEL_START_TOP+y);
		Random rand = new Random();
		nodeMasterSlave.setColor(new Color(rand.nextInt(254),rand.nextInt(254), rand.nextInt(254)));
				
		Computer newMasterSlave = new Computer("216.58.214."+nodeMasterSlave.getID(),"slave"+nodeMasterSlave.getID(), Computer.MASTER_SLAVE, 2048);
		nodeMasterSlave.setComputer(newMasterSlave);
		network.getMasterNode().addSlave(nodeMasterSlave);
		
		//add edges: master-masterSlave
		makeNeighbours(network.getMasterNode(),nodeMasterSlave);
		network.addNode(nodeMasterSlave);
		network.addMasterSlaveNode(nodeMasterSlave);
		
		return nodeMasterSlave;
	}
	
	private void makeSlaveAndAddToNetwork(Node nodeMasterSlave, int leftStart, int x, int y, int yOffset) {
		
		Node nodeSlave = new Node(network, leftStart+x, 3*PIXEL_START_TOP+y+yOffset);
		Computer newSlave = new Computer("216.58.214."+nodeSlave.getID(),"slave"+nodeSlave.getID(), Computer.SLAVE, 2048);
		nodeSlave.setComputer(newSlave);
		nodeMasterSlave.addSlave(nodeSlave);
		nodeSlave.setColor(nodeMasterSlave.getColor());
		
		makeNeighbours(nodeMasterSlave, nodeSlave);
		network.addNode(nodeSlave);
		network.addSlaveNode(nodeSlave);
		
		if (DDoSSimulation.globalDDOSTypeDirect) 
			makeNeighbours(nodeSlave,network.getTargetNode());
	}
	
	private void makeReflectorAndAddToNetwork(int leftStart, int X, int Y, int Yoffset) {
		Node nodeReflector1 = new Node(network, leftStart+X, 3*PIXEL_START_TOP+Y+Yoffset);
		Computer newReflector1 = new Computer("216.58.214."+nodeReflector1.getID(),"slave"+nodeReflector1.getID(), Computer.REFLECTING, 2048);
		nodeReflector1.setComputer(newReflector1);
		network.addNode(nodeReflector1);
		network.addReflectorNode(nodeReflector1);
	}
	
	public void checkClickedComputer(int cordmouseX, int cordmouseY) {
		GUIcontrol.detailPanelVisible(false);
		GUIcontrol.historyPanelVisible(false);
		//check all nodes, and for any node if mouse cords are in PIXEL_RANGE_NODE 
		Set<Node> allNodes = network.getAllNodes();
		for(Node node: allNodes) {
			if ((cordmouseX >= node.getX()-10) && (cordmouseX <= node.getX()+10) &&
				(cordmouseY >= node.getY()-10) && (cordmouseY <= node.getY()+10)) {
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

	public void makeInternalDirectUnder30() {
		int masters = DDoSSimulation.globalNumMasterSlaves = DDoSSimulation.globalNumSlaves/3;
		int toFill = masters % MAX_MASTERS_U30;
		int linePixel = masters / MAX_MASTERS_U30 * Y_STEP_U30;
		
		for (int i=0; i<masters; i++) {
			int X = i % MAX_MASTERS_U30 * X_STEP_U30;
			int Y = i / MAX_MASTERS_U30 * Y_STEP_U30;
			
			int leftStart = PIXEL_START_LEFT_U30;
			if (Y == linePixel && toFill != 0) leftStart = PIXEL_START_LEFT_U30 + 400 - (toFill-1) * 100;
			
			Node nodeMasterSlave = makeMasterSlaveAndAddToNetwork(leftStart, X, Y);
			
			makeSlaveAndAddToNetwork(nodeMasterSlave, leftStart, X-50, Y, 250);
			makeSlaveAndAddToNetwork(nodeMasterSlave, leftStart, X+50, Y, 250);
			
			if (i == (masters-1) && (DDoSSimulation.globalNumSlaves%3 > 0)) {
				for (int j=0; j<DDoSSimulation.globalNumSlaves%3; j++) {
					makeSlaveAndAddToNetwork(nodeMasterSlave, leftStart, X+50+100*(j+1), Y, 250);
				}
			}
		}
		mostRightDown = network.getNodeByID(2 + DDoSSimulation.globalNumSlaves);
	}

	public void makeInternalReflectedUnder30() {
		int masters = DDoSSimulation.globalNumMasterSlaves = DDoSSimulation.globalNumSlaves/3;
		int toFill = masters % MAX_MASTERS_U30;
		int linePixel = masters / MAX_MASTERS_U30 * Y_STEP_U30;
		
		for (int i=0; i<masters; i++) {
			
			int X = i % MAX_MASTERS_U30 * X_STEP_U30;
			int Y = i / MAX_MASTERS_U30 * Y_STEP_U30;
			
			int leftStart = PIXEL_START_LEFT_U30;
			if (Y==linePixel && toFill!=0) leftStart = PIXEL_START_LEFT_U30 + 400 - (toFill-1) * 100;
			
			Node nodeMasterSlave = makeMasterSlaveAndAddToNetwork(leftStart, X, Y);
			
			makeSlaveAndAddToNetwork(nodeMasterSlave, leftStart, X-70, Y, 100);
			makeSlaveAndAddToNetwork(nodeMasterSlave, leftStart, X, Y, 100);
			makeSlaveAndAddToNetwork(nodeMasterSlave, leftStart, X+70, Y, 100);
			
			makeReflectorAndAddToNetwork(leftStart, X-70, Y, 350);
			makeReflectorAndAddToNetwork(leftStart, X, Y, 350);
			makeReflectorAndAddToNetwork(leftStart, X+70, Y, 350);
			
			if (i==(masters-1) && (DDoSSimulation.globalNumSlaves%3 > 0)) {
				for (int j=0; j<DDoSSimulation.globalNumSlaves%3; j++) {
					if (j%2 == 0) makeSlaveAndAddToNetwork(nodeMasterSlave, leftStart, X+70+100*((j+2)/2), Y, 100);
					else makeReflectorAndAddToNetwork(leftStart, X+70+100*((j+2)/2), Y, 350);
				}
			}
			
		}	
		
		Vector<Node> reflectors = network.getReflectorNodes();
		Vector<Node> slaves = network.getSlaveNodes();
		
		for(Node reflector: reflectors) {
			for(int i =0; i<7; i++) {
				
				Random rand = new Random();
				int slaveIndex = rand.nextInt(slaves.size());
				Node nodeSlave = slaves.get(slaveIndex);
				makeNeighbours(nodeSlave, reflector);
				nodeSlave.addSlave(reflector); //not sure if this should stay
			}
			makeNeighbours(reflector, network.getTargetNode());
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