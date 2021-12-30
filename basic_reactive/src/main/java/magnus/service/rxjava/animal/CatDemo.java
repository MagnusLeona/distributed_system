package magnus.service.rxjava.animal;

public class CatDemo {

    public static void main(String[] args) {
        Cat.CatFood catFood = new Cat.CatFood();
        Cat.CatFood catFood1 = new Cat.CatFood();
        System.out.println(catFood1 == catFood);

        Cat cat = new Cat();
        String name = Cat.animal.getName();
        System.out.println(name);

        Animal animal = catFood.getAnimal();
        System.out.println(animal.getName());

    }
}
