package magnus.leona;

import org.apache.kafka.clients.GroupRebalanceConfig;
import org.apache.kafka.clients.admin.PartitionReassignment;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@DisplayName("kafka-consumer")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 标记生命周期以类为单位
public class KafkaConsumerMainTest {

    private String topicName = "magnus";
    private KafkaConsumer<String, String> kafkaConsumer;

    @BeforeAll
    public void init() {
        getInstance();
    }

    public void getInstance() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "124.222.129.114:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // 是否自动提交commit
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        // 自动提交的时间间隔
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "testGroup");
        kafkaConsumer = new KafkaConsumer<>(properties);
        // 默认分配消费的分区
        kafkaConsumer.subscribe(List.of(topicName));
        // assign方法可以指定要消费的分区
    }

    @Test
    @DisplayName("test-consumes")
    public void testConsumes() {
        while (true) {
            ConsumerRecords<String, String> poll = kafkaConsumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> record : poll) {
                System.out.println(record);
            }
        }
    }
}
