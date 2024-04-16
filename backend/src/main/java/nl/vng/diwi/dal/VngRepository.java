package nl.vng.diwi.dal;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;

import nl.vng.diwi.models.SelectModel;
import org.hibernate.query.SelectionQuery;

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

    public List<SelectModel> getMunicipalityRoles() {
        return session.createNativeQuery(String.format("""
            SELECT gr.project_gemeenterol_value_id AS id, gr.value_label AS name FROM %s.project_gemeenterol_value_state gr
            WHERE gr.change_end_date IS NULL
            ORDER BY gr.value_label ASC""", GenericRepository.VNG_SCHEMA_NAME), SelectModel.class).list();
    }
    public List<SelectModel> getMunicipalities() {
        return session.createNativeQuery(String.format("""
            SELECT gs.gemeente_id AS id, gs.waarde_label AS name FROM %s.gemeente_state gs
            WHERE gs.change_end_date IS NULL
            ORDER BY gs.waarde_label ASC""", GenericRepository.VNG_SCHEMA_NAME), SelectModel.class).list();
    }

    public List<SelectModel> getBuurts(List<UUID> wijkIds) {
        String sql = "SELECT bs.buurt_id AS id, bs.waarde_label AS name FROM %s.buurt_state bs " +
            "   WHERE bs.change_end_date IS NULL " +
            ((!wijkIds.isEmpty()) ? " AND bs.wijk_id IN :wijkIds " : "") +
            " ORDER BY bs.waarde_label ASC ";

        SelectionQuery<SelectModel> query = session.createNativeQuery(String.format(sql, GenericRepository.VNG_SCHEMA_NAME), SelectModel.class);
        if (!wijkIds.isEmpty()) {
            query.setParameterList("wijkIds", wijkIds);
        }

        return query.list();
    }

    public List<SelectModel> getWijks(List<UUID> gemeenteIds) {
        String sql = "SELECT ws.wijk_id AS id, ws.waarde_label AS name FROM %s.wijk_state ws " +
            "   WHERE ws.change_end_date IS NULL " +
            ((!gemeenteIds.isEmpty()) ? " AND ws.gemeente_id IN :gemeenteIds " : "") +
            " ORDER BY ws.waarde_label ASC ";

        SelectionQuery<SelectModel> query = session.createNativeQuery(String.format(sql, GenericRepository.VNG_SCHEMA_NAME), SelectModel.class);
        if (!gemeenteIds.isEmpty()) {
            query.setParameterList("gemeenteIds", gemeenteIds);
        }

        return query.list();
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
