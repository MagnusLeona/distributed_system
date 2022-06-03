package magnus.leona.spring.bean;

import org.springframework.stereotype.Component;

@Component
public class Animal {
    String name = "Animal";
    Integer age = 12;
    String color = "red";

    Animal(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    Animal() {

    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
