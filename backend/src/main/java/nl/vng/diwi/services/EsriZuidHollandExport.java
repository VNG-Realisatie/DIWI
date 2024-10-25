package nl.vng.diwi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.HouseblockExportSqlModel;
import nl.vng.diwi.dal.entities.ProjectExportSqlModel;
import nl.vng.diwi.dataexchange.DataExchangeTemplate;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.ConfigModel;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.SingleValueOrRangeModel;
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

public class EsriZuidHollandExport {

    private static final ObjectMapper MAPPER = JsonMapper.builder().findAndAddModules().build();

    static final Map<String, DataExchangeTemplate.TemplateProperty> templatePropertyMap;

    static {
        templatePropertyMap = DataExchangeTemplate.templates.get(DataExchangeType.ESRI_ZUID_HOLLAND)
                .getProperties().stream().collect(Collectors.toMap(DataExchangeTemplate.TemplateProperty::getName, Function.identity()));
    }


    public static FeatureCollection buildExportObject(ConfigModel configModel, List<ProjectExportSqlModel> projects, List<HouseblockExportSqlModel> houseblocks,
                                                      List<PropertyModel> projectFixedProps, Map<String, DataExchangePropertyModel> dxPropertiesMap, LocalDate exportDate,
                                                      List<Object> errors, List<Object> warnings) {

        Map<UUID, List<HouseblockExportSqlModel>> houseblocksMap = new HashMap<>();
        houseblocks.forEach(houseblock -> {
            if (!houseblocksMap.containsKey(houseblock.getProjectId())) {
                houseblocksMap.put(houseblock.getProjectId(), new ArrayList<>());
            }
            houseblocksMap.get(houseblock.getProjectId()).add(houseblock);
        });

        FeatureCollection exportObject = new FeatureCollection();
        Crs crs = new Crs();
        crs.setType(CrsType.name);
        crs.getProperties().put("name", "EPSG:28992");
        exportObject.setCrs(crs);

        projects.forEach(project -> exportObject.add(getProjectFeature(configModel, project,
            houseblocksMap.containsKey(project.getProjectId()) ? houseblocksMap.get(project.getProjectId()) : new ArrayList<>(),
            projectFixedProps, dxPropertiesMap, exportDate, errors, warnings)));

        return exportObject;
    }

    private static Feature getProjectFeature(ConfigModel configModel, ProjectExportSqlModel project, List<HouseblockExportSqlModel> houseblockExportSqlModels,
                                             List<PropertyModel> projectFixedProps, Map<String, DataExchangePropertyModel> dxPropertiesMap, LocalDate exportDate,
                                             List<Object> errors, List<Object> warnings) {

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
                        //TODO: error - this should always be a polygon
                    }
                });
            } catch (IOException e) {
                //TODO: error / warning
            }
        }
        if (!multiPolygon.getCoordinates().isEmpty()) {
            projectFeature.setGeometry(multiPolygon);
        }


        List<Map<String, Object>> houseblockProperties = houseblockExportSqlModels.stream()
            .map(EsriZuidHollandExport::getHouseblockProperties).toList();

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
                case woonplaats -> {
                    String woonplaatsName = null;
                    PropertyModel municipalityFixedProp = projectFixedProps.stream()
                            .filter(pfp -> pfp.getName().equals(Constants.FIXED_PROPERTY_MUNICIPALITY)).findFirst().orElse(null);
                    if (municipalityFixedProp != null) {
                        List<UUID> municipalityOptions = projectCategoricalCustomProps.get(municipalityFixedProp.getId());
                        if (municipalityOptions == null || municipalityOptions.isEmpty()) {
                            //TODO: is this mandatory? add error? if mandatory, flag needs to be updated in DB in property_state
                        } else if (municipalityOptions.size() > 1) {
                            //TODO: is this single select? add error? update flag in property_state?
                        } else {
                            woonplaatsName = municipalityFixedProp.getCategories().stream().filter(o -> o.getId()
                                    .equals(municipalityOptions.get(0))).findFirst().get().getName();
                        }
                    }
                    projectFeature.getProperties().put(prop.name(), woonplaatsName);
                }
                case vertrouwelijkheid -> projectFeature.getProperties().put(prop.name(),
                    EsriZuidHollandEnumMappings.getEsriZuidHollandConfidentiality(project.getConfidentiality()).name());
                case opdrachtgever_type -> addProjectCategoricalCustomProperty(projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.opdrachtgever_type.name()), dxPropertiesMap, projectCategoricalCustomProps);
                case opdrachtgever_naam -> addProjectTextCustomProperty(projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.opdrachtgever_naam.name()), dxPropertiesMap, projectTextCustomProps);
                case oplevering_eerste -> {
                } //TODO The year of the earliest delivery date of all housing blocks
                case oplevering_laatste -> {
                } //TODO The year of the last delivery date of all housing blocks
                case opmerkingen_basis -> addProjectTextCustomProperty(projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.opmerkingen_basis.name()), dxPropertiesMap, projectTextCustomProps);
                case plantype -> {
                    if (project.getPlanType() == null || project.getPlanType().size() != 1) {
                        //TODO: error ?? or warning?
                    } else {
                        projectFeature.getProperties().put(prop.name(),
                            EsriZuidHollandEnumMappings.getEsriZuidHollandPlanType(project.getPlanType().get(0)));
                    }
                }
                case masterplan -> addProjectTextCustomProperty(projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.masterplan.name()), dxPropertiesMap, projectTextCustomProps);
                case bestemmingsplan -> addProjectTextCustomProperty(projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.bestemmingsplan.name()), dxPropertiesMap, projectTextCustomProps);
                case projectfase -> projectFeature.getProperties().put(prop.name(),
                    EsriZuidHollandEnumMappings.getEsriZuidHollandProjectPhase(project.getProjectPhase(), project.getEndDate()));
                case status_planologisch -> {
                    if (project.getPlanningPlanStatus() == null || project.getPlanningPlanStatus().isEmpty()) {
                        projectFeature.getProperties().put(prop.name(),
                            EsriZuidHollandEnumMappings.getEsriZuidHollandPlanningStatus(null));
                    } else if (project.getPlanningPlanStatus().size() == 1) {
                        projectFeature.getProperties().put(prop.name(),
                            EsriZuidHollandEnumMappings.getEsriZuidHollandPlanningStatus(project.getPlanningPlanStatus().get(0)));
                    } else {
                        //TODO: error? warning?
                    }
                }
                case opmerkingen_status -> addProjectTextCustomProperty(projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.opmerkingen_status.name()), dxPropertiesMap, projectTextCustomProps);
                case beoogd_woonmilieu_ABF5 -> addProjectCategoricalCustomProperty(projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.beoogd_woonmilieu_ABF5.name()), dxPropertiesMap, projectCategoricalCustomProps);
                case beoogd_woonmilieu_ABF13 -> addProjectCategoricalCustomProperty(projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.beoogd_woonmilieu_ABF13.name()), dxPropertiesMap, projectCategoricalCustomProps);
                case knelpunten_meerkeuze -> addProjectCategoricalCustomProperty(projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.knelpunten_meerkeuze.name()), dxPropertiesMap, projectCategoricalCustomProps);
                case toelichting_knelpunten -> addProjectTextCustomProperty(projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.toelichting_knelpunten.name()), dxPropertiesMap, projectTextCustomProps);
                case verhuurder_type -> addProjectCategoricalCustomProperty(projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.verhuurder_type.name()), dxPropertiesMap, projectCategoricalCustomProps);
                case opmerkingen_kwalitatief -> addProjectTextCustomProperty(projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.opmerkingen_kwalitatief.name()), dxPropertiesMap, projectTextCustomProps);
                case ph_text1 -> addProjectCategoricalCustomProperty(projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.ph_text1.name()), dxPropertiesMap, projectCategoricalCustomProps);
                case ph_text3 -> addProjectBooleanCustomProperty(projectFeature, EsriZuidHollandProjectProps.ph_text3, dxPropertiesMap, projectBooleanCustomProps);
                case ph_short1 -> projectFeature.getProperties().put(prop.name(), project.getStartDate().getYear()); //TODO put year as string?
                case ph_short2 -> {
                    addProjectNumericCustomProperty(projectFeature,
                            templatePropertyMap.get(EsriZuidHollandProjectProps.ph_short2.name()), dxPropertiesMap, projectNumericCustomProps);
                } // TODO validate
                case ph_text4 -> addProjectBooleanCustomProperty(projectFeature, EsriZuidHollandProjectProps.ph_text4, dxPropertiesMap, projectBooleanCustomProps);
                //TODO: mandatory or not mandatory for boolean props
                case ph_text5 -> addProjectCategoricalCustomProperty(projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.ph_text5.name()), dxPropertiesMap, projectCategoricalCustomProps);
                case ph_text6 -> addProjectCategoricalCustomProperty(projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.ph_text6.name()), dxPropertiesMap, projectCategoricalCustomProps);
                case ph_text7 -> addProjectCategoricalCustomProperty(projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.ph_text7.name()), dxPropertiesMap, projectCategoricalCustomProps);
                case ph_text8 -> addProjectCategoricalCustomProperty(projectFeature,
                        templatePropertyMap.get(EsriZuidHollandProjectProps.ph_text8.name()), dxPropertiesMap, projectCategoricalCustomProps);
                case ph_text9 -> projectFeature.getProperties().put(prop.name(), project.getProjectId().toString()); //TODO add {} in string?
                case ph_short3 -> {
                } //TODO calculate
                case ph_short4 -> {
                } //TODO calculate
                case ph_short7 -> {
                    addProjectNumericCustomProperty(projectFeature,
                            templatePropertyMap.get(EsriZuidHollandProjectProps.ph_short7.name()), dxPropertiesMap, projectNumericCustomProps);
                } // TODO validate
                case ph_short8 -> {
                    addProjectNumericCustomProperty(projectFeature,
                            templatePropertyMap.get(EsriZuidHollandProjectProps.ph_short8.name()), dxPropertiesMap, projectNumericCustomProps);
                } // TODO validate
                case ph_short9 -> {
                    addProjectNumericCustomProperty(projectFeature,
                            templatePropertyMap.get(EsriZuidHollandProjectProps.ph_short9.name()), dxPropertiesMap, projectNumericCustomProps);
                } // TODO validate
                case ph_date1 -> {
                } //TODO  Start date of the project phase "Realisatiefase". May be in the future and in the past. null if missing
                case ph_date2 -> {
                } //TODO Start date of the planologische planstatus 1a, b or c. May be in the future and in the past. null if missing
                case ph_date3 -> projectFeature.getProperties().put(prop.name(), exportDate.toString()); //TODO add {} in string?
                case year_properties -> projectFeature.getProperties().put(prop.name(), houseblockProperties);
            }
        }

        return projectFeature;
    }

    private static void addProjectCategoricalCustomProperty(Feature projectFeature, DataExchangeTemplate.TemplateProperty templateProperty,
                                                     Map<String, DataExchangePropertyModel> dxPropertiesMap, Map<UUID, List<UUID>> projectCategoricalCustomProps) {
        DataExchangePropertyModel dxPropertyModel = dxPropertiesMap.get(templateProperty.getName());
        List<String> ezhValue = new ArrayList<>();
        if (dxPropertyModel == null) {
            //TODO: error? warning? custom property is not defined
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
            //TODO: error? warning?
        }
        if (templateProperty.getSingleSelect() && ezhValue.size() > 1) {
            //TODO error? warning?
        }

        if (templateProperty.getSingleSelect()) {
            projectFeature.getProperties().put(templateProperty.getName(), ezhValue.isEmpty() ? null : ezhValue.get(0));
        } else {
            projectFeature.getProperties().put(templateProperty.getName(), ezhValue.isEmpty() ? null : ezhValue);
        }
    }

    private static void addProjectTextCustomProperty(Feature projectFeature, DataExchangeTemplate.TemplateProperty templateProperty,
                                                     Map<String, DataExchangePropertyModel> dxPropertiesMap, Map<UUID, String> projectTextCustomProps) {
        UUID customPropUuid = dxPropertiesMap.get(templateProperty.getName()).getCustomPropertyId();
        String ezhValue = null;
        if (customPropUuid == null) {
            //TODO: error? warning? custom property is not defined
        } else if (projectTextCustomProps.containsKey(customPropUuid)) {
            ezhValue = projectTextCustomProps.get(customPropUuid);

        } else if (templateProperty.getMandatory()) {
            //TODO: error? warning?
        }
        projectFeature.getProperties().put(templateProperty.getName(), ezhValue);
    }

    private static void addProjectNumericCustomProperty(Feature projectFeature, DataExchangeTemplate.TemplateProperty templateProperty,
                                                     Map<String, DataExchangePropertyModel> dxPropertiesMap, Map<UUID, SingleValueOrRangeModel<BigDecimal>> projectNumericCustomProps) {
        UUID customPropUuid = dxPropertiesMap.get(templateProperty.getName()).getCustomPropertyId();
        BigDecimal ezhValue = null;
        if (customPropUuid == null) {
            //TODO: error? warning? custom property is not defined
        } else if (projectNumericCustomProps.containsKey(customPropUuid)) {
            var numericVal = projectNumericCustomProps.get(customPropUuid);
            if (numericVal.getValue() != null) {
                ezhValue = numericVal.getValue();
            } else if (numericVal.getMin() != null) {
                //TODO: warning?? property configured as range?
            }
        } else if (templateProperty.getMandatory()) {
            //TODO: error? warning?
        }
        projectFeature.getProperties().put(templateProperty.getName(), ezhValue);
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

    private static Map<String, Object> getHouseblockProperties(HouseblockExportSqlModel h) {
        Map<String, Object> houseblockProperties = new LinkedHashMap<>();

        for (var prop : EsriZuidHollandHouseblockProps.values()) {
            switch (prop) {
                default -> houseblockProperties.put(prop.name(), 0);
            }
        }

        return houseblockProperties;
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
