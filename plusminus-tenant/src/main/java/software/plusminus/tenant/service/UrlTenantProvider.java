package software.plusminus.tenant.service;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import software.plusminus.context.WritableContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

@Order(2)
@AllArgsConstructor
@ConditionalOnClass(HttpServletRequest.class)
@ConditionalOnProperty(name = "tenant.useHostAsTenant")
@Component
public class UrlTenantProvider implements TenantProvider {

    private WritableContext<HttpServletRequest> requestContext;
    
    @Nullable
    @Override
    public String currentTenant() {
        Optional<HttpServletRequest> request = requestContext.optional();
        if (!request.isPresent()) {
            return null;
        }
        URL url;
        try {
            url = new URL(request.get().getRequestURL().toString());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
        return url.getHost();
    }
}
