---
title: Monitoring
slogan: keep an eye on this guy!
---

<p class="intro">
Smuggler comes both with general-purpose and specialised monitoring
interfaces.
</p>

Being a Spring Boot app, Smuggler has a general-purpose (remote) monitoring
facility provided by the [Spring Boot Actuator][actuator] that gives you
access to lots of info about system's and JVM's status as detailed below.
Generally useful stuff, but still tells you nothing about imports that may
have gone haywire. So Smuggler has additional HTTP endpoints that let you
find out about and manage failed tasks.
And of course, like any other self-respecting app, Smuggler always writes
what he's up to in a plain-text, old-school log file. Read on to see what's
available.


Actuator Endpoints
------------------
On start up, Smuggler enables the following Actuator endpoints: `autoconfig`,
`beans`, `configprops`, `dump`, `env`, `health`, `info`, `metrics`, `mappings`,
`shutdown`, `trace`. All of them are available both as HTTP services and
though JMX. (If all this tells you nothing, try eyeballing the Actuator's
[docs][actuator]...) You could whip together simple scripts to query the
HTTP endpoints and hook the output into into your existing monitoring
infrastructure, if you have one. If you don't, you could instead monitor
Smuggler using a JMX client. Or you could roll out something like
[Spring Boot Admin][boot-admin] to aggregate monitoring info from all your
Smuggler instances into a shiny Web dashboard.

<div class="pull-quote">
###### Security
You may want to disable some endpoints (e.g. `shutdown`, `env`) and/or
configure password protected access.
</div>


Smuggler's Own Endpoints
------------------------
If an import fails, Smuggler will retry it a few times (depending on your
[configuration][config]) before giving up. If it's a permanent failure,
the sys admin should get a love letter from Smuggler: an email with the
failed import ID so that someone can try to work out the cause of that
failure. Once the issue has been resolved, the failed import log should
be deleted. (Smuggler only deletes the logs of successful imports, but
keeps around the ones of imports that failed permanently.)
Speaking of emails. The sending of an email may obviously fail too---e.g.
mail server's down. When that happens, Smuggler will attempt to send it
again for a [configurable][config] number of times before giving up and
putting the message in the dead mail box. As for permanently failed imports,
you can see what's inside the dead mail box, resend yourself, and then
delete messages. In fact Smuggler lets you monitor and manage both failed
imports and dead mail over HTTP.

### Failed Imports
To get a list of imports that failed permanently, do a `GET` on
`/ome/failed/import` with an `Accept` header of `application/json`.
For example, using `curl`

~~~ {.bash}
$ curl -H 'Accept: application/json' \
       http://localhost:8000/ome/failed/import
~~~

Smuggler will respond with a JSON array, for example

~~~ {.json}
["/ome/failed/import/740f848c",
 "/ome/failed/import/6ea82ae4"]
~~~

If you get back an empty array (`[]`), then all is well, no import failed.
Otherwise, for each failed import, you'll get a corresponding path in the
returned array---the last element in the path is the import ID, if you're
wondering. Use that path to fetch the failed import's log file and find
out what went wrong with the import. You do this with a plain `GET`, for
example using `curl`

~~~ {.bash}
$ curl \
  http://localhost:8000/ome/failed/import/6ea82ae4
~~~

What you get back in this case is not JSON, but plain text (specifically a
`Content-Type` of `text/plain; charset=UTF-8`). After you've worked out the
issue that caused the import to fail, there's no reason to keep the log file
around anymore. So you should tell Smuggler to get rid of it using the HTTP
`DELETE` method as in the below `curl` command

~~~ {.bash}
$ curl -X DELETE \
  http://localhost:8000/ome/failed/import/6ea82ae4
~~~

If you now ask again Smuggler to list the failed imports,

~~~ {.bash}
$ curl -H 'Accept: application/json' \
       http://localhost:8000/ome/failed/import
~~~

you'll see that the import log you've just `DELETE`d is not in the returned
array anymore

~~~ {.json}
["/ome/failed/import/740f848c"]
~~~

In fact, Smuggler deleted the file from its data directory. Note that if
you don't delete failed logs yourself (after working out the cause of failure
though!), no one else will as Smuggler would happily keep them indefinitely
and return them in the list of failed imports. Besides wasting disk space,
this can be confusing as you may happen to look again at a log file of an
old issue that you worked out ages ago...


### Failed Emails
To get a list of emails that failed to send permanently, do a `GET` on
`/ome/failed/mail` with an `Accept` header of `application/json`.
For example, using `curl`

~~~ {.bash}
$ curl -H 'Accept: application/json' \
       http://localhost:8000/ome/failed/mail
~~~

Smuggler will respond with a JSON array, for example

~~~ {.json}
["/ome/failed/mail/6bb4995d"]
~~~

As you may have guessed already, the failed mail interface works exactly
the same as that for failed imports. So an empty array (`[]`) means all
is well. Otherwise, for each failed email, you'll get a corresponding
path you can use to `GET` the email message as in

~~~ {.bash}
$ curl \
  http://localhost:8000/ome/failed/mail/6bb4995d
~~~

What you get back in this case is a plain text email message you could
pipe straight into a program like `sendmail` to send it again. As for
the imports, after you've sent the message, there's no reason to keep it
around any longer. Tell Smuggler to `DELETE` it then, e.g.

~~~ {.bash}
$ curl -X DELETE \
  http://localhost:8000/ome/failed/mail/6bb4995d
~~~


Smuggler's Logs
---------------
Smuggler uses the logging facility that comes with Spring Boot without
any customisation. Pretty much the run of the mill logging you'd expect
for a Java app. Read about it in the Spring Boot [docs][boot-logging],
if you need to. The log files go in the [configured][config] log directory
and are rotated, so they sort of take care of themselves---i.e. the log
directory won't grow huge over time.

<p class="side-note">
Ideally Smuggler should log to native logging facilities (e.g. Event Logs
on Windows) to make a sys admin's life a bit easier.
On this note, if you're going to deploy Smuggler on many different boxes,
getting hold of logs on each box may become a pain in the neck. I hear
you, but have no time to improve on this at the moment!
</p>




[actuator]: http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/html/production-ready.html
    "Spring Boot Actuator Reference"
[boot-admin]: https://github.com/codecentric/spring-boot-admin
    "Spring Boot Admin"
[boot-logging]: http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/html/boot-features-logging.html
    "Spring Boot Logging"
[config]: configuration.html
    "Configuration"
