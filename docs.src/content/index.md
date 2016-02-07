---
title: About Smuggler
slogan: imports images on the sly
---

<p class="intro">
<em>Smuggler</em> is a Web-based work queue to shovel images into
[OMERO][omero]. Still rough but coming alive: soon he'll learn how to interact
properly with OMERO and, with a bit of luck, tackle many other data processing
tasks.
</p>

Say what?! Smuggler is a Java Web app to import images on behalf of OMERO
clients.
Using Smuggler's REST API, any program P can POST a request to add an import
task to Smuggler's work queue; P can then carry on with its life without having
to wait for the import to complete. If P wants to know what's going on with its
import, it can GET a status update from Smuggler. The catch is that P's process
doesn't have to wait for the image data to trek to OMERO and being imported;
P could even exit or crash (ouch!) but the data would still be imported.
(Hope all these P's won't make you want to go to the loo now.) 
In fact, Smuggler will run the import and if it fails, he'll try again, and
again, until he hopefully manages to shovel all the data into OMERO. Or he'll
eventually give up if it's a lost cause. (Retry behaviour is configurable.)
Come hell or high water, Smuggler sends an email to the user who requested
the import to fill them in on the outcome: either a success message (their
image data is safely stored in OMERO) or a failure alert; the sys admin gets
this email alert too so that some techie can work out the issue.
(You have an issue? Here's a tissue...)
To help the techies, Smuggler comes with a full-blown management &amp;
monitoring interface, available both over HTTP and JMX.

<p class="pull-quote">
Oh, I should mention this site is intended as a <em>programmer's notebook</em>,
hence the relaxed and colloquial style of writing.
Unless you're a programmer interested in joining development, you're unlikely
to find anything interesting here. Rather check out the [OME Web site][ome].
Or maybe you're just a bot prowling the interwebs...then go ahead and knock
yourself out!
</p>


Hacking
-------
Come hack with us! Smuggler is open source and [hosted on GitHub][smuggler-git]
so feel free to open a pull request if there's functionality you'd like to
add, something you'd do differently, or you've found a bug. Have ideas or
suggestions? Let them come our way! And naturally, constructive criticism
is always appreciated.

Here's a possible way to get started with Smuggler.

1. Fork the [repo on GitHub][smuggler-git].
2. Read the README (excuse the pun!) and do a full build. Did the tests pass?
Yes? Phew! Move on. No? Well, lucky you, you've just found something to help
us with! 
3. See Smuggler in action. Run the server and go through failure and success
scenarios as explained in [this example][whirlwind-tour].
4. Head over to the [design section][design] to get a feel of the lay of the
the land. As you do that, keep the source code handy so it's easier to map
design concepts and ideas to actual code.

Most of the code is commented, but it's probably easier to chat directly with
us if something is not immediately obvious as, admittedly, it's not always
easy to figure out what the heck was buzzing in someone else's skull when
they wrote the code! If, after testing the waters, you feel like you'd like
to get your hands dirty, here's a [TODO list][smuggler-todo] you can look at.
Finally, you may want to have a look at our [Condensed Use Cases][use-cases]
to get an idea of what were our initial requirements and what we're planning
to do next.


About the Name
--------------
<em>Smuggler</em>. Really?! Why?! Well, if you've followed along, you know our
users have to pay for the time they're logged on any acquisition workstation;
if an OMERO import happens within a login session, you pay for the time it
takes to run the import.
But we love our users and want to save them some money. So we've come up with
this mechanism to import image goodies into OMERO sneakily, outside of login
sessions, without paying MRI session duties...we're <em>smuggling</em> data
into OMERO!

<p class="side-note">
<em>Smuggler</em> is obviously just a codename. We'll come up with a proper,
dignified product name when we release. How about <em>OMERO.Smuggler</em>?
Uh? No? <em>OMERO.Runner</em>? Okay, fine, got it, don't shout...
</p>




[design]: design/index.html
  "Design"
[ome]: http://www.openmicroscopy.org/
  "OME Home"
[omero]: http://www.openmicroscopy.org/site/products/omero
  "OMERO Home"
[smuggler-git]: https://github.com/c0c0n3/ome-smuggler
  "Smuggler on GitHub"
[smuggler-todo]: https://github.com/c0c0n3/ome-smuggler/blob/master/TODO.md
  "Smuggler's TODO List"  
[use-cases]: /content/use-cases/index.html
  "Condensed Use Cases"
[whirlwind-tour]: examples/whirlwind-tour.html
  "Whirlwind Tour of Smuggler"
