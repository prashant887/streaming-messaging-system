Knesis Stream
--
Immutable Log , append only 
All readers see message in same order , Kensis is not responsible for maintaining state of reads 
Messages are stored in shards , each shard has own long and each shard can be in different machines 

Writing Data : Messages has key and value , steams has key range assoicated with it 
This range is divided to multiple shards , keys are hashed with md5 algo and stored in particular shards 
message with same hash go to same shard 

Select Key 
- Random Key 
- User Defined Key 

Reading Data 
One consumer can read from one shard or multiple shards 

Kinesis vs Kafka vs SQS 
-
Kinesis vs Kafka 
Both are Messaging System 
Kinesis 
- Stream/Shard  
- No operational Load
- stores data upto 7 days
- Kinesis client Library
- tied to aws

Kafka
- Topics/Partitions
- Low to Heavy Operational Load 
- can store indefinitely 
- Various clients 
- Log compaction 

Kenisis Vs SQS
Kenisis
- State tracking by Client 
- Ordered
- Message can be read many times
- unified log/stream processing

SQS
- State tracked by SQS
- Order not Guaranteed 
- Processed Message is removed
- balancing task among workers 

Limitations of Shard 
- One shard can read <2MB per second 
- Divide among all consumers 
- Single shard can read 5 Message per sec

Fan Out Consumer 
SubscribeToShard -> Kenis will push records to consumers , typically consumers will pull records 
Amount of Data: Up to 2MB per second per consumer 
Lower Latency : Around 70 MS 
Extra Cost 

ComptableFuture<Record> future=getRecord()

Blocking -> wait till result is ready using get Method //BLocking call , future.get

Dont Block -> wait for result to be ready , future.thenApply(r->processRecord(r))

Scaling Kinesis 
SplitShards -> Replace Single Shard with 2 shards 
MergeShards -> Replace 2 shards with single shard

Data Write Limitations 
Shard Throughput 
- upto 1MB
- Solution:Scale Up 

Number of Transactions 
- Upto 1000 recs per second 
- Solution: Producer Buffering 

KCL 