---
title: Deployment
slogan: where the rubber meets the road!
---

<p class="intro">
Even though *Smuggler* is still a toddler, he can safely run OMERO imports
without tripping! Thrill-seekers wanting to make this happen can have a
look at the deployment instructions below.
</p>

*Smuggler* accepts HTTP requests to run OMERO imports and carries them
out in the background, asynchronously in a server process. You can run the
server as standalone on any OS, as a service on Windows, or as a daemon on
Linux. For each of these options we have a corresponding distribution bundle
but we haven't set up yet a repository with binary distributions so you'll
have to build the bundles from source and then install them on your target
machine.

<div class="pull-quote">
###### Build Requirements
You'll need to have the Java JDK 8 installed on the box where you build.

###### Runtime Requirements
To run the server on your target machine you'll only need the Java 8 Runtime
(JRE), not the whole JDK.
</div>

If you haven't done it yet, head over to our [GitHub repo][smugs-github] and
clone it to your build machine or just download the repo as a ZIP file.

-- note: can change built-in config: server/src/... and /buildSrc/...



[smugs-github]: https://github.com/c0c0n3/ome-smuggler
    "Smuggler on GitHub"
