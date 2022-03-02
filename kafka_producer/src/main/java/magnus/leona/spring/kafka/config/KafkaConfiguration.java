package magnus.leona.spring.kafka.config;

import magnus.distributed.dict.MagnusKafkaDict;
import org.apache.kafka.clients.KafkaClient;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaConfiguration {

    public int acks;
    public String bootstrapServers;

    @Bean
    public KafkaProducer kafkaProducer() {
        Properties properties = new Properties();
        // 需要保证Exactly once，引入幂等性和事务
        properties.put(ProducerConfig.ACKS_CONFIG, "-1");
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "magnusProducer");
        properties.put(MagnusKafkaDict.KEY_SERIALIZER, StringSerializer.class);
        // 解决乱序问题，最大同时请求的窗口数为不大于5，即可保证消息发送的顺序问题。（需要开启幂等性支持）
        // Kafka保证生产者不重复生产的思路是
        // 1.acks设置为-1，每次必须全部副本全部同步完成，再回应。
        // 2.开启幂等性，<pid, topic, partition, sequence number>必须全部满足要求才能接受此消息。
        // 3.开启事务（Exactly once）
        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        properties.put(MagnusKafkaDict.VALUE_SERIALIZER, StringSerializer.class);
        properties.put(MagnusKafkaDict.KAFKA_BOOTSTRAP_SERVERS, bootstrapServers);
        KafkaProducer kafkaProducer = new KafkaProducer<>(properties);
        kafkaProducer.initTransactions();
        return kafkaProducer;
    }
}
