package software.plusminus.data.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import software.plusminus.data.event.config.DataEventAutoconfig;

@SpringBootApplication
@Import(DataEventAutoconfig.class)
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
