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

    private PropertyDAO propertyDAO;

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

    public PropertyDAO getPropertyDAO() {
        if (propertyDAO == null) {
            propertyDAO = new PropertyDAO(session);
        }
        return propertyDAO;
    }

}
