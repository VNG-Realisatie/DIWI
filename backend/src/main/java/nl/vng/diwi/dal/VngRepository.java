package nl.vng.diwi.dal;

import org.hibernate.Session;

public class VngRepository extends AbstractRepository {

    public VngRepository(Session session) {
        super(session);
    }

    private ProjectsDAO projectsDAO;

    private HouseblockDAO houseblockDAO;

    private MilestoneDAO milestoneDAO;

    private OrganizationsDAO organizationDAO;

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

    public OrganizationsDAO getOrganizationDAO() {
        if (organizationDAO == null) {
            organizationDAO = new OrganizationsDAO(session);
        }
        return organizationDAO;
    }

    public PropertyDAO getPropertyDAO() {
        if (propertyDAO == null) {
            propertyDAO = new PropertyDAO(session);
        }
        return propertyDAO;
    }

}
