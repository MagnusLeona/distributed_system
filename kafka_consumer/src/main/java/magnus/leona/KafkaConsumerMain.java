package magnus.leona;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@SpringBootApplication
public class KafkaConsumerMain {

    Logger logger = LoggerFactory.getLogger(KafkaConsumerMain.class);

    public Properties loadProperties() throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource("properties/kafka.properties");
        Properties properties = new Properties();
        assert resource != null;
        properties.load(resource.openStream());
        return properties;
    }

    public static void main(String[] args) throws IOException {
        // 消费者
        String topicname = "magnus";
        KafkaConsumerMain kafkaConsumerMain = new KafkaConsumerMain();
        Properties properties = kafkaConsumerMain.loadProperties();
        properties.put("group.id", "magnus-test-consumer-1");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer(properties);
        List<String> list = new ArrayList<>();
        list.add(topicname);
        kafkaConsumer.subscribe(list);

        System.out.println("begin~");

        while (true) {
            ConsumerRecords<String, String> poll = kafkaConsumer.poll(200);
            for (ConsumerRecord<String, String> record : poll) {
                String value = record.value();
                System.out.println(value);
            }
        }
    }
}
