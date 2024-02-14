package nl.vng.diwi.dal;

import nl.vng.diwi.models.ProjectListModel;
import nl.vng.diwi.models.SelectModel;
import jakarta.persistence.Query;
import org.hibernate.Session;

import java.time.LocalDate;
import java.util.List;

import static nl.vng.diwi.dal.GenericRepository.VNG_SCHEMA_NAME;

public class VngRepository extends AbstractRepository {

    public VngRepository(Session session) {
        super(session);
    }

    public List<ProjectListModel> getProjectsTable(FilterPaginationSorting filtering) {
        Query q = session.createNativeQuery("""
                SELECT * FROM get_active_and_future_projects_list(:now, :offset, :limit, :sortColumn, :sortDirection,
                    :filterColumn, CAST(:filterValue AS text[]), :filterCondition) """ , ProjectListModel.class)
            .setParameter("now", LocalDate.now())
            .setParameter("offset", filtering.getFirstResultIndex())
            .setParameter("limit", filtering.getPageSize())
            .setParameter("sortColumn", filtering.getSortColumn())
            .setParameter("sortDirection", filtering.getSortDirection().name())
            .setParameter("filterColumn", filtering.getFilterColumn())
            .setParameter("filterValue", filtering.getFilterColumn() == null ? null :fromJavaListToSqlArrayLiteral(filtering.getFilterValue()))
            .setParameter("filterCondition", filtering.getFilterColumn() == null ? null : filtering.getFilterCondition().name());

        return q.getResultList();
    }

    public Integer getProjectsTableCount(FilterPaginationSorting filtering) {
        return session.createNativeQuery("""
                SELECT COUNT(*) FROM get_active_and_future_projects_list(:now, :offset, :limit, :sortColumn, :sortDirection,
                :filterColumn, CAST(:filterValue AS text[]), :filterCondition) """, Integer.class)
            .setParameter("now", LocalDate.now())
            .setParameter("offset", 0)
            .setParameter("limit", Integer.MAX_VALUE)
            .setParameter("sortColumn", null)
            .setParameter("sortDirection", null)
            .setParameter("filterColumn", filtering.getFilterColumn())
            .setParameter("filterValue", filtering.getFilterColumn() == null ? null : fromJavaListToSqlArrayLiteral(filtering.getFilterValue()))
            .setParameter("filterCondition", filtering.getFilterColumn() == null ? null :filtering.getFilterCondition().name())
            .uniqueResult();
    }

    public List<SelectModel> getMunicipalityRoles() {
        return session.createNativeQuery(String.format("""
            SELECT CAST(gr.project_gemeenterol_value_id AS TEXT) AS id, gr.value_label AS name FROM %s.project_gemeenterol_value_state gr
            WHERE gr.change_end_date IS NULL
            ORDER BY gr.value_label ASC""", VNG_SCHEMA_NAME), SelectModel.class).list();
    }
    public List<SelectModel> getMunicipalities() {
        return session.createNativeQuery(String.format("""
            SELECT CAST(gs.gemeente_id AS TEXT) AS id, gs.waarde_label AS name FROM %s.gemeente_state gs
            WHERE gs.change_end_date IS NULL
            ORDER BY gs.waarde_label ASC""", VNG_SCHEMA_NAME), SelectModel.class).list();
    }

    public List<SelectModel> getBuurts() {
        return session.createNativeQuery(String.format("""
            SELECT CAST(bs.buurt_id AS TEXT) AS id, bs.waarde_label AS name FROM %s.buurt_state bs
            WHERE bs.change_end_date IS NULL
            ORDER BY bs.waarde_label ASC""", VNG_SCHEMA_NAME), SelectModel.class).list();
    }

    public List<SelectModel> getWijks() {
        return session.createNativeQuery(String.format("""
            SELECT CAST(ws.wijk_id AS TEXT) AS id, ws.waarde_label AS name FROM %s.wijk_state ws
            WHERE ws.change_end_date IS NULL
            ORDER BY ws.waarde_label ASC""", VNG_SCHEMA_NAME), SelectModel.class).list();
    }

    public List<SelectModel> getPriorities() {
        return session.createNativeQuery(String.format("""
            WITH priorities AS (
                SELECT CAST(p.project_priorisering_value_id AS TEXT) AS id, (p.ordinal_level || ' ' || p.value_label) AS name
                FROM %s.project_priorisering_value_state p
                WHERE p.change_end_date IS NULL )
            SELECT * FROM priorities p
            ORDER BY p.name COLLATE "diwi_numeric" ASC""", VNG_SCHEMA_NAME), SelectModel.class).list();
    }
}
