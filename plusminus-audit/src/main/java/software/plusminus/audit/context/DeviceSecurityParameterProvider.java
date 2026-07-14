package software.plusminus.audit.context;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import software.plusminus.context.Context;
import software.plusminus.security.service.SecurityParameterProvider;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

@ConditionalOnClass(HttpServletRequest.class)
@AllArgsConstructor
@Component
public class DeviceSecurityParameterProvider implements SecurityParameterProvider {

    public static final String PARAMETER_NAME = "device";

    private Optional<Context<HttpServletRequest>> requestContext;

    @Nullable
    @Override
    public Map.Entry<String, String> providerParameter() {
        return requestContext.flatMap(Context::optional)
                .map(request -> request.getParameter(PARAMETER_NAME))
                .map(parameter -> new AbstractMap.SimpleEntry<>(PARAMETER_NAME, parameter))
                .orElse(null);
    }
}
