package bin;

public class Edge {
	private Node nodeFrom;
	private Node nodeTo;
	private Network network;
	
	public Edge(Network network, Node nodeFrom, Node nodeTo) {
		this.nodeFrom = nodeFrom;
		this.nodeTo = nodeTo;
		this.network = network;
	}
	
	public Node getNodeFrom() { return nodeFrom; }
	public Node getNodeTo() { return nodeTo; }
}
