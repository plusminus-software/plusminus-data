package software.plusminus.audit.fixtures;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InnerEntityRepository extends JpaRepository<InnerEntity, Long> {
}
