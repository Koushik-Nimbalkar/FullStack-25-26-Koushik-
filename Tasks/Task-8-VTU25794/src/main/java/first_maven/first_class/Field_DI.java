package first_maven.first_class;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Field_DI {

    @Autowired
    private Engine engine;

    public void drive() {
        engine.start();
        System.out.println("Car is moving from Field DI");
    }
}