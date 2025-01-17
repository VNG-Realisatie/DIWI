package nl.vng.diwi.services;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.DataExchange;
import nl.vng.diwi.dal.entities.DataExchangeOption;
import nl.vng.diwi.dal.entities.DataExchangeOptionState;
import nl.vng.diwi.dal.entities.DataExchangeProperty;
import nl.vng.diwi.dal.entities.DataExchangePropertySqlModel;
import nl.vng.diwi.dal.entities.DataExchangePropertyState;
import nl.vng.diwi.dal.entities.DataExchangeState;
import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.Property;
import nl.vng.diwi.dal.entities.PropertyCategoryValue;
import nl.vng.diwi.dal.entities.PropertyOrdinalValue;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.dataexchange.DataExchangeTemplate;
import nl.vng.diwi.models.ConfigModel;
import nl.vng.diwi.models.DataExchangeExportModel;
import nl.vng.diwi.models.DataExchangeModel;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.export.excel.ExcelExport;
import nl.vng.diwi.services.export.geojson.GeoJSONExport;
import nl.vng.diwi.services.export.zuidholland.EsriZuidHollandExport;

public class DataExchangeService {

    public DataExchangeService() {
    }

    public List<DataExchangeModel> getDataExchangeList(VngRepository repo, boolean includeApiKey) {

        List<DataExchangeState> states = repo.getDataExchangeDAO().getActiveDataExchangeStates();

        return states.stream().map(s -> new DataExchangeModel(s, includeApiKey)).toList();

    }

    public DataExchangeModel getDataExchangeModel(VngRepository repo, UUID dataExchangeId, boolean includeApiKey) throws VngNotFoundException {

        DataExchangeState state = repo.getDataExchangeDAO().getActiveDataExchangeStateByDataExchangeUuid(dataExchangeId);
        if (state == null) {
            throw new VngNotFoundException();
        }

        DataExchangeModel model = new DataExchangeModel(state, includeApiKey);
        List<DataExchangePropertySqlModel> dxSqlProperties = repo.getDataExchangeDAO().getDataExchangeProperties(dataExchangeId);
        dxSqlProperties.forEach(sqlProp -> model.getProperties().add(new DataExchangePropertyModel(sqlProp)));

        return model;
    }

    public UUID createDataExchange(VngRepository repo, DataExchangeModel model, ZonedDateTime zdtNow, UUID loggedUserUuid) {

        DataExchange dataExchange = new DataExchange();
        repo.persist(dataExchange);

        createDataExchangeState(repo, dataExchange.getId(), model, zdtNow, loggedUserUuid);

        createDataExchangeTemplate(repo, dataExchange, model.getType());

        return dataExchange.getId();

    }

    private void createDataExchangeTemplate(VngRepository repo, DataExchange dataExchange, DataExchangeType type) {

        DataExchangeTemplate template = DataExchangeTemplate.templates.get(type);

        if (template == null) {
            throw new VngServerErrorException("Template for type " + type.name() + " is not defined.");
        }
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
    }

    public void createDataExchangeState(VngRepository repo, UUID dataExchangeUuid, DataExchangeModel model, ZonedDateTime zdtNow, UUID loggedUserUuid) {

        DataExchangeState state = new DataExchangeState();
        state.setDataExchange(repo.getReferenceById(DataExchange.class, dataExchangeUuid));
        state.setName(model.getName());
        state.setType(model.getType());
        state.setApiKey(model.getApiKey());
        state.setProjectUrl(model.getProjectUrl());
        state.setChangeStartDate(zdtNow);
        state.setCreateUser(repo.getReferenceById(User.class, loggedUserUuid));
        state.setValid(model.getValid());
        repo.persist(state);

    }

    public void updateDataExchange(VngRepository repo, DataExchangeModel dataExchangeModel, DataExchangeModel oldModel, ZonedDateTime now, UUID loggedUserUuid)
            throws VngNotFoundException {

        User loggedUser = repo.getReferenceById(User.class, loggedUserUuid);

        if (dataExchangeModel.areStateFieldsDifferent(oldModel)) {
            deleteDataExchangeState(repo, dataExchangeModel.getId(), now, loggedUserUuid);
            createDataExchangeState(repo, dataExchangeModel.getId(), dataExchangeModel, now, loggedUserUuid);
        }

        Map<UUID, DataExchangePropertyModel> oldPropMap = oldModel.getProperties().stream().collect(Collectors.toMap(DataExchangePropertyModel::getId, p -> p));
        for (var dxProperty : dataExchangeModel.getProperties()) {
            var oldDxProperty = oldPropMap.get(dxProperty.getId());
            if (!Objects.equals(dxProperty.getCustomPropertyId(), oldDxProperty.getCustomPropertyId())) {
                if (oldDxProperty.getCustomPropertyId() != null) {
                    DataExchangePropertyState oldDxPropState = repo.getDataExchangeDAO().getActiveDataExchangePropertyStateByDataExchangePropertyUuid(dxProperty.getId());
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
                List<DataExchangeOptionState> optionStates = repo.getDataExchangeDAO().getActiveDataExchangeOptionsStatesByDataExchangeOptionUuid(oldDxOption.getId());
                optionStates.forEach(os -> {
                    if (newDxOption == null ||
                        (os.getPropertyCategoryValue() != null && (newDxOption.getPropertyCategoryValueIds() == null || !newDxOption.getPropertyCategoryValueIds().contains(os.getPropertyCategoryValue().getId()))) ||
                        (os.getPropertyOrdinalValue() != null && (newDxOption.getPropertyOrdinalValueIds() == null || !newDxOption.getPropertyOrdinalValueIds().contains(os.getPropertyOrdinalValue().getId())))) {
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

    public Object getExportObject(VngRepository repo, ConfigModel configModel, UUID dataExchangeUuid, DataExchangeExportModel dxExportModel,
            List<DataExchangeExportError> errors, LoggedUser loggedUser)
            throws VngNotFoundException, VngBadRequestException {

        DataExchangeModel dataExchangeModel = getDataExchangeModel(repo, dataExchangeUuid, false);
        if (dataExchangeModel.getValid() != Boolean.TRUE) {
            throw new VngBadRequestException("Trying to export based on an invalid data exchange.");
        }

        var template = DataExchangeTemplate.templates.get(dataExchangeModel.getType());

        final var selectedMinConfidentiality = dataExchangeModel.getMinimumConfidentiality();
        final var templateMinconfidentiality = template.getMinimumConfidentiality();

        if (Confidentiality.confidentialityMap.get(selectedMinConfidentiality) < Confidentiality.confidentialityMap.get(templateMinconfidentiality)) {
            throw new VngBadRequestException(
                    "Selected minimum confidentiality (%s) is lower than the minimum confidentiality allowed by the export (%s)"
                            .formatted(selectedMinConfidentiality, templateMinconfidentiality));
        }

        Map<String, DataExchangePropertyModel> dxPropertiesMap = dataExchangeModel.getProperties().stream()
                .collect(Collectors.toMap(DataExchangePropertyModel::getName, Function.identity()));

        List<PropertyModel> customProps = repo.getPropertyDAO().getPropertiesList(null, false, null);
        return switch (dataExchangeModel.getType()) {
            case ESRI_ZUID_HOLLAND -> EsriZuidHollandExport.buildExportObject(
                configModel,
                repo.getProjectsDAO().getProjectsExportList(dxExportModel, loggedUser),
                customProps,
                dxPropertiesMap,
                dxExportModel.getExportDate(),
                configModel.getMinimumExportConfidentiality(),
                errors);
            case GEO_JSON -> GeoJSONExport.buildExportObject(
                configModel,
                repo.getProjectsDAO().getProjectsExportListExtended(dxExportModel, loggedUser),
                customProps,
                dxPropertiesMap,
                dxExportModel.getExportDate(),
                configModel.getMinimumExportConfidentiality(),
                errors);
            case EXCEL -> ExcelExport.buildExportObject(
                repo.getProjectsDAO().getProjectsExportListExtended(dxExportModel, loggedUser),
                customProps);
        };

    }
}
