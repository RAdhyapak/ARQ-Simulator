# ARQ-Simulator[Computer Networks]
This project is a Network based Simulator of Go-Back-N ARQ (Automatic Repeat Request) which is an NACK based error control protocol used in the TCP layer of computer networks

## TCP
In the TCP layer of the OSI network model, packets of data are exchanged between a Sender and a Receiver over the computer network. There are error control protocols that are used between the Sender and the Receiver, so that the Receiver can let the Sender know, when packets are lost or have timed out. There are numerous types of such error control protocols. The most common ones are ACK-based, i.e for each packet sent by the Sender, the Receiver sends an ACK(acknowledgement) of the packet to the Sender. This lets the sender know that the packet sent was received and the Sender can send the next packet. However, this required each packet to be ACK'd by the Receiver and hence increases the network congestion. 

There is another such set of error control protocols which are NACK based, i.e the Receiver sends NACK(negative-acknowledgement) for packets which have been lost or timed out. Hence this reduces the number of NACK packets flowing from the Receiver to the Sender. There are different algorithmic implementation of NACK such as below:

1. Go-Back-N ARQ: In this protocol, the sender sends a continuous stream of packets, and the receiver sends a NACK message for a lost or corrupted packet. Upon receiving a NACK message, the sender retransmits all the packets starting from the lost packet.

2. Stop-and-Wait ARQ: In this protocol, the sender sends a packet and waits for an ACK or NACK message from the receiver before sending the next packet. If the sender receives a NACK message, it retransmits the packet.

3. Selective Repeat ARQ: In this protocol, the sender sends a fixed number of packets, and the receiver sends NACK messages for only the missing or corrupted packets, requesting retransmission of those specific packets.

In this project we try to Simulate the Go-Back-N ARQ. We have used Sockets to Simulate a Sender, Router and Receiver exchanging packets of data. We generate a random string that we send from the Sender to the Receiver. The router has a random drop logic, which drops random packets from time to time to emulate actual network traffic congestion. Each packet sent holds a character of the string. So if the string is 1000 characters long, 1000 packets will be sent by the Sender to the Receiver. 

The entirety of the code is straight-forward and self contained. We are using a few basic concepts of multi-threading to simulate reading and writing to the buffer(A queue that stores packets to be sent out) and timeouts on the receiver's end. 

There are two types of packets that we have simulated IPPacket(contains the dummy IP addresses assigned to Sender and Receiver) and TCP Packet(contains the source and destination ports). 
The TCP packets is packaged inside the IP Packet. So essentially, we are sending IP Packets from the Sender to the Receiver. The Router maintains a routing table, i.e a mapping of the IP address and the socket connection and uses it everytime to figure out where to route the received packet. 

In short, the Sender generates a string of size Y. Sender sends Y packets to the Router. Router forwards Y packets to the Receiver. Receiver receives the random string generated by the Sender. 

## Running the Simulator
The Simulator takes in a few variables:
- failure rate(X): A value based on which the router drops packets
- Size of String(Y): The number of packets to send from the Sender to the Receiver
- Timeout(timeoutOffset): timeout value on each packet at receiver side, if a packet times out, the receiver realizes that the packet was dropped and sends NACK
- Verbosity(verbose): print console statements. This slows the performance, however helps to understand the code and debug issues.

Individually, we can also run the Router, Receiver and Sender in this order. 
Or, We can run the Simulator.java to trigger all the three and simulate Go-Back-N ARQ. 