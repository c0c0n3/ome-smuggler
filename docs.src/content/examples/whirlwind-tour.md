---
title: Whirlwind Tour
slogan: hold tight!
---

<p class="intro">
Meet *Smuggler* on the run! Build from scratch and then run the server
to smuggle some images into OMERO. There's going to be some *Smuggler*
action coming your way, so buckle up and take a deep breath. Off we go!
</p>


Setting the Stage
-----------------
Download or clone from [GitHub](https://github.com/c0c0n3/ome-smuggler).
Then do a full build and package the distributions (requires the Java 8
JDK):

~~~ {.bash}
$ cd ome-smuggler/
$ ./gradlew build :packager:release
~~~

Ya, that works for Unix-like OS's (e.g. OS X, Linux); in the unfortunate event
you're on Windows, replace `./gradlew` with `gradlew.bat`. If the build was
successful, you should see the distribution bundles in the `packager`'s own
build directory

~~~ {.bash}
$ ls components/packager/build/distributions/
ome-smuggler-0.1.0-beta.tgz  ome-smuggler-0.1.0-beta.zip
...
~~~

The version numbers may be different by the time you read this, but you
probably knew that already. Anyway, you'll see there are several files
in there. That's because we have generic and platform-specific bundles.
We're going to use the generic bundle listed above; extract it somewhere

~~~ {.bash}
$ tar xzf com*/p*/b*/dist*/ome-smuggler-0.1.0-beta.tgz -C /tmp
~~~

On Windows you could use the zip file instead. The bundle's contents
will be extracted in an `ome-smuggler` directory. If you want to find
out more about distros and deployment look over [here][deployment].
In the meantime, to make our life easier, we're going to lift some
test scripts from the source base and stash them into the extracted
directory.

~~~ {.bash}
$ cp -r components/server/src/test/scripts/http-import \
        /tmp/ome-smuggler/
$ cd /tmp/ome-smuggler/http-import/
$ ls
    ...
$ chmod +x get delete
$ chmod +x request-import list-failed-imports
$ chmod +x list-failed-mail
~~~

These are just convenience scripts using `curl` to interact with Smuggler
over HTTP. You may have noticed an additional file, `min-import.json`:
this is JSON to request an import; if you're going to configure the mail
service---see below---you should replace the nonsensical email address
in there with yours to get love letters from Smuggler.


Configuration
-------------
We're going to tweak Smuggler to delete import status updates after one
minute and to not retry failed imports.

~~~ {.bash}
$ cd /tmp/ome-smuggler
$ java -jar lib/ome-smuggler-*.jar \
       ome.smuggler.run.ImportYmlGen > config/import.yml
$ emacs config/import.yml
~~~

Change as below, then save.

~~~ {.yaml}
logRetentionMinutes: 1
retryIntervals: []
~~~

If you want to play around with the import options or find out more about
how to generate configuration files, read the [configuration][config]
section. But you may want to configure the sending of email notifications.
For that, you can also generate the configuration file and then tweak it
as explained in the [configuration][config] section.

~~~ {.bash}
$ java -jar lib/ome-smuggler-*.jar \
       ome.smuggler.run.MailYmlGen > config/mail.yml
$ emacs config/mail.yml
~~~

<p class="side-note">
You can skip the configuration of the email service; in that case all email
messages will fail to send which you don't need to give two hoots about for
the sake of this whirlwind tour. But Smuggler tracks mail failures too and
allows you to recover, if that makes you feel better.
</p>

Smuggler's HTTP port is 8000 by default. If another server has taken that
port already, you'll have to change Smuggler's. That's easy too.

~~~ {.bash}
$ java -jar lib/ome-smuggler-*.jar \
       ome.smuggler.run.UndertowYmlGen > config/undertow.yml
$ emacs config/undertow.yml
~~~

Just change the port number to something else than 8000, then save.


Running the Server
------------------
We're ready to run the server. (Java 8 JRE required.) Open a terminal and
go to the directory where you extracted the distribution bundle. You'll
find Unix and Windows start up scripts in the `bin` directory; run the
one for your platform. For example on Linux or OS X:

~~~ {.bash}
$ cd /tmp/ome-smuggler
$ bin/run.sh
~~~

Keep it running in the foreground so you can see what's going on. (Terminal
output is also saved in `log/spring.log`.) At the end of this whirlwind tour
you'll want to shut the server down; just hit `Ctrl+c`.


Making an Import fail
---------------------
Why? Because this workflow touches most of the available functionality.
So on to requesting an import that will fail! Open up another terminal
and go to the directory where we saved the test scripts earlier so we
can request an import:

~~~ {.bash}
$ cd /tmp/ome-smuggler/http-import/
$ ./request-import min-import.json 
~~~
    
This simply POSTs a JSON-encoded import request to upload a non-existing image
(`my/file`) to a non-existing OMERO sever. No wonder it should fail.
The response should be a 200 (import request accepted and queued for execution)
and its body should be something like

~~~ {.json}
[{
 "statusUri":"/ome/import/740f848c-7099-461e-a988-563eecaeccba",
 "targetUri":"file:///abs/path/to/my/file"
 }]
~~~

<div class="pull-quote">
###### Import Interface
Want to dig deeper? [This javadoc][import-controller] explains how the REST
import interface works. You'll see that Smuggler lets you POST more than one
one [import request][import-request] at the same time and gives you back an
[import response][import-response] for each request you POST'ed. But here
we're keeping things simple: we only POST one import and so get back one
import response.
</div>

The absolute path of the `statusUri` above specifies where to get status
updates for the import you've just requested. Status updates will be available
for at least one minute after the import has been executed---this is the
import log retention period we configured earlier. Copy and paste the returned
path and run the `get` script to fetch status updates

~~~ {.bash}
$ ./get /ome/import/740f848c-7099-461e-a988-563eecaeccba
~~~

You can repeat to poll for updates. Now wait one minute, then try it again

~~~ {.bash}
$ ./get /ome/import/740f848c-7099-461e-a988-563eecaeccba
~~~

You should get a fat 404 as we went past the retention period.
Imports are retried at the intervals specified in the import configuration;
but we specified no intervals so no retries will be attempted. As already
mentioned this import should have failed. Smuggler keeps track of failed
imports and can list them,

~~~ {.bash}
$ ./list-failed-imports 
~~~
    
returns the absolute path to each failed import, i.e. where to access the log
to see what went wrong so that the issue can hopefully be resolved. In our case
the response body should look similar to

~~~ {.json}
["/ome/failed/import/740f848c-7099-461e-a988-563eecaeccba"]
~~~
    
as we only have one failed import. (If we had more, they would be returned
too.) Copy and paste to get the corresponding failure log:

~~~ {.bash}
$ ./get /ome/failed/import/740f848c-7099-461e-a988-563eecaeccba
~~~

Once the issue has been resolved, you should tell Smuggler to stop tracking it:

~~~ {.bash}
$ ./delete /ome/failed/import/740f848c-7099-461e-a988-563eecaeccba
~~~

Now if you query the failed imports again,

~~~ {.bash}
$ ./list-failed-imports
~~~
    
you should get back an empty JSON array as there are no failed imports being
tracked, so all you should see in the response body is

~~~ {.json}
[]
~~~

On failure, Smuggler sends a notification email to both the user who requested
the import (see contents of `min-import.json`) and the system administrator---
assuming you [configured one][config]. If you didn't configure the mail
service earlier, then the sending of emails will fail, after a couple of days
of trying though---as per default configuration. On giving up sending, Smuggler
stores the failed email messages and lets you manage them through its REST API
just like we've done for failed imports. Use the `list-failed-mail` script to
list them, and then the `get` and `delete` scripts; the messages are stored in
MIME format so they can be piped directly into a program such as `sendmail` to
send them off.


Success Scenario
----------------
All you need to make an image trek into OMERO is to POST a request for existing
data to an existing OMERO server. (Uh, kinda obvious?)
Create a new file `my-import.json` taking `min-import.json` as an example and
fill out the required fields for an import request as documented by [this
specification][import-request]. (Oh, if you're looking for the Web method spec,
it's [here][import-controller].)
You will need a valid session key to go in the file; to get one, you can use 
the OMERO CLI we all know and love (command: `omero login`) or, if you fancy 
more Smuggler action, use Smuggler's very own [session service][session-controller] 
as explained in the section below. Either way, once you have a session key, 
copy and paste it into `my-import.json`, then

~~~ {.bash}
$ ./request-import my-import.json
~~~

Use the returned URL to poll Smuggler for status updates as we've done
earlier. After Smuggler has shovelled your data into OMERO, you should
log into OMERO (with the same account you've used above to create the
session key) and check the images you've just imported are there.

### Omero Sessions
Smuggler has a Web method you can use to open OMERO sessions. (REST API
documented [here][session-controller].) The functionality is similar to
that of the OMERO CLI but Smuggler can keep your freshly minted session
alive for a period of time you specify. Yup, you could also use Smuggler
as a session keep-alive server! (Handy for running OMERO background tasks
like, uh, well, you know, an import?)

For an example of how to call this session Web method, look at the
scripts in this source directory:

~~~ {.bash}
$ ls components/server/src/test/scripts/http-omero
~~~

### Importing Remote Files
The [import request spec][import-request-uri] says you can also import
remote files. Uh?! Yip, that's right. A client can sit on a different box
than Smuggler's and still POST an import request for a file on that box.
In fact, this comes in handy when you want to have a single Smuggler instance
pull image data from multiple acquisition workstations through a distributed
file system such as NFS or Samba. If you want to go down that road, besides
setting up mount points and permissions, you'll also have to tell Smuggler
how to [map remote paths to local mount points][mount-points].




[config]: /content/deployment/configuration.html
    "Configuration"
[deployment]: /content/deployment/index.html
    "Deployment"
[import-controller]: ../../../javadoc/server/ome/smuggler/web/imports/ImportController.html
    "ImportController Class"
[import-request]: ../../../javadoc/server/ome/smuggler/web/imports/ImportRequest.html
    "ImportRequest Class"
[import-request-uri]: ../../../javadoc/server/ome/smuggler/web/imports/ImportRequest.html#targetUri
    "ImportRequest Class - target URI"
[import-response]: ../../../javadoc/server/ome/smuggler/web/imports/ImportResponse.html
    "ImportResponse Class"
[mount-points]: /content/deployment/configuration.html#mount-points
    "Configuration - Mount Points"
[session-controller]: ../../../javadoc/server/ome/smuggler/web/omero/SessionController.html
    "SessionController Class"
    
