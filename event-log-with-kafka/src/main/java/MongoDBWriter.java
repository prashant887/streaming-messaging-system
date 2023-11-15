import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.confluent.kafka.serializers.KafkaJsonDeserializer;
import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;
import io.confluent.kafka.serializers.KafkaJsonSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.bson.Document;

import java.util.Map;
import java.util.Properties;

public class MongoDBWriter {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
         //props.put("key.serializer", StringSerializer.class.getName());
        //props.put("value.serializer", StringSerializer.class.getName());

        props.put("application.id", "mongodb-writer");
        props.put("auto.offset.reset", "earliest");

        var topology = createTopology();

        KafkaStreams streams = new KafkaStreams(topology, props);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> streams.close()));
        streams.start();
    }

    private static Topology createTopology() {

        MongoCollection<Document> collection = getMongoCollection();

        StreamsBuilder builder = new StreamsBuilder();
        builder
                .stream("typed-job-postings", Consumed.with(
                        Serdes.String(),
                        createValuesSerde(JobPostingWithType.class)))
                .foreach((k, v) -> {
                    System.out.println(k + " -> " + v);
                    writeRecordToMongo(collection, v);
                });
        return builder.build();
    }

    private static MongoCollection<Document> getMongoCollection() {
        MongoClient mongoClient =  MongoClients.create(
                "mongodb+srv://user:pass@test-cluster.frz5a.mongodb.net/jobs-website?retryWrites=true&w=majority");

        MongoDatabase database = mongoClient.getDatabase("jobs-website");
        return database.getCollection("job-postings");
    }
    private static void writeRecordToMongo(MongoCollection collection, JobPostingWithType jobPostingWithType) {

        collection.insertOne(new Document()
                .append("userId", jobPostingWithType.getUserId())
                .append("jobTitle", jobPostingWithType.getJobTitle())
                .append("jobDescription", jobPostingWithType.getJobDescription())
                .append("salary", jobPostingWithType.getSalary())
                .append("type", jobPostingWithType.getType())
        );
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
