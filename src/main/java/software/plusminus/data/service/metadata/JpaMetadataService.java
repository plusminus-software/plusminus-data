package software.plusminus.data.service.metadata;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import software.plusminus.data.exception.NotFoundException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;

@Service
@AllArgsConstructor
@ConditionalOnClass(JpaRepository.class)
public class JpaMetadataService implements MetadataService {

    private EntityManagerFactory entityManagerFactory;

    @Override
    public Class<?> findType(String typeName) {
        return entityManagerFactory.getMetamodel().getEntities().stream()
                .filter(entityType -> entityType.getName().equals(typeName))
                .findFirst()
                .map(EntityType::getJavaType)
                .orElseThrow(NotFoundException::new);
    }
}
