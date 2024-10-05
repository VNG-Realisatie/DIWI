package nl.vng.diwi.services;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.DataExchange;
import nl.vng.diwi.dal.entities.DataExchangeOption;
import nl.vng.diwi.dal.entities.DataExchangeProperty;
import nl.vng.diwi.dal.entities.DataExchangePropertySqlModel;
import nl.vng.diwi.dal.entities.DataExchangeState;
import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.dataexchange.DataExchangeTemplate;
import nl.vng.diwi.models.DataExchangeModel;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.rest.VngNotFoundException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class DataExchangeService {

    public DataExchangeService() {
    }

    public List<DataExchangeModel> getDataExchangeList(VngRepository repo) {

        List<DataExchangeState> states = repo.getDataExchangeDAO().getActiveDataExchangeStates();

        return states.stream().map(DataExchangeModel::new).toList();

    }

    public DataExchangeModel getDataExchangeModel(VngRepository repo, UUID dataExchangeId) throws VngNotFoundException {

        DataExchangeState state = repo.getDataExchangeDAO().getActiveDataExchangeStateByDataExchangeUuid(dataExchangeId);
        if (state == null) {
            throw new VngNotFoundException();
        }

        DataExchangeModel model = new DataExchangeModel(state);
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

        if (template != null) {
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
                        dxOption.setDxOptionName(option);
                        repo.persist(dxOption);
                    });
                }
            });
        }
    }

    public void createDataExchangeState(VngRepository repo, UUID dataExchangeUuid, DataExchangeModel model, ZonedDateTime zdtNow, UUID loggedUserUuid) {

        DataExchangeState state = new DataExchangeState();
        state.setDataExchange(repo.getReferenceById(DataExchange.class, dataExchangeUuid));
        state.setName(model.getName());
        state.setType(model.getType());
        state.setApiKey(model.getApiKey());
        state.setProjectUrl(model.getProjectUrl());
        state.setProjectDetailUrl(model.getProjectDetailUrl());
        state.setChangeStartDate(zdtNow);
        state.setCreateUser(repo.getReferenceById(User.class, loggedUserUuid));
        repo.persist(state);

    }

    public void updateDataExchange(VngRepository repo, DataExchangeModel dataExchangeModel, ZonedDateTime now, UUID loggedUserUuid)
        throws VngNotFoundException {

        deleteDataExchangeState(repo, dataExchangeModel.getId(), now, loggedUserUuid);
        createDataExchangeState(repo, dataExchangeModel.getId(), dataExchangeModel, now, loggedUserUuid);

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

}
