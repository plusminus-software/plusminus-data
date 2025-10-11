package software.plusminus.listener.fixtures;

import lombok.Getter;
import org.springframework.stereotype.Component;
import software.plusminus.listener.CreateListener;

@Component
public class TestCreateListener implements CreateListener<TestEntity> {

    @Getter
    private boolean triggered;

    @Override
    public void onCreate(TestEntity object) {
        this.triggered = true;
    }

    public void reset() {
        triggered = false;
    }
}
