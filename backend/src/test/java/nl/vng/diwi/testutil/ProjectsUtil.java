package nl.vng.diwi.testutil;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import nl.vng.diwi.config.ProjectConfig;
import nl.vng.diwi.dal.Dal;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.UserGroupDAO;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.PropertyCategoryValueState;
import nl.vng.diwi.dal.entities.UserGroup;
import nl.vng.diwi.dal.entities.UserState;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.HouseblockSnapshotModel;
import nl.vng.diwi.models.UserGroupModel;
import nl.vng.diwi.models.UserGroupUserModel;
import nl.vng.diwi.models.superclasses.ProjectCreateSnapshotModel;
import nl.vng.diwi.models.superclasses.ProjectMinimalSnapshotModel;
import nl.vng.diwi.resources.HouseblockResource;
import nl.vng.diwi.resources.ProjectsResource;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.ExcelImportService;
import nl.vng.diwi.services.GeoJsonImportService;
import nl.vng.diwi.services.HouseblockService;
import nl.vng.diwi.services.ProjectService;
import nl.vng.diwi.services.PropertiesService;
import nl.vng.diwi.services.UserGroupService;

public class ProjectsUtil {
    @Data
    @AllArgsConstructor
    @Builder
    static public class CreatedProject {
        ProjectMinimalSnapshotModel project;
        List<HouseblockSnapshotModel> blocks;
        List<UserGroupModel> owners;
    }

    static public CreatedProject createTestProject(
            UserGroup userGroup,
            UserState user,
            LocalDate startDate,
            LocalDate endDate,
            Dal dal,
            ProjectConfig projectConfig,
            VngRepository repo,
            LoggedUser loggedUser)
            throws Exception {
        var projectResource = new ProjectsResource(
                new GenericRepository(dal),
                new ProjectService(),
                new HouseblockService(),
                new UserGroupService(new UserGroupDAO(dal.getSession())),
                new PropertiesService(),
                projectConfig,
                new ExcelImportService(),
                new GeoJsonImportService());
        var blockResource = new HouseblockResource(new GenericRepository(dal), new HouseblockService(), new ProjectService(), new PropertiesService());

        // Some set-up to get the users matching
        UserGroupModel owner = new UserGroupModel(userGroup);
        owner.setName("UG");
        UserGroupUserModel ugum = new UserGroupUserModel();
        ugum.setFirstName(user.getFirstName());
        ugum.setInitials("");
        ugum.setLastName(user.getLastName());
        ugum.setInitials("LF");
        ugum.setUserGroupName("UG");
        owner.setUsers(List.of(ugum));
        List<UserGroupModel> owners = List.of(owner);

        // Create the project
        var originalProjectModel = new ProjectCreateSnapshotModel();
        originalProjectModel.setStartDate(startDate);
        originalProjectModel.setEndDate(endDate);
        originalProjectModel.setProjectName("changeEndDate project");
        originalProjectModel.setProjectColor("#abcdef");
        originalProjectModel.setConfidentialityLevel(Confidentiality.EXTERNAL_GOVERNMENTAL);
        originalProjectModel.setProjectPhase(ProjectPhase._5_PREPARATION);
        originalProjectModel.setProjectOwners(owners);

        ProjectMinimalSnapshotModel createdProject = projectResource.createProject(loggedUser, originalProjectModel);
        repo.getSession().clear();
        UUID projectId = createdProject.getProjectId();

        // Create a block
        var originalBlockModel = new HouseblockSnapshotModel();
        originalBlockModel.setStartDate(startDate);
        originalBlockModel.setEndDate(endDate);
        originalBlockModel.setHouseblockName("changeEndDate block");
        originalBlockModel.setProjectId(projectId);

        var createdBlock = blockResource.createHouseblock(loggedUser, originalBlockModel);
        repo.getSession().clear();

        return CreatedProject.builder()
                .project(createdProject)
                .blocks(List.of(createdBlock))
                .owners(owners)
                .build();
    }
}
