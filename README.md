## Java - reactive backend examples

> As of September 2021 this repository is no longer actively maintained by the GOV.UK Pay team.

This repository contains a collection of examples/patterns that can be adopted to make a java based micorservice `reactive`.
   
*GOV.UK Pay*, a payment platform for the government consists of many microservices. Our backend is [`RESTFUL`](https://en.wikipedia.org/wiki/Representational_state_transfer) by default, and we use [`Dropwizard`](http://www.dropwizard.io/) for most of java based microservices. 
Dropwizard is based on Jetty, a lightweight java http/servlet container. 
Architecturally, jetty is a thread based synchronous (blocking) request processing container, similar to many other traditional java servlet containers.

Over the last decade or so, asynchronous/non-blocking request processing architectures are gaining huge traction across the industry, 
primarily due to the capabilities of optimum usage of available resources (H/W, S/W) allowing significantly higher throughput. 
Although this model already been widely used in other (non-java) tech stacks (e.g. NodeJs, Scala/Akka), it has been a slow progress in Java community due to its lack of language features (functional) 
and lack of libraries / frameworks. However, with the introduction of Java 8 features and the appearance of several reliable libraries/frameworks (e.g. guava listenable futures, Rx Java, Akka) Java landscape is slowly shifting towards 
asynchronous non-blocking programming models.  

The biggest challenge yet is the change of mentality among the developer community from a more traditional synchronous programming models to more functional / reactive 
programming models in order to gain the advantages these recent developments. 
 
This repository presents few alternative non-blocking programming models / patterns and technologies for Java. We expect to adopt these (and possibly more) as we extend GOV.UK Pay in future.   
  
### Further Reading  
- Here is a good reference to read more about [`reactive programming`](https://gist.github.com/staltz/868e7e9bc2a7b8c1f754) principles and examples.
- Here's a comparison of Dropwizard vs Ratpack. http://phillbarber.blogspot.co.uk/2016/01/choosing-between-ratpack-and-dropwizard.html

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
  * See [hystrix based example](https://github.com/alphagov/pay-firebreak-react-backend/blob/master/dropwizard/src/main/java/firebreak/react/drop/resources/AuthorisationService.java#L57)


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
2. Hystrix [hystrix](https://github.com/Netflix/Hystrix) 
3. Guvava LitenableFutures [Guvava](https://github.com/google/guava/wiki/ListenableFutureExplained)
4. Jersey 
 - [Reactive Client](https://jersey.java.net/documentation/latest/user-guide.html#rx-client)
 - [AsyncResponse](https://jersey.java.net/documentation/latest/user-guide.html#d0e10296)
5. Ratpack [Ratpack](https://ratpack.io/manual/current/)
6. Java8  [CompletableFuture](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html)

Other options (not explored)

1. VertX [vert.x](http://vertx.io/)
2. Akka [akka](http://akka.io/)
