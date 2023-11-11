**Latency vs Throughput**

Quote Feedback 
- Latency is important
- Volume is low 
- Linger MS 0

Location Data
- High Volume
- Increased Latency Admissible
- linger.ms > 0 beneficial -> Time interval b/w messages sent 

In a Consumer Group , if a consumer has not committed offsets and dies
a new consumer will reprocess uncommmitted offsets 