---
title: About Smuggler
slogan: imports images on the sly
---

<p class="intro">
<em>Smuggler</em> is a Web-based work queue to shovel images into
[OMERO][omero]. Quite rough, but one day he'll learn how to interact properly
with OMERO and, with a bit of luck, tackle many other data processing tasks.
</p>

Say what?! Smuggler is a Java Web app to import images on behalf of OMERO
clients.
Using Smuggler's REST API, any program P can POST a request to add an import
to Smuggler's work queue; P can then carry on with its life without having to
wait for the import to complete. If P wants to know what's going on with its
import, it can GET a status update from Smuggler. The catch is that P's process
doesn't have to wait for the image data to trek to OMERO and being imported;
P could even exit or crash (ouch!) but the data would still be imported.
(Hope all these P's won't make you want to go to the loo now.) 
In fact, Smuggler will run the import and if it fails, he'll try again, and
again, until he hopefully manages to shovel all the data into OMERO. Or he'll
eventually give up if its a lost case. (Retry behaviour is configurable.)
In any event, the user who requested the import will be emailed with either
a success message (their image data is safely stored in OMERO) or a failure
alert; this alert is emailed to their sys admin too so that some techie can
work out the issue. (You have an issue? Here's a tissue...)
To help the techies, Smuggler comes with a full-blown management &amp;
monitoring interface, available both over HTTP and JMX.


Where from Here?
----------------

At this stage, we only have rough, mostly incomplete documentation.
For


<p class="pull-quote">
Grumpy Wizards make toxic brew for the Evil Queen and Jack.
Grumpy Wizards make toxic brew for the Evil Queen and Jack.
</p>
<p class="side-note">
Grumpy Wizards make toxic brew for the Evil Queen and Jack.
Grumpy Wizards make toxic brew for the Evil Queen and Jack.
</p>


A Little Bit of History
-----------------------
This project started off as a little extension to OMERO that [MRI][mri]
needed to untie login sessions from OMERO imports and that the [FBI][fbi]
paid for. (Nope, not <em>that</em> FBI, the Feds are supposed to bust
smugglers!) In a nutshell, MRI users import images from acquisition
workstations into OMERO with the Java client and so can't logout until the
client has finished importing—or the client dies and the import with
it. Long-running imports side-effects? Other users can't get access to the
workstation and use the microscope. Also, users are billed for the duration
of their login session, so time <em>is</em> money. The full story is recorded
for posterity in [this document][bg-import-overview]; the intro may still be
worth a quick read to put Smuggler into perspective.


About the Name
--------------
Smuggler. Right. Why? Well, if you've followed along, you know our users have
to pay for the time they're logged on any acquisition workstation and at the
moment OMERO imports happen within a login session.
But we love our users and want to save them some money. So we've come up with
a mechanism to import image goodies into OMERO sneakily, without paying MRI
session duties: we're smuggling data into OMERO!




Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor
incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis
nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore
eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt
in culpa qui officia deserunt mollit anim id est laborum.

<figure id="pic-1">
  <object type="image/svg+xml" data="design/messaging/ideas/1.channel.svg">
  alt text + fallback image?
  </object>
  <figcaption>
Grumpy Wizards make toxic brew for the Evil Queen and Jack. Grumpy Wizards
make toxic brew for the Evil Queen and Jack. Grumpy Wizards...
  </figcaption>
</figure>

Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor
incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis
nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore
eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt
in culpa qui officia deserunt mollit anim id est laborum.

[bg-import-overview]: /pdfs/bg-import.project-overview.pdf
  "OMERO Backgound Import - Project Overview"
[fbi]: http://france-bioimaging.org/
  "FBI Home"
[mri]: http://www.mri.cnrs.fr/
  "MRI Home"
[omero]: http://www.openmicroscopy.org/site/products/omero
  "OMERO Home"
