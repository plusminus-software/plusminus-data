package software.plusminus.dehydration;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
@Component
public class DehydrationContext {

    public static final ThreadLocal<Boolean> DEHYDRATE = ThreadLocal.withInitial(() -> false);

    private HttpServletRequest request;

    public boolean shouldDehydrate() {
        if (!isDehydrationEnabled()) {
            return false;
        }
        return DEHYDRATE.get();
    }

    public void runWithDehydration(Runnable runnable) {
        if (!isDehydrationEnabled()) {
            runnable.run();
            return;
        }
        try {
            DEHYDRATE.set(true);
            runnable.run();
        } finally {
            DEHYDRATE.set(false);
        }
    }

    private boolean isDehydrationEnabled() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return false;
        }
        String attribute = request.getParameter("dehydrate");
        return attribute != null && attribute.equalsIgnoreCase("true");
    }
}
