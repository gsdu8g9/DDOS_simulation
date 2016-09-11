package bin;

import java.util.*;

import javax.swing.JTextArea;

public class Edge {
	private Node nodeFrom, nodeTo;
	private String returnIPaddress = "";
	private Network network;
	private Set<Package> packages;
	
	public Edge(Network network, Node nodeFrom, Node nodeTo) {
		this.nodeFrom = nodeFrom;
		this.nodeTo = nodeTo;
		this.network = network;
		packages = new HashSet<Package>();
	}
	
	public Node getNodeFrom() { return nodeFrom; }
	public Node getNodeTo() { return nodeTo; }
	
	public void deletePackage(Package pack) {
		if (packages.contains(pack))
			packages.remove(pack);
	}
	
	public void startSendingPackage(Package pack) {
		pack.setX(nodeFrom.getX());
		pack.setY(nodeFrom.getY());
		packages.add(pack);
	}
	
	public Set<Package> getPackages() { return packages; }
	
	public String getReturnIPAddress() { return returnIPaddress; }
	public void setReturnIPAddress(String ipAddress) { returnIPaddress = ipAddress; }
		
	public void writeSendingStart(Package pack, JTextArea terminal) {
		if (pack.getType() == Package.EMAIL_VIRUS)
			terminal.append("\n>Sending virus email from " + nodeFrom.getComputer().getIpAddress() + 
							" to " + nodeTo.getComputer().getIpAddress());
		else if (pack.getType() == Package.SYN_PACKAGE)
			terminal.append("\n>Sending CYN package from " + nodeFrom.getComputer().getIpAddress() + 
					" to " + nodeTo.getComputer().getIpAddress());
	} 
}
