package magnus.leona.spring.kafka.business.controller;

import magnus.leona.config.ZookeeperTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ZookeeperController {

    @Autowired
    ZookeeperTemplate zookeeperTemplate;

    @PostMapping("create/node")
    public String createNode(@RequestBody Map<String, String> map) {
        boolean node = zookeeperTemplate.createNode(map.get("node"), map.get("data"));
        if (node) {
            return "success";
        }
        return "failure";
    }
}
