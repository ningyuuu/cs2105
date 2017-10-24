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
import java.util.zip.CRC32;

class Bob {
    private int seqNum = 0;
    DatagramSocket socket;
    // arbituary values for NAK and ACK
    long ACK = 112233445;
    long NAK = 667788990;

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
            byte[] inBuffer = new byte[512]; // max 512, leave 16 bytes for headers
            DatagramPacket rcvedPkt = new DatagramPacket(inBuffer, inBuffer.length);
            socket.receive(rcvedPkt);

            // printBinary(rcvedPkt.getData(), 20);

            ByteBuffer pktBuffer = ByteBuffer.wrap(rcvedPkt.getData());
            long crc = pktBuffer.getLong();
            byte[] content = new byte[pktBuffer.remaining()];
            pktBuffer.get(content);

            // System.out.println(crc);

            if (!crcCheck(crc, content)) {
                // printMessage("CRC check fail");
                sendNAK(rcvedPkt.getAddress(), rcvedPkt.getPort());
                continue;
            }

            String incoming = new String(content);
            if (incoming.substring(0, 1).equals("m")) {
                String res = incoming.substring(1).trim();
                printMessage(res);
                sendACK(res.getBytes(), rcvedPkt.getAddress(), rcvedPkt.getPort());
            } else if (incoming.substring(0, 1).equals("f")) {
                printMessage("File incoming:" + incoming.substring(1));
                sendACK(rcvedPkt.getData(), rcvedPkt.getAddress(), rcvedPkt.getPort());
                try {
                    receiveFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

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

    public void sendACK(byte[] msg, InetAddress address, int port) {
        byte[] content = new byte[504];
        ByteBuffer contentBuffer = ByteBuffer.wrap(content);

        byte[] packetData = new byte[512];
        ByteBuffer pktBuffer = ByteBuffer.wrap(packetData);

        // System.out.println("MSG LENGTH" + msg.length);
        contentBuffer.putLong(ACK);
        contentBuffer.put(msg);

        CRC32 crc32 = new CRC32();
        crc32.update(content);
        long crc = crc32.getValue();
        // System.out.println("Byte length: " + content.length);
        // System.out.println(crc);

        pktBuffer.putLong(crc);
        pktBuffer.putLong(ACK);
        pktBuffer.put(msg);

        ByteBuffer test = ByteBuffer.wrap(packetData);
        long testcrc = test.getLong();
        // System.out.println(testcrc);
        byte[] testest = new byte[test.remaining()];
        test.get(testest);
        // System.out.println(testest.length);

        crc32.reset();
        crc32.update(testest);
        // System.out.println(crc32.getValue());
        // printBinary(content, 20);
        // System.out.println("@@@@@@@@@@@@");
        // printBinary(testest, 20);

        DatagramPacket ackPacket = new DatagramPacket(packetData, packetData.length,
                                                      address, port);

        try {
            socket.send(ackPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }

    public void sendNAK(InetAddress address, int port) {
        byte[] content = new byte[504];
        ByteBuffer contentBuffer = ByteBuffer.wrap(content);

        byte[] packetData = new byte[512];
        ByteBuffer pktBuffer = ByteBuffer.wrap(packetData);

        contentBuffer.putLong(NAK);
        CRC32 crc32 = new CRC32();
        crc32.update(content);
        long crc = crc32.getValue();

        pktBuffer.putLong(crc);
        pktBuffer.put(content);

        DatagramPacket nakPacket = new DatagramPacket(packetData, packetData.length,
                                                     address, port);
        try {
            socket.send(nakPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean crcCheck(long crc, byte[] content) {
        CRC32 crc32 = new CRC32();
        crc32.update(content);
        return crc == crc32.getValue();
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