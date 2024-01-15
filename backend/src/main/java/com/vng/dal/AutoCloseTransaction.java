package com.vng.dal;

import org.hibernate.Session;

/**
 * Will automatically rollback on exception or not committed for some other reason.
 * Calling commit will prevent
 *
 * @author emiel
 */
public class AutoCloseTransaction implements AutoCloseable {
    private org.hibernate.Transaction transaction;

    public AutoCloseTransaction(Session session) {
        this.transaction = session.beginTransaction();
    }

    @Override
    public void close() {
        if (transaction.isActive()) {
            transaction.rollback();
        }
    }

    public void commit() {
        transaction.commit();
    }

    public void rollback() {
        transaction.rollback();
    }
}
