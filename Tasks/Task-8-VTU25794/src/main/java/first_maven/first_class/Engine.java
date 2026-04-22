package first_maven.first_class;

import org.springframework.stereotype.Component;

@Component
public class Engine {

    public void start() {
        System.out.println("The Engine is going to start!");
    }
}