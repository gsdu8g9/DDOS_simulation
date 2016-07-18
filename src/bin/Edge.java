package bin;

public class Edge {
	private Node nodeFrom;
	private Node nodeTo;
	private Network network;
	private float cordPackageX, cordPackageY;
	private boolean sendingPackage = false;
	
	public Edge(Network network, Node nodeFrom, Node nodeTo) {
		this.nodeFrom = nodeFrom;
		this.nodeTo = nodeTo;
		this.network = network;
		
		cordPackageX = 0;
		cordPackageY = 0;
	}
	
	public Node getNodeFrom() { return nodeFrom; }
	public Node getNodeTo() { return nodeTo; }
	
	public void updatePackageCordX(float x) { cordPackageX = x;}
	public void updatePackageCordY(float y) { cordPackageY = y;}
	
	public float getPackageCordX() { return cordPackageX; }
	public float getPackageCordY() { return cordPackageY; }
	
	public void startSendingPackage() {
		sendingPackage = true;
		cordPackageX = nodeFrom.getX();
		cordPackageY = nodeFrom.getY();
	}
	
	public boolean packageNotReachedEnd() {
		if (cordPackageX == nodeTo.getX() || cordPackageY == nodeTo.getY())
			return false;
		else return true;
	}
}
