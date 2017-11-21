# Chapter 1: Introduction

By 2016, there are >1bill hosts connected to the internet.  
Network edge - end user  
Network core - switches and routers  
Wireless networks: 1) WiFi wireless lans 2) wide area networks  
Physical media: twisted pair cable/coaxial cables/fibre optics  

## 2 types to transmit data through the internet  
#### 1) Circuit switching:
* Set up a connection, forms a circuit -> __guarantees__ connection to transmit data between 2 hosts exclusively. used most commonly in telephone networks  
* This may take a lot of time, as it requires time to send a _special_ signal to reserve a circuit, which can be used by time exclusively.
* When transmission is done, they will done send another _speical_ signal to release them, so that someone else can use them.
* One problem with circuit switching - we usually provision less than maximum to save resources. However, during peak periods, it may cause requests to be rejected. So circuit switching = either good connection, or no connection.

#### 2) Packet switching
* Your message is broken into small packets, and sent through the internet one packet at a time
* Discussed in detail in this course

## Packet Switching Networks
* __Packet transmission delay__: time needed to transmit L bits = L/R, where R = transmission rate (bits/sec), also known as link capacity or link bandwidth
* Packet may pass through multiple routers to go from one host to the other, which are connected by a network of routers. In this case, end to end delay = N\*L/R, where N = number of links (assuming constant L and R)
* Routers use some kind of routing algorithm to determine where to transmit the packet next, this algorithm carries source and destination information, which the packet carries


* __Queueing delay__ when the packet waits in the router, awaiting it's turn to be sent out
* Once the queue is full, subsequent packets are discarded, leading to *packet loss*
* No extra notification is given for a packet being discarded, sender has to detect himself


* __Processing delay__ is the time the router takes to process the packet, to check its integrity
* If packet is fine, it will place it into the queue, else it is discarded


* __Propagation delay__ is based on the length of the link, it's the time taken for the packet to flow from one end of the link to the other

* __Throughput__: how many bits can be sent per unit time. Usually measured for end-to-end connections

## Network Applications
* Network applications run as distributed software on different hosts
* They communicate with one another through a __protocol__, there are tons of protocols out there used by different kinds of services
* Protocols are organized into layers, each of which handles different functions. In order from highest to lowest levels:
* __Application__ support applications,
* __Transport__ process to process data transfer,
* __Network__ routing of datagrams from source to destination,
* __Link__ data transfer between neighbouring network elements
* __Physical__ handle the actual bits on the wire
