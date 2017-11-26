# DNS

### Hosts
* 2 ways to identify a host: hostname and IP address
* Though, it is much easier to remember a hostname
* Browser will translate a hostname into IP address to access the target host
* This translation is a service provided by __DNS__
* Sometimes, a hostname will have 2 names, a normal name and a __true name__ or __canonical name__, which can be used as a host name as well
* We can use __nslookup__ or __dig__ to get the IP address(es) of a host name
* For redundancy purposes, a single host name may map to several IP addresses

### DNS
* DNSes keep __resource records__ with format (`name, value, type, ttl`)
* If type is `A` then it keeps an address (`name, value`)
* If type is `CNAME` then it keeps the canonical name for the true name also with (`name, value`)
* If type is `NS` then it points to the dns server of a particular domain
* If type is `MX` then it points to an email server address
* DNS is stored in a __distributed database__, with some form of architecture top level:(.com, .org. .net), then domain level (faceboom.com, google.com, etc)
* Hence, to query say, m.facebook.com, it goes to `.com`, then `facebook.com`, then `m.facebook.com`
* Once a DNS server learns a mapping, it caches it for a specific TTL (time to leave)
* DNS runs over UDP - it is faster, without the need for 1 round trip to establish connection

# Socket Programming
* Applications run in hosts as __processes__, for these processes to communicate across the network, they will require some form of protocols
* IP Addresses are insufficient, as each host may run many different processes
* Every process communicating over the network is allocated a __unique__ port number. Some are standardized (<1024). Using port number to identify what process the packet is meant for, is called __de-multiplexing__
* DNS server port number: __53__  

### Two types of sockets
* Stream sockets (TCP sockets): sockets that use TCP as its transport layer protocol. Connection oriented, __reliable__
* Datagram sockets (UDP Sockets) use UDP as its transport layer protocol. Connection-less, __unreliable__ => transmitted data may be lost, corrupted or out-of-order.
* In both versions, client must first contact server, and server must be running first, waiting for client socket to connect.

### TCP
* TCP will establish a connection first before data is sent over
* Server will create welcome socket to await client connection
* Once a client connects, TCP will create a new socket and use this new socket to talk to client dedicatedly

```
import java.net.*;
import java.io.*;
import java.util.*;

class TCPServer {
  public static void main(String[] args) throws IOException {
    int port = 2105;
    ServerSocket welcomeSocket = new ServerSocket(port);
    System.out.println("Server starts");

    Socket connectionSocket = welcomeSocket.accept();
    System.out.println("Connected");
    System.out.println("Client IP: " + connectionSocket.getInetAddress());
    System.out.println("Client Port: " + connectionSocket.getPort());

    Scanner sc = new Scanner(connectionSocket.getInputStream());
    String text = sc.nextLine();
    System.out.println("Client text: " + text);

    PrintWriter toClient = new PrintWriter(connectionSocket.getOutputStream());

    toClient.println(text); // sends back text to client
  }
}
```

### UDP
* For UDP, a datagram (packet) must be created by yourself to send to server/client
* In every packet, the IP, Port, is specified by author
