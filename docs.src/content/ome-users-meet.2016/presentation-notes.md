Omero Web Proxy
===============
Below are the notes for each slide.

Opening
-------
Hello everyone, I work in Montpellier, France for the CNRS, specifically in
the MRI facility which is one of the largest imaging facilities in France.
At MRI we've been successfully running OMERO for a few years now to manage
our data.
We're quite happy with OMERO but needed to be able to import images from 
acquisition workstations without requring a user to be logged on.

Requirements
------------
(Skipped this.)
Our initial motivation was to import images from acquisition workstations
without a user having to be logged on. But then we realised we could use
these background processes for other tasks too (e.g. OMERO back pressure
mechanism, FTP, facility management, etc.)

Java Web Queue
--------------
So we have developed the Omero Web Proxy which is a Java Web-based work queue
to run imports on behalf of OMERO clients and is what I'm going to talk about
just in case it happens to be useful to any of you.

We currently have a tech preview on GitHub with a fully automated build that 
produces a generic release, a Window service, and a Linux deamon. 
You're welcome to try it out, it's stable and fully functional, but like I 
said it's still a tech preview at this stage so we recommend wearing a helmet.

Queuing an import
-----------------
I'm going to give you a quick overview of how this Omero Web Proxy works.
Any Web client can queue an import through the Proxy's REST API. Basically
you POST some JSON describing who wants to import what.

Getting feedback
----------------
You can then get feedback about import progress. We also have a monitoring
interface for system administrators to gauge system performance and health.
This is also a REST API.

Running an import
-----------------
The import is eventually fetched from the queue and run. If it fails, it'll
be retried for a configurable number of times. Also because the queue is
persistent, we can survive a reboot, a crash, or recover from network outages
or OMERO being down, etc.

Outcome notification
--------------------
In any case, the user who requested the import is eventually emailed with
the outcome. And if it's a permanent failure the system administrator is 
notified too.

Reactive architecture
---------------------
(Skipped this slide.)
As I mentioned earlier, we needed a fault-tolerant, resilient solution.
At the same time, we needed a scalable architecture as we literally have
dozens of acquisition workstations. Also, we didn't want the various
components to be tightly coupled together. All this quickly led to a
distributed, message-driven design that, these days, some call a reactive
architecture.

Likes
-----
I'd like to spend just a few seconds to mention what we *merely* loved about
OME. First of all, the developers. Very friendly and extremely helpful, we've
always got prompt advice and solutions whenever we asked for help.
So here goes a huge thank you to all OME developers, you guys are awesome!

The other thing we loved is that OMERO is a robust and flexible platform,
very hackable! But we think there are things that could be added to the OME
ecosystem that may be useful in the future as for example some form of support
for messaging (an OME ESB?) and, perhaps, if some of the current functionality
could also be exposed through REST Web services?
