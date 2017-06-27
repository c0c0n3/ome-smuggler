---
title: Process View
slogan: things in motion
---

<p class="intro">
Processes & threads, concurrency & distribution, performance & scalability...
Oh my!
</p>

Shaw, so much to talk about. The best we can do at the moment is to sum up
very roughly the key points, but we'll come back to this section some day
to expand it. Or you can volunteer to do it :-)


Processes and Threads
---------------------
Each time you run Smuggler, you get a single, multi-threaded server process.
Within this process, Undertow manages its own thread pool and will run each
HTTP request in a separate thread. Threading parameters, IO buffers, and
other params that affect performance are all configurable. (The Undertow
manual is the best place to find out more about it.)
Artemis has its own thread pool too. (Again, read the manual to find out how
to configure Artemis threading.) Each of our queues gets only one consumer
thread---drawn from Artemis's pool---to pick up messages from it.
This is the lay of the land for the Smuggler server process. Additionally,
Smuggler spawns a separate process to run each OMERO import.


Concurrency
-----------
Many threads servicing Web requests can put messages on the import queue,
but only one thread services the queue. Ditto for the other queues. Data
leave one thread and get into another only through a queue which is where
synchronisation happens. Even though no data is shared among threads, we
only use immutable value objects to shuttle data across, just in case we'll
have to start sharing some items in the future.


Distribution
------------
We said already the Artemis server is embedded into Smuggler. Anyway, in
principle it doesn't need to be that way. Queues can be split across machines
in many ways for e.g. performance, backup, or increased availability---have
a look at the Artemis manual. Smuggler only relies on the abstract notion of
an asynchronous communication channel and the implementation makes no
assumptions about the Artemis consumer and producer being in the same
process; also, message data is serialised to JSON. In fact, the design
already caters for adding a distribution boundary some day: the *REST
Controllers* would be in one process and the *services* in another, with
the queues in between them to shuttle the data back and forth. But, for
the sake of keeping things simple for now, we have bundled everything up
in just one fat process.


Performance
-----------
There's an obvious bottleneck: only one consumer thread per queue! This is
to try limiting the impact of OMERO imports on acquisition workstations---
in our deployment, Smuggler runs on each and every acquisition workstation
we have. Now this works if we assume at most a handful of imports happen in
a day (currently true for us) and so the import queue never grows out of
control.
We should make the number of consumers configurable though as, in general,
this may not be what you want from an import server where you'd hope imports
could run in parallel. If then somebody, like us, needs Smuggler not to steal
too many CPU cycles and IO from high-priority processes---e.g. the software
running a microscope image acquisition, well, why not let the OS do the hard
work for you? Utilities such `nice`, `renice`, and `ionice` spring to mind...
