package first_maven.first_class;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

    public static void main(String[] args) {

        ApplicationContext bean =
                new AnnotationConfigApplicationContext(Config.class);

        Field_DI car1 = bean.getBean(Field_DI.class);
        car1.drive();

        Con_DI car2 = bean.getBean(Con_DI.class);
        car2.drive();

        Set_DI car3 = bean.getBean(Set_DI.class);
        car3.drive();

        ((AnnotationConfigApplicationContext) bean).close();
    }
}