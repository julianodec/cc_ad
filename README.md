# Everyone Loves Ads!

Everyone Loves Ads! is a simple ad server written by Juliano DeCarvalho.  It was developed in Java, and using the following technologies:

* Jersey 2.32.2 for RESTful web services
* Spring 3.2.3 for CDI, persistence repository, and transactional support
* Moxy for JSON and XML marshaling
* MongoDB for persistence
* Jetty as the application server

This application should fulfill every requirement posted, including the optional requirements around scalability, and allowing and listing multiple campaigns per partner.

## Running the application

To run the application, following these instructions

1. Download and install Maven 3 from [https://maven.apache.org/download.cgi]
2. Clone this GitHub repository or download the release from [http://github.com/julianodec/]
3. Run this command: `$MAVEN_HOME/bin/mvn clean install jetty:run`

Please note that the last command will start the process of pulling down all dependencies, which may take several minutes.  Once complete you will see this line in your console: `[INFO] Started Jetty Server`.  At this point you may start a web browser and access [http://localhost:8080], and you should see the following screen:

![Everyone Loves Ads! Landing Page](http://github.com/julianodec/ "Everyone Loves Ads! Landing Page")

The user interface is separated into 4 pieces.  The first piece allows you to select a partner from a pick-list.  There are 2 partners currently hardcoded into the app, but more could easily be added in the future dynamically from the backend.

The second piece allows adding a new campaign for the selected partner.  A campaign requires a duration and some content, and can be added as either active or inactive.  If the user attempts to add an active campaign when another campaign is already active for this partner, the user will be shown an error as a JavaScript alert.

The third piece shows the user the currently active campaign for this partner, if any.

The final piece allows the user to see a list of all campaigns, regardless of active state, for the selected partner.  A deactivated campaign can be activated again by clicking the `Activate` link.  Both this and the current active campaign list are refreshed every 2 seconds from the server.  If a campaign expires while the page is being viewed, the list will show that when refreshed.

## Scalability

The application is written using Jetty and MongoDB.  Both servers have already been tuned to allow clustering, in order to provide scalability and fault tolerance.  This can be achieved through Jetty's session failover feature using session replication, and MongoDB's innate scalability through data replication and sharding.  More information available upon request.

## Persistence

MongoDB was chosen over a SQL database due to its out-of-the-box ability to support data replication and sharding, which as mentioned above, allows for easy fault-tolerance and scalability.  MongoDB also supports an extremely flexible and rich data model, which would be ideal for something like advertisements, which come in many flavors, such as text and multimedia.
