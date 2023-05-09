package simulation;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sender class to simulate a Sender host in Sender-Router-Receiver Simulation
 * @author Rohan Adhyapak
 *
 */
public class Sender {
	
	private static String senderIPAddress = "68.21.22.3";
	private static int senderPort = 1234;
	private static String receiverIPAddress = "77.21.33.5";
	private static int receiverPort = 5678;
	
	private int Y = 100;
	private int waitTime = 0;
	private AtomicInteger lastPacketSent = new AtomicInteger(0);
	private String randomString; 
	private boolean verbose;
	
	private TCPPacket tcpPackets[];
	private LinkedBlockingDeque<TCPPacket> queue;
	
	public Sender(int Y, int waitTime, String sendString, boolean verbose) {
		super();
		this.Y = Y;
		this.randomString = sendString;
		this.waitTime = waitTime;
		this.verbose = verbose;
	}
	
	public void generatePacketsWithData(TCPPacket[] tcpPackets, String dataToSend) {
		for (int i = 0; i < tcpPackets.length; i++) {
			TCPPacket packet = new TCPPacket(senderPort, receiverPort, i + 1, 0, dataToSend.charAt(i));
			tcpPackets[i] = packet;
		}
		
		System.out.println("--------------------------------------------------");
    	System.out.printf("SENDER: Data To Send to Receiver: %s \n", dataToSend);
		System.out.println("--------------------------------------------------");
	}

	public void start() throws Exception {
		System.out.println("Sender System");
		Socket socket = new Socket("localhost", 7777);
        System.out.println("Socket created on port: " + socket.getLocalPort());
        
        tcpPackets = new TCPPacket[Y];
        queue = new LinkedBlockingDeque<>();
        generatePacketsWithData(tcpPackets, randomString);
        
        Thread packetHandler = new Thread(() -> {
        	try {
            	while (true) {
            		if (lastPacketSent.get() < Y) {
            			int seq = lastPacketSent.incrementAndGet();
            			if (verbose) System.out.printf("Adding packet with sequence {%s} to queue. \n", seq);
            			queue.add(tcpPackets[seq - 1]);
            			
            		}
            		Thread.sleep(waitTime);
            	}
        	} catch (Exception e) {
    			try {
    				e.printStackTrace();
    				socket.close();
    			} catch (IOException e1) {
    				e1.printStackTrace();
    			}
    		}
        });
        
        Thread queueHandler  = new Thread(() -> {
        	try {
        		while (true) {
        			synchronized(queue) {
        				if (!queue.isEmpty()) {
                    		TCPPacket packet = queue.poll();
                    		OutputStream outputStream = socket.getOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                            IPPacket ipPacket = new IPPacket(senderIPAddress, receiverIPAddress, packet);
                            objectOutputStream.writeObject(ipPacket);
                            if (verbose) System.out.printf("SENT sequence number: %s \n\n", packet.getSequenceNumber());
                            Thread.sleep(waitTime);
                    	}
        			}
                }
        	} catch (Exception e) {
    			try {
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

    		        IPPacket packet = (IPPacket) objectInputStream.readObject();
    		        int nack = packet.getTcpPacket().acknowledgeNumber;
    		        if (nack > 0 && nack <= Y) {
    		        	if (verbose) System.out.printf("Received NACK{%s}. Resetting last packet sent to %s \n",nack, nack - 1);
    		        	lastPacketSent.set(nack - 1);
    		        	synchronized (queue) {
        		        	queue.clear();
    		        	}
    		        	if (verbose) System.out.printf("LastPacketSent set to {%s} \n", lastPacketSent.get());
    		        }
        		}
        	} catch (Exception e) {
    			try {
    				e.printStackTrace();
    				socket.close();
    			} catch (IOException e1) {
    				e1.printStackTrace();
    			}
    		}
        });
        
        packetHandler.start();
        queueHandler.start();
        receiptHandler.start();
	}
	
	private static String generateRandomString(int Y) {
		String randString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder dataToSend = new StringBuilder("");
		
		for (int i = 0; i < Y; i++) {
			int index = (int)(randString.length() * Math.random());
			char x = randString.charAt(index);
			dataToSend.append(x);
		}
		
		return dataToSend.toString();
	}

	public static void main(String[] args) throws Exception {
		int Y = 1000;
		Sender sender = new Sender(Y, 0, generateRandomString(Y), false);
		sender.start();
	}

}
