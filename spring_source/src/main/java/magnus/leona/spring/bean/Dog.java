package magnus.leona.spring.bean;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@DependsOn("cat")
@Component
public class Dog extends Animal {
    String name = "dog";
    Integer age = 11;

    Dog(String name, Integer age) {
        super(name, age);
    }


    public Dog() {

    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }
}
