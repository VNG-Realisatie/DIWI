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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPropertyNames'");
    }

    @Override
    public void setProperty(String name, Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setProperty'");
    }

    @Override
    public void removeProperty(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeProperty'");
    }

    @Override
    public UriInfo getUriInfo() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUriInfo'");
    }

    @Override
    public void setRequestUri(URI requestUri) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRequestUri'");
    }

    @Override
    public void setRequestUri(URI baseUri, URI requestUri) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRequestUri'");
    }

    @Override
    public Request getRequest() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRequest'");
    }

    @Override
    public String getMethod() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMethod'");
    }

    @Override
    public void setMethod(String method) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setMethod'");
    }

    @Override
    public MultivaluedMap<String, String> getHeaders() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHeaders'");
    }

    @Override
    public String getHeaderString(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHeaderString'");
    }

    @Override
    public Date getDate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDate'");
    }

    @Override
    public Locale getLanguage() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLanguage'");
    }

    @Override
    public int getLength() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLength'");
    }

    @Override
    public MediaType getMediaType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMediaType'");
    }

    @Override
    public List<MediaType> getAcceptableMediaTypes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAcceptableMediaTypes'");
    }

    @Override
    public List<Locale> getAcceptableLanguages() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAcceptableLanguages'");
    }

    @Override
    public Map<String, Cookie> getCookies() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCookies'");
    }

    @Override
    public boolean hasEntity() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasEntity'");
    }

    @Override
    public InputStream getEntityStream() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEntityStream'");
    }

    @Override
    public void setEntityStream(InputStream input) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setEntityStream'");
    }

    @Override
    public SecurityContext getSecurityContext() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSecurityContext'");
    }

    @Override
    public void setSecurityContext(SecurityContext context) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setSecurityContext'");
    }

    @Override
    public void abortWith(Response response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'abortWith'");
    }
}
