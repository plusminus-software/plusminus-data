package software.plusminus.dehydration.fixtures;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

public class AProxy extends A implements HibernateProxy {

    @Override
    public Object writeReplace() {
        return this;
    }

    @Override
    public LazyInitializer getHibernateLazyInitializer() {
        throw new UnsupportedOperationException();
    }
}
