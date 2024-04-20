package nl.vng.diwi.dal;

import java.util.List;

import org.hibernate.Session;

import nl.vng.diwi.models.SelectModel;

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

    public List<SelectModel> getPriorities() {
        return session.createNativeQuery(String.format("""
            WITH priorities AS (
                SELECT p.project_priorisering_value_id AS id, (p.ordinal_level || ' ' || p.value_label) AS name
                FROM %s.project_priorisering_value_state p
                WHERE p.change_end_date IS NULL )
            SELECT * FROM priorities p
            ORDER BY p.name COLLATE "diwi_numeric" ASC""", GenericRepository.VNG_SCHEMA_NAME), SelectModel.class).list();
    }
}
