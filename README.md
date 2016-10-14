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

The biggest challenge yet is the mentality change of developers from the traditional synchronous programming model to a more functional / reactive 
programming model in order to gain the advantages these recent developments. 
 
This repository presents couple of alternative non-blocking programming models that is viable for GOV.UK Pay.
Hoping this would provide alternative reference models for more wider adoptation.     
