Whirlwind Tour of Smuggler
==========================

Setting the stage
-----------------
Download or clone from [GitHub](https://github.com/c0c0n3/ome-smuggler).
Then build it (requires Java 8):

    $ cd ome-smuggler/
    $ ./gradlew clean build

Override built-in configuration to delete import status updates after one minute
and to not retry failed imports:

    $ cp src/main/resources/config/import.yml ./
    $ emacs import.yml

Change as below, then save.

    logRetentionMinutes: 1
    retryIntervals: []

(If you want to play around with the import configuration, read [this](https://github.com/c0c0n3/ome-smuggler/blob/master/src/main/java/ome/smuggler/config/items/ImportConfig.java).)
Now we need to bring in the OMERO libraries; Smuggler expects to find them in
the `ome-lib` directory in the current directory. (Though it can be configured
to be wherever you like.) You can copy the `jar` files over from your local
Insight or OMERO `libs` directory, e.g.

    $ mkdir -p ome-lib
    $ cp /opt/OMERO.insight-5.1.2-ice35-b45-linux/libs/*.jar ome-lib/


Running the server
------------------
We're ready to run the server. (Java 8 required.) In the `ome-smuggler` root
directory

    $ java -jar build/libs/ome-smuggler-0.1.0.jar

Keep it running in the foreground so you can see what's going on. In any case,
terminal output is also saved in `ome-smuggler.log` in the current directory.


Making an import fail
---------------------
Why? Because this workflow touches most of the available functionality. First

    $ cd src/test/scripts/http-import/
    $ ls
        delete  get  list-failed-imports  min-import.json  request-import
    $ chmod +x delete get list-failed-imports request-import

These are just convenience scripts using `curl` to interact with Smuggler over
HTTP. So on to requesting an import that will fail

    $ ./request-import min-import.json 

This simply POSTs a JSON-encoded import request to upload a non-existing image
to a non-existing OMERO sever. No wonder it should fail. To see how to build
an import request, look [here](https://github.com/c0c0n3/ome-smuggler/blob/master/src/main/java/ome/smuggler/web/ImportRequest.java).
The response should be a 200 (import request accepted and queued for execution)
and its body should be something like

    {"statusUri":"/ome/import/740f848c-7099-461e-a988-563eecaeccba"}

The absolute path above specifies where to get status updates for the import
you've just requested. Status updates will be available for at least a minute
after the import has been executed---this is the import log retention period
we configured earlier. Copy and paste the returned path and run the `get`
script to get status updates

    $ ./get /ome/import/740f848c-7099-461e-a988-563eecaeccba

You can repeat to poll for updates. Now wait one minute, then try it again

    $ ./get /ome/import/740f848c-7099-461e-a988-563eecaeccba

You should get a fat 404 as we went past the retention period.
Imports are retried at the intervals specified in the import configuration;
but we specified no intervals so no retries will be attempted. As already
mentioned this import should have failed. Smuggler keeps track of failed
imports and can list them:

    $ ./list-failed-imports 

returns the absolute path to each failed import, i.e. where to access the log
to see what went wrong so that the issue can hopefully be resolved. In our case
the response body should look similar to

    ["/ome/failed/import/740f848c-7099-461e-a988-563eecaeccba"]

as we only have one failed import. (If we had more, they would be returned
too.) Copy and paste to get the corresponding failure log:

    $ ./get /ome/failed/import/740f848c-7099-461e-a988-563eecaeccba

Once the issue has been resolved, you should tell Smuggler to stop tracking it:

    $ ./delete /ome/failed/import/740f848c-7099-461e-a988-563eecaeccba

Now if you query the failed imports again,

    $ ./list-failed-imports

you should get back an empty JSON array as there are no failed imports being
tracked, so all you should see in the response body is

    []

TODO
====
$ su omero
Password: 
[omero]$ omero login
Previously logged in to localhost:4064 as root
Server: [localhost:4064]
Username: [root]
Password:
Created session 44ecfba2-9266-422e-87e8-cd3604c64c64 (root@localhost:4064). Idle timeout: 10 min. Current group: system
[omero]$ 

