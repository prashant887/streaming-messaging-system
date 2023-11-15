import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

import java.util.Date;

public class MessageTimestampExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        Date createdAt = ((MessagePosted) record.value()).getCreatedAt();
        return createdAt.getTime();
    }
}
