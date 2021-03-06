package software.plusminus.data.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("software.plusminus.data")
@EnableJpaRepositories("software.plusminus.data")
public class DataAutoconfig {
}
