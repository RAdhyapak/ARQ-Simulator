package simulation;

import java.io.Serializable;

public class IPPacket implements Serializable {
	private static final long serialVersionUID = 6317748420535116657L;
	
	String sourceIPAddress;
	String destinationIPAddress;
	TCPPacket tcpPacket;
	
	public IPPacket(String sourceIPAddress, String destinationIPAddress, TCPPacket tcpPacket) {
		super();
		this.sourceIPAddress = sourceIPAddress;
		this.destinationIPAddress = destinationIPAddress;
		this.tcpPacket = tcpPacket;
	}
	public String getSourceIPAddress() {
		return sourceIPAddress;
	}
	public void setSourceIPAddress(String sourceIPAddress) {
		this.sourceIPAddress = sourceIPAddress;
	}
	public String getDestinationIPAddress() {
		return destinationIPAddress;
	}
	public void setDestinationIPAddress(String destinationIPAddress) {
		this.destinationIPAddress = destinationIPAddress;
	}
	public TCPPacket getTcpPacket() {
		return tcpPacket;
	}
	public void setTcpPacket(TCPPacket tcpPacket) {
		this.tcpPacket = tcpPacket;
	}
	@Override
	public String toString() {
		return "IPPacket [sourceIPAddress=" + sourceIPAddress + ", destinationIPAddress=" + destinationIPAddress
				+ ", tcpPacket=" + tcpPacket.toString() + "]";
	}
	
}
