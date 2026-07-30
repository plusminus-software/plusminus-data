package software.plusminus.data.event.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@ConditionalOnClass(Transactional.class)
@Service
public class TransactionService {

    @Transactional
    public void run(Runnable operation) {
        operation.run();
    }

    @Transactional
    public <T> T run(Supplier<T> operation) {
        return operation.get();
    }
}
