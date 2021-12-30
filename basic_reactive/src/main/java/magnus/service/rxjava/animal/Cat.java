package magnus.service.rxjava.animal;

import lombok.Data;

@Data
public class Cat {
    String name;
    static Animal animal;
    {
        animal = new Animal();
        animal.setName("cattttt");
    }
    static class CatFood {
        String name;

        public Animal getAnimal() {
            return animal;
        }
    }
}
