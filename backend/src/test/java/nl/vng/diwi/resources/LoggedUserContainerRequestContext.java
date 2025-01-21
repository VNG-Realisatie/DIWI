package nl.vng.diwi.resources;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import nl.vng.diwi.security.LoggedUser;

final class LoggedUserContainerRequestContext implements ContainerRequestContext {

    private LoggedUser loggedUser;

    LoggedUserContainerRequestContext(LoggedUser loggedUser) {
        this.loggedUser = loggedUser;
    }

    @Override
    public Object getProperty(String name) {
        return loggedUser;
    }

    @Override
    public Collection<String> getPropertyNames() {
        throw new UnsupportedOperationException("Unimplemented method 'getPropertyNames'");
    }

    @Override
    public void setProperty(String name, Object object) {
        throw new UnsupportedOperationException("Unimplemented method 'setProperty'");
    }

    @Override
    public void removeProperty(String name) {
        throw new UnsupportedOperationException("Unimplemented method 'removeProperty'");
    }

    @Override
    public UriInfo getUriInfo() {
        throw new UnsupportedOperationException("Unimplemented method 'getUriInfo'");
    }

    @Override
    public void setRequestUri(URI requestUri) {
        throw new UnsupportedOperationException("Unimplemented method 'setRequestUri'");
    }

    @Override
    public void setRequestUri(URI baseUri, URI requestUri) {
        throw new UnsupportedOperationException("Unimplemented method 'setRequestUri'");
    }

    @Override
    public Request getRequest() {
        throw new UnsupportedOperationException("Unimplemented method 'getRequest'");
    }

    @Override
    public String getMethod() {
        throw new UnsupportedOperationException("Unimplemented method 'getMethod'");
    }

    @Override
    public void setMethod(String method) {
        throw new UnsupportedOperationException("Unimplemented method 'setMethod'");
    }

    @Override
    public MultivaluedMap<String, String> getHeaders() {
        throw new UnsupportedOperationException("Unimplemented method 'getHeaders'");
    }

    @Override
    public String getHeaderString(String name) {
        throw new UnsupportedOperationException("Unimplemented method 'getHeaderString'");
    }

    @Override
    public Date getDate() {
        throw new UnsupportedOperationException("Unimplemented method 'getDate'");
    }

    @Override
    public Locale getLanguage() {
        throw new UnsupportedOperationException("Unimplemented method 'getLanguage'");
    }

    @Override
    public int getLength() {
        throw new UnsupportedOperationException("Unimplemented method 'getLength'");
    }

    @Override
    public MediaType getMediaType() {
        throw new UnsupportedOperationException("Unimplemented method 'getMediaType'");
    }

    @Override
    public List<MediaType> getAcceptableMediaTypes() {
        throw new UnsupportedOperationException("Unimplemented method 'getAcceptableMediaTypes'");
    }

    @Override
    public List<Locale> getAcceptableLanguages() {
        throw new UnsupportedOperationException("Unimplemented method 'getAcceptableLanguages'");
    }

    @Override
    public Map<String, Cookie> getCookies() {
        throw new UnsupportedOperationException("Unimplemented method 'getCookies'");
    }

    @Override
    public boolean hasEntity() {
        throw new UnsupportedOperationException("Unimplemented method 'hasEntity'");
    }

    @Override
    public InputStream getEntityStream() {
        throw new UnsupportedOperationException("Unimplemented method 'getEntityStream'");
    }

    @Override
    public void setEntityStream(InputStream input) {
        throw new UnsupportedOperationException("Unimplemented method 'setEntityStream'");
    }

    @Override
    public SecurityContext getSecurityContext() {
        throw new UnsupportedOperationException("Unimplemented method 'getSecurityContext'");
    }

    @Override
    public void setSecurityContext(SecurityContext context) {
        throw new UnsupportedOperationException("Unimplemented method 'setSecurityContext'");
    }

    @Override
    public void abortWith(Response response) {
        throw new UnsupportedOperationException("Unimplemented method 'abortWith'");
    }
}
