package magnus.leona.spring.processor;

import magnus.leona.spring.bean.Dog;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnimalInstantiationBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (beanClass.equals(Dog.class)) {
            System.out.println("This is dog class!");
        }
        return null;
    }
}
