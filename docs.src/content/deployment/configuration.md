---
title: Configuration
slogan: season to taste
---

<p class="intro"> 
There are configuration options you can tweak to make *Smuggler* suit your
environment. Even though most of the built-in configuration might work for
you out of the box, we suggest you have a quick read through this section. 
</p>

-- note config dir, will refer to it as "your config dir"
-- note for config changes to take effect, bounce smugs.

HTTP
----
The embedded Undertow server accepts HTTP connections on port `8000`. To use
a different port, you can put an `undertow.yml` file in your configuration
directory with a different port number. An easy way to do that is to copy our
default Undertow file from your local Git repo

    components/server/src/main/resources/config/undertow.yml

into your configuration directory and then edit it. Or you could generate it
with the following command

    java -jar smuggler.jar ome.smuggler.run.UndertowYmlGen > undertow.yml

replacing `smuggler.jar` with the path to the server jar in the `build`
directory, e.g.

    components/server/build/libs/ome-smuggler-0.1.0.jar

(This jar file will be in your local Git repo only after you've done a build.)

Another option you have is to embed your configuration directly into the
server jar instead of using files in a configuration directory. To do that,
edit

    components/server/src/main/resources/config/undertow.yml

Then from the root directory of your local Git repo:

    ./gradlew assemble :packager:release

(use `gradlew.bat` on Windows.) The `undertow.yml` file you've just edited is
now embedded into the server jar file contained in each and every distribution
bundle generated in

    components/packager/build/distributions/

Pick your bundle and deploy it; you won't need to add a separate `undertow.yml`
file to your deployment as the server will read your values from the embedded
`undertow.yml`. If sometime after deployment you need to tweak HTTP configuration
further, you can still put an `undertow.yml` in your configuration directory
and restart the server; this new file will take precedence over the one you've
embedded, so the HTTP configuration the server will use is that of the
`undertow.yml` in the configuration directory.

Import
------

Mail
----

Spring
------

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

    omeCliJarPath: ../cli/build/libs/ome-cli-0.1.0.jar

(You may have to adjust the `ome-cli` version number to match the current one
in `build.gradle`.)



- mention: data dir will be created if not exist. but smugs will need r/w perm
    on parent dir. you may want to enforce more stringent access control and
    create data dir yourself so that smugs doesn't need r/w perm on parent.
- mention: tweak built-in config files to avoid having config on target machine
- mention: generate config through runners
- mention: internal config look at config package
