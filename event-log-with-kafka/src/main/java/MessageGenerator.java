import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class MessageGenerator {

    public static void main(String[] args) throws JsonProcessingException, InterruptedException, ExecutionException {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("retries", 3);

        //  props.put("key.serializer", StringSerializer.class.getName());
        // props.put("value.serializer", StringSerializer.class.getName());

        Producer<String, String> producer = new KafkaProducer<>(props, new StringSerializer(), new StringSerializer());

        for (int i = 0; i < 100; i++) {
            MessagePosted messagePosted = generateUserMessage();
            String pageViewStr = toJsonString(messagePosted);
            System.out.println(messagePosted);
            sleep(500);

            RecordMetadata metadata = producer
                    .send(new ProducerRecord<>("message-posted", messagePosted.getUserId(), pageViewStr))
                    .get();

            System.out.println(String.format("Key = %s; partition = %s; offset = %s",
                    messagePosted.getUserId(),
                    metadata.partition(),
                    metadata.offset()));
            System.out.println();
        }

        producer.flush();
        producer.close();
    }
    private static String toJsonString(MessagePosted messagePosted) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(messagePosted);
    }

    private static MessagePosted generateUserMessage() {

        Faker faker = new Faker();

        MessagePosted messagePosted = new MessagePosted();

        messagePosted.setUserId(faker.number().digits(2));
        messagePosted.setMessage(faker.lorem().characters(10, 20));

        return messagePosted;
    }

    private static void sleep(int ms) throws InterruptedException {
        Thread.sleep(ms);
    }
}
