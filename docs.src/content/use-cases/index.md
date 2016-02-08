---
title: Condensed Use Cases
slogan: use it or lose it!
---

<p class="intro">
Where we've been, where we're going. A quick look at the current and planned
functionality helps to put things into perspective.
</p>

Let's quickly talk about what were our initial requirements before we go on
to see what we hope to do in the short, medium, and long term.


A little Bit of History
-----------------------
This project started off as a little extension to OMERO that [MRI][mri]
needed to untie login sessions from OMERO imports and that the [FBI][fbi]
paid for. (Nope, not <em>that</em> FBI, the Feds are supposed to bust
smugglers!) Long story short, MRI users import images from acquisition
workstations into OMERO with the Java client and so can't logout until the
client has finished importing---or the client dies and the import with
it. Long-running imports side-effects? Other users can't get access to the
workstation and use the microscope. Also, users are billed for the duration
of their login session, so time <em>is</em> money. All this quickly led to
the idea of running imports in a background process outside of any user
session. The full story is recorded for posterity in
[this document][bg-import-overview]; the intro section may still be worth
a quick read to put Smuggler into perspective.


Coming Soon
-----------
Our short-term plan is to make Insight work with Smuggler to do an import.
Once that is done, we'd like to add a generic file-transfer task to move
large amounts of data out of acquisition workstations in such a way that
interrupted transfers can be picked up again from where they stopped,
without having to re-send data that has already been transferred. (In this
regard, adding support for Globus/GridFTP would probably be a good idea.)
Data could be split into chunks and each chunk gets a corresponding transfer
task on the queue so interrupting a transfer wouldn't affect the chunks that
have trekked over already and failed chunks can be retried.


Watch this Space
----------------
Medium-term plans? Well, the next logical step would be to make OMERO imports
work in the same way and moreover slow down the uploading of data chunks if
OMERO can't keep up. At the moment, we have a problem with large imports as
they can make OMERO crash---we strongly suspect Java garbage collection is
to blame rather than OMERO though. Anyway, it'd be nice to have some kind of
back-pressure mechanism in place. And a similar back-pressure mechanism would
work for mass deletes as these too can make OMERO crash.


Maybe One Day...
----------------
What about the long-term? No real plans yet, but probably worth mentioning
that we could get more involved in the integration game. In fact, as things
stand now we could already easily make the Web client work with Smuggler too
so imports became available even in the browser. But then why not push it a
step further and use Smuggler as a distributed event bus where participants
exchange asynchronous messages using plain HTTP. Different components (e.g.
agents, servers) can cooperate towards a common goal (e.g. data transfer,
image rendering, analysis) without even knowing about each other. Each can
be written in any programming language and there's no need for ICE or other
middleware to communicate.

<p class="side-note">
ICE is a fine framework, but we think in a integration scenario REST could
be an easier way to bring together heterogeneous software as HTTP is known
to most programmers and almost each language under the sun comes with robust
support for it.
</p>

Here's a possible scenario:

1. Either Insight or the Web client triggers an import by putting a message
on the distributed event bus.
2. Smuggler picks up the message and starts the import; as it runs the import,
it puts feedback messages on the bus.
3. Any other client connected to the bus may pick up these feedback messages
and display e.g. a progress bar to the user; for example, after triggering
the import, the user shuts down the client and logs off the acquisition
workstation, then later she fires up the Web client on her cell and, voila,
the progress bar is there...

This is just one example though. Stretching it even further, you could think
of Smuggler evolving into an ESB where OMERO, ImageJ, ICY, and other software
can cooperate towards a common goal---as explained earlier---by exchanging
semantic messages, courtesy of the OME ontology...

Another avenue to explore is that of federated repositories---coming to OMERO
soon, hopefully. Chances are we may be able to leverage messaging tech to
help us out there too? Time will tell.




[bg-import-overview]: /pdfs/bg-import.project-overview.pdf
  "OMERO Backgound Import - Project Overview"
[fbi]: http://france-bioimaging.org/
  "FBI Home"
[mri]: http://www.mri.cnrs.fr/
  "MRI Home"

