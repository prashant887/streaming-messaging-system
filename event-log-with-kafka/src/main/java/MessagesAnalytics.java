import io.confluent.kafka.serializers.KafkaJsonDeserializer;
import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;
import io.confluent.kafka.serializers.KafkaJsonSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;


import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class MessagesAnalytics {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        // props.put("key.serializer", StringSerializer.class.getName());
        // props.put("value.serializer", StringSerializer.class.getName());

        props.put("application.id", "messages-analytics");
        props.put("auto.offset.reset", "earliest");

        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, MessageTimestampExtractor.class.getName());

        var topology = createTopology();

        KafkaStreams streams = new KafkaStreams(topology, props);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> streams.close()));
        streams.start();


    }

    private static Topology createTopology() {

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String,MessagePosted> postedMessages= builder.stream(
                "message-posted",
                Consumed.with(
                        Serdes.String(),
                        createValuesSerde(MessagePosted.class))
        );

        postedMessages
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1)))
                .count()
                .toStream()
                .map((Windowed<String> key, Long count) -> {
                    MessagesCount messagesCount = new MessagesCount();
                    messagesCount.setUserId(key.key());
                    messagesCount.setCount(count);


                    Date timestamp = Date.from(key.window().endTime());
                    messagesCount.setTimestamp(timestamp);

                    return new KeyValue<>(key.key(), messagesCount);
                })
                .print(Printed.toSysOut());
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
