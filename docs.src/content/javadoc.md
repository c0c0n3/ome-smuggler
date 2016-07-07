---
title: JavaDoc
slogan: freshly squeezed from the code
---

<p class="intro">
So ya, we've written the Java code docs too. See below.
</p>

The bits and pieces that make up Smuggler's Java code base are arranged
in four top-level components each in its own directory under the root
`components` directory. Each of them is a Gradle sub-project and so
we have generated JavaDocs for each one separately. You can find below
the links to each JavaDoc.

* [Server][server]. This component provides a Web-based work queue to
run tasks on behalf of OMERO clients.
* [CLI][cli]. Command line tool to run OMERO library code.
* [JClient][jclient]. Simple Java 7 client to interact with Smuggler.
* [Util][util]. A general-purpose library, totally independent of
Smuggler and reusable across projects.

Keep in mind we mainly documented public methods and interfaces. That
is, *what* functionality is provided. If you want to understand the
*how*---i.e. how a component or a class works behind the scenes with
its private and protected methods---then there's no real substitute
for reading the source.
But we tried real hard to keep the code as clean and modular as possible.
For example, methods rarely have more than ten lines of code in their
bodies and the majority of classes are pretty small, usually well below
one hundred lines of (actual) code. Also, you'll find lots of additional
notes (non-javadoc, just plain text) in the source to explain the tricky
bits.




[cli]: ../../javadoc/cli/index.html
    "CLI Component's JavaDoc"
[jclient]: ../../javadoc/jclient/index.html
    "JClient Component's JavaDoc"
[server]: ../../javadoc/server/index.html
    "Server Component's JavaDoc"
[util]: ../../javadoc/util/index.html
    "Util Component's JavaDoc"
