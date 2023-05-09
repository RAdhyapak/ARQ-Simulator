package simulation;

import java.net.ServerSocket;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * Router class to simulate a Router host in Sender-Router-Receiver Simulation
 * @author Rohan Adhyapak
 *
 */
public class Router {	

	// IP mapping for the router
	private Map<String, Socket> routingTable;
	private BlockingQueue<IPPacket> packetBuffer;
	private int waitTime = 0;
	private int X;
	private boolean verbose;
	
	
	public Router(int X, int waitTime, boolean verbose) {
		super();
		this.X = X;
		this.waitTime = waitTime;
		this.routingTable = new ConcurrentHashMap<>();
		this.packetBuffer = new LinkedBlockingQueue<>();
		this.verbose = verbose;
	}
	
	public void start() throws IOException {
		@SuppressWarnings("resource")
		ServerSocket routerSocket = new ServerSocket(7777);
        System.out.println("Router is up and running...");

        Thread routerListener = new Thread(() -> {
			while (true) {
	            Socket socket;
				try {
					socket = routerSocket.accept();
		            PacketListener pl = new PacketListener(X, socket, packetBuffer, routingTable, verbose);
		            Thread t = new Thread(pl);
		            t.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
		});
        
        Thread queueHandler = new Thread(() -> {
        	try {
				while (true) {
					if (!packetBuffer.isEmpty()) {
						IPPacket packet = packetBuffer.poll();
						printPacketInfo(packet);
						String destinationKey = packet.getDestinationIPAddress();
						if (routingTable.containsKey(destinationKey)) {
							Socket destSocket = routingTable.get(destinationKey);
				            OutputStream destStream;
							try {
								if (verbose) System.out.printf("ACTION: ROUTING packet to: %s \n\n", destinationKey);
								destStream = destSocket.getOutputStream();
					            ObjectOutputStream destObjectStream = new ObjectOutputStream(destStream);
					            destObjectStream.writeObject(packet);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							if (verbose) System.out.println("ACTION: Packet will be discarded as no record found in routing table...\n");
						}
					}
					try {
						Thread.sleep(waitTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
        	} catch(Exception e) {
        		e.printStackTrace();
        	}
		});
        
        routerListener.start();
        System.out.println("Start: " + System.currentTimeMillis());
        queueHandler.start();
	}

		

	public static void main(String args[]) throws Exception {
		int X = 7; // failure rate % - my last digit of panther ID
		Router r1 = new Router(X, 0, false);
		r1.start();
	}
	
	private void printPacketInfo(IPPacket packet) {
		if (verbose) System.out.println("QUEUE HANDLING Packet:" + packet.toString());
	}
}
