package software.plusminus.fixtures;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Service
public class TransactionService {

    @Transactional
    public <T> T run(Supplier<T> supplier) {
        return supplier.get();
    }

    @Transactional
    public void run(Runnable runnable) {
        runnable.run();
    }
}
