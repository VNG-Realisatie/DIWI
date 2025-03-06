package nl.vng.diwi.testutil;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import nl.vng.diwi.config.ProjectConfig;
import nl.vng.diwi.dal.Dal;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.UserGroupDAO;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.UserGroup;
import nl.vng.diwi.dal.entities.UserGroupState;
import nl.vng.diwi.dal.entities.UserState;
import nl.vng.diwi.dal.entities.UserToUserGroup;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.generic.Json;
import nl.vng.diwi.models.HouseblockSnapshotModel;
import nl.vng.diwi.models.PlotModel;
import nl.vng.diwi.models.UserGroupModel;
import nl.vng.diwi.models.UserGroupUserModel;
import nl.vng.diwi.models.superclasses.ProjectCreateSnapshotModel;
import nl.vng.diwi.models.superclasses.ProjectMinimalSnapshotModel;
import nl.vng.diwi.resources.HouseblockResource;
import nl.vng.diwi.resources.ProjectsResource;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserRole;
import nl.vng.diwi.services.ExcelImportService;
import nl.vng.diwi.services.GeoJsonImportService;
import nl.vng.diwi.services.HouseblockService;
import nl.vng.diwi.services.ProjectService;
import nl.vng.diwi.services.PropertiesService;
import nl.vng.diwi.services.UserGroupService;

public class ProjectsUtil {
    public static final String PLOT_JSON_STRING = """
                    {
                        "type": "FeatureCollection",
                        "numberMatched": 1,
                        "name": "Perceel",
                        "crs": {
                            "type": "name",
                            "properties": { "name": "urn:ogc:def:crs:EPSG::3857" }
                        },
                        "features": [
                        {
                            "type": "Feature",
                            "id": "perceel.e16d366b-7ecd-4c03-ac27-971e4e797907",
                            "properties": {
                            "identificatieNamespace": "NL.IMKAD.KadastraalObject",
                            "identificatieLokaalID": "89250016870000",
                            "beginGeldigheid": "2016/02/16 17:38:46+00",
                            "tijdstipRegistratie": "2016/02/16 17:38:46+00",
                            "volgnummer": 0,
                            "statusHistorieCode": "G",
                            "statusHistorieWaarde": "Geldig",
                            "kadastraleGemeenteCode": "534",
                            "kadastraleGemeenteWaarde": "Lelystad",
                            "sectie": "R",
                            "AKRKadastraleGemeenteCodeCode": "583",
                            "AKRKadastraleGemeenteCodeWaarde": "LLS00",
                            "kadastraleGrootteWaarde": 78501216,
                            "soortGrootteCode": "1",
                            "soortGrootteWaarde": "Vastgesteld",
                            "perceelnummer": 168,
                            "perceelnummerRotatie": 0,
                            "perceelnummerVerschuivingDeltaX": 0,
                            "perceelnummerVerschuivingDeltaY": 0,
                            "perceelnummerPlaatscoordinaatX": 141278.5,
                            "perceelnummerPlaatscoordinaatY": 511328.635
                            },
                            "bbox": [
                            564904.3504781805, 6901888.853878047, 586075.8578339716,
                            6923651.204468503
                            ],
                            "geometry": {
                            "type": "Polygon",
                            "coordinates": [
                                [
                                [586039.323963023, 6923651.204468503],
                                [585792.7831803425, 6923442.3476212295],
                                [585627.7685732216, 6921709.7218406005],
                                [585580.6336043945, 6921214.753407342],
                                [585227.3054058532, 6917503.446812156],
                                [582434.4990003448, 6915975.8558852915],
                                [578821.8509260346, 6913997.96494627],
                                [574390.5798210843, 6911569.012909995],
                                [572203.6365015268, 6910369.095696326],
                                [571659.4709896009, 6910070.407333149],
                                [564904.3504781805, 6906358.565980493],
                                [568289.6714348193, 6901888.853878047],
                                [586075.8578339716, 6901938.659254265],
                                [586055.0843250913, 6914290.3242059015],
                                [586039.323963023, 6923651.204468503]
                                ]
                            ]
                            }
                        }
                        ],
                        "bbox": [
                        564904.35047818, 6901888.85387805, 586075.857833972, 6923651.2044685
                        ]
                    },
                    "subselectionGeometry": null
                    }
            """;

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
        PropertiesService propertiesService = new PropertiesService();

        var projectResource = new ProjectsResource(
                new GenericRepository(dal),
                new ProjectService(),
                new HouseblockService(),
                new UserGroupService(new UserGroupDAO(dal.getSession())),
                propertiesService,
                projectConfig,
                new ExcelImportService(),
                new GeoJsonImportService());
        var blockResource = new HouseblockResource(new GenericRepository(dal), new HouseblockService(), new ProjectService(), propertiesService);

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

        final String projectName = "project name";

        // Create the project
        var originalProjectModel = new ProjectCreateSnapshotModel();
        originalProjectModel.setStartDate(startDate);
        originalProjectModel.setEndDate(endDate);
        originalProjectModel.setProjectName(projectName);
        originalProjectModel.setProjectColor("#abcdef");
        originalProjectModel.setConfidentialityLevel(Confidentiality.EXTERNAL_GOVERNMENTAL);
        originalProjectModel.setProjectPhase(ProjectPhase._5_PREPARATION);
        originalProjectModel.setProjectOwners(owners);

        ProjectMinimalSnapshotModel createdProject = projectResource.createProject(loggedUser, originalProjectModel);
        repo.getSession().clear();
        UUID projectId = createdProject.getProjectId();

        // Create a block
        var originalBlockModel = new HouseblockSnapshotModel();
        originalBlockModel.setProjectId(projectId);
        originalBlockModel.setHouseblockName("block name");
        originalBlockModel.setStartDate(startDate);
        originalBlockModel.setEndDate(endDate);

        var createdBlock = blockResource.createHouseblock(loggedUser, originalBlockModel);
        repo.getSession().clear();

        var plot = Json.mapper.readValue(PLOT_JSON_STRING, ObjectNode.class);
        var plotModel = new PlotModel("1", "2", 3l, null, plot);
        projectResource.setProjectPlots(loggedUser, projectId, List.of(plotModel));

        return CreatedProject.builder()
                .project(createdProject)
                .blocks(List.of(createdBlock))
                .owners(owners)
                .build();
    }

    public static UserState persistUserAndUserGroup(VngRepository repo, User user, UserGroup userGroup, ZonedDateTime now) {
        repo.persist(user);

        userGroup.setSingleUser(true);
        repo.persist(userGroup);

        UserState userState = new UserState();
        userState.setChangeStartDate(now);
        userState.setUser(user);
        userState.setFirstName("FN");
        userState.setLastName("LN");
        userState.setCreateUser(user);
        userState.setIdentityProviderId("identityProviderId");
        userState.setUserRole(UserRole.UserPlus);
        repo.persist(userState);

        UserGroupState userGroupState = new UserGroupState();
        userGroupState.setName("UG");
        userGroupState.setUserGroup(userGroup);
        userGroupState.setCreateUser(user);
        userGroupState.setChangeStartDate(now);
        repo.persist(userGroupState);

        UserToUserGroup utug = new UserToUserGroup();
        utug.setUser(user);
        utug.setUserGroup(userGroup);
        utug.setCreateUser(user);
        utug.setChangeStartDate(now);
        repo.persist(utug);

        return userState;
    }
}
