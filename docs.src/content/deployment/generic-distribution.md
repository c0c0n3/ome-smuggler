---
title: Generic Distribution
slogan: no frills, just thrills!
---

<p class="intro">
Unpack & run distribution. Should just work out of the box, but you may want
to configure a couple of things for an even smoother experience...
</p>

First decide what account to use to run Smuggler. Then being logged on with
that account's user, extract the bundle's contents to some directory. You
should now see an `ome-smuggler` directory with all the goodies in it. To
run the server (Unix)

~~~ {.bash}
$ cd ome-smuggler
$ bin/run.sh
~~~

On Windows use `bin\run.bat` instead. Either way, the server will run in your
terminal, in the foreground. Use `Ctrl+c` to shut it down. That's pretty much
it. But read on for some minimal configuration tweaks you may need if there's
an HTTP port conflict (Smuggler won't start in that case) or you have no mail
service available on the host.


Directory Layout
----------------
The `ome-smuggler` directory you unpacked contains the following sub-directories

* `bin`: scripts to run the server.
* `config`: where to put your configuration files; initially empty.
* `data`: files Smuggler generates as part of his operation; initially empty.
* `lib`: the entirety of Java byte-code that makes up the Smuggler app.
* `log`: where Smuggler writes log files; initially empty.

You'll also notice a fat license file and a README in `ome-smuggler`, but you
know what that stuff is, right?

Note that this distribution packages Smuggler as a self-contained app with
everything in the `ome-smuggler` directory: binaries, libraries, configuration,
and any data that Smuggler will produce while running.


Minimal Configuration
---------------------
Smuggler will try to start an HTTP server on port `8000`. If that doesn't suit
you, stick an `undertow.yml` file in the `config` directory with this line in
it:

~~~ {.yaml}
port: 8000
~~~

but replace `8000` with the port number you want.

<div class="pull-quote">
###### Restart Required!
Every time you change *any* of your configuration files you'll have to stop
and start the server again for the new configuration to take effect.
</div>

As for the mail configuration, Smuggler expects an SMTP service to be available
on port `25` on `localhost`. If this is not the case, email notifications won't
go out (obviously!) but for the rest Smuggler will still work as advertised.
Anyway, you can easily override the default mail configuration with your own:
just provide a `config/mail.yml` file as explained in the [configuration][config]
section.


Hacking
-------
You could use this distribution as a starting point to hack together something
that works better on your platform. In fact, all you need to run Smuggler are
the two jar files in the `lib` directory; starting the server can be as easy as

~~~ {.bash}
$ java -jar lib/ome-smuggler-*.jar
~~~

but with this bare-bones command Smuggler will write logs and data to the
current working directory. The easiest remedy to that is to specify where
you want that stuff to go using Java properties, e.g.

~~~ {.bash}
$ java -Dome.smuggler.ConfigDir=smugs-config \
       -Dome.smuggler.DataDir=smugs-data     \
       -Dlogging.path=smugs-logs             \
       -jar lib/ome-smuggler-*.jar
~~~

This is in fact exactly what the `bin/run.*` scripts do. Alternatively, you
could use environment variables---details in the [configuration][config]
section. Hack on!




[config]: configuration.html
    "Configuration"
