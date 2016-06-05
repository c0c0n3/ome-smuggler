Omero Web Proxy
===============
Below are the notes for each slide.

Opening
-------
Hello everyone or rather bonjour I should say as I work in Montpellier, 
France for the CNRS, specifically in the MRI facility which stand for...
well, you're better off reading it from the slide, please don't ask me to
say it in French!

Anyway, MRI is one of the largest imaging facilities in France and we've 
been successfully running OMERO for a few years now to manage our data. 
We're quite happy with OMERO but needed to be able to import images from 
acquisition workstations without requring a user to be logged in.

Java Web Queue
--------------
So we have developed the Omero Web Proxy which is a Java Web-based work queue
to run imports on behalf of OMERO clients and is what I'm going to talk about
just in case it happens to be useful to any of you.

We currently have a tech preview on GitHub with a fully automated build that 
produces a generic release, a Window service, and a Linux deamon. 
You're welcome to try it out, it's stable and fully functional, but like I 
said it's still a tech preview at this stage so we recommend wearing a helmet.

? Requirements
--------------
Our initial motivation was to import images from acquisition workstations
without a user having to be logged in. But then we realised we could use 
these background processes for other tasks too (e.g. OMERO back pressure 
mechanism, FTP, facility management, etc.)

Queueing an import
------------------
I'm going to give you a quick overview of how this Omero Web Proxy works.
Any Web client can queue an import through the Proxy's REST API. Basically
you POST some JSON describing who wants to import what.

Getting feedback
----------------
You can then get feedback about import progress. We also have a monitoring
interface for system administrators to get information about system health.
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

? Reactive architecture
-----------------------
Built using a reactive architecture as we realised we had to be able to 
scale, to retry tasks, ... (see ome presentation slides)

? What we loved about OME
-------------------------
* Friendly and extremely helpful devs, prompt advice & solutions
* Robust & flexible platform, very hackable

? What we'd love to see
-----------------------
* support for messaging (an OME ESB?)
* Some of the functionality also exposed through REST Web Services
