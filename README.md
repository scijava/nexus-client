[![](https://travis-ci.com/scijava/nexus-client.svg?branch=master)](https://travis-ci.com/scijava/nexus-client)

![](https://travis-ci.com/scijava/nexus-client)
# nexus-client

A generic client for the [Nexus Sonotype ReST Service](https://help.sonatype.com/repomanager3/rest-and-integration-api).

Current implementation supports V3 of the Repositories, Search, Assets and Components APIs which is only available in the Nexus Repository Manager 3.

It does not currently support token authentication as this is only available with the Enterprise (paid) version of Nexus.

In order to run the tests locally you will need:

- test repositories *autotest-maven* and *autotest-raw*
- a test user with full access (read, write, delete) to these repositories

The repository and test user information is passed to the tests via the following environment variables:

- NEXUS_URL the URL of the Nexus server
- NEXUS_USR the test user's login
- NEXUS_PWD the test user's password


