package simulation;

public class Simulator {
	
	public static void main(String args[]) throws Exception {
		int X = 7; // failure rate
		int Y = 1000; // number of packets to send
		int timeoutOffset = 1000; // 1 sec timeout on each packet at receiver
		boolean verbose = false; // print console statements
		
		String stringToSend = generateRandomString(Y);
		System.out.println("Sender sending String: " + stringToSend);
		
		Router router = new Router(X, 0, verbose);
		router.start();

		Receiver receiver = new Receiver(Y, timeoutOffset, verbose);
		receiver.start();
		
		Sender sender = new Sender(Y, 0, stringToSend, verbose);
		sender.start();
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
}
