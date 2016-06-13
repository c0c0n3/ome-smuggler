---
title: Configuration
slogan: season to taste
---

<p class="intro"> 
There are configuration options you can tweak to make Smuggler suit your
environment. Even though most of the built-in configuration might work for
you out of the box, we suggest you have a quick read through this section. 
</p>

Smuggler's configuration is broken down in groups of configuration items:
HTTP, import, mail, and so on, as you can read below. The items of each
group go into their own configuration file (e.g. `undertow.yml` for HTTP)
and each file can go in a configurable configuration (recursion madness!)
directory or can be embedded directly into Smuggler's jar file, or both.
If a configuration file is both in the configuration directory and inside
the jar, then the file in the configuration directory takes precedence:
the one inside the jar is ignored. If Smuggler doesn't find a file in
either place, then he'll use default values for that configuration group.

<div class="pull-quote">
###### Restart Required!
Every time you change *any* of your (external) configuration files you'll
have to stop and start the server again for the new configuration to take
effect. Ya, I know. It'd be nice to have automatic reload, but I had no
time to implement it. Contributions are welcome...
</div>

There are also three sneaky configuration items that determine the directory
layout used by Smuggler during its operation. They belong in their own group
but there's no corresponding file for it.


Directory Layout
----------------
Smuggler uses three directories during his operation

* *Configuration directory*. Where you can put your configuration. Files in
here override corresponding jar-embedded files (if any) as explained earlier.
* *Data directory*. Where Smuggler keeps each bit of data related to task
processing: persistent queues, import and mail recovery data, etc.
* *Log directory*. Guess what! Smuggler writes what he's up to in here: log
files.

Smuggler doesn't read or write any data outside of these directories. That's
all he needs. All these directories default to the current working directory
of Smuggler's process, but you can easily change them to something else: set
any directory's path with either a Java property on the command line or use
an environment variable. If you specify both, the Java property wins and the
value in the environment is ignored. Here are the property and variable names

* *Configuration directory*. Use `ome.smuggler.ConfigDir` (Java prop) or
`SMUGGLER_CONFIGDIR` (environment).
* *Data directory*. Use `ome.smuggler.DataDir` (Java prop) or `SMUGGLER_DATADIR`
(environment). If the directory you specify doesn't exist, it'll be created.
* *Log directory*. Use `logging.path` (Java prop) or `LOG_PATH` (environment).
(This setting comes from the underlying Spring Boot framework.)
If the directory you specify doesn't exist, it'll be created.

<div class="pull-quote">
###### Security
Smuggler needs to be able to access all the above directories as well as
having read/write permissions for their contents. Besides the sys admin,
only the user you run Smuggler with should have access to these directories.
In fact, OMERO session keys are kept in the HornetQ queue; also, depending
on your set up, the mail configuration may contain an account password.

If the data directory doesn't exist and you want Smuggler to create it for
you, then the Smuggler user must be able to access the parent directory and
write to it. Ditto for the log directory. You may be better off creating
these directories yourself so that Smuggler doesn't need to have any rights
on the corresponding parent directories.

And as you're at it, why not secure Smuggler's jar files as well? Ideally,
you'd make them read-only and accessible to the Smuggler user only. You
could even go a step further and make them immutable, e.g. using something
like `chattr +i`.
</div>


Embedded Configuration
----------------------
You can easily embed your configuration into Smuggler's jar file in two
steps. First, edit any of the files in your local Git repo under

        components/server/src/main/resources/config/

For example `undertow.yml`. Then from the root directory of your local
Git repo run

~~~ {.bash}
$ ./gradlew assemble :packager:release
~~~

(Use `gradlew.bat` on Windows.) The files you've just edited are now embedded
into the server jar file contained in each and every distribution bundle
generated in

        components/packager/build/distributions/

Pick your bundle and deploy it; you won't need to add separate configuration
files to your deployment (e.g. an external `undertow.yml`) as the server will
read your values from the embedded files. If sometime after deployment you
need to tweak your configuration further, you can still add the files you
need to your configuration directory and restart the server; these files
will take precedence over the ones you've embedded, so the configuration
the server will use is that of the files you've put in the configuration
directory.


Generating Configuration Files
------------------------------
While you can put together your own configuration files from scratch, it
may be easier to tweak an existing file. You could pick one from

        components/server/src/main/resources/config/

in your local Git repo. Another option is to generate the file. In fact,
all the files in the above directory were generated using commands similar
to the below (run from the root of your local Git repo; use `gradlew.bat`
on Windows)

~~~ {.bash}
$ ./gradlew assemble
$ java -jar components/server/build/libs/ome-smuggler-*.jar \
       ome.smuggler.run.UndertowYmlGen > undertow.yml
~~~

Each configuration group has its own file generation command: a class in
the `ome.smuggler.run` package named after the configuration group and
suffixed with `Gen`---`ome.smuggler.run.UndertowYmlGen` in the example
above. In turn, each of these commands outputs the configuration items
read from a corresponding class in `ome.smuggler.config.data`, e.g.
`UndertowYmlFile`. (All follow the same naming convention, if you're
wondering: group name + `File`.) So you could actually edit the Java
classes in `ome.smuggler.config.data` to generate your configuration
in a more type-safe way. (The compiler should yell at you if you happen
to specify rubbish values.)


Configuration Groups
--------------------
Here's a summary of the various configuration groups. For each of them
you'll find below a short description and the following bullet-point
info:

* *File*. The name of the group's configuration file.
* *Bean*. Fully qualified name of the Java Bean class that holds the
group's items with a link to the corresponding JavaDoc detailing the
configuration settings.
* *Defaults*. Fully qualified name of the Java class holding the group's
default configuration settings. Look at the code to see how to specify
your own settings in a type-safe way, as explained earlier.
* *Command*. Fully qualified name of the Java class that generates the
group's configuration file. Use it to output a configuration file when
doing the configuration in Java. (The type-safe way, as explained earlier.)


### Http
Smuggler comes with an embedded Undertow HTTP server. The items in this
group specify how to configure it.

* *File*: `undertow.yml`
* *Bean*: [ome.smuggler.config.items.UndertowConfig](/server-javadoc/ome/smuggler/config/items/UndertowConfig.html)
* *Defaults*: `ome.smuggler.config.data.UndertowYmlFile`
* *Command*: `ome.smuggler.run.UndertowYmlGen`


### Import
Options to specify how to handle imports, e.g. retries, import logs
retention period.

* *File*: `import.yml`
* *Bean*: [ome.smuggler.config.items.ImportConfig](/server-javadoc/ome/smuggler/config/items/ImportConfig.html)
* *Defaults*: `ome.smuggler.config.data.ImportYmlFile`
* *Command*: `ome.smuggler.run.ImportYmlGen`


### Mail
Mail settings such as SMTP or SMTPS agent to use, mail sending account,
etc.

* *File*: `mail.yml`
* *Bean*: [ome.smuggler.config.items.MailConfig](/server-javadoc/ome/smuggler/config/items/MailConfig.html)
* *Defaults*: `ome.smuggler.config.data.MailYmlFile`
* *Command*: `ome.smuggler.run.MailYmlGen`


### Spring
Classic Java properties file for Spring and Spring Boot settings.
Besides for the app name and log level, we use this file to enable
[Spring Boot Actuator][booty-actuator] features.

* *File*: `application.properties` (Not easy to follow our naming conventions
here as Spring, by default, expects this file name.)
* *Bean*: N/A. But properties are set using a tiny type-safe DSL, see the
`ome.smuggler.config.items.SpringBootConfigProps` class.
* *Defaults*: `ome.smuggler.config.data.SpringBootAppPropsFile`
* *Command*: `ome.smuggler.run.SpringBootPropsGen`


Other Settings
--------------
There are some other internal configuration settings that are not exposed as
they're mainly useful for development---e.g. using in-memory instead of
persistent message queues. If you're curious, have a look at the classes in
the `ome.smuggler.config.items` package. Also, one thing you'll need to do
if you want to debug the server in your IDE is to specify the location of
the `ome-cli` jar as it defaults to the same directory containing the server
jar---see the `OmeCliConfig` class for the details. To do that, stick an
`ome-cli.yml` file in `components/server/` (or whichever directory your IDE
uses as the current working directory for debugging the server component)
with the following content:

~~~ {.yaml}
omeCliJarPath: ../cli/build/libs/ome-cli-0.1.0.jar
~~~

(You may have to adjust the `ome-cli` version number to match the current one
in `build.gradle`.)


Omero Session Timeout
---------------------
Ah, we've saved the best until last! Smuggler needs an active session to
run an import. The client that requests the import is in charge of creating
it and passing it along in the import request. But here's the issue. OMERO
sessions last, by default, at most ten minutes. This would be plenty of time
for Smuggler to fetch the import request from his work queue and service it
as long as the queue were almost always empty and image files were small.
But in practice it can happen that Smuggler's so busy shovelling tons of
data into OMERO that an import request could sit in the queue for more
than ten minutes. So when it's eventually picked up from the queue, its
session has already expired and the import will fail. Too bad. But wait
a minute! You can easily change the default OMERO session timeout

~~~ {.bash}
$ omero config set omero.sessions.timeout 85000000
~~~

The above tells OMERO to up the session timeout to a bit more than a day.
Also note that clients can create a session with a timeout up to ten times
longer than the current value of `omero.sessions.timeout`. (You do this by
specifying a time-to-idle to the `createUserSession` method.)

<div class="pull-quote">
###### Stopgap Solution!
The plan for the future is to use a session-independent, long-lived token
that OMERO would accept to run a specific import. In fact, we're busy
concocting a solution with the friendly OMElings...
</div>




[booty-actuator]: http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/html/production-ready.html
    "Spring Boot Actuator Reference"
