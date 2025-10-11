package software.plusminus.data.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("software.plusminus.data")
@ComponentScan("software.plusminus.crud")
@ComponentScan("software.plusminus.listener")
public class DataAutoconfig {
}
