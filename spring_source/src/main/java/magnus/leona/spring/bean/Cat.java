package magnus.leona.spring.bean;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
public class Cat extends Animal {
    String name = "Cat";
    Integer age = 10;

    Cat(String name, Integer age) {
        super(name, age);
    }

    Cat() {
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }
}
