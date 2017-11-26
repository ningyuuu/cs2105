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
