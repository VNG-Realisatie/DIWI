package nl.vng.diwi.rest.pac4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.WebContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.ext.Providers;

final class WebContextImplementation implements WebContext {
    private final ContainerRequestContext requestContext;
    private final HttpServletRequest request;

    private Map<String, String> responseHeaders;
    private String responseContentType;

    private HashMap<String, Object> requestAttributes;

    private NewCookie responseCookie;
    private MultivaluedHashMap<String, String> parameters;
    private Providers providers;

    public WebContextImplementation(ContainerRequestContext requestContext, HttpServletRequest request) {
        this.requestContext = requestContext;
        this.responseHeaders = new HashMap<String, String>();
        this.requestAttributes = new HashMap<String, Object>();
        this.request = request;
    }

    @Override
    public void setResponseHeader(String name, String value) {
        getResponseHeaders().put(name, value);

    }

    @Override
    public void setResponseContentType(String content) {
        responseContentType = content;
    }

    @Override
    public void setRequestAttribute(String name, Object value) {
        requestAttributes.put(name, value);
    }

    @Override
    public boolean isSecure() {
        return getRequestContext().getSecurityContext().isSecure();
    }

    @Override
    public int getServerPort() {
        return getRequestContext().getUriInfo().getRequestUri().getPort();
    }

    @Override
    public String getServerName() {
        return getRequestContext().getUriInfo().getRequestUri().getHost();
    }

    @Override
    public String getScheme() {
        return getRequestContext().getUriInfo().getRequestUri().getScheme();
    }

    @Override
    public Optional<String> getResponseHeader(String name) {
        return Optional.of(getResponseHeaders().getOrDefault(name, null));
    }

    @Override
    public Map<String, String[]> getRequestParameters() {
        return extractedParameters().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().toArray(new String[e.getValue().size()])));
    }

    @Override
    public Optional<String> getRequestParameter(String name) {
        return Optional.ofNullable(extractedParameters().getFirst(name));
    }

    @Override
    public String getRequestMethod() {
        return getRequestContext().getMethod();
    }

    @Override
    public Optional<String> getRequestHeader(String name) {
        return Optional.ofNullable(request.getHeader(name));
    }

    @Override
    public Collection<Cookie> getRequestCookies() {
        return getRequestContext().getCookies().values().stream().map(c -> {
            Cookie newCookie = new Cookie(c.getName(), c.getValue());
            newCookie.setDomain(c.getDomain());
            newCookie.setPath(c.getPath());
            return newCookie;
        }).toList();
    }

    @Override
    public Optional<Object> getRequestAttribute(String name) {
        return Optional.ofNullable(requestAttributes.getOrDefault(name, null));
    }

    @Override
    public String getRemoteAddr() {
        return null; // Not possible in jax rs?
    }

    @Override
    public String getPath() {
        return "/" + getRequestContext().getUriInfo().getPath();
    }

    @Override
    public String getFullRequestURL() {
        return getRequestContext().getUriInfo().getRequestUri().toString();
    }

    @Override
    public void addResponseCookie(Cookie cookie) {
        NewCookie c = new NewCookie.Builder(cookie.getName()).value(cookie.getValue()).path(cookie.getPath())
                .domain(cookie.getDomain()).maxAge(cookie.getMaxAge()).secure(cookie.isSecure()).build();
        this.responseCookie = c;

    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public ContainerRequestContext getRequestContext() {
        return requestContext;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public NewCookie getResponseCookie() {
        return responseCookie;
    }

    private MultivaluedMap<String, String> extractedParameters() {
        if (parameters == null) {
            MultivaluedHashMap<String, String> multivaluedHashMap = new MultivaluedHashMap<>();
            // efficient
            multivaluedHashMap.putAll(requestContext.getUriInfo().getQueryParameters());
            parameters = multivaluedHashMap;
            if (MediaType.APPLICATION_FORM_URLENCODED_TYPE.isCompatible(requestContext.getMediaType())) {
                readAndResetEntityStream(stream -> {
                    try {
                        Form form = providers.getMessageBodyReader(Form.class, Form.class, new Annotation[0],
                                MediaType.APPLICATION_FORM_URLENCODED_TYPE).readFrom(Form.class, Form.class,
                                        new Annotation[0], MediaType.APPLICATION_FORM_URLENCODED_TYPE,
                                        requestContext.getHeaders(), stream);
                        form.asMap().forEach(parameters::addAll);
                        return null;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        return parameters;
    }

    private <T> T readAndResetEntityStream(Function<InputStream, T> f) {
        try (InputStream entityStream = requestContext.getEntityStream()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = entityStream.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            ByteArrayInputStream stream = new ByteArrayInputStream(baos.toByteArray());
            try {
                return f.apply(stream);
            } finally {
                stream.reset();
                requestContext.setEntityStream(stream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
