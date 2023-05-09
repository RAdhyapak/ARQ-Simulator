package simulation;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 
 * Receiver class to simulate a Receiver host in Sender-Router-Receiver Simulation
 * @author Rohan Adhyapak
 *
 */
public class Receiver {
	private static String senderIPAddress = "68.21.22.3";
	private static int senderPort = 1234;
	private static String receiverIPAddress = "77.21.33.5";
	private static int receiverPort = 5678;
	
	private int Y = 100;
	private int lastPacketReceived = 0;
	private long timeoutOffset = 50;
	private long timeout = System.currentTimeMillis() + timeoutOffset;
	private boolean verbose;
	private boolean printResult = true;
	
	private TCPPacket tcpPackets[];
	private BlockingQueue<TCPPacket> queue;
	
	public Receiver(int Y, int timeoutOffset, boolean verbose) {
		this.Y = Y;
		this.timeoutOffset = timeoutOffset;
		tcpPackets = new TCPPacket[Y + 1];
        queue = new LinkedBlockingDeque<>();
        this.verbose = verbose;
	}
	
	
	
	public void sendNACK(int nackNumber) throws Exception {
        TCPPacket packet = new TCPPacket(receiverPort, senderPort, nackNumber, nackNumber, '#');
        queue.add(packet);
        if (verbose) System.out.printf("SENDING NACK packet with ack-number %s \n", nackNumber);
	}
	
	
	public void start() throws Exception {
		System.out.println("Receiver System");
		Socket socket = new Socket("localhost", 7777);
        
        initRouteToRouter();
        System.out.println("----Route established with the Router----");
        Thread queueHandler = new Thread(() -> {
        	try {
        		while (true) {
        			if (!queue.isEmpty()) {
                		TCPPacket packet = queue.poll();
                		OutputStream outputStream = socket.getOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                        IPPacket ipPacket = new IPPacket(receiverIPAddress, senderIPAddress, packet);
                        objectOutputStream.writeObject(ipPacket);
                        if (verbose) System.out.println("ROUTED packet to Router \n");
                	}
        		}
        	} catch (Exception e) {
    			try {
    				System.out.println("sasa");
					e.printStackTrace();
    				socket.close();
    			} catch (IOException e1) {
    				e1.printStackTrace();
    			}
    		}
        });
        
        Thread receiptHandler = new Thread(() -> {
        	try {
        		while (true) {
                	InputStream inputStream = socket.getInputStream();
        	        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        	        IPPacket ipPacket = (IPPacket) objectInputStream.readObject();
        	        if (verbose) System.out.printf("RECEIVED packet with Sequence number: %s \n", ipPacket.getTcpPacket().getSequenceNumber());
        	        if (verbose) System.out.printf("Last Packet Received: %s \n\n", lastPacketReceived);
        	        int receivedSequenceNumber = ipPacket.getTcpPacket().getSequenceNumber();
        	        if (lastPacketReceived == 0 && receivedSequenceNumber == 1) {
        	        	tcpPackets[receivedSequenceNumber] = ipPacket.getTcpPacket();
        	        	lastPacketReceived = receivedSequenceNumber;
        	        } else if (receivedSequenceNumber > 1 && receivedSequenceNumber <= Y) {
        	        	if (tcpPackets[receivedSequenceNumber] == null) {
        	        		int difference = receivedSequenceNumber - lastPacketReceived;
            	        	if (difference > 1) {
            	        		if (verbose) System.out.println("PACKET LOSS DETECTED!!!!");
            	        		sendNACK(lastPacketReceived + 1);
            	        	} else if (difference == 1) {
            	        		tcpPackets[receivedSequenceNumber] = ipPacket.getTcpPacket();
                	        	lastPacketReceived = receivedSequenceNumber;
                    			timeout += timeoutOffset;
            	        	}
        	        	} else {
        	        		if (verbose) System.out.printf("Already received packet with SEQ{%s} \n\n", receivedSequenceNumber);
        	        	}
        	        }
        	        
        	        if (lastPacketReceived == Y && printResult) {
        	            System.out.println("End: " + System.currentTimeMillis());
        	        	System.out.printf("COMPLETED RECEIVING ALL PACKETS!!!\n");
        	        	StringBuilder dataReceived = new StringBuilder("");
        	        	for (int i = 1; i <= Y; i++) {
        	        		TCPPacket packet = tcpPackets[i];
        	        		dataReceived.append(packet.getData());
        	        		System.out.printf("Packet %s: %s \n", i, packet);
        	        	}
            			System.out.println("--------------------------------------------------");
        	        	System.out.printf("RECEIVER: Data received from Sender: %s \n", dataReceived);
            			System.out.println("--------------------------------------------------");
            			printResult = false;
        	        }
                }
        	} catch (Exception e) {
    			try {
    				System.out.println("sasa");
					e.printStackTrace();
    				socket.close();
    			} catch (IOException e1) {
    				e1.printStackTrace();
    			}
    		}
        });
        
        Thread timeoutHandler = new Thread(() -> {
        	// Check if first packets was never received
        	// Check if last packets were never received
        	try {
            	while (true) {
            		Thread.sleep(timeoutOffset);
            		long currentTime = System.currentTimeMillis();
            		if (currentTime > timeout && lastPacketReceived < Y) {
            			if (verbose) System.out.println("----------------------------------------------------------------");
            			if (verbose) System.out.printf("Sensed a packet timeout for sequence number: %s \n\n", lastPacketReceived);
            			sendNACK(lastPacketReceived + 1);
            			timeout += timeoutOffset;
            			if (verbose) System.out.println("----------------------------------------------------------------");
            		}
            	}
        	} catch (Exception e) {
    			try {
    				System.out.println("sasa");
					e.printStackTrace();
    				socket.close();
    			} catch (IOException e1) {
    				e1.printStackTrace();
    			}
    		}
        });	
        
        queueHandler.start();
        receiptHandler.start();
        timeoutHandler.start();
	}

	private void initRouteToRouter() {
		TCPPacket tcpPacket = new TCPPacket(receiverPort, 0, 0, 0, '#');
        queue.add(tcpPacket);
	}


	public static void main(String[] args) throws Exception {
		int Y = 1000;
		int timeoutOffset = 3000;
		Receiver receiver = new Receiver(Y, timeoutOffset, false);
		receiver.start();
	}

}
