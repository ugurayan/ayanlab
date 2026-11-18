package com.ayanlab.core.config;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.helper.ApiClientHeaders;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class ClientHeadersArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ClientHeaders.class)
                && ClientHeadersPayload.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        ClientHeaders annotation = parameter.getParameterAnnotation(ClientHeaders.class);
        boolean requireTokens = annotation == null || annotation.requireTokens();
        boolean requireAppId = annotation == null || annotation.requireAppId();

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new IllegalStateException("HttpServletRequest is required to resolve client headers");
        }

        String correlationId = requireHeader(request, ApiClientHeaders.CORRELATION_ID);
        String appId = requireAppId
                ? requireHeader(request, ApiClientHeaders.APP_ID)
                : optionalHeader(request, ApiClientHeaders.APP_ID);
        String clientType = requireHeader(request, ApiClientHeaders.CLIENT_TYPE);
        String language = request.getHeader(ApiClientHeaders.CLIENT_LANGUAGE);

        String refreshToken = requireTokens
                ? requireHeader(request, ApiClientHeaders.CLIENT_REFRESH_TOKEN)
                : request.getHeader(ApiClientHeaders.CLIENT_REFRESH_TOKEN);
        String token = requireTokens
                ? requireHeader(request, ApiClientHeaders.CLIENT_TOKEN)
                : request.getHeader(ApiClientHeaders.CLIENT_TOKEN);

        String profileId = request.getHeader(ApiClientHeaders.PROFILE_ID);
        String orgId = request.getHeader(ApiClientHeaders.ORG_ID);

        return new ClientHeadersPayload(correlationId, appId, clientType, refreshToken, token, language, profileId, orgId);
    }

    private static String requireHeader(HttpServletRequest request, String name) throws MissingRequestHeaderException {
        String value = request.getHeader(name);
        if (!StringUtils.hasText(value)) {
            throw new MissingRequestHeaderException(name, null);
        }
        return value;
    }

    private static String optionalHeader(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        return StringUtils.hasText(value) ? value : "";
    }
}
