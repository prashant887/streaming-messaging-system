import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class ProducerDemo {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());

        //        props.put("enable.idempotence", 1);

        Producer<String,String> producer=new KafkaProducer<>(props);

    }

    private static String toJsonString(PageView pageView) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.writeValueAsString(pageView);
    }

    private static PageView generateRecord(){
        Faker faker=new Faker();
        PageView pageView=new PageView();
        pageView.setUserName(randomName());
        pageView.setBrowser(faker
                .internet()
                .userAgentAny());
        pageView.setPage(randomPage());
        pageView.setViewDate(new Date());

        return pageView;
    }

    private static String randomName() {
        return randomSelect(new String[]{"robbin", "joe", "daisy", "lisa", "laurette", "raphael", "elda", "eric"});
    }

    private static String randomPage() {
        return randomSelect(new String[]{"/home", "/user/profile", "/orders", "/search", "/purchase"});
    }

    private static String randomSelect(String[] arr) {
        int rnd = new Random().nextInt(arr.length);
        return arr[rnd];
    }

    private static void sleep(int ms) throws InterruptedException {
        Thread.sleep(ms);
    }
}
