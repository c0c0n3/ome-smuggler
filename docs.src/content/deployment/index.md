---
title: Deployment
slogan: where the rubber meets the road!
---

<p class="intro">
Even though Smuggler is still a toddler, he can safely run OMERO imports
without tripping! Thrill-seekers wanting to make this happen can have a
look at the deployment instructions below.
</p>

Smuggler accepts HTTP requests to run OMERO imports and carries them out in
the background, asynchronously in a server process. You can run the server
as standalone on any OS, as a service on Windows, or as a daemon on Linux.
For each of these options we have a corresponding distribution bundle but we
haven't set up yet a repository with binary distributions so you'll have to
build the bundles from source and then install them on your target machine.

<div class="pull-quote">
###### Build Requirements
You'll need to have the Java 8 JDK installed on the box where you build.

###### Runtime Requirements
To run the server on your target machine you'll only need the Java 8 Runtime
(JRE), not the whole JDK.
</div>

If you haven't done it yet, head over to our [GitHub repo][smugs-github] and
clone it to your build machine or just download the repo's master branch as
a ZIP file.


Building the Distribution Bundles
---------------------------------
Once you have the Smuggler's repo on your build machine, start a terminal in
the repo's root directory. Then run the below command to do a full build and
package the distributions:

~~~ {.bash}
$ ./gradlew build :packager:release
~~~

Ya, that works for Unix-like OS's (e.g. OS X, Linux); in the unfortunate event
you're on Windows, replace `./gradlew` with `gradlew.bat`. If the build was
successful, you should see the distribution bundles in the `packager`'s own
build directory

~~~ {.bash}
$ ls components/packager/build/distributions/
ome-smuggler-0.1.0-beta.tgz  ome-smuggler-0.1.0-beta.zip
ome-smuggler-linux-daemon-0.1.0-beta.tgz
ome-smuggler-winsvc-0.1.0-beta.zip
~~~

The version numbers may be different by the time you read this, but you
probably knew that already.
The two files on the first output line both contain the generic standalone
server, on the second line you can see the Linux daemon tarball, whereas
the Windows service zip is on the third line. Pick your poison!


Installing & Running Smuggler
-----------------------------
What distribution to use? Well, they all come with the same stuffing but
each has its own dressing. (Nope, I'm not applying for *MasterChef*.)
In fact, the server is exactly the same for all distributions but each of
them comes with configuration tweaks and scripts to make it fit for its
purpose:

* [Generic Distribution][generic]. Unpack & run on any OS. Perfect if you
just want to try Smuggler out. You'll need a login session to run the server
though. You could whip together your own wrapper scripts to make it run as a
service on your platform but it may be easier to use one of the distributions
below instead.
* [Linux Daemon][linux-daemon]. Lets you run Smuggler either as a System V
(`init`) daemon or `systemd` service on Linux.
* [Windows Service][winsvc]. A native wrapper to run Smuggler as a Windows
service. Comes with a CLI installer.
* *DIY*. None of the above doable? Well, you could roll up your sleeves
and try [customising][generic-hacking] the generic distribution. (It's
actually not as hard as it sounds!)

Once you've decided what distribution to use, click on the corresponding
link above to find out about distribution-specific installation and launch
instructions. Additionally, you may want to have a read through the
[configuration][config] section to see how you can tweak Smuggler to your
liking, Sir. Oh, and as you're at it, why not read the section about server
[monitoring][monitoring] too? Well, just in case you care about ensuring
Smuggler always runs smoothly.
As the French say, *bon courage!* (That's "good luck" I think, literally
"good courage".)




[config]: configuration.html
    "Configuration"
[generic]: generic-distribution.html
    "Generic Distribution"
[generic-hacking]: generic-distribution.html#hacking
    "Generic Distribution - Hacking"
[linux-daemon]: linux-daemon.html
    "Linux Daemon"
[monitoring]: monitoring.html
    "Monitoring"
[smugs-github]: https://github.com/c0c0n3/ome-smuggler
    "Smuggler on GitHub"
[winsvc]: windows-service.html
    "Windows Service"
