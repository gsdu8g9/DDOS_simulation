package bin;

import java.util.*;

import graphic.DDoSSimulation;

public class Computer {
	
	public static final int ATTACKER = 0, MASTER_ZOMBIE = 1, SLAVE = 2, REFLECTOR =3, TARGET = 4, USER = 5;
	
	private String ipAddress;
	private String domain;
	private int type, TTL;
	private int maxSize = 0, currSize = 0;
	private long lastWave = 0;
	private int numberOfPackagesReceived = 0;
	private int numberOfPackagesSent = 0;
	private List<Package> receivedPackages = new LinkedList<Package>();			// packages should be sorted by received time
	private Set<Package> sentPackages = new HashSet<Package>();
	
	public Computer(String ipAddress, String domain, int type, int memSize, int TTL) {
		this.ipAddress = ipAddress;
		this.domain = domain;
		this.type = type;
		this.TTL = TTL;
		maxSize = memSize;
	}
	
	public int getTTL() { return TTL; }
	public int getMemBuffSizeCurrent() { return currSize;}
	public int getMemBuffSize() {return maxSize; }
	public int getType() { return type; }
	public String getIpAddress() { return ipAddress; }
	public String getDomain() {return domain; }
	
	public String getTypeString() {
		if (type == ATTACKER) return "ATTACKER";
		if (type == MASTER_ZOMBIE) return "MASTER SLAVE";
		if (type == SLAVE) return "SLAVE";
		if (type == TARGET) return "TARGET";
		if (type == USER) return "USER";
		
		return "missing info";
	}
	
	public void increaseMemory(int size) { 
		if ((currSize + size) < maxSize) currSize += size;
		else if ( currSize + size >= maxSize) currSize = maxSize;
	} 
	
	public void decreaseMemory(int size) {
		if ((currSize - size) > 0) currSize -= size;
		else if (currSize > 0 && (currSize - size <= 0) ) currSize = 0;
	}
	
	public boolean isMemoryFull() {
		return currSize == maxSize;
	}
	
	public void refreshMemory() {
		Package head = null;
		if (receivedPackages.size() > 0)
			head = receivedPackages.get(0);
		
		while (head != null && head.getTimeInBuffer() >= DDoSSimulation.globalInMemTimeConf) {
				// delete this package from computer and also from Edge packages
				Edge e = head.getEdge();
				e.deletePackage(head);
				decreaseMemory(head.getSize());
				receivedPackages.remove(0);
				if (receivedPackages.size() > 0)
					head = receivedPackages.get(0);
				else
					head = null;
			}
	}
	
	public long refreshMemory(int numPacks, long sec) {
		long currSec = System.currentTimeMillis();
		if (lastWave == 0 || (currSec-lastWave) >= sec ) {
			Package head = null;
			if (receivedPackages.size() > 0)
				head = receivedPackages.get(0);
			
			int numReleasedPacks = 0;
			while (head != null && (numReleasedPacks < numPacks)) {
				Edge e = head.getEdge();
				e.deletePackage(head);
				decreaseMemory(head.getSize());
				receivedPackages.remove(0);
				if (receivedPackages.size() > 0)
					head = receivedPackages.get(0);
				else
					head = null;
				numReleasedPacks++;
			}
			return currSec;
		}	
		else return lastWave;
	}
	
	public void setLastWave(long time) { lastWave = time; }
	
	public Package getFirstReceivedPackage() {
		if (receivedPackages.size() > 0)
			return receivedPackages.get(0);
		else
			return null;
	}
		
	public List<Package> getReceivedPackages() { return receivedPackages; }
	
	public void addReceivedPackage(Package pack) { setNumberOfPackagesReceived(getNumberOfPackagesReceived() + 1); receivedPackages.add(pack); }
	
	public Set<Package> getSentPackages() { return sentPackages; }
	
	public void addSentPackage(Package pack) { setNumberOfPackagesSent(getNumberOfPackagesSent() + 1); sentPackages.add(pack); }

	public int getNumberOfPackagesReceived() {
		return numberOfPackagesReceived;
	}

	private void setNumberOfPackagesReceived(int numberOfPackagesReceived) {
		this.numberOfPackagesReceived = numberOfPackagesReceived;
	}

	public int getNumberOfPackagesSent() {
		return numberOfPackagesSent;
	}

	private void setNumberOfPackagesSent(int numberOfPackagesSent) {
		this.numberOfPackagesSent = numberOfPackagesSent;
	}
	

}