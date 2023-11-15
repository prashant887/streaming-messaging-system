import io.confluent.kafka.serializers.KafkaJsonDeserializer;
import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;
import io.confluent.kafka.serializers.KafkaJsonSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.util.Map;
import java.util.Properties;

public class MessagesFilter {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("auto.offset.reset", "earliest");
        props.put("replication.factor", "3");
        props.put("application.id", "blocked-messages-filter");
        props.put("bootstrap.servers", "localhost:9092");

        var topology = createTopology();

        KafkaStreams streams = new KafkaStreams(topology, props);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> streams.close()));
        streams.start();
    }

    private static Topology createTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        KTable<String,UserBlocked> blockedUsersTable=builder
                .table("user-blocked",
                        Materialized.with(Serdes.String(),createValuesSerde(UserBlocked.class))
                );
        blockedUsersTable.toStream().print(Printed.toSysOut());

        KStream<String, MessagePosted> postedMessages = builder.stream(
                "message-posted",
                Consumed.with(
                        Serdes.String(),
                        createValuesSerde(MessagePosted.class)));

 postedMessages.leftJoin(blockedUsersTable,(message,blockedUser)->{
            MessageFiltered messageFiltered = new MessageFiltered();
            messageFiltered.setUserId(message.getUserId());
            messageFiltered.setMessage(message.getMessage());

            if (blockedUser == null) {
                messageFiltered.setStatus("PASSED");
            } else {
                messageFiltered.setStatus("FILTERED");
            }

            return messageFiltered;
        })
                .peek((k, message) -> {
                    System.out.println("Filtered message: " + k + " -> " + message);
                })
                .to("message-filtered", Produced.with(
                        Serdes.String(),
                        createValuesSerde(MessageFiltered.class))
                );
        return builder.build();

    }

        private static <T> Serde<T> createValuesSerde(Class<T> valueType) {
        var serializer = new KafkaJsonSerializer<T>();
        serializer.configure(Map.of(), false);

        var deseriazlier = new KafkaJsonDeserializer<T>();
        deseriazlier.configure(Map.of(
                KafkaJsonDeserializerConfig.JSON_VALUE_TYPE, valueType.getName()
        ), false);

        return Serdes.serdeFrom(serializer, deseriazlier);
    }
}
