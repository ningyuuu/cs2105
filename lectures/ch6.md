# Network Layer: IP Addressing
* 32 bit integer expressed as binary/decimal

### DHCP
* A process to allow a host to obtain an IP
* 1) host broadcast DHCP discover
  * Src: 0.0.0.0:68
  * Dest: 255.255.255.255:67 [dhcp port]
* 2) DHCP server will respond with "DHCP offer"
  * yiaddrr: the address you are suggested to take up
  * many different servers will broadcast to you, but you can only take 1 up
* 3) Host requests IP with "DHCP request", requested one of the yiaddrr
  * this is a broadcast message (dest 255x4) so that all DHCP know you are reserving that address
* 4) DHCP server sends address "DHCP ACK"
  * this is also a broadcast
* It also provides additional information about the server you join, such as the network mask and first hop router
* __Runs over UDP__
* Server port: 67, client port: 68

### Special IP Addresses
* 0.0.0.0/8 - meta address for when you first join
* 127.0.0.0/8 - loops back inside the host => localhost
  * typically, it's 127.0.0.1/32
* 10.0.0.0/8 => 00001010...
* 172.16.0.0/12 => 1100000 0001...
* 192.168.0.0/16 => 11010100 10101000....
  * they are private IPs for internal use without needing an internet registry
* 255.255.255.255/32 - broadcast address for subnet

### Network Interface
* ...../N => first N are fixed in the subnet as a network/subnet prefix. the remaining bits are host id
* the hosts in the same subnet are directly connected, and can talk to each other without the need of a router
* __CIDR: Classless Inter-domain Routing__: the ip address's assignment strategy
  * format: a.b.c.d/x, where x is length of subnet prefix
  * Subnet mask: all first X bits are 1, remaining are 0. for example, for 16, 255.255.0.0
  * __Hierarchical routing__: we only pay intention to the first N bits of the subnet mask to check it is the destination we are looking for
  * In the case where 2 locations' subnet mask both match the destination IP address, we find the more specific address (greater N), aka __longest prefix match__

## IPv4 Datagram Format
* contains an IP datagram length, header checksum, source and dest IP addresses, and TTL (hop limit)
* __IP Fragmentation__: MTU (max transfer unit) is the max data length of a packet for a link
* When a router comes from a link with MTU into a link with smaller MTU, it will divide from a big packet into several small packet fragments before sending
* They will then be combined at the receiver
* For example, length = 1200 = 20(header)+1180(data) at an MTU of 500
  * First segment will be 0~479 + 20(header) = 500 [length=500, ID=x, flag=1, offset=0]
  * Second: 480~959 + 20(header) = 500 [length=500, ID=x, flag=1, offset=60]
  * Third: 960~1199 + 20(header) = 240 [length=240, ID=x, flag=0, offset=120]
  * flag of 1 means this isn't the last fragment of the packet
