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
import java.util.Arrays;
import java.util.Properties;

@SpringBootApplication
public class KafkaConsumerMain {

    Logger logger = LoggerFactory.getLogger(KafkaConsumerMain.class);

    public Properties loadProperties() throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource("properties/kafka.properties");
        Properties properties = new Properties();
        properties.load(resource.openStream());
        return properties;
    }

    public static void main(String[] args) throws IOException {
        // 消费者
        String topicname = "magnus";
        KafkaConsumerMain kafkaConsumerMain = new KafkaConsumerMain();
        Properties properties = kafkaConsumerMain.loadProperties();
        properties.put("group.id", "magnus");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        if (properties != null) {
            KafkaConsumer kafkaConsumer = new KafkaConsumer<String, String>(properties);
            kafkaConsumer.subscribe(Arrays.asList(topicname));

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
}
