package software.plusminus.metadata;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;

@Component
@AllArgsConstructor
@ConditionalOnClass(JpaRepository.class)
public class JpaMetadataProvider implements MetadataProvider {

    private EntityManagerFactory entityManagerFactory;

    @Override
    public List<Class<?>> provideClasses() {
        return entityManagerFactory.getMetamodel().getEntities().stream()
                .map(EntityType::getJavaType)
                .collect(Collectors.toList());
    }
}
