---
title: High-level View
slogan: as seen from the moon
---

<p class="intro">
The basic idea: a Web-based work queue to run tasks on behalf of OMERO
clients.
</p>

A task is queued, then run and as it runs a URL is available from which to
get status updates. If the task fails, it may be retried. On completion, an
email report is sent to the interested parties. This is Smuggler's life
purpose---at least for now. And for now, the only task Smuggler knows how
to run is an OMERO import. But it should be possible to add any other task
without too much sweat using the same run/retry/notify mechanism.
If you haven't done it yet, now it'd be a good time to take Smuggler for a
spin as explained [here][whirlwind-tour] so you have a good idea of what is
the functionality Smuggler makes available through its REST API before we
look at what happens under the bonnet.


Conceptual Overview
-------------------
Smuggler is a server that makes available two main service points: the one
to run an OMERO import and the other to monitor and manage failed tasks.
In both instances, service access is over HTTP and client-server interaction
patterns are those of REST. Smuggler is a [Spring Boot][booty] app with an
embedded [Undertow][undertow] Web server engine that propels external
interaction with HTTP clients. Internally, HTTP requests and responses are
managed by the [Spring][spring] MVC framework; MVC is what runs the request
handlers in Smuggler's Web front-end component, named *REST Controllers*.
These handlers turn Web requests into calls to an internal service API and
massage the results back into Web responses. This internal service API is
provided by the Smuggler component named *services* which is where the
actual app logic sits.

The *services* component splits work into tasks and sends them as messages
on an asynchronous messaging channel; the channel then delivers these
messages (asynchronously) back to the *services* task handlers that carry
out the work; in turn, a task handler may put more messages on the channel
to ask another handler to do some other work.
This mechanism is akin to continuations in functional programming, where you
can save your task's state at any point, then get back to it later to do some
more work, and so on until you're done with the task.

The asynchronous messaging channel is a service provided by yet another of
Smuggler's components: *messaging*. This channel abstraction is loosely (very
loosely!) based on Communicating Sequential Processes and is implemented using
message queues, courtesy of [HornetQ][hornetq] and its neat Core API. 
Though the implementation is transparent to the *services* component, using
message-oriented middleware brings a lot of added value into Smuggler as a
whole. For starters, queues are persistent and so, after a crash, Smuggler
can carry on with a task from where he left off when he was so rudely
interrupted. Also a queue can act as an effective back-pressure mechanism
when OMERO is overloaded; work can be split across queues on different
machines to cope with increased workloads; and so on. In short, we can use
message queues as a foundation to build a [reactive system][reactive].

The last piece of the puzzle is the *config* component. This is where we wire
all the bits and pieces together. What we do is embed Undertow and HornetQ
into the app, configure MVC, and beanify our own services. All this is done
through Spring Boot auto-configuration and the Spring's IoC container API.
The reading and writing of configuration data we do ourselves though so as
to have typed configuration items instead of just plain strings.

Why not condense this lengthy explanation of Smuggler's inner body into a
picture? Here's an X-ray for you then.

<div class="diagram" id="components" src="components.svg">
Wiring of Smuggler's components and third-party software.<br/>
UML component diagram.</div>

There's a note at the bottom of the diagram telling you how the conceptual
components map to Java packages. You can find a lot more about source code
breakdown and dependencies in the next section.
But before moving on, we should mention that Smuggler comes with an off-the-shelf
management and monitoring facility that you can access both over HTTP and
JMX. It's provided by the Spring Boot Actuator component that we enabled and
configured to let sys admins gather app metrics and manage the app remotely.


Codebase Essentials
-------------------
Time to start digging into the source. Besides bringing your bucket and spade,
it may help at this point to keep your editor handy so as to be able to move
back and forth between the code and the narrative below.

The source base is split into two root packages: `ome.smuggler`, containing
the app itself, and `util`, a general-purpose library whose code is totally
independent of Smuggler and reusable across projects. In fact, `util` is part
of the dowry we got when we forked from [Spring D'oh][springdoh].
Since then we added a few more `util` classes but we can potentially ditch
the whole lot if there's a better alternative.
In any case, if you realise some of the code you're writing for Smuggler can
be generalised to make it reusable in other projects, well, now you know
where to put it :-)

With that out of the way, let's have a closer look at what's in `ome.smuggler`.
First things first, we should tell you  how Smuggler's components---the
conceptual modules we talked about earlier---map to actual Java code:

* the *REST controllers* are in the `web` package;
* the *services* are in `core.service`;
* the *messaging* functionality is split between `providers.q` and `core.mgs`;
* whereas we keep all *config* in, well you guessed it, `config`.

A few more words about messaging. The split mentioned above is between the
abstract definition of the messaging functionality we need---the content of
`core.msg`---and the actual implementation in terms of a specific message
oriented middleware, HornetQ at the moment, that sits in `q`. The idea is
that Smuggler should play the messaging game only by the rules (interfaces)
defined in `core.msg` without caring about the actual implementation in `q`.
Accordingly, the only code that depends on `q` is that in `config` as it
needs to tie the interfaces in `core.msg` to the actual implementations in
`q` and make them available through the Spring's IoC container.
To keep our sanity, we decided to do away with both JMS and Spring's own
flavour of it: the code in `q` piggybacks directly on the HornetQ Core API
to implement the various interfaces defined in `core.msg`. Besides these
interfaces, `core.msg` is home to classes that carry out generic messaging
tasks that only depend on the `core.msg` interfaces.

Think it's time we started visualising all these packages and dependencies
so we can actually *see* what was the thinking behind the code structure
breakdown. Here goes.

<div class="diagram" id="package-dependencies" src="package-dependencies.svg">
Top level Java packages and their dependencies.<br/>
UML package diagram.</div>

As noted in the diagram, the bulk of the functionality sits in `core` and is
not meant to have any dependencies on third-party libraries. In fact, if we
need functionality from a third-party library (mail, logging, etc.), we define
what we need abstractly in `core` through service provider interfaces and
then put the actual implementation in `providers`---exactly the same deal
as for `core.msg` and `providers.q`.
This way we can change the underlying frameworks we use without having to
rewrite the whole app from scratch. For example, using a Web server other
than Undertow requires just a few configuration tweaks---picture rubbing
off Undertow from the diagram and follow the arrows to see what would be
affected. Ditching HornetQ would take slightly more work as some of the
code in `q` would need to change. Moving away from Spring? A bit more
involved, but still doable.

There are a few more packages shown in the diagram that we haven't touched
on yet, but looking at them is not essential to get the hang of the code
base. Anyway, let's just mention what they're there for. In `core`, you'll
find `types`, which is where we keep the shared data types we use to shuttle
data across app components; `convert` and `io` contain, respectively, some 
util classes to deal with data format transformation (serialisation, string
to typed value, etc.) and file-system tasks.
Finally, if you're wondering how the Web app starts or how we generate config
files, then you should look at the launchers in `run`.



[booty]: http://projects.spring.io/spring-boot/
  "Spring Boot Home"
[hornetq]: http://hornetq.jboss.org/
  "HornetQ Home"
[reactive]: http://www.reactivemanifesto.org/
  "Reactive Manifesto"  
[spring]: https://spring.io/
  "Spring Home"
[springdoh]: /content/design/index.html#springdoh
  "Spring...D'oh!"  
[springdoh-git]: https://github.com/c0c0n3/spring-doh
  "Spring D'oh Project on GitHub"
[undertow]: http://undertow.io/
  "Undertow Home"  
[whirlwind-tour]: /content/examples/whirlwind-tour.html
  "Whirlwind Tour"  
