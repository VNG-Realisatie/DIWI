package nl.vng.diwi.dal;

import org.hibernate.Session;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractRepository {

    protected Session session;

    public AbstractRepository(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public AutoCloseTransaction beginTransaction() {
        return new AutoCloseTransaction(session);
    }

    public <T> T persist(T entity) {
        session.persist(entity);
        return entity;
    }

    public <T> void delete(T entity) {
        session.remove(entity);
    }

    public void flush() {
        session.flush();
    }

    public <T> List<T> findAll(Class<T> clazz) {
        return session.createQuery("FROM " + clazz.getName(), clazz).getResultList();
    }

    public <T> T findById(Class<T> clazz, Serializable id) {
        return session.get(clazz, id);
    }

    public <T> T getReferenceById(Class<T> clazz, Serializable id) {
        if (id == null) {
            return null;
        }
        return session.getReference(clazz, id);
    }

    public String fromJavaListToSqlArrayLiteral(List<String> javaList) {
        StringBuilder sqlString = new StringBuilder();
        sqlString.append("{");
        sqlString.append(String.join(",", javaList));
        sqlString.append("}");
        return sqlString.toString();
    }
}
