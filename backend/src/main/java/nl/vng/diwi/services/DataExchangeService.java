package nl.vng.diwi.services;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.StreamingOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import nl.vng.diwi.dal.DataExchangeDAO;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.DataExchange;
import nl.vng.diwi.dal.entities.DataExchangeOption;
import nl.vng.diwi.dal.entities.DataExchangeOptionState;
import nl.vng.diwi.dal.entities.DataExchangePriceCategoryMapping;
import nl.vng.diwi.dal.entities.DataExchangePriceCategoryMappingState;
import nl.vng.diwi.dal.entities.DataExchangeProperty;
import nl.vng.diwi.dal.entities.DataExchangePropertySqlModel;
import nl.vng.diwi.dal.entities.DataExchangePropertyState;
import nl.vng.diwi.dal.entities.DataExchangeState;
import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.Property;
import nl.vng.diwi.dal.entities.PropertyCategoryValue;
import nl.vng.diwi.dal.entities.PropertyOrdinalValue;
import nl.vng.diwi.dal.entities.PropertyRangeCategoryValue;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.OwnershipCategory;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.dataexchange.DataExchangeTemplate;
import nl.vng.diwi.models.ConfigModel;
import nl.vng.diwi.models.DataExchangeExportModel;
import nl.vng.diwi.models.DataExchangeModel;
import nl.vng.diwi.models.DataExchangeModel.PriceCategories;
import nl.vng.diwi.models.DataExchangeModel.PriceCategoryMapping;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.export.ArcGisProjectExporter;
import nl.vng.diwi.services.export.DataExchangeConfigForExport;
import nl.vng.diwi.services.export.excel.ExcelExport;
import nl.vng.diwi.services.export.gelderland.GdbGelderlandExport;
import nl.vng.diwi.services.export.geojson.GeoJSONExport;
import nl.vng.diwi.services.export.zuidholland.EsriZuidHollandExport;

@RequiredArgsConstructor
@Log4j2
public class DataExchangeService {

    private final ArcGisProjectExporter arcGisProjectExporter;

    public List<DataExchangeModel> getDataExchangeList(VngRepository repo, boolean includeApiKey) {

        List<DataExchangeState> states = repo.getDataExchangeDAO().getActiveDataExchangeStates();

        return states.stream().map(s -> {
            var template = DataExchangeTemplate.templates.get(s.getType());
            return new DataExchangeModel(s, includeApiKey, template);
        }).toList();

    }

    public DataExchangeModel getDataExchangeModel(VngRepository repo, UUID dataExchangeId, boolean includeApiKey) throws VngNotFoundException {
        DataExchangeDAO dataExchangeDAO = repo.getDataExchangeDAO();

        DataExchangeState state = dataExchangeDAO.getActiveDataExchangeStateByDataExchangeUuid(dataExchangeId);
        if (state == null) {
            throw new VngNotFoundException();
        }
        var template = DataExchangeTemplate.templates.get(state.getType());
        DataExchangeModel model = new DataExchangeModel(state, includeApiKey, template);

        DataExchangeDAO dataExchangeDAO2 = repo
                .getDataExchangeDAO();
        List<DataExchangePropertySqlModel> dxSqlProperties = dataExchangeDAO2
                .getDataExchangeProperties(dataExchangeId);
        dxSqlProperties.forEach(sqlProp -> model.getProperties().add(new DataExchangePropertyModel(sqlProp)));

        var dataExchangePriceMappings = dataExchangeDAO.getDataExchangePriceMappings(dataExchangeId);
        if (!dataExchangePriceMappings.isEmpty()) {
            var mappingsModel = new PriceCategories();
            for (var mapping : dataExchangePriceMappings) {
                OwnershipCategory ownershipCategory = mapping.getOwnershipCategory();
                var priceRangeIds = mapping.getMappings().stream().map(m -> m.getPriceRange().getId()).toList();
                if (ownershipCategory.getType() == OwnershipCategory.Type.BUY) {
                    mappingsModel.getBuy().add(new PriceCategoryMapping(ownershipCategory, priceRangeIds));
                } else if (ownershipCategory.getType() == OwnershipCategory.Type.RENT) {
                    mappingsModel.getRent().add(new PriceCategoryMapping(ownershipCategory, priceRangeIds));
                }
            }
            model.setPriceCategories(mappingsModel);
        }
        return model;
    }

    public UUID createDataExchange(VngRepository repo, DataExchangeModel model, ZonedDateTime zdtNow, UUID loggedUserUuid) {

        DataExchange dataExchange = new DataExchange();
        repo.persist(dataExchange);

        createDataExchangeState(repo, dataExchange.getId(), model, zdtNow, loggedUserUuid, false);

        DataExchangeTemplate template = DataExchangeTemplate.templates.get(model.getType());

        if (template == null) {
            throw new VngServerErrorException("Template for type " + model.getType() + " is not defined.");
        }

        createDataExchangeFromTemplate(repo, dataExchange, template);

        return dataExchange.getId();

    }

    public void createDataExchangeFromTemplate(VngRepository repo, DataExchange dataExchange, DataExchangeTemplate template) {

        template.getProperties().forEach(prop -> {
            DataExchangeProperty dxProp = new DataExchangeProperty();
            dxProp.setDataExchange(dataExchange);
            dxProp.setDxPropertyName(prop.getName());
            dxProp.setMandatory(prop.getMandatory());
            dxProp.setSingleSelect(prop.getSingleSelect());
            dxProp.setObjectType(prop.getObjectType());
            dxProp.setPropertyTypes(prop.getPropertyTypes().toArray(PropertyType[]::new));
            repo.persist(dxProp);

            if (prop.getOptions() != null) {
                prop.getOptions().forEach(option -> {
                    DataExchangeOption dxOption = new DataExchangeOption();
                    dxOption.setDataExchangeProperty(dxProp);
                    dxOption.setName(option);
                    repo.persist(dxOption);
                });
            }
        });

        if (template.getPriceCategoryMappings() != null) {
            for (var cat : template.getPriceCategoryMappings()) {
                var mapping = new DataExchangePriceCategoryMapping();
                mapping.setDataExchange(dataExchange);
                mapping.setOwnershipCategory(cat);
                repo.persist(mapping);
            }
        }
    }

    public void createDataExchangeState(VngRepository repo, UUID dataExchangeUuid,
            DataExchangeModel model, ZonedDateTime zdtNow,
            UUID loggedUserUuid, boolean isUpdate) {

        DataExchangeState state = new DataExchangeState();
        state.setDataExchange(repo.getReferenceById(DataExchange.class, dataExchangeUuid));
        state.setName(model.getName());
        state.setType(model.getType());
        state.setApiKey(model.getApiKey());
        state.setClientId(model.getClientId());
        state.setUserId(loggedUserUuid);
        state.setProjectUrl(model.getProjectUrl());
        state.setChangeStartDate(zdtNow);
        state.setCreateUser(repo.getReferenceById(User.class, loggedUserUuid));
        state.setValid(getDefaultValidTypes().contains(model.getType()) || model.getValid());
        repo.persist(state);
    }

    public void updateDataExchange(
            VngRepository repo,
            DataExchangeModel dataExchangeModel,
            DataExchangeModel oldModel,
            ZonedDateTime now,
            UUID loggedUserUuid) throws VngNotFoundException {

        User loggedUser = repo.getReferenceById(User.class, loggedUserUuid);

        if (dataExchangeModel.hasUpdatedStateFields(oldModel)) {
            deleteDataExchangeState(repo, dataExchangeModel.getId(), now, loggedUserUuid);
            createDataExchangeState(repo, dataExchangeModel.getId(), dataExchangeModel, now, loggedUserUuid, true);
        }

        Map<UUID, DataExchangePropertyModel> oldPropMap = oldModel.getProperties().stream().collect(Collectors.toMap(DataExchangePropertyModel::getId, p -> p));
        for (var dxProperty : dataExchangeModel.getProperties()) {
            var oldDxProperty = oldPropMap.get(dxProperty.getId());
            if (!Objects.equals(dxProperty.getCustomPropertyId(), oldDxProperty.getCustomPropertyId())) {
                if (oldDxProperty.getCustomPropertyId() != null) {
                    DataExchangePropertyState oldDxPropState = repo.getDataExchangeDAO()
                            .getActiveDataExchangePropertyStateByDataExchangePropertyUuid(dxProperty.getId());
                    oldDxPropState.setChangeUser(loggedUser);
                    oldDxPropState.setChangeEndDate(now);
                    repo.persist(oldDxPropState);
                }
                if (dxProperty.getCustomPropertyId() != null) {
                    DataExchangePropertyState newDxPropState = new DataExchangePropertyState();
                    newDxPropState.setDataExchangeProperty(repo.getReferenceById(DataExchangeProperty.class, dxProperty.getId()));
                    newDxPropState.setProperty(repo.getReferenceById(Property.class, dxProperty.getCustomPropertyId()));
                    newDxPropState.setCreateUser(loggedUser);
                    newDxPropState.setChangeStartDate(now);
                    repo.persist(newDxPropState);
                }
            }
            if (dxProperty.getOptions() == null) {
                dxProperty.setOptions(new ArrayList<>());
            }
            if (oldDxProperty.getOptions() == null) {
                oldDxProperty.setOptions(new ArrayList<>());
            }
            for (var oldDxOption : oldDxProperty.getOptions()) {
                var newDxOption = dxProperty.getOptions().stream().filter(o -> o.getId().equals(oldDxOption.getId())).findFirst().orElse(null);
                List<DataExchangeOptionState> optionStates = repo.getDataExchangeDAO()
                        .getActiveDataExchangeOptionsStatesByDataExchangeOptionUuid(oldDxOption.getId());
                optionStates.forEach(os -> {
                    if (newDxOption == null ||
                            (os.getPropertyCategoryValue() != null && (newDxOption.getPropertyCategoryValueIds() == null
                                    || !newDxOption.getPropertyCategoryValueIds().contains(os.getPropertyCategoryValue().getId())))
                            ||
                            (os.getPropertyOrdinalValue() != null && (newDxOption.getPropertyOrdinalValueIds() == null
                                    || !newDxOption.getPropertyOrdinalValueIds().contains(os.getPropertyOrdinalValue().getId())))) {
                        os.setChangeUser(loggedUser);
                        os.setChangeEndDate(now);
                        repo.persist(os);
                    }
                });
            }
            for (var newDxOption : dxProperty.getOptions()) {
                var oldDxOption = oldDxProperty.getOptions().stream().filter(o -> o.getId().equals(newDxOption.getId())).findFirst().orElse(null);
                if (newDxOption.getPropertyCategoryValueIds() != null) {
                    newDxOption.getPropertyCategoryValueIds().forEach(catValId -> {
                        if (oldDxOption == null || !oldDxOption.getPropertyCategoryValueIds().contains(catValId)) {
                            createDataExchangeOptionState(repo, newDxOption.getId(), catValId, null, now, loggedUser);
                        }
                    });
                }
                if (newDxOption.getPropertyOrdinalValueIds() != null) {
                    newDxOption.getPropertyOrdinalValueIds().forEach(ordValId -> {
                        if (oldDxOption == null || !oldDxOption.getPropertyOrdinalValueIds().contains(ordValId)) {
                            createDataExchangeOptionState(repo, newDxOption.getId(), null, ordValId, now, loggedUser);
                        }
                    });
                }
            }
        }

        if (dataExchangeModel.getPriceCategories() != null) {
            var newMappings = new HashMap<UUID, OwnershipCategory>();
            for (var newMapping : getAllMappings(dataExchangeModel)) {
                for (var id : newMapping.getCategoryValueIds()) {
                    newMappings.put(id, newMapping.getName());
                }
            }

            var oldMappings = new HashMap<UUID, OwnershipCategory>();
            var dbMappings = new HashMap<OwnershipCategory, DataExchangePriceCategoryMapping>();
            for (var existingMappingsForCategory : repo.getDataExchangeDAO().getDataExchangePriceMappings(dataExchangeModel.getId())) {
                dbMappings.put(existingMappingsForCategory.getOwnershipCategory(), existingMappingsForCategory);
                for (var existingMapping : existingMappingsForCategory.getMappings()) {
                    UUID priceRangeId = existingMapping.getPriceRange().getId();
                    OwnershipCategory category = existingMappingsForCategory.getOwnershipCategory();
                    if (newMappings.containsKey(priceRangeId)) {
                        oldMappings.put(priceRangeId, category);
                    } else {
                        existingMapping.setChangeEndDate(now);
                        existingMapping.setChangeUser(loggedUser);
                        repo.persist(existingMapping);
                    }
                }
            }

            for (var newMapping : newMappings.entrySet()) {
                if (!oldMappings.containsKey(newMapping.getKey())) {
                    // This one needs to be added to the database
                    var newEntity = new DataExchangePriceCategoryMappingState();
                    newEntity.withDataExchangePriceCategoryMapping(dbMappings.get(newMapping.getValue()));
                    newEntity.withPriceRange(repo.getReferenceById(PropertyRangeCategoryValue.class, newMapping.getKey()));
                    newEntity.setChangeStartDate(now);
                    newEntity.setChangeUser(loggedUser);
                    repo.persist(newEntity);
                }
            }

        }

    }

    private ArrayList<PriceCategoryMapping> getAllMappings(DataExchangeModel dataExchangeModel) {
        var allMappings = new ArrayList<PriceCategoryMapping>();
        if (dataExchangeModel.getPriceCategories().getBuy() != null) {
            allMappings.addAll(dataExchangeModel.getPriceCategories().getBuy());
        }
        if (dataExchangeModel.getPriceCategories().getRent() != null) {
            allMappings.addAll(dataExchangeModel.getPriceCategories().getRent());
        }
        return allMappings;
    }

    private void createDataExchangeOptionState(VngRepository repo, UUID optionId, UUID catValId, UUID ordValId, ZonedDateTime now, User loggedUser) {
        DataExchangeOptionState newState = new DataExchangeOptionState();
        newState.setDataExchangeOption(repo.getReferenceById(DataExchangeOption.class, optionId));
        if (catValId != null) {
            newState.setPropertyCategoryValue(repo.getReferenceById(PropertyCategoryValue.class, catValId));
        }
        if (ordValId != null) {
            newState.setPropertyOrdinalValue(repo.getReferenceById(PropertyOrdinalValue.class, catValId));
        }
        newState.setCreateUser(loggedUser);
        newState.setChangeStartDate(now);
        repo.persist(newState);
    }

    public void deleteDataExchangeState(VngRepository repo, UUID dataExchangeId, ZonedDateTime now, UUID loggedUserUuid)
            throws VngNotFoundException {

        DataExchangeState state = repo.getDataExchangeDAO().getActiveDataExchangeStateByDataExchangeUuid(dataExchangeId);
        if (state == null) {
            throw new VngNotFoundException();
        }
        state.setChangeEndDate(ZonedDateTime.now());
        state.setChangeUser(repo.getReferenceById(User.class, loggedUserUuid));
        repo.persist(state);

    }

    public StreamingOutput getExportObject(VngRepository repo, ConfigModel configModel, UUID dataExchangeUuid, DataExchangeExportModel dxExportModel,
            List<DataExchangeExportError> errors, LoggedUser loggedUser)
            throws VngNotFoundException, VngBadRequestException {

        DataExchangeModel dataExchangeModel = getDataExchangeModel(repo, dataExchangeUuid, false);
        if (dataExchangeModel.getValid() != Boolean.TRUE) {
            throw new VngBadRequestException("Trying to export based on an invalid data exchange.");
        }
        String validationError = dxExportModel.validate(dataExchangeModel.getType());
        if (validationError != null) {
            throw new VngBadRequestException(validationError);
        }

        var template = DataExchangeTemplate.templates.get(dataExchangeModel.getType());

        final var templateMinConfidentiality = template.getMinimumConfidentiality();

        if (dxExportModel.getConfidentialityLevels() != null && !dxExportModel.getConfidentialityLevels().isEmpty()) {
            var selectedMinConfidentiality = dxExportModel.getConfidentialityLevels().stream()
                    .min(Comparator.comparing(Confidentiality.confidentialityMap::get)).get();
            if (Confidentiality.confidentialityMap.get(selectedMinConfidentiality) < Confidentiality.confidentialityMap.get(templateMinConfidentiality)) {
                throw new VngBadRequestException(
                        "Selected minimum confidentiality (%s) is lower than the minimum confidentiality allowed by the export (%s)"
                                .formatted(selectedMinConfidentiality, templateMinConfidentiality));
            }
        }

        DataExchangeConfigForExport dataExchangeConfigForExport = new DataExchangeConfigForExport(dataExchangeModel);

        List<PropertyModel> customProps = repo.getPropertyDAO().getPropertiesList(null, false, null);
        return switch (dataExchangeModel.getType()) {
        case ESRI_ZUID_HOLLAND -> EsriZuidHollandExport.buildExportObject(
                configModel,
                repo.getProjectsDAO().getProjectsExportList(dxExportModel, loggedUser),
                customProps,
                dataExchangeConfigForExport.getDxPropertiesMap(),
                dxExportModel.getExportDate(),
                templateMinConfidentiality,
                errors);
        case GEO_JSON -> GeoJSONExport.buildExportObject(
                repo.getProjectsDAO().getProjectsExportListExtended(dxExportModel, loggedUser),
                customProps);
        case EXCEL -> ExcelExport.buildExportObject(
                repo.getProjectsDAO().getProjectsExportListExtended(dxExportModel, loggedUser),
                customProps);

        case GDB_GELDERLAND -> GdbGelderlandExport.buildExportObject(
                repo.getProjectsDAO().getProjectsExportListExtended(dxExportModel, loggedUser),
                customProps,
                dataExchangeConfigForExport,
                template,
                dxExportModel.getExportDate(),
                errors,
                loggedUser);
        };

    }

    public void exportProject(StreamingOutput output, String token, String filename, String username) {
        arcGisProjectExporter.exportProject(output, token, filename, username);
    }

    private Set<DataExchangeType> getDefaultValidTypes() {
        return Set.of(DataExchangeType.GEO_JSON, DataExchangeType.EXCEL);
    }

}
