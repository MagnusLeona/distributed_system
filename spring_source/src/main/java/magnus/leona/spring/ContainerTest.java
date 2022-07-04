package magnus.leona.spring;

import magnus.leona.spring.bean.Cat;
import magnus.leona.spring.bean.Dog;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ContainerTest {
    public static void main(String[] args) {


        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 6);
        Optional<Integer> reduce = integers.stream().map(item -> item).reduce(Integer::sum);
        Integer integer = reduce.get();
        System.out.println("----------------------");
        System.out.println(integer);


        System.out.println("&FactoryBean".substring(1));
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
        annotationConfigApplicationContext.scan("magnus.leona.spring");
        annotationConfigApplicationContext.refresh();
        Dog dog = (Dog) annotationConfigApplicationContext.getBean("dog");
        System.out.println(dog.getName() + dog.getAge());
        System.out.println(dog.getColor());

        Cat cat = (Cat) annotationConfigApplicationContext.getBean("cat");
        dog.setColor("abc");

        String color = cat.getColor();
        System.out.println(color);

        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext();
        classPathXmlApplicationContext.setConfigLocation("classpath:bean.xml");
        classPathXmlApplicationContext.refresh();
    }

}
