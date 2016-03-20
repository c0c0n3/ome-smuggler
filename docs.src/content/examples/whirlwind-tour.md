---
title: Whirlwind Tour
slogan: hold tight!
---

<p class="intro">
Meet <em>Smuggler</em> on the run! Build from scratch and then run the server
to smuggle some images into OMERO. There's going to be some <em>Smuggler</em>
action coming your way, so buckle up and take a deep breath. Off we go!
</p>

Setting the Stage
-----------------
Download or clone from [GitHub](https://github.com/c0c0n3/ome-smuggler).
Then build it (requires Java 8):

~~~ {.bash}
$ cd ome-smuggler/
$ ./gradlew clean build
~~~

Override built-in configuration to delete import status updates after one minute
and to not retry failed imports:

~~~ {.bash}
$ cp src/main/resources/config/import.yml ./
$ emacs import.yml
~~~

Change as below, then save.

~~~ {.yaml}
logRetentionMinutes: 1
retryIntervals: []
~~~

(If you want to play around with the import configuration, read [this][import-config].)
Now we need to bring in the OMERO libraries; Smuggler expects to find them in
the `ome-lib` directory in the current directory. (Though it can be configured
to be wherever you like.) You can copy the `jar` files over from your local
Insight or OMERO `libs` directory, e.g.

~~~ {.bash}
$ mkdir -p ome-lib
$ cp /opt/OMERO.insight-5.1.2-ice35-b45-linux/libs/*.jar ome-lib/
~~~

You should configure the sending of email notifications---details [here][mail-config].
For that, you can copy the default configuration file in the current
directory and then tweak it:

~~~ {.bash}
$ cp src/main/resources/config/mail.yml ./
~~~

<p class="side-note">
You can skip the configuration of the email service; in that case all email
messages will fail to send which you don't need to give two hoots about for
the sake of this whirlwind tour. But Smuggler tracks mail failures too and
allows you to recover, if that makes you feel better.
</p>

Running the Server
------------------
We're ready to run the server. (Java 8 required.) In the `ome-smuggler` root
directory

~~~ {.bash}
$ java -jar build/libs/ome-smuggler-0.1.0.jar
~~~

Keep it running in the foreground so you can see what's going on. In any case,
terminal output is also saved in `ome-smuggler.log` in the current directory.

<div class="side-note">
###### Default Port
Smuggler's HTTP port is 8000 by default. You can change this by copying 
`undertow.yml` from `src/main/resources/config/` to the directory from
which you started Smuggler and edit the copied file to change the port
number. Then restart Smuggler.
</div>

Making an Import fail
---------------------
Why? Because this workflow touches most of the available functionality. First

~~~ {.bash}
$ cd src/test/scripts/http-import/
$ ls
    ...
$ chmod +x get delete
$ chmod +x request-import list-failed-imports
$ chmod +x list-failed-mail
~~~

These are just convenience scripts using `curl` to interact with Smuggler over
HTTP. You may have noticed an additional file, `min-import.json`: this is JSON
to request an import; if you configured the mail service, you should replace
the nonsensical email address in there with yours to get love letters from
Smuggler. So on to requesting an import that will fail

~~~ {.bash}
$ ./request-import min-import.json 
~~~
    
This simply POSTs a JSON-encoded import request to upload a non-existing image
to a non-existing OMERO sever. No wonder it should fail. To see how to build
an import request, look [here][import-request].
The response should be a 200 (import request accepted and queued for execution)
and its body should be something like

~~~ {.json}
{"statusUri":"/ome/import/740f848c-7099-461e-a988-563eecaeccba"}
~~~

The absolute path above specifies where to get status updates for the import
you've just requested. Status updates will be available for at least a minute
after the import has been executed---this is the import log retention period
we configured earlier. Copy and paste the returned path and run the `get`
script to fetch status updates

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
assuming you [configured one][mail-config]. If you didn't configured the mail
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
data to an existing OMERO server. Create a new file `my-import.json` taking
`min-import.json` as an example and referring to [this specification][import-request].
You will need a valid session key to go in the file; to obtain one, use the
OMERO CLI as shown below:

~~~ {.bash}
$ omero login
Server: [localhost:4064]
Username: [root] your-user
Password: your-pass
Created session 44ecfba2-9266-422e-87e8-cd3604c64c64   (...)
~~~

Copy and paste the session key into `my-import.json`, then

~~~ {.bash}
$ ./request-import my-import.json
~~~
    
###### Note
Your OMERO session will expire in 10 minutes. Smuggler needs an active session
to run the import; if no other imports are queued, then this is not a problem
as your request will be serviced shortly after being put on the queue. I have
some experimental code to keep the session alive while the import sits on the
queue, but haven't merged the code in.



[import-config]: https://github.com/c0c0n3/ome-smuggler/blob/master/src/main/java/ome/smuggler/config/items/ImportConfig.java
[import-request]: https://github.com/c0c0n3/ome-smuggler/blob/master/src/main/java/ome/smuggler/web/imports/ImportRequest.java
[mail-config]: https://github.com/c0c0n3/ome-smuggler/blob/master/src/main/java/ome/smuggler/config/items/MailConfig.java
