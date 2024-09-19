package nl.vng.diwi.services;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.DataExchange;
import nl.vng.diwi.dal.entities.DataExchangeState;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.models.DataExchangeModel;
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

    public DataExchangeState getDataExchange(VngRepository repo, UUID dataExchangeId) throws VngNotFoundException {

        DataExchangeState state = repo.getDataExchangeDAO().getActiveDataExchangeStateByDataExchangeUuid(dataExchangeId);

        if (state == null) {
            throw new VngNotFoundException();
        }

        return state;
    }


    public UUID createDataExchange(VngRepository repo, DataExchangeModel model, ZonedDateTime zdtNow, UUID loggedUserUuid) {

        DataExchange dataExchange = new DataExchange();
        repo.persist(dataExchange);

        createDataExchangeState(repo, dataExchange.getId(), model, zdtNow, loggedUserUuid);

        return dataExchange.getId();

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

    public void deleteDataExchangeState(VngRepository repo, UUID dataExchangeUuid, ZonedDateTime now, UUID loggedUserUuid)
        throws VngNotFoundException {

        DataExchangeState state = getDataExchange(repo, dataExchangeUuid);

        state.setChangeEndDate(ZonedDateTime.now());
        state.setChangeUser(repo.getReferenceById(User.class, loggedUserUuid));
        repo.persist(state);

    }

}
