package util;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Random;
import bin.*;
import graphic.DDoSSimulation;
import processing.core.PFont;

public class drawingAlgorithms {
	
	private static final int PIXEL_START_TOP = 50, APPLET_WIDTH = 1200, PIXEL_START_LEFT = 100,
							 MATRIX_RANGE_6 = 75, MATRIX_RANGE_5 = 100, MATRIX_RANGE_4 = 125, 
							 MATRIX_RANGE_3 = 150, MATRIX_RANGE_2 = 175, MATRIX_RANGE_1 = 200,
							 MAX_PER_LINE_UNDER_50 = 10;
	
	private Network network;
	private LinkedList<Node> nodesLine1 = null, nodesLine2 = null, nodesLine3 = null, nodesLine4 = null, nodesLine5 = null;
	private int numLines;
	
	public drawingAlgorithms(Network net) {
		network = net;
	}

	private void connectEgde(Node masterSlave, LinkedList nodesLine) {
		Node nodeSlave = (Node)nodesLine.removeFirst();
		masterSlave.addSlave(nodeSlave);
		nodeSlave.setColor(new Color(masterSlave.getColor().getRed(), masterSlave.getColor().getGreen(), masterSlave.getColor().getBlue()));
		Edge edge1 = new Edge(masterSlave, nodeSlave);
		network.addEdge(edge1);
		masterSlave.addNeighbor(nodeSlave);
		nodeSlave.addNeighbor(masterSlave);
	}
	
	// making and connecting Egdes for MASTERSLAVEs - NODES from list 
	private void processLinkedListNodes(boolean last, Node masterSlave, LinkedList nodesLine, int toFill) {
			
		int nodesPerMaster = DDoSSimulation.globalNumSlaves / DDoSSimulation.globalNumMasterSlaves;
		
		if (toFill > 0) nodesPerMaster++;
			
		// if last - get all from all lists
		if (last || nodesLine.size() < nodesPerMaster) {
			if (nodesLine1 != null) {
				while (nodesLine1.size() != 0) connectEgde(masterSlave, nodesLine1);
				nodesLine1 = null;
			}
			if (nodesLine2 != null) {
				while (nodesLine2.size() != 0) connectEgde(masterSlave, nodesLine2);
				nodesLine2 = null;
			}
			if (nodesLine3 != null) {
				while (nodesLine3.size() != 0) connectEgde(masterSlave, nodesLine3);
				nodesLine3 = null;
			}
			if (nodesLine4 != null) {
				while (nodesLine4.size() != 0) connectEgde(masterSlave, nodesLine4);
				nodesLine4 = null;
			}
			if (nodesLine5 != null) {
				while (nodesLine5.size() != 0) connectEgde(masterSlave, nodesLine5);
				nodesLine5 = null;
			}
		} else { for (int i = 0; i<nodesPerMaster; i++) connectEgde(masterSlave, nodesLine); }
	}
	
	// determines number of lists to use -> calls function above: processLinkedListNodes()
	private void connectMasterAndNodesFromLists(int i, Node masterSlave, int toFill) {
			
		int noOrderList = (i+1) % numLines;
			
		// if last - get all from all lists
		boolean last = false;
		if ( i == DDoSSimulation.globalNumMasterSlaves - 1) last = true;
			
		switch(numLines) {
		case 1: processLinkedListNodes(last, masterSlave, nodesLine1, toFill--); break;
		case 2: switch(noOrderList) {
				case 1: processLinkedListNodes(last, masterSlave, nodesLine1, toFill); break;
				case 0: processLinkedListNodes(last, masterSlave, nodesLine2, toFill); break;
				}
				break;
		case 3: switch(noOrderList) {
				case 1:	processLinkedListNodes(last, masterSlave, nodesLine1, toFill); break;
				case 2: processLinkedListNodes(last, masterSlave, nodesLine2, toFill); break;
				case 0: processLinkedListNodes(last, masterSlave, nodesLine3, toFill); break;
				}
				break;
		case 4: switch(noOrderList) {
				case 1:	processLinkedListNodes(last, masterSlave, nodesLine1, toFill); break;
				case 2: processLinkedListNodes(last, masterSlave, nodesLine2, toFill); break;
				case 3: processLinkedListNodes(last, masterSlave, nodesLine3, toFill); break;
				case 0: processLinkedListNodes(last, masterSlave, nodesLine4, toFill); break;
				}
				break;
		case 5: switch(noOrderList) {
				case 1:	processLinkedListNodes(last, masterSlave, nodesLine1, toFill); break;
				case 2: processLinkedListNodes(last, masterSlave, nodesLine2, toFill); break;
				case 3: processLinkedListNodes(last, masterSlave, nodesLine3, toFill); break;
				case 4: processLinkedListNodes(last, masterSlave, nodesLine4, toFill); break;
				case 0: processLinkedListNodes(last, masterSlave, nodesLine5, toFill); break;
				}
		}	
	}
	
	// returns HEIGHT padding between lines of slaves
	// arg: skipFirstRow - to make space for MASTER-SLAVES
	private int getPaddingBetweenTopDown(boolean skipFirstRow) {
		int n = DDoSSimulation.globalNumSlaves / MAX_PER_LINE_UNDER_50;
			
		if (DDoSSimulation.globalNumSlaves % MAX_PER_LINE_UNDER_50 != 0) n += 1;
			
		if (skipFirstRow) n += 1;
			
		if (n == 6) return MATRIX_RANGE_6;
		if (n == 5) return MATRIX_RANGE_5;
		if (n == 4) return MATRIX_RANGE_4;
		if (n == 3) return MATRIX_RANGE_3;
		if (n == 2) return MATRIX_RANGE_2;
		if (n == 1) return MATRIX_RANGE_1;
			
		// default - middle range
		return MATRIX_RANGE_3;	
	}
	
	// create network without MASTER-SLAVEs
	// arg1: skipFirstRow - to make space for MASTER-SLAVES
	// arg2: drawEdgesToSlaves - to draw edges from masterSlaves to slaves (or this will be done from other func)
	private void makeDirectedOnlySlavesUnder50(boolean skipFirstRow, boolean drawEdgesToSlaves) {
			
		Node masterNode = network.getMasterNode();
		Node targetNode = network.getTargetNode();
			
		int n = 1; numLines = 1;
			
		if (DDoSSimulation.globalNumSlaves <= 60 && DDoSSimulation.globalNumSlaves > 50) 	  { n = 6; numLines = 6; }
		else if (DDoSSimulation.globalNumSlaves <= 50 && DDoSSimulation.globalNumSlaves > 40) { n = 5; numLines = 5; }
		else if (DDoSSimulation.globalNumSlaves <= 40 && DDoSSimulation.globalNumSlaves > 30) { n = 4; numLines = 4; }
		else if (DDoSSimulation.globalNumSlaves <= 30 && DDoSSimulation.globalNumSlaves > 20) { n = 3; numLines = 3; }
		else if (DDoSSimulation.globalNumSlaves <= 20 && DDoSSimulation.globalNumSlaves > 10) { n = 2; numLines = 2; }
			
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
				
			int paddingBegging = PIXEL_START_LEFT;
			//if ((j+1)%2 == 0 ) paddingBegging = PIXEL_START_LEFT_EVEN;
				
			if (skipFirstRow == false)
				nodeSlave = new Node(network, paddingBegging + padding*i, PIXEL_START_TOP + getPaddingBetweenTopDown(false)*(j+1));
			else
				nodeSlave = new Node(network, paddingBegging + padding*i, PIXEL_START_TOP + getPaddingBetweenTopDown(true)*(j+2)+30);
					
			if (lastFilling) padding = (APPLET_WIDTH - PIXEL_START_LEFT) / (DDoSSimulation.globalNumSlaves/n);
				
			// check if LinkedList should be made
			if (DDoSSimulation.globalNumMasterSlaves > 0) {
					
				//allocating lists 
				if (nodesLine1 == null) {
					switch (n) {
					case 5: nodesLine5 = new LinkedList<Node>();
					case 4: nodesLine4 = new LinkedList<Node>();
					case 3: nodesLine3 = new LinkedList<Node>();
					case 2: nodesLine2 = new LinkedList<Node>();
					case 1: nodesLine1 = new LinkedList<Node>();
					}
				}
					
				//filling lists with nodes
				switch(j) {
				case 0: nodesLine1.add(nodeSlave); break;
				case 1: nodesLine2.add(nodeSlave); break;
				case 2: nodesLine3.add(nodeSlave); break;
				case 3: nodesLine4.add(nodeSlave); break;
				case 4: nodesLine5.add(nodeSlave); break;	
				}
			}
				
			Computer newSlave = new Computer("216.58.214."+nodeSlave.getID(),"slave"+nodeSlave.getID(), Computer.SLAVE, DDoSSimulation.globalMemoryConf, DDoSSimulation.globalInMemTimeConf);
			nodeSlave.setComputer(newSlave);
			
			if (drawEdgesToSlaves) {
				Edge edge1 = new Edge(masterNode, nodeSlave);
				network.addEdge(edge1);
				masterNode.addNeighbor(nodeSlave);
				nodeSlave.addNeighbor(masterNode);
			}
				
			Edge edge2 = new Edge(nodeSlave, targetNode);
			network.addEdge(edge2);				
			nodeSlave.addNeighbor(targetNode);
			targetNode.addNeighbor(nodeSlave);
								
			network.addNode(nodeSlave);
			}
		}
	}
	
	//TODO: pre poziva ove funkcije se mora proveriti da li je globalMasterSlaves > 10
	//		i ako jeste ispisati ERROR i uopste ne pozivati funckiju - zato se ovde ni ne proverava to
	private void makeInternalDirectUnder50() {
		if (DDoSSimulation.globalNumMasterSlaves == 0) makeDirectedOnlySlavesUnder50(false,true);
			
		Random rand = new Random();
			
		//first make slaves without egdes to them and with space for masters
		makeDirectedOnlySlavesUnder50(true, false);
			
		int paddingMasterSlaves = (APPLET_WIDTH - PIXEL_START_LEFT) / (DDoSSimulation.globalNumMasterSlaves);
			
		int toFill = DDoSSimulation.globalNumSlaves % DDoSSimulation.globalNumMasterSlaves;
			
		for (int i=0; i < DDoSSimulation.globalNumMasterSlaves; i++) {
			Node masterSlave = new Node(network, PIXEL_START_LEFT + paddingMasterSlaves * i, PIXEL_START_TOP + getPaddingBetweenTopDown(false) - 30);
			masterSlave.setColor(new Color(rand.nextInt(254),rand.nextInt(254), rand.nextInt(254)));
			Computer newMasterSlave = new Computer("216.58.214." + masterSlave.getID(), "MASTER_ZOMBIE" + masterSlave.getID(), Computer.MASTER_ZOMBIE, DDoSSimulation.globalMemoryConf, DDoSSimulation.globalInMemTimeConf);
			masterSlave.setComputer(newMasterSlave);
				
				//drawing egdes from MASTERSLAVE -> SLAVEs
				connectMasterAndNodesFromLists(i, masterSlave, toFill--);
				
				//draw edges from ATTACKER -> MASTERSLAVEs
				Edge edge1 = new Edge(network.getMasterNode(), masterSlave);
				network.addEdge(edge1);
				masterSlave.addNeighbor(network.getMasterNode());
				network.getMasterNode().addNeighbor(masterSlave);
				network.getMasterNode().addSlave(masterSlave);
				
				network.addNode(masterSlave);
			}
		}
	
}
