# Transport Layer Protocol - UDP

* Down from application layer, an application message is turned into a transport layer packet by adding the transport layer header.
* At receiver side, the transport layer will then strip down the transport layer header and send the data to the application

### Gist
* UDP has very little overhead: only multiplexing/de-multiplexing and checksum
* However, it is __unreliable__
* Sender will create a datagram, then sent to UDP socket with dest. IP and port
* When UDP receiver receives the packet, it will deliver the packet to the right process based on the port __[connectionless de-multiplexing]__

### Header
* 64 bit header in total
* source and dest port number: 16 bits each
* length: 16 bits
* checksum: 16 bits

### Checksum
* Checksum is computed by the server, and placed into the header
* Receiver will recompute checksum, and compare with sender. If data is corrupted, checksum will be wrong, and receiver will discard packet
* Uses a one's complement 16 bit: sum every 16 bit chunk, then take one's complement
* In Java, we use CRC checksum instead as there is no 1's comp API

# Designing a Reliable Transport Layer Protocol
* We are trying to design a reliable transport layer protocol, on top of a unreliable channel
* In this channel, packets may be corrupted, dropped, reordered or delayed (for an arbitrary length)
* Reordered due to pathing differences leading to differences in distance covered/experience different delays
* We use the functions `rdt_send(), udt_send(), rdt_rcv(), udt_rc()` to denote API calls

### RDT 1.0
* Assume that the underlying network is perfectly reliable
* Then, sender will make packet, then send to receiver
* Receiver will simply receive and unpack packet

### RDT 2.0
* Packet may corrupt
* Sender will create a checksum, and send to receiver
* Receiver will receive checksum, and if it appears to be corrupted, receiver will discard packet
* __How then do we ask for resend when packet is corrupted?__
* When packet is corrupted, receiver can __NAK__ to tell sender to re-send. If packet is ok, it will send __ACK__
* If receiver receives __ACK__, it will send next packet and await ACK/NAK
* Else, if it receives __NAK__, it will send current packet again and await ACK/NAK

### RDT 2.1
* *__However, RDT 2.0 has a major flaw__*: the ACK/NAK packet may be corrupted as well
* Hence, if sender receives a corrupt ACK/NAK, it should retransmit.
* But in this case, if it retransmits at ACK, receiver will receive packet twice => receive needs to __detect duplication__
* We can use sequence numbers to do this: every packet can be tagged with a sequence number
* This way, if receiver detects duplicate, it will throw away and then **send ACK**
* We can use a sequence number of just 1 and 0, since they're always in linear sequence - 2 sequence numbers is already sufficient

### RDT 2.2
* We only use ACK, not NAK. If a packet is corrupted, we send ACK for the last correctly received packet.
* Sender will send first un-ACKED packet (which is basically the current packet)

### RDT 3.0
* We assume packet loss and arbitrarily long packet delays, in addition to corruption
* Hence, we set a timer - if timeout does not happen, either pack or ACK is lost, and we will resend packet
* Resending packet that is already received is ok, since we have sequence number verification
* If sender receives duplicate ACK, on the second ACK, it will __NOT__ resend, but instead use timeout to retransmit data
  * Hence, even if packet corrupts, and receiver sends ACK of previous packet, sender will not immediately send. It will wait for timeout of current ACK, before sending.
