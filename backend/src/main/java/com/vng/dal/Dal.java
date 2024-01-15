package com.vng.dal;

import org.hibernate.Session;

/**The dal owns 1 session. Use the dal
* to initiate repos, which need a session to operate with.
*/
public class Dal implements AutoCloseable
{
    private Session session = null;

    /**
     * Not accessible from outside, use DalFactory to get a dal
     * @param session
     */
    Dal(Session session)
    {
        this.session = session;
    }

    public Session getSession()
    {
        return session;
    }

    public AutoCloseTransaction beginTransaction() {
        return new AutoCloseTransaction(session);
    }

    /**
     * Will try to close a session which is still open. Should be used to clean
     * up resources at the end of a dal session. If a transaction is still open,
     * it will be rolled back. Will also accept a dal reference as null
     *
     * @param dal
     *            Dal with a potentionally open session. Can be null, in which
     *            case nothing will be done.
     */
    public static void closeSessionIfOpened(Dal dal)
    {
        if ((dal != null) && (dal.session != null))
        {
            if (dal.getSession().isConnected())
            {
                if (dal.getSession().getTransaction().isActive())
                {
                    dal.getSession().getTransaction().rollback();
                }
                dal.getSession().close();
            }
        }
    }

    @Override
    public void close()
    {
        closeSessionIfOpened(this);
    }

}
