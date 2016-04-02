OME Smuggler
============
Sneakily imports image goodies into OMERO without paying MRI session duties.


Idea
----
The basic idea: a Web-based work queue to run tasks on behalf of OMERO clients.
A task is queued, then run and as it runs a URL is available from which to get
status updates. If the task fails, it may be retried. On completion, an email
report is sent to the interested parties. This is Smuggler’s life purpose—at
least for now. And for now, the only task Smuggler knows how to run is an OMERO
import. But it should be possible to add any other task without too much sweat
using the same run/retry/notify mechanism.


Contributing
------------
Want to hack Smuggler to pieces? Or contribute a couple of tweaks or a bug fix?
You're welcome to fork the repo and submit a pull request.
If you're planning to do open-heart surgery, you may find it useful to read the
developer docs over here:

* [http://c0c0n3.github.io/ome-smuggler/docs/content/](http://c0c0n3.github.io/ome-smuggler/docs/content/)

And by the way, if you're looking for the docs sources, we keep the whole lot
in the `gh-pages` branch:

* [https://github.com/c0c0n3/ome-smuggler/tree/gh-pages](https://github.com/c0c0n3/ome-smuggler/tree/gh-pages)

The docs site is generated from the sources in the same branch so that GitHub
can kindly publish it as a GitHub project site. To find out how, start from
the README in the `gh-pages` branch...


Build & Run...for the loo
-------------------------
Build and test everything:

    ./gradlew build

Use `gradlew <task>` (Unix; `gradlew.bat` for Windows) for finer control over
building, testing, etc. This lists all available build tasks:

    ./gradlew tasks

To run the app just follow the instructions TODO :-)


Tricksy Eclipsie
----------------
Using Eclipse? You can generate all project files with:

    ./gradlew eclipse

then just import the project into Eclipse; alternatively if you have the Gradle 
Eclipse plugin (Buildship), you could instead run: 

    ./gradlew build
 
then import into Eclipse as a Gradle project.

