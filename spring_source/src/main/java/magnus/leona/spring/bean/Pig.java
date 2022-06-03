package magnus.leona.spring.bean;

import org.springframework.beans.factory.annotation.Autowired;

public class Pig {

    String food;

    @Autowired
    Pig(String food) {
        this.food = food;
    }

    public Pig() {
    }
}
