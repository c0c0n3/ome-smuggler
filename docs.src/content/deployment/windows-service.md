---
title: Windows Service
slogan: easy peasy!
---

<p class="intro">
A native wrapper to run Smuggler as a Windows service. Makes it easier to
install, run and manage Smuggler on Windows.
</p>

So we've got a .NET program (a customised [winsw][winsw]) to control Smuggler
on Windows---just in case you felt things were getting out of control. It has
no GUI, but it comes with the usual commands you'd expect (start, stop, etc.)
as well as commands to (un-)install Smuggler as a service. So you can run
Smuggler as a Windows service and manage it using existing admin tools (e.g.
Services GUI, `net` command) but you still have an option to run straight
from the command line if for some reason you need to do that.


Directory Layout
----------------
Unzip the distribution bundle. You should see an `ome-smuggler` directory
with license and README files as well as the following sub-directories

* `bin`: contains the Windows service wrapper (`ome-smuggler.exe`) along
with the wrapper's own configuration files. There's also [winice][winice]
(`nice.exe`) in here; you can use it to run imports with a Windows scheduling
priority---[see below][advanced-config-section].
* `config`: where to put your configuration files; initially empty.
* `data`: files Smuggler generates as part of his operation; initially empty.
* `lib`: the entirety of Java byte-code that makes up the Smuggler app.
* `log`: where Smuggler writes log files; initially empty.

The wrapper will write some files to the `bin` directory: its own log file,
a file with Smuggler's redirected `stdout` and one for Smuggler's `stderr`.
These files really belong in the `log` directory, but I didn't have time to
change this. (But I welcome any contribution if you want to change that!)
The wrapper will also log start up and shutdown events to the Windows event
log---under "Windows Logs/Application" with a source name of "smuggler".
Ideally all the info that can be useful to sys admins should also go in the
Windows event log, but again I had no time for this. (Contributions are most
welcome though!)

Note that this distribution packages Smuggler as a self-contained app with
everything in the `ome-smuggler` directory: binaries, libraries, configuration,
and any data that Smuggler will produce while running. You can easily change
this though, just read on.


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


Advanced Configuration
----------------------
You have many more options to tweak the way Smuggler works, just have a read
through the [configuration][config] section to see what's available. Over and
above that, there are some configuration settings specific to the Windows
service wrapper that you can change by editing `bin\ome-smuggler.xml` and
that we list below.

###### Java Command
The wrapper launches Smuggler using the `java` command in the system `PATH`.
You can change this by editing the `executable` tag.

###### Directory Layout
If you want to change the location of any of the data, log or configuration
directories, edit the corresponding Java property in the `arguments` tag.
Alternatively, you could delete the Java property and use an environment
variable instead---details in the [configuration][config] section.

###### Service Priority
The Smuggler service runs with a process priority class of "Normal". To
change that, put one of these values (case insensitive) in the `priority`
tag: `Idle`, `BelowNormal`, `Normal`, `AboveNormal`, `High`, `RealTime`.
But we don't recommend using `Idle` or `BelowNormal` as they make the
service unresponsive. More about priorities [here][priority-enum] and
over [here][win-scheduling] too.

And as we're speaking of priorities, keep in mind you can fine-tune import
runs. In fact, each import runs in a separate process that inherits the
scheduling priority of its parent, the Smuggler service. But you can override
this in the import configuration with your own command to set the priority.
For your convenience, you'll find a `nice.exe` in the `bin` directory that
works pretty much like the Unix `nice`. Here's an example line you could
add to your import configuration file.

~~~ {.yaml}
niceCommand: nice -n 10
~~~

Because the service automatically adds the `bin` directory to its `PATH`,
you can call `nice.exe` without specifying the full path. If you use your
own command, you may have to specify the full path though. (But ya, you
knew that.)


Windows Service Installation
----------------------------
Now install Smuggler as a Windows service. Open a command prompt as a sys
admin (right click on icon, then select "Run as administrator") and go to
the `ome-smuggler\bin` directory. From inside the `bin` directory run

~~~ {.bash}
> ome-smuggler.exe install
~~~

Yip, this does exactly what you think: it sets up Smuggler as a service.
Note that you *really* need to run this command inside `bin`. (Otherwise
configuration, data, and log directories will point to the wrong place;
have a look at the `APP_HOME` variable defined in `ome-smuggler.xml`.)
The installer configures the service to start automatically at boot, if
you want to start it immediately run

~~~ {.bash}
> ome-smuggler.exe start
~~~

<div class="pull-quote">
###### Security Considerations
By default the service runs in the context of the `LocalSystem` account.
You may want to consider using a less privileged account such as the
`LocalService` account or a custom account. Pass the `install` command a
`/p` option to specify the account details interactively at install time.
Note that if you use a different account, that account will need to be
able to access Smuggler's configuration, data, and log directories and
read/write files inside them.
</div>

Even though Smuggler is now going to have a life of its own as a service,
you can still control it from the command line---have a look at the next
section for the available commands. To uninstall the service, run

~~~ {.bash}
> ome-smuggler.exe stop
> ome-smuggler.exe uninstall
~~~

And to wipe Smuggler out from the face of the earth, just delete the
`ome-smuggler` directory. (If you changed the locations of Smuggler's
configuration, data and log directories to be outside of `ome-smuggler`,
you'll also have to delete those directories.)


Command Line Usage
------------------
The `ome-smuggler.exe` wrapper can be used both to install and control the
Smuggler service. As an installer, it takes these command arguments

* `install`. Installs Smuggler as a Windows service. Followed by `/p`, it
prompts you for the credentials of the account to use for running the Windows
service.
* `uninstall`. Removes the Smuggler service from Windows system configuration.
You'll have to delete the `ome-smuggler` directory yourself.

After installing the service, the following commands can be used to control
it:

* `start`. Starts the Smuggler service.
* `stop`. Stops the Smuggler service.
* `restart`. Restarts the Smuggler service.

Additionally, there's a command to report on service status:

* `status`. Outputs one of the following status flags: `NonExistent` (service
not installed yet), `Started` (service's running), `Stopped` (service installed
but not running).




[advanced-config-section]: #advanced-configuration
    "Advanced Configuration"
[config]: configuration.html
    "Configuration"
[priority-enum]: https://msdn.microsoft.com/en-us/library/system.diagnostics.processpriorityclass(v=vs.110).aspx
    "ProcessPriorityClass Enumeration"
[win-scheduling]: https://msdn.microsoft.com/en-us/library/windows/desktop/ms685100(v=vs.85).aspx
    "Scheduling Priorities"
[winice]: https://github.com/c0c0n3/winice
    "winice"
[winsw]: https://github.com/kohsuke/winsw
    "winsw"
