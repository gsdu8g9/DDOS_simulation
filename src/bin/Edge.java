package bin;

import java.util.*;

import javax.swing.JTextArea;

public class Edge {
	private Node nodeFrom, nodeTo;
	private String returnIPaddress = "";
	private Set<Package> packages;
	
	public Edge(Node nodeFrom, Node nodeTo) {
		this.nodeFrom = nodeFrom;
		this.nodeTo = nodeTo;
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
		else if (pack.getType() == Package.TCP_PACKAGE) 	// here is always SYN, ACK is outside
			terminal.append("\n>Sending SYN package from " + nodeFrom.getComputer().getIpAddress() + 
					" to " + nodeTo.getComputer().getIpAddress());
		else if (pack.getType() == Package.ICMP_PACKAGE) {	// for reflectors
			ICMPpacket icmp = (ICMPpacket)pack.getPacket();
			
			if (icmp.getType() == ICMPpacket.ECHO_REPLY)	
				terminal.append("\n>Sending ECHO REPLY from " + nodeFrom.getComputer().getIpAddress() + 
						" to " + nodeTo.getComputer().getIpAddress());
			else if (icmp.getType() == ICMPpacket.ECHO_REQUEST)	
				terminal.append("\n>Sending ECHO REQUEST from " + nodeFrom.getComputer().getIpAddress() + 
						" to " + nodeTo.getComputer().getIpAddress());
		}
		else if (pack.getType() == Package.COMMAND) {
			CommandPacket comm = (CommandPacket) pack.getPacket();
			
			if (comm.getType() == CommandPacket.GEN_ECHO_REQ) 
				terminal.append("\n>Sending command to generate ECHO REQUEST from " + nodeFrom.getComputer().getIpAddress() + 
						" to " + nodeTo.getComputer().getIpAddress());
			if (comm.getType() == CommandPacket.GEN_SYN)
				terminal.append("\n>Sending command to generate SYN from " + nodeFrom.getComputer().getIpAddress() + 
						" to " + nodeTo.getComputer().getIpAddress());
			if (comm.getType() == CommandPacket.INFECT)
				terminal.append("\n>Sending INFECTING command from " + nodeFrom.getComputer().getIpAddress() + 
						" to " + nodeTo.getComputer().getIpAddress());
		}
	} 
		
}
