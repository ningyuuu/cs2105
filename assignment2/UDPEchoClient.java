import java.io.*;
import java.net.*;
import java.util.*;

class UDPEchoClient {
    
    public static void main(String[] args) throws IOException {
        
        // Check if the number of command line argument is 2
        if (args.length != 2) {
            System.err.println("Usage: java UDPEchoClient serverIP, serverPort");
            System.exit(1);
        }
        
        InetAddress serverAddress = InetAddress.getByName(args[0]);
        int serverPort = Integer.parseInt(args[1]);
        
        DatagramSocket clientSocket = new DatagramSocket();
        
        Scanner sc = new Scanner(System.in);
        String fromKeyboard;
        
        while ( !((fromKeyboard = sc.nextLine()).equalsIgnoreCase("bye")) ) {
            
            byte[] sendData = fromKeyboard.getBytes();
            DatagramPacket sendPkt = new DatagramPacket(sendData, sendData.length,
                                                        serverAddress, serverPort);
            
            clientSocket.send(sendPkt);
            
            byte[] inBuffer = new byte[1000];
            DatagramPacket rcvedPkt = new DatagramPacket(inBuffer, inBuffer.length);
            
            clientSocket.receive(rcvedPkt);
            System.out.println("Echo from server: " +
                               new String(rcvedPkt.getData(), 0, rcvedPkt.getLength()));
        }
        
        clientSocket.close();
    }
}