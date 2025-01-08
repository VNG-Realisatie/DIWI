package nl.vng.diwi.dal;

import static nl.vng.diwi.util.Json.MAPPER;

import com.fasterxml.jackson.databind.JavaType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

public class JsonListType implements DynamicParameterizedType, UserType<ArrayList<?>> {


    private JavaType valueType = null;

    @Override
    public void setParameterValues(Properties parameters) {
        try {
            // Get entity class
            Class<?> entityClass = Class.forName(parameters.getProperty(DynamicParameterizedType.ENTITY));
            Field property = null;

            // Find the field
            while (property == null && entityClass != null) {
                try {
                    property = entityClass.getDeclaredField(parameters.getProperty(DynamicParameterizedType.PROPERTY));
                } catch (NoSuchFieldException e) {
                    entityClass = entityClass.getSuperclass();
                }
            }
            if (property != null) {
                ParameterizedType listType = (ParameterizedType) property.getGenericType();
                Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                valueType = MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, listClass);
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }

    }

    @Override
    public int getSqlType() {
        return Types.LONGVARCHAR;
    }

    @Override
    public Class returnedClass() {
        return ArrayList.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(ArrayList o1, ArrayList o2) {
        return Objects.equals(o1, o2);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public int hashCode(ArrayList o) {
        return Objects.hashCode(o);
    }

    @Override
    public ArrayList<?> nullSafeGet(ResultSet resultSet, int i, SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws SQLException {
        final String cellContent = resultSet.getString(i);
        if (cellContent == null) {
            return null;
        }
        try {
            return MAPPER.readValue(cellContent.getBytes(StandardCharsets.UTF_8), valueType);
        } catch (final Exception ex) {
            throw new RuntimeException("Failed to convert String to JsonListType: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, ArrayList value, int i, SharedSessionContractImplementor sharedSessionContractImplementor) throws SQLException {
        if (value == null) {
            st.setNull(i, Types.OTHER);
            return;
        }
        try {
            final StringWriter w = new StringWriter();
            MAPPER.writeValue(w, value);
            w.flush();
            st.setObject(i, w.toString(), Types.OTHER);
        } catch (final Exception ex) {
            throw new RuntimeException("Failed to convert JsonListType to String: " + ex.getMessage(), ex);
        }
    }

    @Override
    public ArrayList<?> deepCopy(ArrayList value) {
        if (value == null) {
            return null;
        } else {
            return new ArrayList<>(value);
        }
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(ArrayList o) {
        return deepCopy(o);
    }

    @Override
    public ArrayList<?> assemble(Serializable serializable, Object o) {
        return deepCopy((ArrayList<?>) serializable);
    }

}
