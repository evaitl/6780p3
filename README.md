Introduction
============

The code here is a solution to CSCI 6780 [Programming Project
3](./docs/Programming-Project3.pdf).

While this file is human readable, it is best to view it as a
processed markdown file on the github site
[here](https://github.com/evaitl/6780p3).


Running
=======

- Copy this whole tree into both the client and server destinations.
- Type "make" to build.
- Create client and server configuration files for your system.
- Start a server with "./coordinator.sh cfgname", where cfgname is the name of a server config file.
- Start one or more clients with "./participant.sh cfgname".

Architecture
============

There was no requirement on this project to create a bunch of threads
in the server, so the server uses a reactive (event-driven)
architecture with a Java `Selector`.  I think on Linux this boils down
to `epoll()` system calls. This should be faster than threading while
scaling better and we don't have to screw around with synchronization
problems.

The client just creates the two threads ThreadA and ThreadB. ThreadA
handles user input while ThreadB creates a `ServerSocket` and `accept()`s
while waiting for a connection from the coordinator (server). The
`ServerSocket` is only used once. After the `accept()`, we call `close()` on
it.


Disclaimer
==========

This project was done in its entirety by Eric Vaitl and Ankita
Joshi. We hereby state that we have not received unauthorized help of
any form.

