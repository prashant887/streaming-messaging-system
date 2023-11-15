import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConsumerDemo {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());

        props.put("group.id", "test-consumer-group");
        props.put("enable.auto.commit", "false");
        props.put("auto.offset.reset", "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(List.of("page-visits"));

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                processRecords(records);

                consumer.commitAsync();
            }
        } catch (Exception e) {
            consumer.close();
        }
    }

    private static void processRecords(ConsumerRecords<String, String> records) throws IOException {
        for (ConsumerRecord<String, String> record : records) {

            System.out.printf("Partition = %s, offset = %d, key = %s\n",
                    record.partition(),
                    record.offset(),
                    record.key());
            PageView pageView = parse(record.value());
            System.out.println(pageView);
        }
    }
    private static PageView parse(String pageViewStr) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(pageViewStr, PageView.class);
    }
}
