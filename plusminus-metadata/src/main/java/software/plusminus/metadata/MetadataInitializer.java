package software.plusminus.metadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MetadataInitializer implements BeanPostProcessor {

    @Autowired
    void init(List<MetadataProvider> providers) {
        providers.stream()
                .map(MetadataProvider::provideClasses)
                .flatMap(List::stream)
                .forEach(MetadataContext::addClass);
    }
}
