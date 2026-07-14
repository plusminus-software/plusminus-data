package software.plusminus.audit.context;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import software.plusminus.context.Context;
import software.plusminus.security.Security;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

@ConditionalOnClass(HttpServletRequest.class)
@AllArgsConstructor
@Component
public class DeviceContext implements Context<String> {

    private Context<Security> securityContext;

    @Nullable
    @Override
    public String get() {
        return securityContext.optional()
                .map(Security::getParameters)
                .map(others -> others.get(DeviceSecurityParameterProvider.PARAMETER_NAME))
                .orElse(null);
    }
}
