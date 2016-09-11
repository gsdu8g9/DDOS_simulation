package bin;

public class CommandPacket extends Packet{
	
	public static final int INFECT = 1, GEN_ECHO_REQ = 2, GEN_SYN = 3;
	
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
		if (type == INFECT)
			s += "Infecting command";
		else if (type == GEN_ECHO_REQ)
			s += "Generate ECHO request packets command";
		else if (type == GEN_SYN)
			s += "Generate SYN packets command";
		
		s += "\n From: "+ source +"\n To: "+ destination +"\n";
		return s;		
	}
	
}
