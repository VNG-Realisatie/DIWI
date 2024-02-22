package nl.vng.diwi.dal;

import java.util.List;

import org.hibernate.Session;

import nl.vng.diwi.models.SelectModel;

public class VngRepository extends AbstractRepository {

    public VngRepository(Session session) {
        super(session);
    }

    private ProjectsDAO projectsDAO;

    private MilestoneDAO milestoneDAO;

    private OrganizationsDAO organizationDAO;

    public ProjectsDAO getProjectsDAO() {
        if (projectsDAO == null) {
            projectsDAO = new ProjectsDAO(session);
        }
        return projectsDAO;
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

    public List<SelectModel> getMunicipalityRoles() {
        return session.createNativeQuery(String.format("""
            SELECT CAST(gr.project_gemeenterol_value_id AS TEXT) AS id, gr.value_label AS name FROM %s.project_gemeenterol_value_state gr
            WHERE gr.change_end_date IS NULL
            ORDER BY gr.value_label ASC""", GenericRepository.VNG_SCHEMA_NAME), SelectModel.class).list();
    }
    public List<SelectModel> getMunicipalities() {
        return session.createNativeQuery(String.format("""
            SELECT CAST(gs.gemeente_id AS TEXT) AS id, gs.waarde_label AS name FROM %s.gemeente_state gs
            WHERE gs.change_end_date IS NULL
            ORDER BY gs.waarde_label ASC""", GenericRepository.VNG_SCHEMA_NAME), SelectModel.class).list();
    }

    public List<SelectModel> getBuurts() {
        return session.createNativeQuery(String.format("""
            SELECT CAST(bs.buurt_id AS TEXT) AS id, bs.waarde_label AS name FROM %s.buurt_state bs
            WHERE bs.change_end_date IS NULL
            ORDER BY bs.waarde_label ASC""", GenericRepository.VNG_SCHEMA_NAME), SelectModel.class).list();
    }

    public List<SelectModel> getWijks() {
        return session.createNativeQuery(String.format("""
            SELECT CAST(ws.wijk_id AS TEXT) AS id, ws.waarde_label AS name FROM %s.wijk_state ws
            WHERE ws.change_end_date IS NULL
            ORDER BY ws.waarde_label ASC""", GenericRepository.VNG_SCHEMA_NAME), SelectModel.class).list();
    }

    public List<SelectModel> getPriorities() {
        return session.createNativeQuery(String.format("""
            WITH priorities AS (
                SELECT CAST(p.project_priorisering_value_id AS TEXT) AS id, (p.ordinal_level || ' ' || p.value_label) AS name
                FROM %s.project_priorisering_value_state p
                WHERE p.change_end_date IS NULL )
            SELECT * FROM priorities p
            ORDER BY p.name COLLATE "diwi_numeric" ASC""", GenericRepository.VNG_SCHEMA_NAME), SelectModel.class).list();
    }
}
