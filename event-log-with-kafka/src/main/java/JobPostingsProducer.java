import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class JobPostingsProducer {
    public static void main(String[] args) throws InterruptedException, ExecutionException, JsonProcessingException {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());

        Producer<String, String> producer = new KafkaProducer<>(props, new StringSerializer(), new StringSerializer());

        for (int i = 0; i < 100; i++) {
            JobPostingCreated jobPostingCreated = generateJobPostingEvent();
            String pageViewStr = toJsonString(jobPostingCreated);
            System.out.println(jobPostingCreated);
            sleep(500);

            RecordMetadata metadata = producer
                    .send(new ProducerRecord<>("job-postings", jobPostingCreated.getUserId(), pageViewStr))
                    .get();

            System.out.println(String.format("Key = %s; partition = %s; offset = %s",
                    jobPostingCreated.getUserId(),
                    metadata.partition(),
                    metadata.offset()));
            System.out.println();
        }

        producer.flush();
        producer.close();

    }

    private static String toJsonString(JobPostingCreated jobPostingCreated) throws  JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(jobPostingCreated);
    }

    private static JobPostingCreated generateJobPostingEvent() {
        Faker faker = new Faker();

        JobPostingCreated jobPostingCreated = new JobPostingCreated();

        jobPostingCreated.setUserId(faker.number().digits(2));
        jobPostingCreated.setJobTitle(faker.job().title());
        jobPostingCreated.setJobDescription(generateJobDescription(faker));
        jobPostingCreated.setSalary(faker.number().numberBetween(0, 300000));

        return jobPostingCreated;
    }

    private static String generateJobDescription(Faker faker) {

        return faker.job().seniority() + " " + faker.job().position();
    }

    private static String randomSelect(String[] arr) {
        int rnd = new Random().nextInt(arr.length);
        return arr[rnd];
    }

    private static void sleep(int ms) throws InterruptedException {
        Thread.sleep(ms);
    }

}
