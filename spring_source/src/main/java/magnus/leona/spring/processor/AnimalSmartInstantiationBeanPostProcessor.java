package magnus.leona.spring.processor;

import magnus.leona.spring.bean.Animal;
import magnus.leona.spring.bean.Pig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Constructor;

@Configuration
public class AnimalSmartInstantiationBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {
    @Override
    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException {
        if (beanName.equals("animal")) {
            Class<Animal> animalClass = Animal.class;
            Constructor<?>[] declaredConstructors = animalClass.getDeclaredConstructors();
            return declaredConstructors;
        }
        return null;
    }
}
