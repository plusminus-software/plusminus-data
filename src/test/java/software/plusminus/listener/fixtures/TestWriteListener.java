package software.plusminus.listener.fixtures;

import lombok.Getter;
import org.springframework.stereotype.Component;
import software.plusminus.listener.DataAction;
import software.plusminus.listener.WriteListener;

@Component
public class TestWriteListener implements WriteListener<TestEntity> {

    @Getter
    private DataAction lastAction;

    @Override
    public void onWrite(TestEntity object, DataAction action) {
        this.lastAction = action;
    }

    public void reset() {
        lastAction = null;
    }
}
