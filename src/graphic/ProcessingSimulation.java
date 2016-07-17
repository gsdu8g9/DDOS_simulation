package graphic;

import bin.*;
import processing.core.*;
import java.util.*;

public class ProcessingSimulation extends PApplet{
	
	private int numOfSlaves;
	private boolean wait = true;
	private Network network;
	
	private int appletWidth;
	private int appletHeight;
	
	private int speedPackage = 1;
	
	float x;
	float y;
	float xspeed = 1;
	float yspeed = (float)10;
	
	public void settings() {
		size(800,600);
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
					
				PImage img;
				img = loadImage("photo.png");
				image(img, n.getX(), n.getY(), 25, 25);
					
				textFont(font);
				fill(0);
				text(n.getID(), n.getX()+7, n.getY()-2);
			}
		}
		
	}

	public void draw() {
	
		//draw package : master - slave1
		Edge e = network.getEdge(network.getMasterNode(), network.getSlaveById(3));
				
		//x = e.getNodeFrom().getX();
		//y = e.getNodeFrom().getY();
		
		//noStroke();
		//fill(255,10);
		//rect(0,0,width,height);
			
		//x = x + xspeed;
		//y = y + yspeed;
			
		//stroke(0);
		//fill(175);
			
		//ellipse(x, y, 16, 16);
		
		
	  }
	
	public void setNumOfSlaves(int num) { numOfSlaves = num; } 
	
	public void makeNetwork() { 
		appletWidth = 800;
		appletHeight = 600;
		
		network = new Network();
		
		Computer masterComputer = new Computer("79.101.110.24", "Marko Markovic", Computer.MASTER, 2048);
		Node masterNode = new Node(masterComputer, appletWidth/2, 50);
		network.addNode(masterNode);
		
		Computer targetComputer = new Computer("69.171.230.68", "Nikola Nikolic", Computer.TARGET, 2048);
		Node targetNode = new Node(targetComputer, appletWidth/2, appletHeight-50);
		network.addNode(targetNode);
		
		for (int i=0; i<numOfSlaves; i++) {
			Node nodeSlave = new Node( (appletWidth/15*(i+1))+(15*15), appletHeight/2);
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
		
		//wait = false; // can draw now
		
	}
	
}
