package magnus.leona.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.*;

import java.util.Properties;

@DisplayName("magnus-kafka-producer")
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MagnusKafkaProducer {

    private KafkaProducer<String, String> kafkaProducer;
    private final String topicName = "magnus";

    @DisplayName("init")
    @BeforeAll()
    public void init() {
        log.info("magnus-kafka-producer starts");
        this.produceProducer();
    }

    @Test
    @DisplayName("producer")
    public void testProducer() {
        Assertions.assertDoesNotThrow(() -> {
            kafkaProducer.beginTransaction();
            kafkaProducer.send(new ProducerRecord<>(topicName, "test-message")).get();
            kafkaProducer.commitTransaction();
        });
    }

    @Test
    @DisplayName("producer2")
    public void testProducer2() {
        Assertions.assertDoesNotThrow(() -> {
            kafkaProducer.beginTransaction();
            kafkaProducer.send(new ProducerRecord<>(topicName, "test-messsage")).get();
            kafkaProducer.commitTransaction();
        });
    }

    public void produceProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "124.222.129.114:9092");
        // 响应模式
        properties.put(ProducerConfig.ACKS_CONFIG, "-1");
        // 开启幂等性支持
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "magnus-1");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // 允许最大无ack响应仍然继续发送的消息数量
        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "magnus-1");
        // 发往broker的请求每批次限制大小 16kb
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16 * 1024);
        // 多少秒延迟之后把缓冲区的数据发送出去，和batchSize配合工作
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        // 缓冲区最大值（没有发送出去的消息），当达到max.block.ms的时间后，producer将阻塞，不再往缓冲区放数据。1Gb
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 1024 * 1024 * 1024);
        kafkaProducer = new KafkaProducer<>(properties);
        kafkaProducer.initTransactions();
    }


}
