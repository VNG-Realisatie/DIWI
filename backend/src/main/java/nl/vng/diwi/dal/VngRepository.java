package nl.vng.diwi.dal;

import org.hibernate.Session;

public class VngRepository extends AbstractRepository {

    public VngRepository(Session session) {
        super(session);
    }

    private ProjectsDAO projectsDAO;

    private HouseblockDAO houseblockDAO;

    private MilestoneDAO milestoneDAO;

    private UserGroupDAO usergroupDAO;

    private UserDAO userDAO;

    private PropertyDAO propertyDAO;

    private BlueprintDAO blueprintDAO;

    public ProjectsDAO getProjectsDAO() {
        if (projectsDAO == null) {
            projectsDAO = new ProjectsDAO(session);
        }
        return projectsDAO;
    }

    public HouseblockDAO getHouseblockDAO() {
        if (houseblockDAO == null) {
            houseblockDAO = new HouseblockDAO(session);
        }
        return houseblockDAO;
    }

    public MilestoneDAO getMilestoneDAO() {
        if (milestoneDAO == null) {
            milestoneDAO = new MilestoneDAO(session);
        }
        return milestoneDAO;
    }

    public UserGroupDAO getUsergroupDAO() {
        if (usergroupDAO == null) {
            usergroupDAO = new UserGroupDAO(session);
        }
        return usergroupDAO;
    }

    public UserDAO getUserDAO() {
        if (userDAO == null) {
            userDAO = new UserDAO(session);
        }
        return userDAO;
    }

    public PropertyDAO getPropertyDAO() {
        if (propertyDAO == null) {
            propertyDAO = new PropertyDAO(session);
        }
        return propertyDAO;
    }

    public BlueprintDAO getBlueprintDAO() {
        if (blueprintDAO == null) {
            blueprintDAO = new BlueprintDAO(session);
        }
        return blueprintDAO;
    }

}
