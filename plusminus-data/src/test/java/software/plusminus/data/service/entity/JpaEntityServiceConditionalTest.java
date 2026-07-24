package software.plusminus.data.service.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManagerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class JpaEntityServiceConditionalTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(JpaEntityService.class);

    @Test
    public void beanIsCreatedWhenJpaIsOnClasspath() {
        runner.withBean(EntityManagerFactory.class, () -> mock(EntityManagerFactory.class))
                .run(context -> assertThat(context).hasSingleBean(EntityService.class));
    }

    @Test
    @SuppressFBWarnings("DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED")
    public void beanIsSkippedWhenJpaIsNotOnClasspath() {
        runner.withClassLoader(new FilteredClassLoader(JpaRepository.class))
                .run(context -> assertThat(context).doesNotHaveBean(EntityService.class));
    }
}
