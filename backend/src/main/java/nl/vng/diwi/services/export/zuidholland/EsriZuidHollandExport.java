package nl.vng.diwi.services.export.zuidholland;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.ProjectExportSqlModel;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dataexchange.DataExchangeTemplate;
import nl.vng.diwi.dataexchange.DataExchangeTemplate.TemplateProperty;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.ConfigModel;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.SelectModel;
import nl.vng.diwi.models.SingleValueOrRangeModel;
import nl.vng.diwi.services.DataExchangeExportError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geojson.Crs;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;
import org.geojson.jackson.CrsType;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static nl.vng.diwi.services.DataExchangeExportError.EXPORT_ERROR.MISSING_DATAEXCHANGE_MAPPING;
import static nl.vng.diwi.services.DataExchangeExportError.EXPORT_ERROR.MISSING_MANDATORY_VALUE;
import static nl.vng.diwi.services.DataExchangeExportError.EXPORT_ERROR.MULTIPLE_SINGLE_SELECT_VALUES;
import static nl.vng.diwi.services.DataExchangeExportError.EXPORT_ERROR.NUMERIC_RANGE_VALUE;
import static nl.vng.diwi.services.DataExchangeExportError.EXPORT_ERROR.VALUE_LARGER_THAN_CONSTRUCTION_HOUSEBLOCKS;
import static nl.vng.diwi.services.export.zuidholland.EsriZuidHollandExport.EsriZuidHollandProjectProps.ph_short2;
import static nl.vng.diwi.services.export.zuidholland.EsriZuidHollandExport.EsriZuidHollandProjectProps.ph_short7;
import static nl.vng.diwi.services.export.zuidholland.EsriZuidHollandExport.EsriZuidHollandProjectProps.ph_short8;
import static nl.vng.diwi.services.export.zuidholland.EsriZuidHollandExport.EsriZuidHollandProjectProps.ph_short9;
import static nl.vng.diwi.services.export.zuidholland.EsriZuidHollandExport.EsriZuidHollandProjectProps.plantype;
import static nl.vng.diwi.services.export.zuidholland.EsriZuidHollandExport.EsriZuidHollandProjectProps.status_planologisch;
import static nl.vng.diwi.services.export.zuidholland.EsriZuidHollandExport.EsriZuidHollandProjectProps.woonplaats;

public class EsriZuidHollandExport {

    private static final Logger logger = LogManager.getLogger();

    private static final ObjectMapper MAPPER = JsonMapper.builder().findAndAddModules().build();

    static final Map<String, DataExchangeTemplate.TemplateProperty> templatePropertyMap;

    static {
        templatePropertyMap = DataExchangeTemplate.templates.get(DataExchangeType.ESRI_ZUID_HOLLAND)
                .getProperties().stream().collect(Collectors.toMap(DataExchangeTemplate.TemplateProperty::getName, Function.identity()));
    }


    public static FeatureCollection buildExportObject(ConfigModel configModel, List<ProjectExportSqlModel> projects,
                                                      List<PropertyModel> customProps, Map<String, DataExchangePropertyModel> dxPropertiesMap, LocalDate exportDate,
                                                      List<DataExchangeExportError> errors) {

        FeatureCollection exportObject = new FeatureCollection();
        Crs crs = new Crs();
        crs.setType(CrsType.name);
        crs.getProperties().put("name", "EPSG:28992");
        exportObject.setCrs(crs);

        PropertyModel priceRangeBuyFixedProp = customProps.stream()
            .filter(pfp -> pfp.getName().equals(Constants.FIXED_PROPERTY_PRICE_RANGE_BUY)).findFirst().orElse(null);
        PropertyModel priceRangeRentFixedProp = customProps.stream()
            .filter(pfp -> pfp.getName().equals(Constants.FIXED_PROPERTY_PRICE_RANGE_RENT)).findFirst().orElse(null);
        PropertyModel municipalityFixedProp = customProps.stream()
            .filter(pfp -> pfp.getName().equals(Constants.FIXED_PROPERTY_MUNICIPALITY)).findFirst().orElse(null);

        Map<UUID, PropertyModel> customPropsMap = customProps.stream().collect(Collectors.toMap(PropertyModel::getId, Function.identity()));

        projects.forEach(project -> exportObject.add(getProjectFeature(configModel, project,
            customPropsMap, priceRangeBuyFixedProp, priceRangeRentFixedProp, municipalityFixedProp, dxPropertiesMap, exportDate, errors)));

        return exportObject;
    }

    private static Feature getProjectFeature(ConfigModel configModel, ProjectExportSqlModel project,
                                             Map<UUID, PropertyModel> customPropsMap, PropertyModel priceRangeBuyFixedProp, PropertyModel priceRangeRentFixedProp,
                                             PropertyModel municipalityFixedProp, Map<String, DataExchangePropertyModel> dxPropertiesMap, LocalDate exportDate,
                                             List<DataExchangeExportError> errors) {

        Feature projectFeature = new Feature();
        projectFeature.setProperties(new LinkedHashMap<>());

        MultiPolygon multiPolygon = new MultiPolygon();
        for (String geometryString : project.getGeometries()) {
            FeatureCollection geometryObject;
            try {
                geometryObject = MAPPER.readValue(geometryString, FeatureCollection.class);
                geometryObject.getFeatures().forEach(f -> {
                    if (f.getGeometry() instanceof Polygon) {
                        multiPolygon.add((Polygon) f.getGeometry());
                    } else {
                        logger.error("Geometry for project id {} is not instance of Polygon: {}", project.getProjectId(), geometryString);
                    }
                });
            } catch (IOException e) {
                logger.error("Geometry for project id {} could not be deserialized into a FeatureCollection: {}", project.getProjectId(), geometryString);
            }
        }
        if (!multiPolygon.getCoordinates().isEmpty()) {
            projectFeature.setGeometry(multiPolygon);
        }


        List<EsriZuidHollandHouseblockExportModel> houseblockExportModels = project.getHouseblocks().stream()
            .map(h -> new EsriZuidHollandHouseblockExportModel(project.getProjectId(), h, priceRangeBuyFixedProp, priceRangeRentFixedProp, errors)).toList();
        int totalProjectConstructionHouses = houseblockExportModels.stream()
            .filter(h -> h.getMutationKind() == MutationType.CONSTRUCTION).mapToInt(EsriZuidHollandHouseblockExportModel::getMutationAmount).sum();
        List<Map<String, Object>> houseblockProperties = getHouseblockProperties(houseblockExportModels);

        Map<UUID, String> projectTextCustomProps = project.getTextProperties().stream()
            .collect(Collectors.toMap(ProjectExportSqlModel.TextPropertyModel::getPropertyId, ProjectExportSqlModel.TextPropertyModel::getTextValue));
        Map<UUID, SingleValueOrRangeModel<BigDecimal>> projectNumericCustomProps = project.getNumericProperties().stream()
            .collect(Collectors.toMap(ProjectExportSqlModel.NumericPropertyModel::getPropertyId, ProjectExportSqlModel.NumericPropertyModel::getSingleValueOrRangeModel));
        Map<UUID, Boolean> projectBooleanCustomProps = project.getBooleanProperties().stream()
            .collect(Collectors.toMap(ProjectExportSqlModel.BooleanPropertyModel::getPropertyId, ProjectExportSqlModel.BooleanPropertyModel::getBooleanValue));
        Map<UUID, List<UUID>> projectCategoricalCustomProps = project.getCategoryProperties().stream()
                .collect(Collectors.toMap(ProjectExportSqlModel.CategoryPropertyModel::getPropertyId, ProjectExportSqlModel.CategoryPropertyModel::getOptionValues));

        for (var prop : EsriZuidHollandProjectProps.values()) {
            switch (prop) {
                case plannaam -> projectFeature.getProperties().put(prop.name(), project.getName());
                case provincie -> projectFeature.getProperties().put(prop.name(), configModel.getProvinceName());
                case regio -> projectFeature.getProperties().put(prop.name(), configModel.getRegionName());
                case gemeente -> projectFeature.getProperties().put(prop.name(), configModel.getMunicipalityName());
                case woonplaats -> { //TODO:  have official list of values and validate against it
                    String woonplaatsName = null;
                    if (municipalityFixedProp != null) {
                        List<UUID> municipalityOptions = projectCategoricalCustomProps.get(municipalityFixedProp.getId());
                        if (municipalityOptions == null || municipalityOptions.isEmpty()) {
                            errors.add(new DataExchangeExportError(project.getProjectId(), woonplaats.name(), MISSING_MANDATORY_VALUE));
                        } else if (municipalityOptions.size() > 1) {
                            errors.add(new DataExchangeExportError(project.getProjectId(), woonplaats.name(), MULTIPLE_SINGLE_SELECT_VALUES));
                        } else {
                            woonplaatsName = municipalityFixedProp.getCategories().stream().filter(o -> o.getId()
                                    .equals(municipalityOptions.get(0))).findFirst().get().getName();
                        }
                    }
                    projectFeature.getProperties().put(prop.name(), woonplaatsName);
                }
                case vertrouwelijkheid -> projectFeature.getProperties().put(prop.name(),
                    EsriZuidHollandEnumMappings.getEsriZuidHollandConfidentiality(project.getConfidentiality()).name());
                case opdrachtgever_type -> addProjectCategoricalCustomProperty(project.getProjectId(), projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.opdrachtgever_type.name()), dxPropertiesMap, projectCategoricalCustomProps, errors);
                case opdrachtgever_naam -> {
                    TemplateProperty templateProperty = templatePropertyMap.get(EsriZuidHollandProjectProps.opdrachtgever_naam.name());
                    DataExchangePropertyModel dxPropertyModel = dxPropertiesMap.get(EsriZuidHollandProjectProps.opdrachtgever_naam.name());
                    if (projectTextCustomProps.containsKey(dxPropertyModel.getCustomPropertyId())) {
                        addProjectTextCustomProperty(project.getProjectId(), projectFeature, templateProperty, dxPropertiesMap, projectTextCustomProps, errors);
                    } else if (projectCategoricalCustomProps.containsKey(dxPropertyModel.getCustomPropertyId())) {
                        addProjectCategoricalCustomPropertyAsText(project.getProjectId(), projectFeature, templateProperty, dxPropertiesMap,
                            projectCategoricalCustomProps, customPropsMap, errors);
                    } else {
                        projectFeature.getProperties().put(EsriZuidHollandProjectProps.opdrachtgever_naam.name(), null);
                    }
                }
                case oplevering_eerste -> {
                    Integer earliestDeliveryYear = null;
                    if (!houseblockExportModels.isEmpty()) {
                        earliestDeliveryYear = houseblockExportModels.stream().mapToInt(EsriZuidHollandHouseblockExportModel::getDeliveryYear).min().getAsInt();
                    }
                    projectFeature.getProperties().put(prop.name(), earliestDeliveryYear);
                }
                case oplevering_laatste -> {
                    Integer latestDeliveryYear = null;
                    if (!houseblockExportModels.isEmpty()) {
                        latestDeliveryYear = houseblockExportModels.stream().mapToInt(EsriZuidHollandHouseblockExportModel::getDeliveryYear).max().getAsInt();
                    }
                    projectFeature.getProperties().put(prop.name(), latestDeliveryYear);
                }
                case opmerkingen_basis -> addProjectTextCustomProperty(project.getProjectId(), projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.opmerkingen_basis.name()), dxPropertiesMap, projectTextCustomProps, errors);
                case plantype -> {
                    if (project.getPlanType() == null || project.getPlanType().isEmpty()) {
                        errors.add(new DataExchangeExportError(project.getProjectId(), plantype.name(), MISSING_MANDATORY_VALUE));
                    } else if (project.getPlanType().size() > 1) {
                        errors.add(new DataExchangeExportError(project.getProjectId(), plantype.name(), MULTIPLE_SINGLE_SELECT_VALUES));
                    } else {
                        projectFeature.getProperties().put(prop.name(),
                            EsriZuidHollandEnumMappings.getEsriZuidHollandPlanType(project.getPlanType().get(0)));
                    }
                }
                case masterplan -> addProjectTextCustomProperty(project.getProjectId(), projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.masterplan.name()), dxPropertiesMap, projectTextCustomProps, errors);
                case bestemmingsplan -> addProjectTextCustomProperty(project.getProjectId(), projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.bestemmingsplan.name()), dxPropertiesMap, projectTextCustomProps, errors);
                case projectfase -> projectFeature.getProperties().put(prop.name(),
                    EsriZuidHollandEnumMappings.getEsriZuidHollandProjectPhase(project.getProjectPhase(), project.getEndDate()));
                case status_planologisch -> {
                    if (project.getPlanningPlanStatus() == null || project.getPlanningPlanStatus().isEmpty()) {
                        errors.add(new DataExchangeExportError(project.getProjectId(), status_planologisch.name(), MISSING_MANDATORY_VALUE));
                    } else if (project.getPlanningPlanStatus().size() > 1) {
                        errors.add(new DataExchangeExportError(project.getProjectId(), status_planologisch.name(), MULTIPLE_SINGLE_SELECT_VALUES));
                    } else{
                        projectFeature.getProperties().put(prop.name(),
                            EsriZuidHollandEnumMappings.getEsriZuidHollandPlanningStatus(project.getPlanningPlanStatus().get(0)));
                    }
                }
                case opmerkingen_status -> addProjectTextCustomProperty(project.getProjectId(), projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.opmerkingen_status.name()), dxPropertiesMap, projectTextCustomProps, errors);
                case beoogd_woonmilieu_ABF5 -> addProjectCategoricalCustomProperty(project.getProjectId(), projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.beoogd_woonmilieu_ABF5.name()), dxPropertiesMap, projectCategoricalCustomProps, errors);
                case beoogd_woonmilieu_ABF13 -> addProjectCategoricalCustomProperty(project.getProjectId(), projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.beoogd_woonmilieu_ABF13.name()), dxPropertiesMap, projectCategoricalCustomProps, errors);
                case knelpunten_meerkeuze -> addProjectCategoricalCustomProperty(project.getProjectId(), projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.knelpunten_meerkeuze.name()), dxPropertiesMap, projectCategoricalCustomProps, errors);
                case toelichting_knelpunten -> addProjectTextCustomProperty(project.getProjectId(), projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.toelichting_knelpunten.name()), dxPropertiesMap, projectTextCustomProps, errors);
                case verhuurder_type -> addProjectCategoricalCustomProperty(project.getProjectId(), projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.verhuurder_type.name()), dxPropertiesMap, projectCategoricalCustomProps, errors);
                case opmerkingen_kwalitatief -> addProjectTextCustomProperty(project.getProjectId(), projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.opmerkingen_kwalitatief.name()), dxPropertiesMap, projectTextCustomProps, errors);
                case ph_text1 -> addProjectCategoricalCustomProperty(project.getProjectId(), projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.ph_text1.name()), dxPropertiesMap, projectCategoricalCustomProps, errors);
                case ph_text3 -> addProjectBooleanCustomProperty(projectFeature, EsriZuidHollandProjectProps.ph_text3, dxPropertiesMap, projectBooleanCustomProps);
                case ph_short1 -> projectFeature.getProperties().put(prop.name(), project.getStartDate().getYear());
                case ph_short2 -> {
                    BigDecimal val = addProjectNumericCustomProperty(project.getProjectId(), projectFeature,
                            templatePropertyMap.get(EsriZuidHollandProjectProps.ph_short2.name()), dxPropertiesMap, projectNumericCustomProps, errors);
                    if (val != null && val.intValue() > totalProjectConstructionHouses) {
                        errors.add(new DataExchangeExportError(project.getProjectId(), ph_short2.name(), VALUE_LARGER_THAN_CONSTRUCTION_HOUSEBLOCKS));
                    }
                }
                case ph_text4 -> addProjectBooleanCustomProperty(projectFeature, EsriZuidHollandProjectProps.ph_text4, dxPropertiesMap, projectBooleanCustomProps);
                case ph_text5 -> addProjectCategoricalCustomProperty(project.getProjectId(), projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.ph_text5.name()), dxPropertiesMap, projectCategoricalCustomProps, errors);
                case ph_text6 -> addProjectCategoricalCustomProperty(project.getProjectId(), projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.ph_text6.name()), dxPropertiesMap, projectCategoricalCustomProps, errors);
                case ph_text7 -> addProjectCategoricalCustomProperty(project.getProjectId(), projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.ph_text7.name()), dxPropertiesMap, projectCategoricalCustomProps, errors);
                case ph_text8 -> addProjectCategoricalCustomProperty(project.getProjectId(), projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.ph_text8.name()), dxPropertiesMap, projectCategoricalCustomProps, errors);
                case ph_text9 -> projectFeature.getProperties().put(prop.name(), project.getProjectId().toString());
                case ph_short3 -> {
                    int phShor3Val = 0;
                    for (var h : houseblockExportModels) {
                        for (var o : h.getOwnershipValueList()) {
                            if (o.getOwnershipType() == OwnershipType.HUURWONING_WONINGCORPORATIE &&
                                (o.getOwnershipCategory() == EsriZuidHollandHouseblockExportModel.OwnershipCategory.huur1 || o.getOwnershipCategory() == EsriZuidHollandHouseblockExportModel.OwnershipCategory.huur2)) {
                                if (h.getMutationKind() == MutationType.CONSTRUCTION) {
                                    phShor3Val += o.getAmount();
                                } else {
                                    phShor3Val -= o.getAmount();
                                }
                            }
                        }
                    }
                    projectFeature.getProperties().put(prop.name(), phShor3Val);
                }
                case ph_short4 -> {
                    int phShor4Val = 0;
                    for (var h : houseblockExportModels) {
                        for (var o : h.getOwnershipValueList()) {
                            if (o.getOwnershipType() == OwnershipType.HUURWONING_WONINGCORPORATIE && o.getOwnershipCategory() == EsriZuidHollandHouseblockExportModel.OwnershipCategory.huur3 ) {
                                if (h.getMutationKind() == MutationType.CONSTRUCTION) {
                                    phShor4Val += o.getAmount();
                                } else {
                                    phShor4Val -= o.getAmount();
                                }
                            }
                        }
                    }
                    projectFeature.getProperties().put(prop.name(), phShor4Val);
                }
                case ph_short7 -> {
                    BigDecimal val = addProjectNumericCustomProperty(project.getProjectId(), projectFeature,
                            templatePropertyMap.get(ph_short7.name()), dxPropertiesMap, projectNumericCustomProps, errors);
                    if (val != null && val.intValue() > totalProjectConstructionHouses) {
                        errors.add(new DataExchangeExportError(project.getProjectId(), ph_short7.name(), VALUE_LARGER_THAN_CONSTRUCTION_HOUSEBLOCKS));
                    }
                }
                case ph_short8 -> {
                    BigDecimal val = addProjectNumericCustomProperty(project.getProjectId(), projectFeature,
                            templatePropertyMap.get(ph_short8.name()), dxPropertiesMap, projectNumericCustomProps, errors);
                    if (val != null && val.intValue() > totalProjectConstructionHouses) {
                        errors.add(new DataExchangeExportError(project.getProjectId(), ph_short8.name(), VALUE_LARGER_THAN_CONSTRUCTION_HOUSEBLOCKS));
                    }
                }
                case ph_short9 -> {
                    BigDecimal val = addProjectNumericCustomProperty(project.getProjectId(), projectFeature,
                            templatePropertyMap.get(ph_short9.name()), dxPropertiesMap, projectNumericCustomProps, errors);
                    if (val != null && val.intValue() > totalProjectConstructionHouses) {
                        errors.add(new DataExchangeExportError(project.getProjectId(), ph_short9.name(), VALUE_LARGER_THAN_CONSTRUCTION_HOUSEBLOCKS));
                    }
                }
                case ph_date1 -> projectFeature.getProperties().put(prop.name(), project.getRealizationPhaseDate() != null ? project.getRealizationPhaseDate().toString() : null);
                case ph_date2 ->  projectFeature.getProperties().put(prop.name(), project.getPlanStatusPhase1Date() != null ? project.getPlanStatusPhase1Date().toString() : null);
                case ph_date3 -> projectFeature.getProperties().put(prop.name(), exportDate.toString());
                case year_properties -> projectFeature.getProperties().put(prop.name(), houseblockProperties);
            }
        }

        return projectFeature;
    }

    private static void addProjectCategoricalCustomProperty(UUID projectUuid, Feature projectFeature, DataExchangeTemplate.TemplateProperty templateProperty,
                                                     Map<String, DataExchangePropertyModel> dxPropertiesMap, Map<UUID, List<UUID>> projectCategoricalCustomProps,
                                                            List<DataExchangeExportError> errors) {
        DataExchangePropertyModel dxPropertyModel = dxPropertiesMap.get(templateProperty.getName());
        List<String> ezhValue = new ArrayList<>();
        if (dxPropertyModel == null) {
            errors.add(new DataExchangeExportError(null, templateProperty.getName(), MISSING_DATAEXCHANGE_MAPPING));
        } else if (projectCategoricalCustomProps.containsKey(dxPropertyModel.getCustomPropertyId())) {
            List<UUID> projectCategoryOptions = projectCategoricalCustomProps.get(dxPropertyModel.getCustomPropertyId());
            for (UUID option : projectCategoryOptions) {
                dxPropertyModel.getOptions().forEach(dxOption -> {
                    if (dxOption.getPropertyCategoryValueIds().contains(option)) {
                        ezhValue.add(dxOption.getName());
                    }
                });
            }
        }

        if (templateProperty.getMandatory() && ezhValue.isEmpty()) {
            errors.add(new DataExchangeExportError(projectUuid, templateProperty.getName(), MISSING_MANDATORY_VALUE));
        }
        if (templateProperty.getSingleSelect() && ezhValue.size() > 1) {
            errors.add(new DataExchangeExportError(projectUuid, templateProperty.getName(), MULTIPLE_SINGLE_SELECT_VALUES));
        }

        if (templateProperty.getSingleSelect()) {
            projectFeature.getProperties().put(templateProperty.getName(), ezhValue.isEmpty() ? null : ezhValue.get(0));
        } else {
            projectFeature.getProperties().put(templateProperty.getName(), ezhValue.isEmpty() ? null : ezhValue);
        }
    }


    private static void addProjectCategoricalCustomPropertyAsText(UUID projectUuid, Feature projectFeature, TemplateProperty templateProperty,
                                                                  Map<String, DataExchangePropertyModel> dxPropertiesMap, Map<UUID, List<UUID>> projectCategoricalCustomProps,
                                                                  Map<UUID, PropertyModel> customPropsMap, List<DataExchangeExportError> errors) {
        UUID customPropUuid = dxPropertiesMap.get(templateProperty.getName()).getCustomPropertyId();
        String ezhValue = null;
        if (customPropUuid == null) {
            errors.add(new DataExchangeExportError(null, templateProperty.getName(), MISSING_DATAEXCHANGE_MAPPING));
        } else if (projectCategoricalCustomProps.containsKey(customPropUuid)) {
            UUID optionUuid = projectCategoricalCustomProps.get(customPropUuid).get(0);
            PropertyModel propertyModel = customPropsMap.get(customPropUuid);
            ezhValue = propertyModel.getCategories().stream().filter(o -> o.getDisabled() == Boolean.FALSE && o.getId().equals(optionUuid))
                .map(SelectModel::getName).findFirst().orElse(null);
        } else if (templateProperty.getMandatory()) {
            errors.add(new DataExchangeExportError(projectUuid, templateProperty.getName(), MISSING_MANDATORY_VALUE));
        }
        projectFeature.getProperties().put(templateProperty.getName(), ezhValue);

    }

    private static void addProjectTextCustomProperty(UUID projectUuid, Feature projectFeature, DataExchangeTemplate.TemplateProperty templateProperty,
                                                     Map<String, DataExchangePropertyModel> dxPropertiesMap, Map<UUID, String> projectTextCustomProps,
                                                     List<DataExchangeExportError> errors) {
        UUID customPropUuid = dxPropertiesMap.get(templateProperty.getName()).getCustomPropertyId();
        String ezhValue = null;
        if (customPropUuid == null) {
            errors.add(new DataExchangeExportError(null, templateProperty.getName(), MISSING_DATAEXCHANGE_MAPPING));
        } else if (projectTextCustomProps.containsKey(customPropUuid)) {
            ezhValue = projectTextCustomProps.get(customPropUuid);
        } else if (templateProperty.getMandatory()) {
            errors.add(new DataExchangeExportError(projectUuid, templateProperty.getName(), MISSING_MANDATORY_VALUE));
        }
        projectFeature.getProperties().put(templateProperty.getName(), ezhValue);
    }

    private static BigDecimal addProjectNumericCustomProperty(UUID projectUuid, Feature projectFeature, DataExchangeTemplate.TemplateProperty templateProperty,
                                                     Map<String, DataExchangePropertyModel> dxPropertiesMap, Map<UUID, SingleValueOrRangeModel<BigDecimal>> projectNumericCustomProps,
                                                              List<DataExchangeExportError> errors) {
        UUID customPropUuid = dxPropertiesMap.get(templateProperty.getName()).getCustomPropertyId();
        BigDecimal ezhValue = null;
        if (customPropUuid == null) {
            errors.add(new DataExchangeExportError(null, templateProperty.getName(), MISSING_DATAEXCHANGE_MAPPING));
        } else if (projectNumericCustomProps.containsKey(customPropUuid)) {
            var numericVal = projectNumericCustomProps.get(customPropUuid);
            if (numericVal.getValue() != null) {
                ezhValue = numericVal.getValue();
            } else if (numericVal.getMin() != null) {
                errors.add(new DataExchangeExportError(projectUuid, templateProperty.getName(), NUMERIC_RANGE_VALUE));
            }
        } else if (templateProperty.getMandatory()) {
            errors.add(new DataExchangeExportError(projectUuid, templateProperty.getName(), MISSING_MANDATORY_VALUE));
        }
        projectFeature.getProperties().put(templateProperty.getName(), ezhValue);

        return ezhValue;
    }

    private static void addProjectBooleanCustomProperty(Feature projectFeature, EsriZuidHollandProjectProps ezhProperty,
                                                     Map<String, DataExchangePropertyModel> dxPropertiesMap, Map<UUID, Boolean> projectBooleanCustomProps) {
        UUID customPropUuid = dxPropertiesMap.get(ezhProperty.name()).getCustomPropertyId();
        String ezhValue = "Onbekend";

        if (customPropUuid != null && projectBooleanCustomProps.containsKey(customPropUuid)) {
            Boolean boolValue = projectBooleanCustomProps.get(customPropUuid);
            if (boolValue != null) {
                ezhValue = boolValue ? "JA" : "NEE";
            }
        }
        projectFeature.getProperties().put(ezhProperty.name(), ezhValue);
    }

    private static List<Map<String, Object>> getHouseblockProperties(List<EsriZuidHollandHouseblockExportModel> houseblocks) {

        Map<Integer, List<EsriZuidHollandHouseblockExportModel>> houseblocksByDeliveryYear = new HashMap<>();
        houseblocks.forEach(houseblock -> {
            if (!houseblocksByDeliveryYear.containsKey(houseblock.getDeliveryYear())) {
                houseblocksByDeliveryYear.put(houseblock.getDeliveryYear(), new ArrayList<>());
            }
            houseblocksByDeliveryYear.get(houseblock.getDeliveryYear()).add(houseblock);
        });

        List<Map<String, Object>> allHouseblockProperties = new ArrayList<>();

        List<Integer> deliveryYears = houseblocksByDeliveryYear.keySet().stream().sorted().toList();
        deliveryYears.forEach(deliveryYear -> {
            allHouseblockProperties.add(getHouseblockPropertiesForDeliveryYear(houseblocksByDeliveryYear.get(deliveryYear)));
        });

        return allHouseblockProperties;
    }

    private static Map<String, Object> getHouseblockPropertiesForDeliveryYear(List<EsriZuidHollandHouseblockExportModel> houseblockExportModels) {

        Map<String, Object> hbPropsByDeliveryYear = new LinkedHashMap<>();

        EsriZuidHollandHouseblockModel constructionHbTotals = new EsriZuidHollandHouseblockModel();
        EsriZuidHollandHouseblockModel demolitionHbTotals = new EsriZuidHollandHouseblockModel();

        houseblockExportModels.forEach(h -> {
                if ((h.getMutationKind() == MutationType.CONSTRUCTION)) {
                    constructionHbTotals.addHouseblockData(h);
                } else {
                    demolitionHbTotals.addHouseblockData(h);
                }
            }
        );

        Map<EsriZuidHollandHouseblockProps, Integer> constructionValuesMap = constructionHbTotals.calculateHouseTypeOwnershipValuesMap();
        Map<EsriZuidHollandHouseblockProps, Integer> demolitionValuesMap = demolitionHbTotals.calculateHouseTypeOwnershipValuesMap();


        for (var prop : EsriZuidHollandHouseblockProps.values()) {
            switch (prop) {
                case jaartal -> hbPropsByDeliveryYear.put(prop.name(), houseblockExportModels.get(0).getDeliveryYear());

                case meergezins_koop1 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop1));
                case meergezins_koop2 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop2));
                case meergezins_koop3 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop3));
                case meergezins_koop4 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop4));
                case meergezins_koop_onb -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop_onb));
                case meergezins_huur1 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur1));
                case meergezins_huur2 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur2));
                case meergezins_huur3 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur3));
                case meergezins_huur4 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur4));
                case meergezins_huur_onb -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur_onb));
                case meergezins_onbekend -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_onbekend));

                case eengezins_koop1 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop1));
                case eengezins_koop2 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop2));
                case eengezins_koop3 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop3));
                case eengezins_koop4 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop4));
                case eengezins_koop_onb -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop_onb));
                case eengezins_huur1 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur1));
                case eengezins_huur2 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur2));
                case eengezins_huur3 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur3));
                case eengezins_huur4 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur4));
                case eengezins_huur_onb -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur_onb));
                case eengezins_onbekend -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_onbekend));

                case onbekend_koop1 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop1));
                case onbekend_koop2 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop2));
                case onbekend_koop3 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop3));
                case onbekend_koop4 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop4));
                case onbekend_koop_onb -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop_onb));
                case onbekend_huur1 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur1));
                case onbekend_huur2 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur2));
                case onbekend_huur3 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur3));
                case onbekend_huur4 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur4));
                case onbekend_huur_onb -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur_onb));
                case onbekend_onbekend -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_onbekend));

                case bouw_gerealiseerd -> hbPropsByDeliveryYear.put(prop.name(), LocalDate.now().getYear() < houseblockExportModels.get(0).getDeliveryYear() ? "Ja" : "Nee");

                case sloop_meergezins_koop1 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop1));
                case sloop_meergezins_koop2 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop2));
                case sloop_meergezins_koop3 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop3));
                case sloop_meergezins_koop4 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop4));
                case sloop_meergezins_koop_onb -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop_onb));
                case sloop_meergezins_huur1 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur1));
                case sloop_meergezins_huur2 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur2));
                case sloop_meergezins_huur3 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur3));
                case sloop_meergezins_huur4 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur4));
                case sloop_meergezins_huur_onb -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur_onb));
                case sloop_meergezins_onbekend -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_onbekend));

                case sloop_eengezins_koop1 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop1));
                case sloop_eengezins_koop2 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop2));
                case sloop_eengezins_koop3 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop3));
                case sloop_eengezins_koop4 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop4));
                case sloop_eengezins_koop_onb -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop_onb));
                case sloop_eengezins_huur1 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur1));
                case sloop_eengezins_huur2 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur2));
                case sloop_eengezins_huur3 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur3));
                case sloop_eengezins_huur4 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur4));
                case sloop_eengezins_huur_onb -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur_onb));
                case sloop_eengezins_onbekend -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_onbekend));

                case sloop_onbekend_koop1 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop1));
                case sloop_onbekend_koop2 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop2));
                case sloop_onbekend_koop3 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop3));
                case sloop_onbekend_koop4 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop4));
                case sloop_onbekend_koop_onb -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop_onb));
                case sloop_onbekend_huur1 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur1));
                case sloop_onbekend_huur2 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur2));
                case sloop_onbekend_huur3 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur3));
                case sloop_onbekend_huur4 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur4));
                case sloop_onbekend_huur_onb -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur_onb));
                case sloop_onbekend_onbekend -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_onbekend));

                case sloop_gerealiseerd -> hbPropsByDeliveryYear.put(prop.name(), LocalDate.now().getYear() < houseblockExportModels.get(0).getDeliveryYear() ? "Ja" : "Nee");
            }
        }

        return hbPropsByDeliveryYear;

    }

    public enum EsriZuidHollandProjectProps {
        plannaam,
        provincie,
        regio,
        gemeente,
        woonplaats,
        vertrouwelijkheid,
        opdrachtgever_type,
        opdrachtgever_naam,
        oplevering_eerste,
        oplevering_laatste,
        opmerkingen_basis,
        plantype,
        masterplan,
        bestemmingsplan,
        projectfase,
        status_planologisch,
        opmerkingen_status,
        beoogd_woonmilieu_ABF5,
        beoogd_woonmilieu_ABF13,
        knelpunten_meerkeuze,
        toelichting_knelpunten,
        verhuurder_type,
        opmerkingen_kwalitatief,
        ph_text1,
        ph_text3,
        ph_short1,
        ph_short2,
        ph_text4,
        ph_text5,
        ph_text6,
        ph_text7,
        ph_text8,
        ph_text9,
        ph_text10,
        ph_short3,
        ph_short4,
        ph_short7,
        ph_short8,
        ph_short9,
        ph_date1,
        ph_date2,
        ph_date3,
        year_properties;
    }

    public enum EsriZuidHollandHouseblockProps {
        jaartal,
        meergezins_koop1,
        meergezins_koop2,
        meergezins_koop3,
        meergezins_koop4,
        meergezins_koop_onb,
        meergezins_huur1,
        meergezins_huur2,
        meergezins_huur3,
        meergezins_huur4,
        meergezins_huur_onb,
        meergezins_onbekend,
        eengezins_koop1,
        eengezins_koop2,
        eengezins_koop3,
        eengezins_koop4,
        eengezins_koop_onb,
        eengezins_huur1,
        eengezins_huur2,
        eengezins_huur3,
        eengezins_huur4,
        eengezins_huur_onb,
        eengezins_onbekend,
        onbekend_koop1,
        onbekend_koop2,
        onbekend_koop3,
        onbekend_koop4,
        onbekend_koop_onb,
        onbekend_huur1,
        onbekend_huur2,
        onbekend_huur3,
        onbekend_huur4,
        onbekend_huur_onb,
        onbekend_onbekend,
        bouw_gerealiseerd,
        sloop_meergezins_koop1,
        sloop_meergezins_koop2,
        sloop_meergezins_koop3,
        sloop_meergezins_koop4,
        sloop_meergezins_koop_onb,
        sloop_meergezins_huur1,
        sloop_meergezins_huur2,
        sloop_meergezins_huur3,
        sloop_meergezins_huur4,
        sloop_meergezins_huur_onb,
        sloop_meergezins_onbekend,
        sloop_eengezins_koop1,
        sloop_eengezins_koop2,
        sloop_eengezins_koop3,
        sloop_eengezins_koop4,
        sloop_eengezins_koop_onb,
        sloop_eengezins_huur1,
        sloop_eengezins_huur2,
        sloop_eengezins_huur3,
        sloop_eengezins_huur4,
        sloop_eengezins_huur_onb,
        sloop_eengezins_onbekend,
        sloop_onbekend_koop1,
        sloop_onbekend_koop2,
        sloop_onbekend_koop3,
        sloop_onbekend_koop4,
        sloop_onbekend_koop_onb,
        sloop_onbekend_huur1,
        sloop_onbekend_huur2,
        sloop_onbekend_huur3,
        sloop_onbekend_huur4,
        sloop_onbekend_huur_onb,
        sloop_onbekend_onbekend,
        sloop_gerealiseerd;
    }
}
