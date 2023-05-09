package simulation;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PacketListener implements Runnable {
	
	private Socket socket;
	private BlockingQueue<IPPacket> packetBuffer;
	private Map<String, Socket> routingTable;
	private boolean verbose;
	private int X = 7; // my last digit panther number

	public PacketListener(int X, Socket socket, BlockingQueue<IPPacket> packetBuffer, 
			Map<String, Socket> routingTable, boolean verbose) {
		super();
		this.X = X;
		this.socket = socket;
		this.packetBuffer = packetBuffer;
		this.routingTable = routingTable;
		this.verbose = verbose;
	}

	@Override
	public void run() {
		try {
			while (true) {
		        InputStream inputStream = socket.getInputStream();
		        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

		        IPPacket packet = (IPPacket) objectInputStream.readObject();
		        int seqNumber = packet.getTcpPacket().getSequenceNumber();
		        int ackNumber = packet.getTcpPacket().getAcknowledgeNumber();
		        if (verbose) System.out.printf("INCOMING packet with details: %s \n\n", packet.toString());
		        String key = packet.getSourceIPAddress();
		        routingTable.put(key, socket);
		        if (shouldPacketBeDropped()) {
		        	if (verbose) System.out.printf("LOST/DROPPED packet with SEQ{%s}, ACK{%s} \n\n", seqNumber, ackNumber);
		        } else {
			        this.packetBuffer.add(packet);
		        }
			}
		} catch (Exception e) {
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private boolean shouldPacketBeDropped() {
		int random = new Random().nextInt(1000);
		return (random < X);
	}
}
