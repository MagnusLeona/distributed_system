package magnus.leona.spring.configuration;

import magnus.leona.spring.bean.Dog;
import magnus.leona.spring.bean.Pig;
import org.springframework.context.annotation.Bean;

public class AnimalFactory {

    @Bean
    public Pig pig(Dog dog) {
        return new Pig();
    }
}
