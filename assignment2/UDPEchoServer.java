import java.io.*;
import java.net.*;

class UDPEchoServer {
    
    public static void main(String[] args) throws IOException {
        
        // check if the number of command line argument is 1
        if (args.length != 1) {
            System.err.println("Usage: java UDPEchoServer serverPort");
            System.exit(1);
        }
        
        int port = Integer.parseInt(args[0]);
        DatagramSocket serverSocket = new DatagramSocket(port);
        
        while (true) {
            
            byte[] inBuffer = new byte[1000];
            DatagramPacket rcvedPkt = new DatagramPacket(inBuffer, inBuffer.length);
            serverSocket.receive(rcvedPkt);
            
            String rcvedData = new String(rcvedPkt.getData(), 0, rcvedPkt.getLength());
            InetAddress clientAddress = rcvedPkt.getAddress();
            int clientPort = rcvedPkt.getPort();
            
            byte[] outBuffer = rcvedData.getBytes();
            DatagramPacket sendPkt = new DatagramPacket(outBuffer, outBuffer.length,
                                                        clientAddress, clientPort);
            
            serverSocket.send(sendPkt);
        }
    }
}