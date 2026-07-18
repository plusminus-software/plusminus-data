package software.plusminus.metadata;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class MetadataInitializer implements SmartInitializingSingleton {

    private List<MetadataProvider> providers;

    @Override
    public void afterSingletonsInstantiated() {
        providers.stream()
                .map(MetadataProvider::provideClasses)
                .flatMap(List::stream)
                .forEach(MetadataContext::addClass);
    }
}
