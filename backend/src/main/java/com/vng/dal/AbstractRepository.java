package com.vng.dal;

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
        session.delete(entity);
    }

    public void flush() {
        session.flush();
    }

    public <T> void saveOrUpdate(T entity) {
        session.saveOrUpdate(entity);
    }

    public <T> List<T> findAll(Class<T> clazz) {
        List<T> entities = session.createQuery("FROM " + clazz.getName(), clazz).getResultList();
        return entities;
    }

    public <T> T findById(Class<T> clazz, Serializable id) {
        T result = (T) session.get(clazz, id);
        return result;
    }

    public <T> T getReferenceById(Class<T> clazz, Serializable id) {
        if (id == null) {
            return null;
        }
        T result = (T) session.getReference(clazz, id);
        return result;
    }

}
