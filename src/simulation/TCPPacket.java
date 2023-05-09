package simulation;

import java.io.Serializable;

public class TCPPacket implements Serializable {
	private static final long serialVersionUID = -1430929510129882579L;
	int sourcePort;
	int destinationPort;
	int sequenceNumber;
	int acknowledgeNumber;
	char data;
	
	public TCPPacket(int sourcePort, int destinationPort, int sequenceNumber, int nacknowledgeNumber, char data) {
		super();
		this.sourcePort = sourcePort;
		this.destinationPort = destinationPort;
		this.sequenceNumber = sequenceNumber;
		this.acknowledgeNumber = nacknowledgeNumber;
		this.data = data;
	}
	public int getSourcePort() {
		return sourcePort;
	}
	public void setSourcePort(int sourcePort) {
		this.sourcePort = sourcePort;
	}
	public int getDestinationPort() {
		return destinationPort;
	}
	public void setDestinationPort(int destinationPort) {
		this.destinationPort = destinationPort;
	}
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	public int getAcknowledgeNumber() {
		return acknowledgeNumber;
	}
	public void setAcknowledgeNumber(int nacknowledgeNumber) {
		this.acknowledgeNumber = nacknowledgeNumber;
	}
	
	public char getData() {
		return data;
	}
	public void setData(char data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "TCPPacket [sourcePort=" + sourcePort + ", destinationPort=" + destinationPort + ", sequenceNumber="
				+ sequenceNumber + ", acknowledgeNumber=" + acknowledgeNumber + ", data=" + data + "]";
	}
	
	
}
