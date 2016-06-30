---
title: Linux Daemon
slogan: kinda scary!
---

<p class="intro">
No, not an evil spirit. Rather a wicked "fully executable" jar to run
Smuggler as a Linux service.
</p>

This distro comes with a Spring Boot "fully executable" jar you can use to
run Smuggler either as a System V (`init`) daemon or `systemd` service.
This executable jar is just the Smuggler self-contained jar prepended with
a startup script. No black magic.


Service Account
---------------
Decide what account to use to run Smuggler. Ideally, you should use an
unprivileged user with no shell access. For example the command below
creates both a group named `smuggler` and a user with the same name inside
this new group while making sure the user has no shell access.

~~~ {.bash}
$ sudo useradd -r -s /usr/bin/nologin smuggler -U
~~~

Of course, YMMV.


Unpacking
---------
Now unpack the distro tarball. You could extract the contents to `/opt`,
for example

~~~ {.bash}
$ sudo tar xzf ome-smuggler-linux-daemon-*tgz -C /opt
~~~

or to any other directory as appropriate for your system. Either way,
you should now see an `ome-smuggler` directory with the following in
it

* `ome-smuggler-*.exe.jar`: the Spring Boot executable jar.
* `ome-smuggler-*.exe.conf`: Spring Boot configuration file for the startup
script embedded into the executable jar.
* `ome-cli-*.jar`: the app Smuggler uses to run imports.
* `LICENSE.md`, `README.md`: can you guess what these are yet?

You need to make your service user/group own this `ome-smuggler` directory.
For example, if you have a user and group both named `smuggler`

~~~ {.bash}
$ cd /opt
$ sudo chown smuggler:smuggler ome-smuggler
~~~

And as we're at it, let's raise the security bar even more:

~~~ {.bash}
$ sudo chmod 500 ome-smuggler/ome-smuggler-*.exe.jar
$ sudo chmod 400 ome-smuggler/ome-cli-*.jar
$ sudo chattr +i ome-smuggler/*.jar
~~~

Over and above these basic things, you should also do whatever additional
hardening advised by your system security guidelines.


Minimal Configuration
---------------------
Smuggler uses three directories during his operation: one for configuration,
one for data related to task processing, and one for logs. (Details in the
[configuration][config] section.) The Linux daemon is pre-configured to
create a data and log directory inside `ome-smuggler`---i.e. the directory
you unpacked earlier---and to read your configuration from a `config`
directory also inside `ome-smuggler`. The horror! But you can make the
daemon follow Unix conventions in below a minute: just edit
`ome-smuggler-*.exe.conf` to specify your directory layout. For example,
a decent arrangement could be:

* `/etc/opt/ome-smuggler.d` for your configuration directory;
* `/var/opt/ome-smuggler` for data; and
* `/var/log/ome-smuggler` for the logs.

Smuggler will try to start an HTTP server on port `8000`. If that doesn't
suit you, stick an `undertow.yml` file in your configuration directory with
this line in it:

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
just provide a `mail.yml` file as explained in the [configuration][config]
section.


Daemon Installation
-------------------
At this point you're ready to hook the fully executable jar into `init.d`
or `systemd`. The [installation instructions][boot-daemon] in the Spring
Boot manual explain how to do that.




[boot-daemon]: http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/html/deployment-install.html
    "Installing Spring Boot Applications"
[config]: configuration.html
    "Configuration"
