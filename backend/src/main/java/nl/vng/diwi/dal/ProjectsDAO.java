package nl.vng.diwi.dal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import nl.vng.diwi.dal.entities.MultiProjectPolicyGoalSqlModel;
import nl.vng.diwi.dal.entities.ProjectAuditSqlModel;
import nl.vng.diwi.dal.entities.ProjectExportSqlModel;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended;
import nl.vng.diwi.models.DataExchangeExportModel;
import org.hibernate.Session;
import org.hibernate.query.SelectionQuery;

import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import nl.vng.diwi.dal.entities.MultiProjectDashboardSqlModel;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.ProjectDashboardSqlModel;
import nl.vng.diwi.dal.entities.ProjectHouseblockCustomPropertySqlModel;
import nl.vng.diwi.dal.entities.ProjectListSqlModel;
import nl.vng.diwi.dal.entities.ProjectState;
import nl.vng.diwi.generic.Json;
import nl.vng.diwi.security.LoggedUser;

public class ProjectsDAO extends AbstractRepository {

    public ProjectsDAO(Session session) {
        super(session);
    }

    @Nullable
    public Project getCurrentProject(@NonNull UUID projectUuid) {
        session.enableFilter(GenericRepository.CURRENT_DATA_FILTER);
        String statement = "FROM Project P WHERE P.id = :uuid";
        SelectionQuery<Project> query = session
                .createSelectionQuery(statement, Project.class)
                .setParameter("uuid", projectUuid);
        return query.getSingleResultOrNull();
    }

    public ProjectListSqlModel getProjectByUuid(UUID projectUuid, LoggedUser loggedUser) {
        SelectionQuery<ProjectListSqlModel> q = session.createNativeQuery(String.format(
                "SELECT * FROM %s.get_active_or_future_project_snapshot(:projectUuid, :now, :userRole, :userUuid) ",
                GenericRepository.VNG_SCHEMA_NAME),
                ProjectListSqlModel.class)
                .setParameter("now", LocalDate.now())
                .setParameter("projectUuid", projectUuid)
                .setParameter("userRole", loggedUser.getRole().name())
                .setParameter("userUuid", loggedUser.getUuid());
        ProjectListSqlModel result = q.getSingleResultOrNull();
        if (result != null) {
            session.evict(result);
        }
        return result;
    }

    public List<ProjectHouseblockCustomPropertySqlModel> getProjectCustomProperties(UUID projectUuid) {
        List<ProjectHouseblockCustomPropertySqlModel> result = session.createNativeQuery(String.format(
                "SELECT * FROM %s.get_active_or_future_project_custom_properties(:projectUuid, :now) ",
                GenericRepository.VNG_SCHEMA_NAME),
                ProjectHouseblockCustomPropertySqlModel.class)
                .setParameter("now", LocalDate.now())
                .setParameter("projectUuid", projectUuid)
                .list();

        return result;
    }

    public List<ProjectListSqlModel> getProjectsTable(FilterPaginationSorting filtering, LoggedUser loggedUser) {
        SelectionQuery<ProjectListSqlModel> q = session.createNativeQuery(String.format("""
                SELECT * FROM %s.get_active_and_future_projects_list(:now, :offset, :limit, :sortColumn, :sortDirection,
                    :filterColumn, CAST(:filterValue AS text[]), :filterCondition, :userRole, :userUuid) """,
                GenericRepository.VNG_SCHEMA_NAME), ProjectListSqlModel.class)
                .setParameter("now", LocalDate.now())
                .setParameter("offset", filtering.getFirstResultIndex())
                .setParameter("limit", filtering.getPageSize())
                .setParameter("sortColumn", filtering.getSortColumn())
                .setParameter("sortDirection", filtering.getSortDirection().name())
                .setParameter("filterColumn", filtering.getFilterColumn())
                .setParameter("filterValue",
                        filtering.getFilterColumn() == null ? null
                                : fromJavaListToSqlArrayLiteral(filtering.getFilterValue()))
                .setParameter("filterCondition",
                        filtering.getFilterColumn() == null ? null : filtering.getFilterCondition().name())
                .setParameter("userRole", loggedUser.getRole().name())
                .setParameter("userUuid", loggedUser.getUuid());

        return q.getResultList();
    }

    public Integer getProjectsTableCount(FilterPaginationSorting filtering, LoggedUser loggedUser) {
        return session.createNativeQuery(String.format(
                """
                        SELECT COUNT(*) FROM %s.get_active_and_future_projects_list(:now, :offset, :limit, :sortColumn, :sortDirection,
                        :filterColumn, CAST(:filterValue AS text[]), :filterCondition, :userRole, :userUuid) """,
                GenericRepository.VNG_SCHEMA_NAME), Integer.class)
                .setParameter("now", LocalDate.now())
                .setParameter("offset", 0)
                .setParameter("limit", Integer.MAX_VALUE)
                .setParameter("sortColumn", null)
                .setParameter("sortDirection", null)
                .setParameter("filterColumn", filtering.getFilterColumn())
                .setParameter("filterValue",
                        filtering.getFilterColumn() == null ? null
                                : fromJavaListToSqlArrayLiteral(filtering.getFilterValue()))
                .setParameter("filterCondition",
                        filtering.getFilterColumn() == null ? null : filtering.getFilterCondition().name())
                .setParameter("userRole", loggedUser.getRole().name())
                .setParameter("userUuid", loggedUser.getUuid())
                .uniqueResult();
    }

    public ProjectState getCurrentProjectState(UUID projectUuid) {
        session.enableFilter(GenericRepository.CURRENT_DATA_FILTER);
        String statement = "FROM ProjectState ps WHERE ps.project.id = :projectUuid";
        SelectionQuery<ProjectState> query = session
                .createSelectionQuery(statement, ProjectState.class)
                .setParameter("projectUuid", projectUuid);
        return query.getSingleResultOrNull();
    }

    public ProjectDashboardSqlModel getProjectDashboardSnapshot(UUID projectUuid, LocalDate snapshotDate,
            LoggedUser loggedUser) {
        return session.createNativeQuery(String.format(
                "SELECT * FROM %s.get_project_dashboard_snapshot(:projectUuid, :snapshotDate, :userRole, :userUuid) ",
                GenericRepository.VNG_SCHEMA_NAME),
                ProjectDashboardSqlModel.class)
                .setParameter("projectUuid", projectUuid)
                .setParameter("snapshotDate", snapshotDate)
                .setParameter("userRole", loggedUser.getRole().name())
                .setParameter("userUuid", loggedUser.getUuid())
                .getSingleResultOrNull();
    }

    public MultiProjectDashboardSqlModel getMultiProjectDashboardSnapshot(LocalDate snapshotDate,
            LoggedUser loggedUser) {
        return session.createNativeQuery(String.format(
                "SELECT * FROM %s.get_multi_project_dashboard_snapshot(:snapshotDate, :userRole, :userUuid) ",
                GenericRepository.VNG_SCHEMA_NAME),
                MultiProjectDashboardSqlModel.class)
                .setParameter("snapshotDate", snapshotDate)
                .setParameter("userRole", loggedUser.getRole().name())
                .setParameter("userUuid", loggedUser.getUuid())
                .setTupleTransformer((tuple, aliases) -> {
                    try {
                        MultiProjectDashboardSqlModel model = new MultiProjectDashboardSqlModel();
                        for (int i = 0; i <= 4; i++) {
                            if (tuple [i] != null) {
                                switch (i) {
                                    case 0 -> model.setPhysicalAppearance(Json.mapper.readValue((String)tuple[i], new TypeReference<>() { }));
                                    case 1 -> model.setTargetGroup(Json.mapper.readValue((String)tuple[i], new TypeReference<>() { }));
                                    case 2 -> model.setPriceCategoryOwn(Json.mapper.readValue((String)tuple[i], new TypeReference<>() { }));
                                    case 3 -> model.setPriceCategoryRent(Json.mapper.readValue((String)tuple[i], new TypeReference<>() { }));
                                    case 4 -> model.setPlanning(Json.mapper.readValue((String)tuple[i], new TypeReference<>() { }));
                                }
                            }
                        }
                        return model;
                    } catch (Exception e) {
                        throw new RuntimeException();
                    }
                })
                .getSingleResultOrNull();
    }


    public List<MultiProjectPolicyGoalSqlModel> getMultiProjectPolicyGoals(LocalDate snapshotDate, LoggedUser loggedUser) {
        return session.createNativeQuery(String.format(
                "SELECT * FROM %s.apply_policy_goals(:snapshotDate, :userRole, :userUuid) ",
                GenericRepository.VNG_SCHEMA_NAME),
            MultiProjectPolicyGoalSqlModel.class)
            .setParameter("snapshotDate", snapshotDate)
            .setParameter("userRole", loggedUser.getRole().name())
            .setParameter("userUuid", loggedUser.getUuid())
            .getResultList();


    }

    public List<ProjectAuditSqlModel> getProjectAuditLog(UUID projectUuid, LocalDateTime startDateTime, LocalDateTime endDateTime, LoggedUser loggedUser) {
        List<ProjectAuditSqlModel> result = session.createNativeQuery(String.format(
                    "SELECT * FROM %s.get_project_auditlog(:now, :projectUuid, :startDateTime, :endDateTime, :userRole, :userUuid) ",
                    GenericRepository.VNG_SCHEMA_NAME),
                ProjectAuditSqlModel.class)
            .setParameter("now", LocalDate.now())
            .setParameter("projectUuid", projectUuid)
            .setParameter("startDateTime", startDateTime)
            .setParameter("endDateTime", endDateTime)
            .setParameter("userRole", loggedUser.getRole().name())
            .setParameter("userUuid", loggedUser.getUuid())
            .list();

        return result;
    }

    public List<ProjectExportSqlModel> getProjectsExportList(DataExchangeExportModel dxExportModel, LoggedUser loggedUser) {
        SelectionQuery<ProjectExportSqlModel> q = session.createNativeQuery(String.format(
                "SELECT * FROM %s.get_projects_export_list(:exportDate, :userRole, :userUuid, :allowedProjectIds, :allowedConfidentialities) ",
                GenericRepository.VNG_SCHEMA_NAME), ProjectExportSqlModel.class)
            .setParameter("exportDate", dxExportModel.getExportDate())
            .setParameter("userRole", loggedUser.getRole().name())
            .setParameter("userUuid", loggedUser.getUuid())
            .setParameter("allowedProjectIds", dxExportModel.getProjectIds() != null ? dxExportModel.getProjectIds().toArray(new UUID[0]) : null)
            .setParameter("allowedConfidentialities", (dxExportModel.getConfidentialityLevelsAsStrings() != null) ? dxExportModel.getConfidentialityLevelsAsStrings().toArray(new String[0]) : null);

        return q.getResultList();
    }


    public List<ProjectExportSqlModelExtended> getProjectsExportListExtended(DataExchangeExportModel dxExportModel, LoggedUser loggedUser) {
        return session.createNativeQuery(String.format(
                "SELECT * FROM %s.get_projects_export_list_extended(:userRole, :userUuid, :allowedProjectIds, :allowedConfidentialities) ",
                GenericRepository.VNG_SCHEMA_NAME), ProjectExportSqlModelExtended.class)
            .setParameter("userRole", loggedUser.getRole().name())
            .setParameter("userUuid", loggedUser.getUuid())
            .setParameter("allowedProjectIds", dxExportModel.getProjectIds() != null ? dxExportModel.getProjectIds().toArray(new UUID[0]) : null)
            .setParameter("allowedConfidentialities", (dxExportModel.getConfidentialityLevelsAsStrings() != null) ? dxExportModel.getConfidentialityLevelsAsStrings().toArray(new String[0]) : null)
            .list();
    }
}
