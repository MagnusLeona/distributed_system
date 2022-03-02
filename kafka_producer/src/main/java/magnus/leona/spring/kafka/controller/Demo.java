package magnus.leona.spring.kafka.controller;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

public class Demo {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "124.222.129.114:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 1024 * 1024 * 32);
        KafkaProducer kafkaProducer = new KafkaProducer<>(properties);
        String topicname = "magnus";
        for (int i = 0; i < 5; i++) {
            // 指定partitions分区
            ProducerRecord producerRecord = new ProducerRecord<>(topicname, 0, "a", "test message " + i);
            kafkaProducer.send(producerRecord, (recordMetadata, e) -> {
                if (e == null) {
                    // 消息发送成功
                    System.out.println("message sent successfully to topic : " + recordMetadata.topic() + " , partition " + recordMetadata.partition());
                } else {
                    e.printStackTrace();
                }
            }).get();
        }
    }
}
