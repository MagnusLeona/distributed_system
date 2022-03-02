package magnus.leona.spring.kafka.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
public class KafkaController {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("create/record")
    public String createRecord(@RequestBody Map<String, Object> map) {
        log.info("start to send message to topic : " + map.get("message"));
        String topic = "magnus";
        String value = "kafkaTemplate -> execute in transation -> message ";
        kafkaTemplate.executeInTransaction(kafkaOperations -> kafkaOperations.send(topic, value));
        return "success";
    }
}
