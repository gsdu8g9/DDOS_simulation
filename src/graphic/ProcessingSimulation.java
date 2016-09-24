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
	public static final int STAGE_INFECTING_VIRUS = 1, STAGE_INIT_NETWORK = 2, STAGE_ALL_INFECTED = 3, STAGE_ATTACKING = 4,
			STAGE_GEN = 5, STAGE_FINISHED = 6, STAGE_PAUSE = 7;
	public static final int X_STEP_U30 = 200, Y_STEP_U30 = 70;
	public static final int TIMER_ACK_PROCESSING = 1000;

	public static int speedUp = 3;
	public static int stageBeforePause = 2;
	public static int infected = 0;
	public static int lastInfected = 0;
	public static int numPacksToRelease = 1;

	private long lastMSWave = 0, lastSRWave = 0, lastToTargetWave, lastVirusWave;
	private Network network;
	private DDoSSimulation GUIcontrol;
	private int stage = ProcessingSimulation.STAGE_INIT_NETWORK;
	private int prevRandomX = 0, prevRandomY = 0;
	private PImage networkBackground;
	private boolean newImageToLoad = false, firstImageLoad = false, speedDoubled = false, infectingSlavesDone = false;
	private Node mostRightDown;
	private List<Package> virusPackageQueue = new LinkedList<Package>();
	private List<Package> MSPackageQueue = new LinkedList<Package>();
	private List<Package> SRPackageQueue = new LinkedList<Package>();
	private List<Package> toTargetPackageQueue = new LinkedList<Package>();
	private Set<Package> travellingPackages = new HashSet<Package>();
	private List<OutsidePackage> travellingPings = new LinkedList<OutsidePackage>();	//travelling list for pings - they have the highest prior
	private List<OutsidePackage> pendingPings = new LinkedList<OutsidePackage>();
	private Set<OutsidePackage> ackPackages = new HashSet<OutsidePackage>();

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
	
	public DDoSSimulation getGUIControl() { return GUIcontrol; }

	public void infectSlaves() { 
		stage = ProcessingSimulation.STAGE_INFECTING_VIRUS;
		network.infectMasterZombies();
		GUIcontrol.updateLastInputTerminal();
	}

	public void drawNetworkBegining() {	
		background(255);

		PImage imgMaster = loadImage("Laptop3.png");
		PImage imgTaget = loadImage("Laptop1.png");
		PImage imgClear = loadImage("server.png");
		PImage imgMasterSlave = loadImage("computer-success.png");
		PImage imgInfected = loadImage("computer-alert.png");
		PImage imgRouter = loadImage("router.png");

		PFont font = loadFont("coder-15.vlw");

		stroke(Color.LIGHT_GRAY.getRed(), Color.LIGHT_GRAY.getGreen(), Color.LIGHT_GRAY.getBlue());
		strokeWeight(3);

		int dots = 100;
		if (DDoSSimulation.globalPackageTypeTCP == false) dots = 50;

		// connection from user
		for (int i = 0; i <= dots; i++) {
			float x = lerp((float)network.getTargetNode().getX(), (float)network.getUserNode().getX(), (float)i/dots);
			float y = lerp((float)network.getTargetNode().getY() - 10, (float)network.getUserNode().getY() - 10, (float)i/dots);
			point(x, y);
		}

		// connection to user
		for (int i = 0; i <= dots; i++) {
			float x = lerp((float)network.getTargetNode().getX(), (float)network.getUserNode().getX(), (float)i/dots);
			float y = lerp((float)network.getTargetNode().getY() + 15, (float)network.getUserNode().getY() + 15, (float)i/dots);
			point(x, y);
		}

		if (DDoSSimulation.globalPackageTypeTCP == false) {
			for (int i = 0; i <= dots; i++) {
				float x = lerp((float)network.getTargetNode().getX(), (float)network.getRouterNode().getX(), (float)i/dots);
				float y = lerp((float)network.getTargetNode().getY() + 15, (float)network.getRouterNode().getY() + 15, (float)i/dots);
				point(x, y);
			}
		}

		stroke(0);
		fill(175);
		ellipse(network.getMasterNode().getX(), network.getMasterNode().getY(), 16, 16);

		textFont(font);
		fill(0);
		text("ATTACKER", network.getMasterNode().getX()-35, network.getMasterNode().getY()-40);

		//------------should appear only when DDoSSimulation.globalNumSlaves > 45
		if (!DDoSSimulation.globalGraphTypeU45) {
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

		if (DDoSSimulation.globalPackageTypeTCP == false)
			ellipse(network.getRouterNode().getX(), network.getRouterNode().getY(), 16, 16);

		ellipse(network.getTargetNode().getX(), network.getTargetNode().getY(), 16, 16);

		//draw edges
		Set<Edge> allEdges = network.getAllEdges();
		for(Edge e: allEdges) {

			if (e.getNodeTo().getComputer().getType() == Computer.TARGET || e.getNodeTo().getComputer().getType() == Computer.MASTER_SLAVE) {
				stroke(0);
				strokeWeight(1);
			}
			else {
				stroke(e.getNodeFrom().getColor().getRed(), e.getNodeFrom().getColor().getGreen(), e.getNodeFrom().getColor().getBlue());
				if (DDoSSimulation.globalGraphTypeU45 && DDoSSimulation.globalDDOSTypeDirect) strokeWeight(3);
				else strokeWeight(1);
			}
			line(e.getNodeFrom().getX(), e.getNodeFrom().getY(), e.getNodeTo().getX(), e.getNodeTo().getY());
		}

		image(imgMaster, network.getMasterNode().getX()-40, network.getMasterNode().getY()-38, 80, 60);
		image(imgTaget, network.getTargetNode().getX()-40, network.getTargetNode().getY()-40, 80, 80);

		if (DDoSSimulation.globalPackageTypeTCP == false) 
			image(imgRouter, network.getRouterNode().getX()-40, network.getRouterNode().getY()-45, 80, 80);

		textFont(font);
		fill(0);
		text("TARGET", network.getTargetNode().getX()-30, network.getTargetNode().getY()+45);

		if (DDoSSimulation.globalPackageTypeTCP == false)
			text("ROUTER", network.getRouterNode().getX()-30, network.getRouterNode().getY()+45);

		//draw slaves
		Set<Node> allNodes = network.getAllNodes();
		for(Node n: allNodes) {
			int ntype = n.getComputer().getType();
			if ((ntype == Computer.SLAVE)||(ntype == Computer.MASTER_SLAVE)||(ntype == Computer.REFLECTING)) {

				if (n.getInfected() == true) {
					stroke(0);
					fill(ntype == Computer.MASTER_SLAVE? 100: 255,0 ,0);
					ellipse(n.getX(), n.getY(), ntype==Computer.MASTER_SLAVE? 13 : 10, ntype==Computer.MASTER_SLAVE? 13 : 10);
					if (DDoSSimulation.globalNumSlaves<=45) image(imgInfected, n.getX()-15, n.getY()-25, PIXEL_RANGE_NODE, PIXEL_RANGE_NODE);
				}
				else {
					stroke(0);
					strokeWeight(1);
					//fill(0, ntype == Computer.MASTER_SLAVE? 100: 255 ,0);
					fill(n.getColor().getRed(), n.getColor().getGreen(), n.getColor().getBlue());
					ellipse(n.getX(), n.getY(), ntype==Computer.MASTER_SLAVE? 13 : 10, ntype==Computer.MASTER_SLAVE? 13 : 10);

					if (DDoSSimulation.globalNumSlaves<=45) 
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

		//drawing user icon
		PImage imgUser = loadImage("user.png");
		image(imgUser, APPLET_WIDTH - 100, network.getTargetNode().getY()-40, 70, 70); 

		saveFrame("data/initalNetwork.png");
		newImageToLoad = true;
	}

	private void update() {

		travellingPings.addAll(pendingPings);
		pendingPings.clear();

		if (stage != ProcessingSimulation.STAGE_INIT_NETWORK) {
			lastMSWave = refreshPackageQueue(MSPackageQueue, DDoSSimulation.globalGenPackagePerSec, lastMSWave, 500);
			if (!DDoSSimulation.globalDDOSTypeDirect) {
				Collections.shuffle(SRPackageQueue);
				lastSRWave = refreshPackageQueue(SRPackageQueue, DDoSSimulation.globalGenPackagePerSec + 1, lastSRWave, 500);
			}
			if (travellingPackages.size() < 15)
				lastToTargetWave = refreshPackageQueue(toTargetPackageQueue, 1, lastToTargetWave, 500);
			else
				lastToTargetWave = refreshPackageQueue(toTargetPackageQueue, DDoSSimulation.globalGenPackagePerSec + 3, lastToTargetWave, 500);

			//function for refreshing based on TTL
			network.refreshComputerMemories();

			// function for refreshing based on number of processed packages
			// this needs to be done for all SLAVES, REFLECTORS and TARGET
			network.refreshComputerMemory(network.getTargetNode(), 2);
		}

		if (stage == ProcessingSimulation.STAGE_INFECTING_VIRUS) {

			lastVirusWave = refreshPackageQueue(virusPackageQueue, 3, lastVirusWave, 1000);

			if (lastInfected != infected) {
				lastInfected = infected;
				drawNetworkBegining();
				newImageToLoad = true;
				saveFrame("data/initalNetwork.png");
			}

			//wait for all slaves to be infected by virus
			if (infected == DDoSSimulation.globalNumMasterSlaves) {
				infected = 0;
				drawNetworkBegining();
				JTextArea terminal = getTerminal();
				terminal.append("\n>Infecting done... \n>");
				GUIcontrol.updateLastInputTerminal();

				stage = ProcessingSimulation.STAGE_ALL_INFECTED;
				GUIcontrol.setStartDDoSEnabled();

			}
		} else if (stage == ProcessingSimulation.STAGE_GEN) {
			network.sendFromAllMasters();
			stage = ProcessingSimulation.STAGE_ATTACKING;
		}
	}

	public void pingFromUser() {
		// insert some limit time between two ping clicks
		if (stage != ProcessingSimulation.STAGE_PAUSE) {
			long currSec = System.currentTimeMillis()/1000;
			Packet packet = new ICMPpacket(ICMPpacket.ECHO_REQUEST, DDoSSimulation.globalPackageSizeConf);
			OutsidePackage newPack = new OutsidePackage(network.getUserNode(), network.getTargetNode().getX(), network.getTargetNode().getY(), OutsidePackage.USER_PING, packet);
			newPack.setTimeCreated(currSec);
			
			getGUIControl().getTerminal().append("\n>Sending ECHO REQUEST from USER to TARGET");
			network.getProcSim().getGUIControl().updateLastInputTerminal();
			
			pendingPings.add(newPack);
		}
	}

	public void draw() {
		if (!isPaused() && stage!= ProcessingSimulation.STAGE_FINISHED) {
			update();

			if (newImageToLoad == true) {
				networkBackground = loadImage("initalNetwork.png");
				newImageToLoad = false;
			}

			if (stage != ProcessingSimulation.STAGE_INIT_NETWORK || (stage == ProcessingSimulation.STAGE_INIT_NETWORK && travellingPings.size() > 0)) {
				if (firstImageLoad == false) {
					networkBackground = loadImage("initalNetwork.png");
					firstImageLoad = true;
				}
				image(networkBackground, 0, 0, APPLET_WIDTH, APPLET_HEIGHT);
			}

			if (mousePressed == true) checkClickedComputer(mouseX, mouseY);

			if (stage == ProcessingSimulation.STAGE_ATTACKING || stage == ProcessingSimulation.STAGE_INFECTING_VIRUS) {
				drawAllTravellingPackages();
				drawAllAckPackages();
			}
			drawMemoryInfoBar();

			drawAllTravellingPings();

			if (network.getTargetNode().getComputer().isMemoryFull())
				stage = ProcessingSimulation.STAGE_FINISHED;
			else
				if (stage == ProcessingSimulation.STAGE_FINISHED)
					stage = ProcessingSimulation.STAGE_ATTACKING;
		}
		else if (stage == ProcessingSimulation.STAGE_FINISHED) {
			// TODO: maybe to change opacity over the picture
			// TODO: and to write that simulation is over
			image(networkBackground, 0, 0, APPLET_WIDTH, APPLET_HEIGHT);
			if (mousePressed == true) checkClickedComputer(mouseX, mouseY);
			drawMemoryInfoBar();

			// ping from user can be sent - but target won't answer
			travellingPings.addAll(pendingPings);
			pendingPings.clear();
			drawAllTravellingPings();

		}
		else if (stage == ProcessingSimulation.STAGE_PAUSE) {
			// details clicking
			if (mousePressed == true) {
				checkClickedComputer(mouseX, mouseY);
				checkClickedPackages(mouseX, mouseY);
				mousePressed = false;
			}

		}
	}

	private void drawMemoryInfoBar() {
		float targetMemory = network.getTagetLeftPercent() * 100;
		Color color = null;
		PFont font = loadFont("coder-15.vlw");
		textFont(font);

		if ((targetMemory) < 30) color = Color.GREEN;
		else if ((targetMemory) > 30 && (targetMemory) < 60) color = Color.ORANGE;
		else color = Color.RED;

		int x, y;
		if (DDoSSimulation.globalPackageTypeTCP) {
			x = network.getTargetNode().getX();
			y = network.getTargetNode().getY();
		}
		else {
			x = network.getRouterNode().getX();
			y = network.getRouterNode().getY();
		}


		fill(0);
		text("TARGET MEMORY:", 25, y + 17);

		stroke(0);
		fill(255);
		strokeWeight(1);
		rect(150, y-5, x-150-50, 35);

		noStroke();
		fill(color.getRGB(), 127);
		rect(x - 50, y, -targetMemory*4, 25);

		fill(0);
		text(targetMemory+"%", x - 120, y + 15);
	}

	private long refreshPackageQueue(List<Package> packageQueue, int numPacks, long lastWave, long sec) {
		long currSec = System.currentTimeMillis();
		if (lastWave == 0 || (currSec-lastWave) >= sec ) {
			
			Package head = null;
			if (packageQueue.size() > 0)
				head = packageQueue.get(0);

			int numReleasedPacks = 0;
			while (head != null && (numReleasedPacks < numPacks)) {
				boolean isMSqueue = head.getEdge().getNodeFrom().getComputer().getType() == Computer.MASTER_SLAVE;
				head.setStatus(Package.TRAVELING);
				travellingPackages.add(head);
				Edge edge = head.getEdge();
				edge.startSendingPackage(head);
				
				edge.writeSendingStart(head, getTerminal());
				GUIcontrol.updateLastInputTerminal();
				
				packageQueue.remove(0);
				if (packageQueue.size() > 0)
					head = packageQueue.get(0);
				else {
					head = null;
					if (isMSqueue)
						infectingSlavesDone = true;
				}
				numReleasedPacks++;
			}
			return currSec;
		}	
		else return lastWave;
	}

	private void drawAllAckPackages() {
		long currSec = System.currentTimeMillis();
		Set<OutsidePackage> newAckPackages = new HashSet<OutsidePackage>();

		for (OutsidePackage ack: ackPackages)
			if (currSec - ack.getTimeCreated() < TIMER_ACK_PROCESSING) {
				newAckPackages.add(ack);
				drawAckPackage_Helper(ack);
			}
		ackPackages = newAckPackages;
	}

	private void drawAckPackage_Helper(OutsidePackage ack) {
		stroke(3);
		strokeWeight(2);

		for (int i = 0; i <= 25; i++) {
			float x = lerp((float)network.getTargetNode().getX(), (float)ack.getXTo(), (float)i/25);
			float y = lerp((float)network.getTargetNode().getY(), (float)ack.getYTo(), (float)i/25);
			point(x, y);
		}

		PImage img = loadImage("ackUnkown.png");
		image(img, ack.getXTo()-10, ack.getYTo()-13, PIXEL_RANGE_NODE/2, PIXEL_RANGE_NODE/2);

		PFont font = loadFont("coder-15.vlw");
		textFont(font);

		fill(0);
		text("IP:?", ack.getXTo() + PIXEL_RANGE_NODE/2 - 5 , ack.getYTo()-5);
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
		if (travellingPackages.size() == 0 && stage == ProcessingSimulation.STAGE_ATTACKING && infectingSlavesDone)
			stage = ProcessingSimulation.STAGE_GEN;
	}

	private OutsidePackage drawPingPackage(OutsidePackage pack) {
		boolean receivedStatus = pack.isReceived();
		OutsidePackage retAck = null;
		if (receivedStatus) {	//if received - process it, send ping back
			if (pack.getStartNode().equals(network.getUserNode()))
				retAck = network.getTargetNode().processPing(pack);
		}
		else drawPing_Helper(pack);

		return retAck;
	}

	private void drawPing_Helper(OutsidePackage pack) {
		float x = pack.getCurrentX();

		if (pack.getType() == OutsidePackage.USER_PING) x = x - speedUp;
		else
			if (pack.getType() == OutsidePackage.TARGET_PING) x = x + speedUp;

		pack.setCurrentX(x);

		stroke(0);
		fill(175);

		PImage img;
		if (pack.getType() == OutsidePackage.USER_PING) {
			img = loadImage("userPing.png");
			image(img, x, pack.getCurrentY()-25, PIXEL_RANGE_NODE/2, PIXEL_RANGE_NODE/2);
		}
		else if (pack.getType() == OutsidePackage.TARGET_PING) {
			img = loadImage("targetPing.png");
			image(img, x, pack.getCurrentY()-2, PIXEL_RANGE_NODE/2, PIXEL_RANGE_NODE/2);
		}

	}

	private void drawAllTravellingPings() {
		List<OutsidePackage> newTravellingPackages = new LinkedList<OutsidePackage>();
		List<OutsidePackage> receivedAcks = new LinkedList<OutsidePackage>();
		OutsidePackage ackPack = null;

		for(OutsidePackage pack: travellingPings) {
			if (pack.getType() == OutsidePackage.USER_PING || pack.getType() == OutsidePackage.TARGET_PING) {
				ackPack = drawPingPackage(pack);
				if (ackPack == null)	// deleting received - if returned null, ack pack is not made - still travelling
					newTravellingPackages.add(pack);
				else
					receivedAcks.add(ackPack);	//this list is needed because if we add to travelling directly - concurency problems
			}
		}

		//removing received from list
		travellingPings = newTravellingPackages;
		travellingPings.addAll(receivedAcks);
	}

	private void drawPackage_Helper(Package pack) {
		float x = pack.getX();
		float y = pack.getY();
		
		float oldX = x, oldY = y;	// coords will be updated before they get to fixed settings, so we save original coords for fixed
		
		float fixedX = pack.getFixedX();
		float fixedY = pack.getFixedY();
		
		boolean fixedPackage = false;
		
		// to speed up more 
		if (travellingPackages.size() >= 20) {
			speedUp = 25;
			speedDoubled = true;
		}
		else if (travellingPackages.size() < 15 && speedDoubled) {
			speedUp = DDoSSimulation.globalSpeedUpBar;
			speedDoubled = false;
		}

		float speedX = 0;
		float speedY = 1;
		boolean minus = false;

		if (pack.getEdge().getNodeFrom().getX() > pack.getEdge().getNodeTo().getX()) {
			speedX = (float)(pack.getEdge().getNodeFrom().getX() - pack.getEdge().getNodeTo().getX()) / 
					(float)(pack.getEdge().getNodeTo().getY() - pack.getEdge().getNodeFrom().getY());
			x = x - speedX*speedUp;
			minus = true;
		}
		else if (pack.getEdge().getNodeFrom().getX() < pack.getEdge().getNodeTo().getX()) {
			speedX = (float)(pack.getEdge().getNodeTo().getX() - pack.getEdge().getNodeFrom().getX()) / 
					(float)(pack.getEdge().getNodeTo().getY() - pack.getEdge().getNodeFrom().getY());
			x = x + speedX*speedUp;
		}

		y = y + speedY*speedUp;

		pack.setX(x);
		pack.setY(y);
		
		//for real simulation the packages travel on edges
		if (!DDoSSimulation.globalGraphTypeU45) {
			blinkEdges(pack); 
			fixedPackage= true;
			
			if (fixedX == 0 || fixedY == 0) {
				Random rand = new Random();
				
				int low = 0;
				int high = pack.getEdge().getNodeTo().getY() - pack.getEdge().getNodeFrom().getY();
				 
				float speed = rand.nextInt(high - low) + low;
				
				if (minus) fixedX = oldX - speed * speedX; 
				else fixedX = oldX + speed * speedX; 
				fixedY = oldY + speed;
				pack.setFixedX(fixedX);
				pack.setFixedY(fixedY);
			}
		}
		
		stroke(0);
		fill(175);
		
		int imgX = PIXEL_RANGE_NODE/2;
		int imgY = PIXEL_RANGE_NODE/2;
		int moveX = 15;
		
		PImage img;
		if (pack.getType() == Package.EMAIL_VIRUS) 
			img = loadImage("email2.png");
		else if (pack.getType() == Package.COMMAND) {
			CommandPacket command = (CommandPacket)pack.getPacket();
			if (command.getType() == CommandPacket.GEN_SYN) {
				img = loadImage("comm_syn.png");
				imgX = 30;
				imgY = 30;
				moveX = 15;
			}
			else if (command.getType() == CommandPacket.GEN_ECHO_REQ) {
				img = loadImage("comm_echo.png");
				imgX = 30;
				imgY = 30;
				moveX = 15;
			}
			else
				img = loadImage("email2.png");	//same as for infecting
		}
		else if ((pack.getType() == Package.TCP_PACKAGE || pack.getType() == Package.ICMP_PACKAGE ) && pack.getEdge().getNodeTo().getComputer().getType() == Computer.TARGET)
			img = loadImage("toTarget.png");
		else 
			img = loadImage("spoofedPackage.png");

		if (fixedPackage)
			image(img, fixedX-moveX, fixedY, imgY, imgX);
		else
			image(img, x-moveX, y, imgY, imgX);
	}

	private void blinkEdges(Package pack) {
		Edge e = pack.getEdge();

		stroke(255,0,0);
		strokeWeight(4);
		line(e.getNodeFrom().getX(), e.getNodeFrom().getY(), e.getNodeTo().getX(), e.getNodeTo().getY());

		stroke(e.getNodeFrom().getColor().getRed(), e.getNodeFrom().getColor().getGreen(), e.getNodeFrom().getColor().getBlue());
		strokeWeight(1);
		line(e.getNodeFrom().getX(), e.getNodeFrom().getY(), e.getNodeTo().getX(), e.getNodeTo().getY());
	}

	private void drawAckUnknown(Package pack) {
		int borderXTop = mostRightDown.getX();
		float yFromArrow = network.getTargetNode().getY();
		float yToArrow = mostRightDown.getY() + ( network.getTargetNode().getY() - mostRightDown.getY() ) / 4 ;
		yToArrow += 20;

		Random random = new Random();
		int MAX = borderXTop;
		int MIN = borderXTop - 150;

		//Y: ako su suvise blizu generise koordinate, onda u while generise opet - da bi se lepo videle razlike
		float randomY = random.nextInt((int)yFromArrow - (int)yToArrow + 1) + (int)yToArrow;
		if (prevRandomY == 0) prevRandomY = (int)randomY;
		else {
			while (Math.abs(prevRandomY - randomY) < (yFromArrow - yToArrow)/2)
				randomY = random.nextInt((int)yFromArrow - (int)yToArrow + 1) + (int)yToArrow;
		}
		prevRandomY = (int)randomY;

		//X: ako su suvise blizu generise koordinate, onda u while generise opet - da bi se lepo videle razlike
		float randomX = random.nextInt(MAX - MIN) + MIN;
		if (prevRandomX == 0) prevRandomX = (int)randomX;
		else {
			while (Math.abs(prevRandomX - randomX) < (MAX - MIN) / 3)
				randomX = random.nextInt(MAX - MIN) + MIN;
		}
		prevRandomX = (int)randomX;

		Packet packet = new TCPpacket(network.getTargetNode().getComputer().getIpAddress(), "???.???.???.???", TCPpacket.ACK, DDoSSimulation.globalPackageSizeConf, pack.getPacket().getChecksum()+1);
		OutsidePackage unknown = new OutsidePackage(pack.getEdge().getNodeTo(), randomX, randomY, OutsidePackage.ACKNOWLEDGE, packet);

		unknown.writeSending(getTerminal());
		GUIcontrol.updateLastInputTerminal();
		
		long currSec = System.currentTimeMillis();
		unknown.setTimeCreated(currSec);

		ackPackages.add(unknown);
	}

	private void drawPackage(Package pack) {

		if (!(pack.packageReceived())) {
			drawPackage_Helper(pack);
		}
		else {
			Node retNode = pack.getEdge().getNodeTo().processPackage(pack);
			if (pack.getType() == Package.TCP_PACKAGE) {	
				if ((retNode == null) && (pack.getEdge().getNodeTo().getComputer().getType() == Computer.TARGET)) drawAckUnknown(pack);	// spoofed ip 
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

	public void addPackageToQueue(Package pack) { 

		if (pack.getType() == Package.EMAIL_VIRUS)
			virusPackageQueue.add(pack); 
		else {
			int typeFrom = pack.getEdge().getNodeFrom().getComputer().getType();

			if (typeFrom == Computer.MASTER_SLAVE)	MSPackageQueue.add(pack);
			else if (typeFrom == Computer.REFLECTING) toTargetPackageQueue.add(pack);
			else {
				if (DDoSSimulation.globalDDOSTypeDirect) toTargetPackageQueue.add(pack);
				else SRPackageQueue.add(pack);
			}
		}

	}

	public void makeNetworkDefault() { 
		network = new Network(this);

		Computer masterComputer = new Computer("79.101.110.24", "Marko Markovic", Computer.MASTER, DDoSSimulation.globalMemoryConf, DDoSSimulation.globalInMemTimeConf);
		Node masterNode = new Node(network, masterComputer, APPLET_WIDTH/2, 50);
		masterNode.setID(500);
		network.addNode(masterNode);

		Computer targetComputer = new Computer("69.171.230.68", "Nikola Nikolic", Computer.TARGET, DDoSSimulation.globalMemoryConf, DDoSSimulation.globalInMemTimeConf);
		Node targetNode = null;
		if (DDoSSimulation.globalPackageTypeTCP == true)
			targetNode = new Node(network, targetComputer, APPLET_WIDTH/2, APPLET_HEIGHT-50);
		else
			targetNode = new Node(network, targetComputer, APPLET_WIDTH/2 + APPLET_WIDTH/4, APPLET_HEIGHT-50);
		targetNode.setID(505);
		network.addNode(targetNode);

		if (DDoSSimulation.globalPackageTypeTCP == false)	//ICMP
		{
			Node routerNode = new Node(network, targetComputer, APPLET_WIDTH/2, APPLET_HEIGHT-50);
			routerNode.setID(506);
			network.addNode(routerNode);
		}

		if (DDoSSimulation.globalDDOSTypeDirect) {
			if (DDoSSimulation.globalGraphTypeU45) { 	
				makeInternalDirectUnder30();	
			} else { 									
				makeInternalDirectAbove50();
			}
		} else { 
			if (DDoSSimulation.globalGraphTypeU45) { 
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
			Node lastRefl = makeReflectorAndAddToNetwork(leftStart, X+35, Y, 350);

			if (mostRightDown != null) {
				if (mostRightDown.getX() <= lastRefl.getX())
					mostRightDown = lastRefl;
				else if ((mostRightDown.getX() <= lastRefl.getX() + 30) && (mostRightDown.getY() < lastRefl.getY()))
					mostRightDown = lastRefl;
			}
			else
				mostRightDown = lastRefl;

			if (i==(masters-1) && (DDoSSimulation.globalNumSlaves%7 > 0)) {
				for (int j=0; j<DDoSSimulation.globalNumSlaves%7; j++) {
					if (j%2 == 0) makeSlaveAndAddToNetwork(nodeMasterSlave, leftStart, X+35+35*((j+2)/2), Y, 100);
					else {
						Node lastRefl2 =makeReflectorAndAddToNetwork(leftStart, X+35+35*((j+2)/2), Y, 350);
						if (mostRightDown.getX() <= lastRefl2.getX())
							mostRightDown = lastRefl2;
						else if ((mostRightDown.getX() <= lastRefl2.getX() + 30) && (mostRightDown.getY() < lastRefl2.getY()))
							mostRightDown = lastRefl2;
					}
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
			makeNeighbours(reflector, network.getRouterNode());
		}

		network.createConnectionWithUser();
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

		Node mostRightDownPrev = null;
		if (DDoSSimulation.globalNumSlaves % 10 != 0)
			mostRightDownPrev = network.getNodeByID(DDoSSimulation.globalNumSlaves - DDoSSimulation.globalNumSlaves % 10);

		mostRightDown = network.getNodeByID(DDoSSimulation.globalNumSlaves);

		if (mostRightDownPrev != null) {
			if (mostRightDown.getX() < mostRightDownPrev.getX())
				mostRightDown = mostRightDownPrev;
		}

		network.createConnectionWithUser();
	}

	private void makeNeighbours(Node from, Node to) {
		Edge edge = new Edge(from, to);
		network.addEdge(edge);
		from.addNeighbor(to);
		to.addNeighbor(from);
	}

	private Node makeMasterSlaveAndAddToNetwork(int leftStart, int x, int y) {

		Node nodeMasterSlave = new Node(network, leftStart+x, 2*PIXEL_START_TOP+y);
		Random rand = new Random();
		nodeMasterSlave.setColor(new Color(rand.nextInt(254),rand.nextInt(254), rand.nextInt(254)));

		Computer newMasterSlave = new Computer("216.58.214."+nodeMasterSlave.getID(),"slave"+nodeMasterSlave.getID(), Computer.MASTER_SLAVE, DDoSSimulation.globalMemoryConf, DDoSSimulation.globalInMemTimeConf);
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
		Computer newSlave = new Computer("216.58.214."+nodeSlave.getID(),"slave"+nodeSlave.getID(), Computer.SLAVE, DDoSSimulation.globalMemoryConf, DDoSSimulation.globalInMemTimeConf);
		nodeSlave.setComputer(newSlave);
		nodeMasterSlave.addSlave(nodeSlave);
		nodeSlave.setColor(nodeMasterSlave.getColor());

		makeNeighbours(nodeMasterSlave, nodeSlave);
		network.addNode(nodeSlave);
		network.addSlaveNode(nodeSlave);

		if (DDoSSimulation.globalDDOSTypeDirect) 
			makeNeighbours(nodeSlave,network.getTargetNode());
	}

	private Node makeReflectorAndAddToNetwork(int leftStart, int X, int Y, int Yoffset) {
		Node nodeReflector1 = new Node(network, leftStart+X, 3*PIXEL_START_TOP+Y+Yoffset);
		Computer newReflector1 = new Computer("216.58.214."+nodeReflector1.getID(),"slave"+nodeReflector1.getID(), Computer.REFLECTING,DDoSSimulation.globalMemoryConf, DDoSSimulation.globalInMemTimeConf);
		nodeReflector1.setComputer(newReflector1);
		network.addNode(nodeReflector1);
		network.addReflectorNode(nodeReflector1);
		return nodeReflector1;
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
			}
		}
	}

	public void checkClickedPackages(int X, int Y) {

		for (Package p : travellingPackages) {
			float x,y;
			
			if (!DDoSSimulation.globalGraphTypeU45) {x = p.getFixedX(); y = p.getFixedY();}
			else {x = p.getX(); y = p.getY();}
			
			if ((X >= x - 10) && (X <= x + 10) &&
					(Y >= y - 10) && (Y <= y + 10)) {
				//clicked on pack -> show details
				if (p.getPacket() != null)
					GUIcontrol.makePacketWindow(p.getPacket().toString());
				else
					GUIcontrol.makePacketWindow("No packet!");
			}
		}
		
		//check for pings
		
		for (OutsidePackage p : travellingPings) {
			float x = p.getCurrentX(); 
			float y = p.getCurrentY();
			
			if ((X >= x - 10) && (X <= x + 10) &&
					(Y >= y - 10) && (Y <= y + 10)) {
				//clicked on pack -> show details
				if (p.getPacket() != null) {
					ICMPpacket icmpPack = (ICMPpacket)p.getPacket();
					if (icmpPack.getType() == ICMPpacket.ECHO_REQUEST)
						GUIcontrol.makePacketWindow(icmpPack.toString(network.getUserNode().getComputer().getIpAddress(), network.getTargetNode().getComputer().getIpAddress()));
					else
						GUIcontrol.makePacketWindow(icmpPack.toString(network.getTargetNode().getComputer().getIpAddress(), network.getUserNode().getComputer().getIpAddress()));
				}
				else
					GUIcontrol.makePacketWindow("No packet!");
			}
		}
		
		//check for ack
		for (OutsidePackage p : ackPackages) {
			float x = p.getXTo();
			float y = p.getYTo();
			
			if ((X >= x - 10) && (X <= x + 10) &&
					(Y >= y - 10) && (Y <= y + 10)) {
				//clicked on pack -> show details
				if (p.getPacket() != null)
					GUIcontrol.makePacketWindow(p.getPacket().toString());
				else
					GUIcontrol.makePacketWindow("No packet!");
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
		Node mostRightDownPrev = null;
		if (DDoSSimulation.globalNumSlaves % 10 != 0)
			mostRightDownPrev = network.getNodeByID(DDoSSimulation.globalNumSlaves - DDoSSimulation.globalNumSlaves % 10);

		mostRightDown = network.getNodeByID(DDoSSimulation.globalNumSlaves);

		if (mostRightDownPrev != null) {
			if (mostRightDown.getX() < mostRightDownPrev.getX())
				mostRightDown = mostRightDownPrev;
		}

		network.createConnectionWithUser();
	}

	public void makeInternalReflectedUnder30() {
		int masters = DDoSSimulation.globalNumMasterSlaves = DDoSSimulation.globalNumSlaves/7;
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
			Node lastRefl = makeReflectorAndAddToNetwork(leftStart, X+70, Y, 350);

			if (mostRightDown != null) {
				if (mostRightDown.getX() <= lastRefl.getX())
					mostRightDown = lastRefl;
				else if ((mostRightDown.getX() <= lastRefl.getX() + 30) && (mostRightDown.getY() < lastRefl.getY()))
					mostRightDown = lastRefl;
			}
			else
				mostRightDown = lastRefl;

			if (i==(masters-1) && (DDoSSimulation.globalNumSlaves%7 > 0)) {
				for (int j=0; j<DDoSSimulation.globalNumSlaves%7; j++) {
					if (j%2 == 0) makeSlaveAndAddToNetwork(nodeMasterSlave, leftStart, X+70+100*((j+2)/2), Y, 100);
					else  {
						Node lastRefl2 = makeReflectorAndAddToNetwork(leftStart, X+70+100*((j+2)/2), Y, 350);
						if (mostRightDown.getX() <= lastRefl2.getX())
							mostRightDown = lastRefl2;
						else if ((mostRightDown.getX() <= lastRefl2.getX() + 30) && (mostRightDown.getY() < lastRefl2.getY()))
							mostRightDown = lastRefl2;
					}
				}
			}

		}	

		Vector<Node> reflectors = network.getReflectorNodes();
		Vector<Node> slaves = network.getSlaveNodes();
		
		for(Node reflector: reflectors) {
			for(int i = 0; i < slaves.size()/2; i++) {

				Random rand = new Random();
				int slaveIndex = rand.nextInt(slaves.size());
				Node nodeSlave = slaves.get(slaveIndex);
				makeNeighbours(nodeSlave, reflector);
				nodeSlave.addSlave(reflector); //not sure if this should stay
			}
			makeNeighbours(reflector, network.getRouterNode());
		}
		
		network.createConnectionWithUser();
	}

	public void startDDos() { stage = ProcessingSimulation.STAGE_GEN; }

	public JTextArea getTerminal() { return GUIcontrol.getTerminal(); }

	public int getStage() { return stage; }

	public void continueSimulation() { stage = stageBeforePause; }

	public void pauseSimulation () {
		stageBeforePause = stage;
		stage = STAGE_PAUSE;
	}

	public boolean isPaused() { return stage == STAGE_PAUSE; }

	public void shuffleMS() { Collections.shuffle(MSPackageQueue); }
}