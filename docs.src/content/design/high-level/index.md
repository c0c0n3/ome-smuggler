---
title: High-level View
slogan: as seen from the moon
---

<p class="intro">
The basic idea: a Web-based work queue to run tasks on behalf of OMERO
clients.
</p>

Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor
incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis
nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore
eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt
in culpa qui officia deserunt mollit anim id est laborum.

Conceptual Overview
-------------------
You can think of Smuggler as...[wada, wada, wada]
[TODO: a para on tech stack]
[Late night silly thought:
Java, having become the bastion of [abject-oriented programming]
(http://typicalprogrammer.com/abject-oriented/), is now poised to
be the sanctuary of dysfunctional programming. 
]

<div class="diagram" id="components" src="components.svg">
Wiring of Smuggler's components and third-party software.<br/>
UML component diagram.</div>

The note at the bottom of the diagram tells you how the conceptual components
map to Java packages. You can find a lot more about source code breakdown and
dependencies in the next section. But before we dive deep into the source, a
word of caution: the tech sauce is quite heavy to digest so it may be too
much to gulp down in just one go if you're not already pretty familiar with
all the various techs we use. (It proved definitely too much for me, I tell
you my tummy wasn't a happy camper for days afterwards!)
In that case, it's probably best to take it slowly and skill up a bit before
hand (as I did myself!), especially on Java 8 (dys-)functional programming
and streams API.

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


Codebase Essentials
-------------------
The source base is split into two root packages: `ome.smuggler`, containing
the app itself, and `util`, a general-purpose library with code that is totally
independent of Smuggler and reusable across projects. In fact, `util` is part
of the dowry we got when we forked from [Spring D'oh][springdoh-git].
Since then we added a few more `util` classes but we can potentially ditch
the whole lot if there's a better alternative.
In any case, if you realise some of the code you're writing for Smuggler can
be generalised to make it reusable in other projects, well, then you know
where to put it :-)

With that out of the way, let's have a closer look at what's in `ome.smuggler`.
As noted in the diagram above, this is how Smuggler's components map to actual
Java code:

* the *REST controllers* are in the `web` package;
* the *services* are in `core.service`;
* the *messaging* functionality is split between `q` and `core.mgs`;
* whereas we keep all *config* in, well you guessed it, `config`.

A few more words about messaging. The split mentioned above is between the
abstract definition of the messaging functionality we need (what goes in
`core.msg`) and the actual implementation in terms of a specific message
oriented middleware, HornetQ at the moment, that sits in `q`. The idea is
that Smuggler should only play the messaging game as defined in `core.msg`
without caring about the actual implementation in `q`. Accordingly, the
only code that depends on `q` is that in `config` as it needs to tie the
interfaces in `core.msg` to the actual implementations in `q` and make
them available through Spring.
To keep our sanity, we decided to do away with both JMS and Spring's own
flavour of it: the code in `q` piggybacks directly on the HornetQ core API
to implement the various interfaces defined in `core.msg`. Besides these
interfaces, `core.msg`

<div class="diagram" id="package-dependencies" src="package-dependencies.svg">
Top level Java packages and their dependencies.<br/>
UML package diagram.</div>




[springdoh-git]: https://github.com/c0c0n3/spring-doh
  "Spring D'oh Project on GitHub"
