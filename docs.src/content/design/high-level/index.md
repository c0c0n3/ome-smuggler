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

<div class="diagram" id="components" src="components.svg">
Wiring of Smuggler's components and third-party software.<br/>
UML component diagram.</div>

The note at the bottom of the diagram tells you how the conceptual components
map to Java packages. You can find a lot more about source code breakdown and
dependencies in the next section. Before diving deep in the source...
[
TODO mention not easy to follow what's going on in the code if you're not familiar
with the various techs: Java 8, Spring, Messaging, etc. So you could use
Spring D'Oh to get to grips with all this madness; it helped me, it may help
you too...
]


Codebase Essentials
-------------------
The source base is split into two root packages: `ome.smuggler`, containing
the app itself, and `util`, a general-purpose library with code that is totally
independent of Smuggler and reusable across projects. In fact, `util` is the
dowry we got when we forked from [Spring D'oh][springdoh-git]. We then added
a few more classes but we can potentially ditch the whole lot the if there is
a better alternative.
In any case, if you realise some of the code you're writing for Smuggler can
be generalised to make it reusable in other projects, then you know where to
put it :-)

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
