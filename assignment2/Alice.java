/*
Name: Ning Yu
Student number: A0111277M
Is this a group submission (yes/no)? no

If it is a group submission:
Name of 2nd group member: THE_OTHER_NAME_HERE_PLEASE
Student number of 2nd group member: THE_OTHER_NO

*/


// Please DO NOT copy from the Internet (or anywhere else)
// Instead, if you see nice code somewhere try to understand it.
//
// After understanding the code, put it away, do not look at it,
// and write your own code.
// Subsequent exercises will build on the knowledge that
// you gain during this exercise. Possibly also the exam.
//
// We will check for plagiarism. Please be extra careful and
// do not share solutions with your friends.
//
// Good practices include
// (1) Discussion of general approaches to solve the problem
//     excluding detailed design discussions and code reviews.
// (2) Hints about which classes to use
// (3) High level UML diagrams
//
// Bad practices include (but are not limited to)
// (1) Passing your solution to your friends
// (2) Uploading your solution to the Internet including
//     public repositories
// (3) Passing almost complete skeleton codes to your friends
// (4) Coding the solution for your friend
// (5) Sharing the screen with a friend during coding
// (6) Sharing notes
//
// If you want to solve this assignment in a group,
// you are free to do so, but declare it as group work above.




import java.net.*;
import java.nio.*;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;
import java.util.zip.CRC32;


class Alice {
    private int seqNum = 0;
    private DatagramSocket socket;

    // arbituary values for NAK and ACK
    long ACK = 112233445;
    long NAK = 667788990;

    public static void main(String[] args) throws Exception {
        // Do not modify this method
        if (args.length != 2) {
            System.out.println("Usage: java Alice <host> <unreliNetPort>");
            System.exit(1);
        }
        InetAddress address = InetAddress.getByName(args[0]);
        new Alice(address, Integer.parseInt(args[1]));
    }

    public Alice(InetAddress address, int port) throws Exception {
        // Do not modify this method
        socket = new DatagramSocket();
        socket.setSoTimeout(100);

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            handleLine(line, socket, address, port);
            // Sleep a bit. Otherwise (if we type very very fast)
            // sunfire might get so busy that it actually drops UDP packets.
            Thread.sleep(20);
        }
        socket.close();
    }

    public void handleLine(String line, DatagramSocket socket, InetAddress address, int port) throws Exception {
        // Do not modify this method
        if (line.startsWith("/send ")) {
            String path = line.substring("/send ".length());
            System.err.println("Sending file: " + path);
            try {
                File file = new File(path);
                if (!(file.isFile() && file.canRead())) {
                    System.out.println("Path is not a file or not readable: " + path);
                    return;
                }
            } catch (Exception e) {
                System.out.println("Could not read " + path);
                return;
            }
            sendFile(path, socket, address, port);
            System.err.println("Sent file.");
        } else {
            if (line.length() > 450) {
                System.out.println("Your message is too long to be sent in a single packet. Rejected.");
                return;
            }
            sendMessage(line, socket, address, port);
        }
    }

    public void sendFile(String path, DatagramSocket socket, InetAddress address, int port) throws Exception {
        // Implement me!
        String fileName = path.substring(path.lastIndexOf(" ")+1);
        byte[] fileNameBytes = ("f" + fileName).getBytes();
        DatagramPacket fileNamePkt = new DatagramPacket(fileNameBytes, fileNameBytes.length,
                                                        address, port);

        socket.send(fileNamePkt);
        receiveAck();
        
        byte[] buffer = new byte[496];
		FileInputStream fis = new FileInputStream(path);
        BufferedInputStream bis = new BufferedInputStream(fis);
        int numBytes;

        while (true) {
			numBytes = bis.read(buffer);
			if (numBytes == -1) {
				break;
            }

            byte[] pktBytes = new byte[500];
            ByteBuffer bbuffer = ByteBuffer.wrap(pktBytes);
            System.out.println(numBytes);
            System.out.println(new String(buffer));
            bbuffer.putInt(numBytes);
            bbuffer.put(buffer);
            System.out.println(new String(pktBytes));

            DatagramPacket msgPkt = new DatagramPacket(pktBytes, pktBytes.length,
                                                       address, port);

            socket.send(msgPkt);
        }
        
        byte[] eofBytes = new byte[496];
        ByteBuffer eofbuffer = ByteBuffer.wrap(eofBytes);
        eofbuffer.putInt((byte) 0);

        DatagramPacket eofPkt = new DatagramPacket(eofBytes, eofBytes.length,
                                                   address, port);
        System.out.println("SENDING: eof");
        socket.send(eofPkt);
    }

    public void sendMessage(String message, DatagramSocket socket, InetAddress address, int port) throws Exception {
        // Implement me!
        byte[] messageBytes = ("m" + message).getBytes();
        byte[] contentBytes = new byte[504];
        ByteBuffer contentBuffer = ByteBuffer.wrap(contentBytes);
        contentBuffer.put(messageBytes);

        CRC32 crc32 = new CRC32();
        crc32.update(contentBytes);
        long crc = crc32.getValue();

        byte[] pktBytes = new byte[512];
        ByteBuffer pktBuffer = ByteBuffer.wrap(pktBytes);
        pktBuffer.putLong(crc);
        pktBuffer.put(contentBytes);

        DatagramPacket msgPkt = new DatagramPacket(pktBytes, pktBytes.length,
                                                   address, port);
        socket.send(msgPkt);
        // printBinary(pktBytes, 20);
        ByteBuffer test = ByteBuffer.wrap(pktBytes);
        long crctest = test.getLong();
        // System.out.println(crctest);
        // System.out.println(crc);        
        byte[] testest = new byte[test.remaining()];
        test.get(testest);
        crc32.reset();
        crc32.update(testest);
        // System.out.println(testest.length);
        // System.out.println(crc32.getValue());
        if (!receiveAck()) {
            sendMessage(message, socket, address, port);
        }
    }

    public boolean receiveAck() {
        byte[] inBuffer = new byte[512];
        DatagramPacket ackPkt = new DatagramPacket(inBuffer, inBuffer.length);

        try {
            socket.receive(ackPkt);
            System.out.println("Echo from server: " + new String(ackPkt.getData(), 0, ackPkt.getLength()));

            byte[] pktBytes = ackPkt.getData();
            ByteBuffer pktBuffer = ByteBuffer.wrap(pktBytes);

            long crc = pktBuffer.getLong();
            byte[] content = new byte[pktBuffer.remaining()];
            pktBuffer.get(content);

            CRC32 crc32 = new CRC32();
            crc32.update(content);
            // System.out.println(content.length);
            // printBinary(content, 20);
            // System.out.println(crc);
            // System.out.println(crc32.getValue());

            if (crc == crc32.getValue()) {
                ByteBuffer contentBuffer = ByteBuffer.wrap(content);
                long ACKrcv = contentBuffer.getLong();
                System.out.println(ACKrcv);
                if (ACKrcv == ACK) {
                    return true;
                }
            } 

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    public void printBinary(byte[] b, int firstLen) {
        byte[] i = new byte[firstLen];
        for (int j=0; j<firstLen; j++) {
            i[j] = b[j];
        }
        for (byte bb: i) {
            System.out.println(Integer.toBinaryString((bb+256)%256));    
        }
    }
}