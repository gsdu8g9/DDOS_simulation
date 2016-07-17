package graphic;

import bin.*;
import processing.core.*;

public class ProcessingSimulation extends PApplet{
	
	private int numOfSlaves;
	private boolean wait = true;
	
	public void settings() {
		size(800,600);
	}
	
	float x = 100;
	float y = 100;
	float xspeed = 1;
	float yspeed = (float)3.3;

	public void setup() {
	  smooth();
	  background(255);
	}

	public void draw() {
	  
	  stroke(0);
	  fill(175);
	  ellipse(x,y,16,16);
	}
	
	public void setNumOfSlaves(int num) { numOfSlaves = num; } 
	
	public void makeNetwork() { 
		wait = false; 
		Network network = new Network();
		
		Computer masterComputer = new Computer("79.101.110.24", "Marko Markovic", Computer.MASTER, 2048);
		Node masterNode = new Node(masterComputer, width/2, 50);
		network.addNode(masterNode);
		
		Computer targetComputer = new Computer("69.171.230.68", "Nikola Nikolic", Computer.TARGET, 2048);
		Node targetNode = new Node(targetComputer, width/2, height-50);
		network.addNode(targetNode);
		
		for (int i=0; i<numOfSlaves; i++) {
			Node nodeSlave = new Node( width/15*i, height/2);
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
