package software.plusminus.data.event.aspect;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.transaction.annotation.Transactional;
import software.plusminus.data.event.config.DataEventAutoconfig;
import software.plusminus.data.event.service.TransactionService;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionServiceConditionalTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(DataEventAutoconfig.class);

    @Test
    public void beanIsCreatedWhenSpringTxIsOnClasspath() {
        runner.run(context -> assertThat(context).hasSingleBean(TransactionService.class));
    }

    @Test
    @SuppressFBWarnings("DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED")
    public void beanIsSkippedWhenSpringTxIsNotOnClasspath() {
        runner.withClassLoader(new FilteredClassLoader(Transactional.class))
                .run(context -> {
                    assertThat(context).doesNotHaveBean(TransactionService.class);
                    assertThat(context).hasSingleBean(RepositoryEventAspect.class);
                });
    }
}
