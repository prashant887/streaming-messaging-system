
import io.confluent.kafka.serializers.KafkaJsonDeserializer;
import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;
import io.confluent.kafka.serializers.KafkaJsonSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Map;
import java.util.Properties;

public class StreamsDemo {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
       // props.put("key.serializer", StringSerializer.class.getName());
       // props.put("value.serializer", StringSerializer.class.getName());

        props.put("application.id", "streams-demo");
        props.put("auto.offset.reset", "earliest");

        var topology = createTopology();

        KafkaStreams streams = new KafkaStreams(topology, props);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> streams.close()));
        streams.start();
    }

    private static Topology createTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        builder
                .stream("job-postings", Consumed.with(
                        Serdes.String(),
                        createValuesSerde(JobPostingCreated.class)))
                .mapValues(jobPostingCreated -> {
                    JobPostingWithType jobPostingWithType = new JobPostingWithType();
                    jobPostingWithType.setUserId(jobPostingCreated.getUserId());
                    jobPostingWithType.setJobTitle(jobPostingCreated.getJobTitle());
                    jobPostingWithType.setJobDescription(jobPostingCreated.getJobDescription());
                    jobPostingWithType.setSalary(jobPostingCreated.getSalary());

                    Integer salary = jobPostingCreated.getSalary();
                    if (salary < 1_000) {
                        jobPostingWithType.setType("pro-bono");
                    } else if (salary < 60_000) {
                        jobPostingWithType.setType("normal");
                    } else {
                        jobPostingWithType.setType("high-salary");
                    }

                    return jobPostingWithType;
                })
                .peek((k, jobPostingWitType) -> {
                    System.out.println("Typed job: " + k + " -> " + jobPostingWitType);
                })
                .to("typed-job-postings", Produced.with(
                        Serdes.String(),
                        createValuesSerde(JobPostingWithType.class)
                ));
        return builder.build();

    }


        private static <T> Serde<T> createValuesSerde(Class<T> valueType) {
        KafkaJsonSerializer<T> serializer = new KafkaJsonSerializer<T>();
        serializer.configure(Map.of(), false);

        KafkaJsonDeserializer<T> deseriazlier = new KafkaJsonDeserializer<T>();
        deseriazlier.configure(Map.of(
                KafkaJsonDeserializerConfig.JSON_VALUE_TYPE, valueType.getName()
        ), false);

        return Serdes.serdeFrom(serializer, deseriazlier);
    }
}
