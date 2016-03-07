---
title: Design
slogan: doodles and giggles
---

<p class="intro">
So here's our *Hitch-Hacker's Guide to the Code*. Even though the answer to
life, the universe and everything has already been "[calculated][_42]", you
may still have a lot of questions about Smuggler's design. Then you might
find some of the answers here...
</p>

High on our list of suggested places to visit is the breezy peak of
[High-level View][hi-level]. From up there, it should be easy to see all of
Smuggler's components at once and take away a conceptual model of what
Smuggler does behind the scenes. There you'll also find a road map for the
source code complete with dependencies and modularity signposts.
On the way down from high-level, you should swing by [Messaging][msg] place
where you can get a feel of the ideas behind messaging in Smuggler as well
as the details of the design.
After that, we suggest hiking to [Process View][proc-view] as from there
you can see Smuggler's dynamics: processes, threads, and all that jazz.

<div class="pull-quote">
###### Security
Hey, you have said nothing about security; also looking at the code, there
seems to be nothing in place?! *Guilty as charged*. At the moment Smuggler
only runs in a secure, highly-trusted computing environment; that is why
we're getting away with murder. But soon we'll have to put some *serious*
thought into security if we want to make Smuggler part of the OMERO family.
</div>

Now before you read about Smuggler's design and check out the code, you
should have a look at the next section on technology. (In most of the
docs we take for granted you're familiar with the components of our tech
stack.)
One last thing: on the way back from your trip in design land, it may be
worth stopping again here to read the [last section](#perspective) to
understand the reasons behind the design and to put it into perspective.


Technology Stack
----------------
The bad news: Smuggler relies on lots of frameworks---[Spring][spring] and
its offspring (love puns!) [Spring Boot][booty], [HornetQ][hornetq], and
[Undertow][undertow]. The good news: its `core` package, where most of
the functionality sits, has no dependencies on any of them. So we have the
good and the bad, what about the ugly? `core` relies heavily on Java 8 and
the new Streams API.

<p class="side-note">
Java, having become the bastion of [abject-orientation][abject-oriented],
is now also poised to be the sanctuary of dysfunctional programming.
But in a twist of fate, Spring came along to simplify Java development!
*Ouch*. But in a twist of fate, Spring Boot came along to simplify Spring
development! *Ouch*. (I suspect we've bumped into a case of non-terminating
recursion...)
</p>

As we're at it, we should also mention the testing frameworks we use.
From unit to integration to end to end testing, our tech sauce is becoming
creamier by the minute: [JUnit][junit] (with theories), [Hamcrest][hamcrest],
[Mockito][mockito], and all the Spring testing goodies such as Mock MVC.

Before we dive deep into the design and the source, a word of caution: the
tech sauce is quite heavy to digest so it may be too much to gulp down in
just one go if you're not already pretty familiar with the various frameworks
we use. (It proved definitely too much for me, I tell you my tummy wasn't a
happy camper for days afterwards!)
In that case, it's probably best to take it slowly and skill up a bit before
hand (as I did myself!), especially on Java 8 (dys-)functional programming
and Streams API.

<div class="side-note">
###### Spring...D'oh!
Before starting developing Smuggler, we built a prototype app to learn about
and experiment with the various (many!) bits and pieces that make up our
current technology stack. Aptly named "Spring...D'oh", this initial prototype
has basically no app logic but pretty much the same architecture and exactly
the same technology stack as Smuggler; we actually used its source base as a
blueprint for Smuggler's.
If you're not familiar with Java 8, Spring, Messaging, and all the other
techs in Smuggler, "Spring...D'oh" may be a good starting point to get to
grips with all this madness as it should be easier to see how the pieces
of the puzzle all fall into place. At least it helped me...
You can find the [project on GitHub][springdoh-git].
</div>


Perspective
-----------
What kind of sick mind does it take to come up with a whole Web app to run
an OMERO import outside of a user session when a `cron` job and an OMERO
script could have been (*almost*) enough? Ya, but hold your fire. (Well, at
least until I get out of the way!) Thing is, we're planning to use Smuggler
for a whole bunch of tasks where a message-based architecture seems a good
fit.

<p class="pull-quote">
Smuggler is in a sense an exploration of a [reactive systems][reactive]
architecture in the context of the OMERO ecosystem and integration of
heterogeneous software. Ours is just *one* architecture, others are
possible too and eventually we may give them a shot.
</p>

To find out more about our plans, have a quick read through the [Condensed
Use Cases][use-cases]. We think the functionality we're planning to add
sort of justifies the kind of architecture we put in place, but this is
obviously up for debate...




[_42]: https://en.wikipedia.org/wiki/42_(number)#Hitchhiker.27s_Guide_to_the_Galaxy
  "The Answer to the Ultimate Question of Life, the Universe, and Everything"
[abject-oriented]: http://typicalprogrammer.com/abject-oriented/
  "Introduction to Abject-Oriented Programming"
[booty]: http://projects.spring.io/spring-boot/
  "Spring Boot Home"
[hi-level]: high-level/index.html
  "High-level View"
[hamcrest]: http://hamcrest.org/
  "Hamcrest Home"
[hornetq]: http://hornetq.jboss.org/
  "HornetQ Home"
[junit]: http://junit.org/
  "JUnit Home"
[mockito]: http://mockito.org/
  "Mockito Home"
[msg]: messaging/index.html
  "Messaging"
[proc-view]: high-level/process-view.html
  "Process View"
[reactive]: http://www.reactivemanifesto.org/
  "Reactive Manifesto"
[spring]: https://spring.io/
  "Spring Home"
[springdoh-git]: https://github.com/c0c0n3/spring-doh
  "Spring D'oh Project on GitHub"
[undertow]: http://undertow.io/
  "Undertow Home"
[use-cases]: /content/use-cases/index.html
  "Condensed Use Cases"
