package software.plusminus.softdelete.interceptor;

import org.springframework.stereotype.Component;
import software.plusminus.hibernate.HibernateFilter;

@Component
public class SoftDeleteFilter implements HibernateFilter {

    @Override
    public String filterName() {
        return "softDeleteFilter";
    }

}
