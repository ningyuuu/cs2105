/*
Name: Ning Yu
Student number: A0111277M
Is this a group submission (yes/no)? no

If it is a group submission:
Name of 2nd group member: THE_OTHER_NAME_HERE_PLEASE
Student number of 2nd group member: THE_OTHER_NO

*/


import java.net.*;
import java.nio.*;
import java.io.*;


class Bob {
    private int seqNum = 0;
    DatagramSocket socket;

    public static void main(String[] args) throws Exception {
        // Do not modify this method
        if (args.length != 1) {
            System.out.println("Usage: java Bob <port>");
            System.exit(1);
        }
        new Bob(Integer.parseInt(args[0]));
    }

    public Bob(int port) throws Exception {
        // Implement me
        socket = new DatagramSocket(port);

        while (true) {
            // headers: 8 byte (long) crc, 4 byte (int) sequence, 4 byte (int) size
            byte[] inBuffer = new byte[496]; // max 512, leave 16 bytes for headers
            DatagramPacket rcvedPkt = new DatagramPacket(inBuffer, inBuffer.length);
            socket.receive(rcvedPkt);

            String incoming = new String(rcvedPkt.getData(), 0, rcvedPkt.getLength());
            if (incoming.substring(0, 1).equals("m")) {
                printMessage(incoming.substring(1));
            } else if (incoming.substring(0, 1).equals("f")) {
                printMessage("File incoming:" + incoming.substring(1));
                sendACK(rcvedPkt.getAddress(), rcvedPkt.getPort());
                try {
                    receiveFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            sendACK(rcvedPkt.getAddress(), rcvedPkt.getPort());
        }

    }

    public void printMessage(String message) {
        // Do not modify this method
        // Call me to print out the messages!
        System.out.println(message);
    }

    public void receiveFile() throws Exception {
        byte[] inBuffer = new byte[500];
        DatagramPacket rcvedPkt = new DatagramPacket(inBuffer, inBuffer.length);

        int size;

		FileOutputStream fos = new FileOutputStream("output");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        

        while (true) {
            socket.receive(rcvedPkt);
            byte[] rcvBytes = rcvedPkt.getData();
            ByteBuffer bbuffer = ByteBuffer.wrap(rcvBytes);
            size = bbuffer.getInt();
            printMessage(size + "");

            if (size > 0) {
                byte[] content = new byte[size];
                bbuffer.get(content);
                printMessage("WRITING TO FILE");
                bos.write(content);
            } else {
                bos.close();
                fos.close();
                break;
            }
        }
        printMessage("done");
    }

    public void sendACK(InetAddress address, int port) {
        String ack = "ACK";
        byte[] ackData = ack.getBytes();
        DatagramPacket ackPacket = new DatagramPacket(ackData, ackData.length, address, port);
        try {
            socket.send(ackPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}