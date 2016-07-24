package bin;

import java.util.*;

import javax.swing.JTextArea;

public class Edge {
	private Node nodeFrom;
	private Node nodeTo;
	private Network network;
	private Set<Package> packages;
	private Package virusPackage = null;
	
	public Edge(Network network, Node nodeFrom, Node nodeTo) {
		this.nodeFrom = nodeFrom;
		this.nodeTo = nodeTo;
		this.network = network;
		packages = new HashSet<Package>();
	}
	
	public Node getNodeFrom() { return nodeFrom; }
	public Node getNodeTo() { return nodeTo; }
	
	public void startSendingPackage(Package pack) {
		pack.setX(nodeFrom.getX());
		pack.setY(nodeFrom.getY());
		packages.add(pack);
		if (pack.getType() == Package.EMAIL_VIRUS) virusPackage = pack;
	}
	
	public Set<Package> getPackages() { return packages; }
	public Package getVirusPackage() { return virusPackage; }
	
	public void deleteVirusPackage() { virusPackage = null; }
	
	public void writeSendingStart(Package pack, JTextArea terminal) {
		terminal.append("\n>Sending virus email from " + nodeFrom.getComputer().getIpAddress() + 
						" to " + nodeTo.getComputer().getIpAddress());
	} 
}
