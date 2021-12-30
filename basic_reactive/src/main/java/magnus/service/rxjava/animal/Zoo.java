package magnus.service.rxjava.animal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Iterator;

@Data
@NoArgsConstructor
public class Zoo {

    public ArrayList<Animal> animals = new ArrayList();
    public String name;

    Zoo(String name) {
        this.name = name;
    }

    Zoo(ArrayList<Animal> as) {
        as.stream().forEach(animal -> this.animals.add(animal));
    }

    public static ArrayList<Zoo> ZooBuilder() {
        ArrayList<Zoo> zoos = new ArrayList<>();
        Animal animal1 = new Animal("a1", 1, "a1");
        Animal animal2 = new Animal("a2", 1, "a2");
        Animal animal3 = new Animal("a3", 1, "a3");
        Animal animal4 = new Animal("a4", 1, "a4");
        Animal animal5 = new Animal("a5", 1, "a5");
        ArrayList<Animal> animals = new ArrayList<>();
        animals.add(animal1);
        animals.add(animal2);
        animals.add(animal3);
        animals.add(animal4);
        animals.add(animal5);

        Zoo zoo1 = new Zoo(animals);
        zoo1.setName("zoosA");
        Zoo zoo2 = new Zoo(animals);
        zoo2.setName("zoosB");
        Zoo zoo3 = new Zoo(animals);
        zoo3.setName("zoosC");
        Zoo zoo4 = new Zoo(animals);
        zoo4.setName("zoosD");

        zoos.add(zoo1);
        zoos.add(zoo2);
        zoos.add(zoo3);
        zoos.add(zoo4);
        return zoos;
    }
}
