## GOV.UK Pay firebreak 

### Reactive Backend

GOV.UK Pay adopts Dropwizard as the container for backend microservices. Pretty much all of GOV.UK Pay's RESTFUL
resources are based on Dropwizard/Jersey tech stack.
Dropwizard is based on Jetty, a lightweight java http/servlet container. Architecturally, jetty is a thread based 
synchronous (blocking) request processing container, similar to many other traditional java servlet containers.

Over the last decade or so asynchronous (non-blocking) request processing architectures are gaining huge traction across the industry due to
its capabilities of optimum usage of available resources (H/W, S/W) allowing significantly higher throughput. Although this has already been widely 
adopted in non-Java technologies (e.g. Node.js, Scala/Akka), it has been a slow progress in Java due to its lack of language features (functional) 
and lack of libraries / frameworks. However, over the last few years with the introduction of Java 8 features and the appearance of 
several popular libraries and frameworks (e.g. guava listenable futures, Rx Java, Akka) the landscape is slowly shifting towards 
asynchronous non-blocking architectures in Java technology. 

The biggest challenge yet is the change of mentality in developer community from a more traditional synchronous programming model to a more functional / reactive 
programming model in order to gain the advantages these recent developments. 
 
This repository presents couple of alternative non-blocking programming models that is viable for GOV.UK Pay.
Hoping this would provide few alternative reference models for more wider adoption.     

Here's a comparison of Dropwizard vs Ratpack. 
http://phillbarber.blogspot.co.uk/2016/01/choosing-between-ratpack-and-dropwizard.html

### Examples.

####1. Handling inbound requests 

![alt handling inbound requests](img/inbound.png)

This is about how to handle HTTP/REST requests in a non-blocking way.  

  * See [dropwizard example](https://github.com/alphagov/pay-firebreak-react-backend/blob/master/dropwizard/src/main/java/firebreak/react/drop/resources/CardResource.java#L40)
  * See [ratpack example](https://github.com/alphagov/pay-firebreak-react-backend/blob/master/ratpack/src/main/java/firebreak/react/rat/CardResource.java#L43)


####2. Handling outbound requests 

![alt handling outbound requests](img/outbound.png)

This is about how to handle outbound HTTP/REST requests in a non-blocking way.  

  * See [dropwizard example](https://github.com/alphagov/pay-firebreak-react-backend/blob/master/dropwizard/src/main/java/firebreak/react/drop/resources/AuthorisationService.java#L42)
  * See [ratpack example](https://github.com/alphagov/pay-firebreak-react-backend/blob/master/ratpack/src/main/java/firebreak/react/rat/CardResource.java#L144)


####3. Handling internal workflows 

![alt internal workflows](img/internal-flow.png)

This is about how we can compose a internal workflow of bunch of functions together in a reactive way.  

  * See [dropwizard example](https://github.com/alphagov/pay-firebreak-react-backend/blob/master/dropwizard/src/main/java/firebreak/react/drop/resources/AuthorisationService.java#L26)
  * See [ratpack example](https://github.com/alphagov/pay-firebreak-react-backend/blob/master/ratpack/src/main/java/firebreak/react/rat/CardResource.java#L57)

####4. Decoupling of microservices using an event source  

![alt decoupling](img/decoupling.png)

This is about how we can compose a internal workflow of bunch of functions together in a reactive way.  

  * See [publisher](https://github.com/alphagov/pay-firebreak-react-backend/blob/master/dropwizard/src/main/java/firebreak/react/drop/resources/KafkaAuthorisationService.java#L40)
  * And [subscriber](https://github.com/alphagov/pay-firebreak-react-backend/blob/master/dropwizard/src/main/java/firebreak/react/drop/resources/GatewayHandlerResource.java#L33)  


###References

1. RxJava [rxjava](https://github.com/ReactiveX/RxJava)
2. Guvava LitenableFutures [Guvava](https://github.com/google/guava/wiki/ListenableFutureExplained)
3. Jersey 
 - [Reactive Client](https://jersey.java.net/documentation/latest/user-guide.html#rx-client)
 - [AsyncResponse](https://jersey.java.net/documentation/latest/user-guide.html#d0e10296)
4. Ratpack [Ratpack](https://ratpack.io/manual/current/)
5. Java8  [CompletableFuture](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html)

Other options (not explored)

1. VertX [vert.x](http://vertx.io/)
2. Akka [akka](http://akka.io/)
