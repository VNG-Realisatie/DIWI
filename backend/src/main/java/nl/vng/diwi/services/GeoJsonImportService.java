package nl.vng.diwi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.ImportError;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.SelectModel;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GeoJsonImportService {

    private static final ObjectMapper MAPPER = JsonMapper.builder().findAndAddModules().build();

    public GeoJsonImportService() {
    }

    public Map<String, Object> importGeoJson(String geoJsonFilePath, VngRepository repo, UUID loggedInUserUuid) {

        FeatureCollection geoJsonObject;
        try {
            geoJsonObject = MAPPER.readValue(new File(geoJsonFilePath), FeatureCollection.class);
        } catch (IOException e) {
            return Map.of(ExcelImportService.errors, new ImportError("io_error", e.getMessage()));
        }

        ZonedDateTime importTime = ZonedDateTime.now();
        List<ImportError> geoJsonErrors = new ArrayList<>();
        List<SelectModel> geoJsonProjects = new ArrayList<>();

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            List<PropertyModel> activeProperties = repo.getPropertyDAO().getPropertiesList(null, false, null);
            Map<String, PropertyModel> activePropertiesMap = activeProperties.stream().collect(Collectors.toMap(PropertyModel::getName, Function.identity()));

            UUID geometryPropertyId = activeProperties.stream().filter(p -> p.getName().equals(Constants.FIXED_PROPERTY_GEOMETRY))
                .map(PropertyModel::getId).findFirst().orElse(null);
            if (geometryPropertyId == null) {
                return Map.of(ExcelImportService.errors, new ImportError(0, Constants.FIXED_PROPERTY_GEOMETRY, ImportError.ERROR.MISSING_FIXED_PROPERTY));
            }

            User user = repo.getReferenceById(User.class, loggedInUserUuid);
            for (Feature feature : geoJsonObject.getFeatures()) {
                List<ImportError> featureErrors = new ArrayList<>();
                try {
                    GeoJsonImportModel geoJsonImportModel = MAPPER.convertValue(feature.getProperties(), GeoJsonImportModel.class);
                    ProjectImportModel projectImportModel = geoJsonImportModel.toProjectImportModel(activePropertiesMap, featureErrors);

                    if (featureErrors.isEmpty()) { //no errors validating individual fields
                        GeoJsonObject featureGeometry = feature.getGeometry();
                        if (featureGeometry != null) {
                            if (featureGeometry.getCrs() == null) {
                                featureGeometry.setCrs(geoJsonObject.getCrs());
                            }
                            String geometryStr = MAPPER.writeValueAsString(featureGeometry);
                            projectImportModel.getProjectStringPropsMap().put(geometryPropertyId, geometryStr);
                        }
                        projectImportModel.validate(repo, projectImportModel.getId(), featureErrors, importTime.toLocalDate()); //business logic validation
                    }
                    if (featureErrors.isEmpty()) { //still no errors
                        geoJsonProjects.add(projectImportModel.persistProjectAndHouseblocks(repo, user, importTime));
                    }
                } catch (Exception e) {
                    featureErrors.add(new ImportError("feature_error", e.getMessage()));
                } finally {
                    geoJsonErrors.addAll(featureErrors);
                }
            }

            if (geoJsonErrors.isEmpty()) {
                transaction.commit();
                return Map.of(ExcelImportService.result, geoJsonProjects);
            } else {
                transaction.rollback();
                return Map.of(ExcelImportService.errors, geoJsonErrors);
            }
        }
    }
}
