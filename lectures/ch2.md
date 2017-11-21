# Network Applications: HTTP

## 3 types of network architectures:
#### Client-Server
* Server is always alive, awaits for incoming requests
* Server provides requested service to client
* Client initiates contact with server, to request a service
* In the case of the web, the client is typically the browser

#### Peer-2-Peer
* No server. Users connect to users, peers request for service from other peers
* Highly dynamic, and may be difficult to track

#### Hybrid CS/P2P
* A mix of services
* Places where peers interact more, use P2P
* Other stuff use CS
* E.g. Facebook and Facebook chat

#### Requirements of an application protocol
* __Data integrity__: some apps require very reliable data transfer (e.g. games), but some don't (e.g. voice chat)
* __Throughput__: some require high throughput: streaming, data downloading
* __Timing__: for interactive applications, timing is critical, latency is higher priority
* __Security__: encryption, data protection and privacy

__Transport__: application data are transported by 2 main transport layer protocols, TCP and UDP. TCP is reliable, will resent if lost. UDP is simple but unreliable, will not care if lost. Both do not provide guarantee on timing or throughput rates

## HTTP: Hypertext Transfer Protocol
* __Client server model__, web's application layer protocol
* Runs over __TCP__ with the following basic model:
  1. Client initiate TCP connection
  2. Server accepts TCP connection
  3. Client sends a request for file
  4. Server sends back the file, and closes the connection
* If client realizes file has an embedded image, repeats steps 1-4 to request for image
* __RTT__: round trip time. Takes 2 * RTT + transmission time per file to get an object.
* __IMPROVEMENT__: use parallel TCP connections, to send multiple requests at once, which makes it faster, but definitely not faster than HTTP 1.1 PwP (below)

## HTTP 1.1: Persistent
* Server does not close the connection at step 4 (using the `keep-alive` header)
* It allows client to use `keep-alive` to keep it alive, before finally closing at the end
* With __pipelining__, it can ask for >1 file at once, client will not wait for response from server before sending another request

## Message structure
* Two types of messages: __request__, __response__
* Request:
  * First line: __request line__
  * Second line onwards: __header lines__ (only host is compulsory)
* Response:
  * First line: __status line__ `HTTP/1.1 200 OK`
  * Second line onwards: __header lines__ including `Content-Type`
  * Then, a empty line `\r\n`
  * Finally, the file data at the back

#### Cookies
* In the original design of HTTP, server would never try to recognize the client. Hence, the requests are __stateless__
* However, we realize that sometimes servers want to know clients, such as when we want to log in
* Hence we use __cookies__, sent through a header in the request message

#### Conditional GET
* Using a `If-Modified-Since` header in the request, the server will only send over a new copy if the file requested has been updated
* Else, send `304 Not Modified`
