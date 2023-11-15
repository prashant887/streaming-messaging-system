import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class UserBlockedProducer {

    public static void main(String[] args) throws ExecutionException, InterruptedException, JsonProcessingException {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
      //  props.put("key.serializer", StringSerializer.class.getName());
        // props.put("value.serializer", StringSerializer.class.getName());

        Producer<String, String> producer = new KafkaProducer<>(props, new StringSerializer(), new StringSerializer());

        for (int id = 10; id < 30; id++) {
            UserBlocked userBlocked = generateUserBlockedEvent(id);
            String pageViewStr = toJsonString(userBlocked);
            System.out.println(userBlocked);
            sleep(500);

            RecordMetadata metadata = producer
                    .send(new ProducerRecord<>("user-blocked", userBlocked.getUserId(), pageViewStr))
                    .get();

            System.out.println(String.format("Key = %s; partition = %s; offset = %s",
                    userBlocked.getUserId(),
                    metadata.partition(),
                    metadata.offset()));
            System.out.println();
        }

        producer.flush();
        producer.close();
    }
    private static String toJsonString(UserBlocked userBlocked) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(userBlocked);
    }

    private static UserBlocked generateUserBlockedEvent(int id) {
        UserBlocked userBlocked = new UserBlocked();

        userBlocked.setUserId(Integer.toString(id));

        return userBlocked;
    }

    private static void sleep(int ms) throws InterruptedException {
        Thread.sleep(ms);
    }
}
