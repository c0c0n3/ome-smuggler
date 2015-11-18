TODO (hopefully soon!) 
====

Security
--------
* Session Key. Ends up in the HornetQ journal on disk; if an attacker gains
access to the disk, they can decode the journal file and grab the session
key.
* Actuator. End points access needs to be restricted; be strict about what
info is published. Ditto for JMX.
* Import Report. The import status URL lets you access the import run's log.
Not a good idea as the CLI importer may output sensitive information, most
notably the session key!
* HTTPS. Needs to be enabled for production deployment; clients shouldn't be
able to open a HTTP connection to POST an import request as it contains the
session key, a yummy bite for eavesdroppers.
* Access Control. Non-existent; find out what users require.
* Loopholes. Probably many. The code was written quickly, run a thorough
security audit!

Session Keep Alive
------------------
Session key expires after 10 mins. Ping server periodically until import
request is fetched from the queue to make sure we have a valid session
key at the time we process the request and run the import.
Snag: OMERO is down for more than 10 mins, can't keep session alive, import
fails, angry users, oh dear!

Import Target Resolution
------------------------
Introduce URI resolution strategies so that the import target URI specified
by the client can be mapped to a location from which to fetch image(s) data.
This could be a network share visible both to client and server or perhaps
even a URL from which to download data---e.g. HTTP, FTP, etc.

Clustering, Fail-over, Backup
-----------------------------
Allow administrator to configure these features. For the most part, we just
need to tweak and expose HornetQ configuration. Low-hanging fruit.

OMERO Integration
-----------------
* Should the import run in the same JVM as the import server? Or should it be
a separate process as it is now? Each approach has its pros and cons, some
serious forehead banging needed to find the best solution.
* Should we write our own import client along the lines of the CLI importer?
This would give us finer control over the import which we're going to need
if we want to provide better feedback to the users or explore architectures
for large-scale imports---e.g. parallel/distributed, map/reduce, etc. Also,
we could recover from broken imports (e.g. retry if OMERO is down) or resume
partial file transfers.
* Should the import server become part of the OME software suite? A common
import platform that could be used both by Insight and the Web Client? The
queue could be used as a distributed event bus so that it'd become possible
to import from the Web client too.

Task Queue
----------
* Generalise the OMERO import so that the server can be used to run generic
tasks. A task is queued, then run and as it runs a URL is available from which
to get status updates. On completion, a report email is sent to the interested
parties.
* Add file copy and transfer tasks. A general-purpose way to move data out of
an acquisition workstation, perhaps using Globus/GridFTP.

World Domination
----------------
Discuss plans to take over the world and build a spacecraft to go to Titan.
I'm not at liberty to detail the plans here as I work for the FBI (French Bio
Imaging!) and, being the FBI, we like to keep our secrets secret, you know.

Technical Debt
--------------
Erm, uh well, this is going to spoil our plan for a weekend on Titan.
But we *really* need to make the code more robust, add logging and proper
exception handling. And as we're at it, why not

* implement import log retention policy (e.g. schedule message to delete file);
* ...[will add more as I go along]
