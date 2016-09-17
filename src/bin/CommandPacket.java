package bin;

public class CommandPacket extends Packet{
	
	public static final int INFECT = 1, GEN_ECHO_REQ = 2, GEN_SYN = 3;
	
	public static final String spoofedIp = "98.138.253.109";
	
	private String source = "";				
	private String destination = "";		
	private int type;
	
	public CommandPacket(String source, String destination, int type) {
		this.source = source;
		this.destination = destination;
		this.type = type;
	}
	
	public String toString() {
		String s = "";
		if (getType() == INFECT)
			s += "Infecting command";
		else if (getType() == GEN_ECHO_REQ)
			s += "Generate ECHO request packets command";
		else if (getType() == GEN_SYN)
			s += "Generate SYN packets command";
		
		s += "\n From: "+ source +"\n To: "+ destination +"\n";

		if (getType() == GEN_SYN)
			s += "Spoofed ip: 98.138.253.109 \n";
		return s;		
	}

	public int getType() {
		return type;
	}

}
