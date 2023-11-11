import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;

import java.util.Properties;

public class ProtobufProducer {
    public static void main(String[] args) throws Exception {

        var userId = 123124;

        /*
        RideHailingProtos.TripIntent tripIntent = RideHailingProtos.TripIntent.newBuilder()
        .setUserId(userId)
        .setLatLonFrom("48.2026,16.3688")
        .setLatLonTo("48.1869,16.3133")
        .build();


        var props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("key.serializer", IntegerSerializer.class.getName());
        // TODO: Add schema registry url
        props.setProperty("schema.registry.url", "http://localhost:8081");
        // TODO: Use protobuf serializer
        props.setProperty("value.serializer", KafkaProtobufSerializer.class.getName());

        KafkaProducer<Integer, RideHailingProtos.TripIntent> producer = new KafkaProducer(props);

        ProducerRecord<Integer, RideHailingProtos.TripIntent> r = new ProducerRecord<>("trip-intent", userId, tripIntent);
        producer.send(r).get();

         */
    }
}
