---
title: Monitoring
slogan: keep an eye on this guy!
---

<p class="intro">
???
</p>

mention: on windows, the wrapper's own output goes in the bin dir; also the
wrapper logs start up & shutdown events to the windows event log (look here
for start up issues). all the rest goes in log/spring.log. Not optimal, but
didn't have time to sort this out. ideally, important events should also be
written to the windows event log as this is what a sys admin would look at.
(logging on the machine to see spring.log is just going to be a pain in the
backside if you have deployed smugs on many machines...may fix this in the
future!)

mention: log dir won't grow huge over time as logs are rotated.

mention: data dir shouldn't grow huge over time as long as sys admin deletes
failed tasks after resolving the issues that caused the failure.
