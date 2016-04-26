OME Smuggler JClient
====================
Simple Java 7 client to interact with Smuggler.

Description
-----------
The only interaction with Smuggler we support at the moment is to POST an
import. This is because for now, we're only using this component to trigger
an offline import from Insight.
On the other hand, the HTTP and JSON functionality is generic enough to be
reused in other contexts.

Dependencies
------------
As we have to use this code from Insight, we compile to Java 7 and use the
same Apache `HttpClient` library that Insight also uses. Ditto for Blitz.
This makes things simpler and avoids version conflicts.
