---
title: Messaging
slogan: ...in a bottle ♫ yeah ♫
---

<p class="intro">
The basic idea: asynchronous messaging (very) loosely based on
Communicating Sequential Processes (CSP).
</p>

The key idea behind Smuggler's messaging component is that of an abstract
communication channel where you can send and receive data asynchronously,
very much in the style of CSP. We have a `ChannelSource` interface to `send`
input data and symmetrically a `ChannelSink` interface to `consume` it. We
then specialise these two with, respectively, `MessageSource` and `MessageSink`
to impose a minimal structure on the data item being exchanged: what goes
on the channel is now a message having some metadata and some actual data,
the payload for the receiving end. Metadata is typically used to configure
the sending of the message---e.g. schedule when to deliver it---or to set
some message properties---e.g. a delivery count to tell how many times the
message has been dispatched to a consumer in the case of repeat delivery.
In fact, we specialise `MessageSource` further into a `SchedulingSource` so
that the sender can request the delivery of a message to the consumer at a
specified time in the future. With these interfaces in place, it's easy to
implement a `MessageSink` to let a consumer loop over a stream of data items
at specified points in time---this is what the `ReschedulingSink` does.
As an added bonus, we use the special case when all the items in the stream
are the same as a way to retry failed tasks: picture a stream of requests
to execute a given task and imagine a stream handler that executes each
request at a specified time stopping as soon as the task succeeds.

Well, I think some doodles to visualise all this would be nice at this
point. [This UML class diagram][key-msg-classes] shows you the structure of
the `core.msg` package in terms of its key classes and their relationships.
Down below there are a couple of slides with more doodles to help explain
how we conceptualised channels, messages, and all the other stuff we've
been talking about here.

<div class="diagram" id="ideas-slides" src="ideas/1.channel.svg">
Conceptualisation of asynchronous channels and their specialisation to
exchange messages and to exchange messages with repeat schedules.
<br/>
Navigation: click on blue arrows, then use browser back button to go back
to first slide.
</div>

As you can see in the first slide, the idea is to define the messaging
functionality needed in Smuggler abstractly in terms of interfaces and
classes that only depend on those interfaces. This is what goes in the
`core.msg` package and is the only thing all the other modules in
Smuggler depend on.
We implement these channel abstractions in the `q` package using HornetQ
queues. Here's an [annotated UML class diagram][key-q-classes] that shows
the key implementation classes in `q` and their relationship to both the
abstract specification in `core.msg` and the HornetQ Core API.

We also have a couple of UML object diagrams to show examples of how the
instances of the various classes are wired together to provide communication
channels to the services that need them---the wiring happens in the `config`
package, through the Spring IoC API.
[The first diagram][omero-import-gc-wiring] shows how the import GC queue
serves as a channel between the `ImportRunner` instance---the message
producer---and the `ImportLogDisposer` instance, the consumer. In running
an import, the `ImportRunner` schedules the deletion of the import log file
by putting a message on the import GC queue using a `SchedulingSource`.
This message is a basically a deletion request that, when the time comes,
is delivered to the `ImportLogDisposer` through a `ChannelSink` instance.
[The second diagram][omero-import-wiring] is more involved as it shows the
wiring of the various class instances involved in the running of an import
and how they're connected through the import queue.




[key-msg-classes]: structure/key-msg-classes.svg
  "Key classes in core.msg - UML class diagram"
[key-q-classes]: structure/key-q-classes.svg
  "Key classes in q - UML class diagram"
[omero-import-wiring]: structure/omero-import-wiring.svg
  "Wiring of import service - UML object diagram"
[omero-import-gc-wiring]: structure/omero-import-gc-wiring.svg
  "Wiring of import GC - UML object diagram"
