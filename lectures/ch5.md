# RDT 3.0 Continued

* __PERFORMANCE__:
  * d_trans = L/R = length/bandwidth
  * throughput = L/(RTT + d_trans)
  * utilization = d_trans / (d_trans + throughput)
* Hence, we should use __pipelining__ to send packets back to back
* In other words, we allow a __window size__ > 1
* With that, utilization = (window_size + throughput) / (d_trans + throughput)
* But to allow for better utilization, protocol must be more complex
  * For pipelining, window size of 2 is no longer sufficient

## 2 Main Pipelining Protocols
* Go-Back-N
* Selective Repeat
* Assumption: packets cannot be reordered, but can delay, loss, corrupt

### Go-Back-N
* Receiver will only receive packets in the correct order
  * e.g. if packet 2 is lost, then it will not accept pkt3, it will wait for pkt2 before moving on to accept and ACK pkt3 => it will continuously ACK1 until pkt2 is received
* When timeout, sender will receive all the packets that were not ACKed, which is the first N unACKed, which is also the corrupted/lost one + (N-1) after
* This is done using a k-bit sequence number, with a sliding window
* Only 1 timer is kept for the oldest unACKed packet
* Receiver will discard all out-of-order packets, with cumulative ACK. This is great, as if the latest ACK is not lost, it assumes all prior ACK are in order, even if they are lost or corrupt

### Selective Repeat
* Receiver will individually acknowledge all correctly received packets, while __buffering__ out of order packets for future use
* Sender maintains timer for each unACKed packet
* However, it still holds a window - it will not move forward until the 1st packet in the window is ACKed

# TCP
* Connection oriented - handshaking involved
* MSS - maximum segment size refers to application data only, excluding headers
* Identified by 4 fields - srcIP, srcPort, destIP, destPort
* Single bit A - ack(1) or data(0) packet
* S, F: TCP connection establishment and teardown


* TCP sequence number - first byte number in a data segment. Note: byte number, not packet number
  * initial sequence number is randomly chosen - doesn't need to start from 0
* ACK number: the next byte expected = next packet's segment number == received everything up to N-1 - cumulative
  * no mention of how out of order packets or bytes should be handled
* A: TCP can have ACK on data packets: __piggybacking__
  * receiver will ack 2 packets at a time, if they arrive within 500ms
* timeout value is dynamic, computed based on some engineering formula
  * estimatedRTT = (1-alpha) * estimatedRTT + alpha*sampleRTT
  * devRTT + (1-beta) * devRTT + beta*|samplertt-estimatedrtt|
  * timeout = estimated + 4*devRTT
* TCP fast retransmission: if sender recevies N=3 duplicate ACKs, then it will resend unACKed segment (N=3 is an engineering heuristic)
* Establishing connection:
  * Client sends S = 1, seq = initial seq number (x)
  * Server reply: S = 1, seq = inital seq number (y) ack = 1, acknum = X+1
  * Then client sends ACK = 1, acknum = y+1, and data of seq=x+1 [data starts flowing here]
* Closing:
  * some kind of FIN = 1 from client
  * ack from server
  * separate pack FIN = 1 from server
  * client ack
