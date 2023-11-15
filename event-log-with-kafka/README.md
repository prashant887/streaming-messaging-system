Idempotent Producer 
props.put("enable.idempotent",1) -> Enable Idempotent
"acks" should be set to all

Has negligible performance impact (if you use acks=all)
Gaurntees that records are always recoreded in order 

Sending Multiple Records 
-
Like Delivery and Notification and send to 2 diff topic for Orders, there can be cases where one of the message fails
There is transactions 
-> producer.beginTransaction()
-> producer.send(topic_one,key,value)
-> producer.send(topic_two,key,value)
-> producer.sendOffsetsToTransaction(offset,group)
-> producer.commitTransaction()


Microservice Architecture 
Synchronous Microservices 
-> Communicate Eachother via synchronous calls , like Rest , gRPC , Thrift

Asynchronous Microservices
-> Communicates via sending message via messaging system , like Kafka


Historical Events 
- 
Dont build over final state 
CRUD -> On every operation update the mutable state and store final mutable 
For streaming it would be immutable system , history of events would be immutable, every event would be stored 
This is called event souring , State of the system can be recreated form past events 

Event Processing
-
Stateless Event Processing - Each event is processed individually without looking at history , (map,filter,flatMap)
StateFul Even Processing - Depends on current state (aggregations,joins)

How long to store events
-
Limited Time 
Disk Space 

Kafka Streams
-
Kafka Stream Topology 

Event Carried State Transfer -> Read data from WAL and not acutal db 

Co Partitioned Topics
-
Each Stream Processor has access to local data 
Read data from One stream and match with data from other Stream 
Need subset of keys from both topic 
both topics should have same number of partitions and have same parition key

Exactly Once Processing
-
Just a configuration change
Relatively lower over ahead 
only for messages with Kafka Streams 

kafka Streams Storage Options
-
State Store : Low Level Key Value Store 
Table : High level Store 
Global Table: Same data is present on all consumer 

Convert Stream to Table , aggregate , count , join 

KTable (Create Table )
Store latest value for each key in table , value is updated for same keys 
"null" valu removes the key

Joins
-
Table Stream Joins 
   Inner Joins
   Left Outer Joins 

Stream to Stream Joins
Table to Table Joins 

Complex Transactions
-
Orchestrations 
  Single Orchestrator 
Choreography 
  No single point of Control

Analytics Quires 
-
Analytics on Read : Store Raw Data , Execute ad-hoc quires 
  Store all events
  Write data to data warehouse
  Allow to perform any quries 
  High-latency
  useful for adhoc quries

Analytics on Write: Compute results in real time 
  Store Derived Data
  Write Data to Database
  Quries are predefined
  Low-latency
  Useful for dashbaords

Time in Streaming 
Processing Time 
Ingestion Time 
Event Time

Tier Storage
--
Store Data Externally




