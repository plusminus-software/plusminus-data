package software.plusminus.softdelete;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface TestRepository extends PagingAndSortingRepository<TestEntity, Long> {
}