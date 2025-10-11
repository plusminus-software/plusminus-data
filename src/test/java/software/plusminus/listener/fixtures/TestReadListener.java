package software.plusminus.listener.fixtures;

import lombok.Getter;
import org.springframework.stereotype.Component;
import software.plusminus.listener.ReadListener;

@Component
public class TestReadListener implements ReadListener<TestEntity> {

    @Getter
    private boolean triggered;

    @Override
    public void onRead(TestEntity object) {
        this.triggered = true;
    }

    public void reset() {
        triggered = false;
    }
}
