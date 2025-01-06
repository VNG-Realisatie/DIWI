package nl.vng.diwi.generic;

import org.hibernate.Session;

public interface CopyObject {

    Object getCopyWithoutMilestones(Session session);
}
